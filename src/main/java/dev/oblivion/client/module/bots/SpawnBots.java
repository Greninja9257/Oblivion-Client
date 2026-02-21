package dev.oblivion.client.module.bots;

import dev.oblivion.client.setting.impl.BoolSetting;
import dev.oblivion.client.setting.impl.IntSetting;
import dev.oblivion.client.setting.impl.StringSetting;
import dev.oblivion.client.util.ChatUtil;
import net.minecraft.client.network.ServerInfo;

import java.util.List;

public final class SpawnBots extends BotModule {
    private final BoolSetting randomNames = settings.getDefaultGroup().add(
        new BoolSetting.Builder().name("Random Names").description("Use generated random usernames for spawned bots.").defaultValue(true).build()
    );

    private final StringSetting namePrefix = settings.getDefaultGroup().add(
        new StringSetting.Builder().name("Name Prefix").description("Prefix used for generated random names.").defaultValue("obv_").visible(randomNames::get).build()
    );

    private final IntSetting randomLength = settings.getDefaultGroup().add(
        new IntSetting.Builder().name("Random Length").description("Suffix length for generated random names.").defaultValue(6).range(3, 16).visible(randomNames::get).build()
    );

    private final BoolSetting useCurrentServer = settings.getDefaultGroup().add(
        new BoolSetting.Builder().name("Use Current Server").description("Auto-use the server you are currently connected to.").defaultValue(true).build()
    );

    private final StringSetting serverHost = settings.getDefaultGroup().add(
        new StringSetting.Builder().name("Server Host").description("Target server host for bot connections.").defaultValue("127.0.0.1").visible(() -> !useCurrentServer.get()).build()
    );

    private final IntSetting serverPort = settings.getDefaultGroup().add(
        new IntSetting.Builder().name("Server Port").description("Target server port.").defaultValue(25565).range(1, 65535).visible(() -> !useCurrentServer.get()).build()
    );

    private final StringSetting fixedName = settings.getDefaultGroup().add(
        new StringSetting.Builder().name("Fixed Name").description("Used if random names is disabled.").defaultValue("obv_bot").visible(() -> !randomNames.get()).build()
    );

    public SpawnBots() {
        super("SpawnBots", "Spawns integrated Java bots with configurable amount/random names.");
    }

    @Override
    protected void onEnable() {
        String host = serverHost.get();
        int port = serverPort.get();

        if (useCurrentServer.get()) {
            HostPort target = resolveCurrentServer();
            if (target != null) {
                host = target.host();
                port = target.port();
                ChatUtil.info("Using current server for bots: " + host + ":" + port);
            } else {
                ChatUtil.warning("Could not detect current server. Falling back to manual host/port.");
            }
        }

        List<String> spawned = botManager().spawnSwarm(
            host,
            port,
            botAmount.get(),
            randomNames.get(),
            namePrefix.get(),
            randomLength.get(),
            fixedName.get()
        );

        reportAction("spawn requested for " + spawned.size() + " bot(s)");
        disable();
    }

    private HostPort resolveCurrentServer() {
        if (mc == null) return null;
        ServerInfo entry = mc.getCurrentServerEntry();
        if (entry == null || entry.address == null || entry.address.isBlank()) {
            return null;
        }

        String raw = entry.address.trim();
        String host = raw;
        int port = 25565;

        int lastColon = raw.lastIndexOf(':');
        if (lastColon > 0 && lastColon < raw.length() - 1) {
            String possiblePort = raw.substring(lastColon + 1);
            try {
                port = Integer.parseInt(possiblePort);
                host = raw.substring(0, lastColon);
            } catch (NumberFormatException ignored) {
                host = raw;
            }
        }

        if (host.startsWith("[") && host.endsWith("]") && host.length() > 2) {
            host = host.substring(1, host.length() - 1);
        }

        return new HostPort(host, port);
    }

    private record HostPort(String host, int port) {}
}
