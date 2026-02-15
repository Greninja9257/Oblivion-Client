package dev.oblivion.client.proxy;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProxyManager {
    private static final String MC_TEST_HOST = "mc.hypixel.net";
    private static final int MC_TEST_PORT = 25565;

    public record FetchReport(int fetchedUnique, int added, int skipped) {}
    public record PruneReport(int total, int alive, int removedDead, int removedSlow, double averageMs) {}

    public enum ProxyType {
        SOCKS5,
        HTTP
    }

    public static class ProxyEntry {
        public final String name;
        public final ProxyType type;
        public final String host;
        public final int port;
        public final String username;
        public final String password;

        public ProxyEntry(String name, ProxyType type, String host, int port, String username, String password) {
            this.name = name;
            this.type = type;
            this.host = host;
            this.port = port;
            this.username = username == null ? "" : username;
            this.password = password == null ? "" : password;
        }
    }

    private final List<ProxyEntry> proxies = new ArrayList<>();
    private int activeIndex = -1;

    public void init() {
    }

    public synchronized List<ProxyEntry> getProxies() {
        return Collections.unmodifiableList(new ArrayList<>(proxies));
    }

    public synchronized int getActiveIndex() {
        return activeIndex;
    }

    public synchronized ProxyEntry getActive() {
        if (activeIndex < 0 || activeIndex >= proxies.size()) return null;
        return proxies.get(activeIndex);
    }

    public synchronized boolean add(ProxyEntry entry) {
        if (entry == null || entry.name.isBlank() || entry.host.isBlank() || entry.port <= 0) return false;
        boolean exists = proxies.stream().anyMatch(p ->
            p.name.equalsIgnoreCase(entry.name) ||
                (p.type == entry.type && p.host.equalsIgnoreCase(entry.host) && p.port == entry.port &&
                    p.username.equals(entry.username))
        );
        if (exists) return false;
        proxies.add(entry);
        return true;
    }

    public synchronized void remove(int index) {
        if (index < 0 || index >= proxies.size()) return;
        proxies.remove(index);
        if (activeIndex == index) activeIndex = -1;
        if (activeIndex > index) activeIndex--;
        if (activeIndex == -1) clearSystemProxy();
    }

    public synchronized void use(int index) {
        if (index < 0 || index >= proxies.size()) return;
        activeIndex = index;
        applySystemProxy(proxies.get(index));
    }

    public synchronized void disable() {
        activeIndex = -1;
        clearSystemProxy();
    }

    private void applySystemProxy(ProxyEntry entry) {
        clearSystemProxy();

        if (entry.type == ProxyType.SOCKS5) {
            System.setProperty("socksProxyHost", entry.host);
            System.setProperty("socksProxyPort", Integer.toString(entry.port));
            if (!entry.username.isBlank()) {
                System.setProperty("java.net.socks.username", entry.username);
                System.setProperty("java.net.socks.password", entry.password);
                Authenticator.setDefault(new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(entry.username, entry.password.toCharArray());
                    }
                });
            }
        } else {
            System.setProperty("http.proxyHost", entry.host);
            System.setProperty("http.proxyPort", Integer.toString(entry.port));
            System.setProperty("https.proxyHost", entry.host);
            System.setProperty("https.proxyPort", Integer.toString(entry.port));
            if (!entry.username.isBlank()) {
                Authenticator.setDefault(new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(entry.username, entry.password.toCharArray());
                    }
                });
            }
        }
    }

    private void clearSystemProxy() {
        System.clearProperty("socksProxyHost");
        System.clearProperty("socksProxyPort");
        System.clearProperty("java.net.socks.username");
        System.clearProperty("java.net.socks.password");
        System.clearProperty("http.proxyHost");
        System.clearProperty("http.proxyPort");
        System.clearProperty("https.proxyHost");
        System.clearProperty("https.proxyPort");
        Authenticator.setDefault(null);
    }

    public synchronized void load(Path file) throws IOException {
        proxies.clear();
        activeIndex = -1;
        clearSystemProxy();
        if (!Files.exists(file)) return;

        JsonObject root = JsonParser.parseString(Files.readString(file, StandardCharsets.UTF_8)).getAsJsonObject();
        JsonArray arr = root.getAsJsonArray("proxies");
        if (arr != null) {
            arr.forEach(e -> {
                JsonObject p = e.getAsJsonObject();
                String name = p.has("name") ? p.get("name").getAsString() : "";
                String type = p.has("type") ? p.get("type").getAsString() : "SOCKS5";
                String host = p.has("host") ? p.get("host").getAsString() : "";
                int port = p.has("port") ? p.get("port").getAsInt() : 0;
                String user = p.has("username") ? p.get("username").getAsString() : "";
                String pass = p.has("password") ? p.get("password").getAsString() : "";
                add(new ProxyEntry(name, ProxyType.valueOf(type), host, port, user, pass));
            });
        }

        if (root.has("activeIndex")) {
            int idx = root.get("activeIndex").getAsInt();
            if (idx >= 0 && idx < proxies.size()) {
                use(idx);
            }
        }
    }

    public synchronized void save(Path file) throws IOException {
        JsonObject root = new JsonObject();
        JsonArray arr = new JsonArray();

        for (ProxyEntry p : proxies) {
            JsonObject obj = new JsonObject();
            obj.addProperty("name", p.name);
            obj.addProperty("type", p.type.name());
            obj.addProperty("host", p.host);
            obj.addProperty("port", p.port);
            obj.addProperty("username", p.username);
            obj.addProperty("password", p.password);
            arr.add(obj);
        }

        root.add("proxies", arr);
        root.addProperty("activeIndex", activeIndex);
        Files.writeString(file, root.toString(), StandardCharsets.UTF_8);
    }

    public synchronized FetchReport fetchFastOnlineProxies(int maxPerType, int timeoutMs) {
        List<ProxyEntry> fetched = new ArrayList<>();

        // Prefer SOCKS5 for Minecraft traffic. Public HTTP proxies frequently fail CONNECT for game ports.
        // Use multiple sources because some endpoints rate-limit or return empty.
        int perSourceLimit = Math.max(20, maxPerType / 3);
        fetched.addAll(fetchFromUrl("https://api.proxyscrape.com/v4/free-proxy-list/get?request=display_proxies&protocol=socks5&timeout=2000&proxy_format=ipport&format=text", ProxyType.SOCKS5, "S5-PS", perSourceLimit, timeoutMs));
        fetched.addAll(fetchFromUrl("https://raw.githubusercontent.com/TheSpeedX/PROXY-List/master/socks5.txt", ProxyType.SOCKS5, "S5-TSX", perSourceLimit, timeoutMs));
        fetched.addAll(fetchFromUrl("https://raw.githubusercontent.com/ShiftyTR/Proxy-List/master/socks5.txt", ProxyType.SOCKS5, "S5-SH", perSourceLimit, timeoutMs));
        fetched.addAll(fetchFromUrl("https://raw.githubusercontent.com/monosans/proxy-list/main/proxies/socks5.txt", ProxyType.SOCKS5, "S5-MS", perSourceLimit, timeoutMs));

        int added = 0;
        for (ProxyEntry entry : fetched) {
            if (proxies.size() >= maxPerType) break;
            if (add(entry)) added++;
        }

        return new FetchReport(fetched.size(), added, fetched.size() - added);
    }

    public synchronized PruneReport pruneBadAndSlow(int timeoutMs, double slowFactor) {
        int total = proxies.size();
        if (total == 0) return new PruneReport(0, 0, 0, 0, 0);

        List<Long> latencies = new ArrayList<>();
        List<Integer> deadIndices = new ArrayList<>();

        for (int i = 0; i < proxies.size(); i++) {
            ProxyEntry p = proxies.get(i);
            long latency = testProxyLatency(p, timeoutMs);
            if (latency < 0) {
                deadIndices.add(i);
            } else {
                latencies.add(latency);
            }
        }

        for (int i = deadIndices.size() - 1; i >= 0; i--) {
            remove(deadIndices.get(i));
        }

        int removedDead = deadIndices.size();
        if (latencies.isEmpty()) {
            return new PruneReport(total, 0, removedDead, 0, 0);
        }

        double avg = latencies.stream().mapToLong(v -> v).average().orElse(0);
        double threshold = avg * Math.max(1.0, slowFactor);

        int removedSlow = 0;
        for (int i = proxies.size() - 1; i >= 0; i--) {
            ProxyEntry p = proxies.get(i);
            long latency = testProxyLatency(p, timeoutMs);
            if (latency >= 0 && latency > threshold) {
                remove(i);
                removedSlow++;
            }
        }

        return new PruneReport(total, proxies.size(), removedDead, removedSlow, avg);
    }

    private List<ProxyEntry> fetchFromUrl(String url, ProxyType type, String prefix, int limit, int timeoutMs) {
        List<ProxyEntry> out = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setConnectTimeout(timeoutMs);
            conn.setReadTimeout(timeoutMs);
            conn.setRequestProperty("User-Agent", "OblivionClient/1.0");

            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null && out.size() < limit) {
                    String s = line.trim();
                    if (s.isEmpty() || s.startsWith("#")) continue;
                    String[] parts = s.split(":");
                    if (parts.length != 2) continue;

                    String host = parts[0].trim();
                    int port;
                    try {
                        port = Integer.parseInt(parts[1].trim());
                    } catch (Exception ignored) {
                        continue;
                    }
                    if (port <= 0 || port > 65535) continue;

                    String key = type + "|" + host.toLowerCase() + ":" + port;
                    if (!seen.add(key)) continue;

                    // Quick filter for "fast" proxies.
                    long latency = testTcpLatency(host, port, timeoutMs);
                    if (latency < 0 || latency > 1200) continue;

                    out.add(new ProxyEntry(prefix + "-" + host + ":" + port, type, host, port, "", ""));
                }
            }
        } catch (Exception ignored) {
        }

        return out;
    }

    private long testTcpLatency(String host, int port, int timeoutMs) {
        long start = System.currentTimeMillis();
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeoutMs);
            return System.currentTimeMillis() - start;
        } catch (Exception e) {
            return -1L;
        }
    }

    private long testProxyLatency(ProxyEntry proxy, int timeoutMs) {
        long start = System.currentTimeMillis();
        try {
            Proxy type = proxy.type == ProxyType.SOCKS5
                ? new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxy.host, proxy.port))
                : new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxy.host, proxy.port));

            try (Socket socket = new Socket(type)) {
                socket.connect(new InetSocketAddress(MC_TEST_HOST, MC_TEST_PORT), timeoutMs);
                return System.currentTimeMillis() - start;
            }
        } catch (Exception e) {
            return -1L;
        }
    }
}
