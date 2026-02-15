package dev.oblivion.client.gui.screen;

import dev.oblivion.client.OblivionClient;
import dev.oblivion.client.proxy.ProxyManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ProxiesScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget nameField;
    private TextFieldWidget hostField;
    private TextFieldWidget portField;
    private TextFieldWidget userField;
    private TextFieldWidget passField;

    private ButtonWidget typeButton;
    private ButtonWidget fetchButton;
    private ButtonWidget pruneButton;
    private ButtonWidget useButton;
    private ButtonWidget removeButton;
    private ButtonWidget statusLabel;
    private final List<ButtonWidget> rowButtons = new ArrayList<>();

    private ProxyManager.ProxyType currentType = ProxyManager.ProxyType.SOCKS5;
    private int selectedIndex = -1;
    private int scrollOffset = 0;
    private int listTop = 138;
    private String status = "";
    private boolean busy = false;

    public ProxiesScreen(Screen parent) {
        super(Text.literal("Proxies"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int cx = this.width / 2;

        nameField = addDrawableChild(new TextFieldWidget(this.textRenderer, cx - 130, 30, 100, 20, Text.literal("Name")));
        hostField = addDrawableChild(new TextFieldWidget(this.textRenderer, cx - 24, 30, 120, 20, Text.literal("Host")));
        portField = addDrawableChild(new TextFieldWidget(this.textRenderer, cx + 102, 30, 58, 20, Text.literal("Port")));
        userField = addDrawableChild(new TextFieldWidget(this.textRenderer, cx - 130, 56, 140, 20, Text.literal("User")));
        passField = addDrawableChild(new TextFieldWidget(this.textRenderer, cx + 16, 56, 144, 20, Text.literal("Pass")));

        nameField.setMaxLength(32);
        hostField.setMaxLength(128);
        portField.setMaxLength(5);
        userField.setMaxLength(64);
        passField.setMaxLength(64);

        typeButton = addDrawableChild(ButtonWidget.builder(Text.literal("Type: SOCKS5"), b -> {
            currentType = currentType == ProxyManager.ProxyType.SOCKS5 ? ProxyManager.ProxyType.HTTP : ProxyManager.ProxyType.SOCKS5;
            typeButton.setMessage(Text.literal("Type: " + currentType.name()));
        }).dimensions(cx - 130, 82, 110, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Add Proxy"), b -> addProxy()).dimensions(cx - 14, 82, 84, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Disable"), b -> {
            OblivionClient.get().proxyManager.disable();
            setStatus("Proxy disabled");
        }).dimensions(cx + 76, 82, 84, 20).build());

        fetchButton = addDrawableChild(ButtonWidget.builder(Text.literal("Fetch Fast"), b -> fetchFastProxies()).dimensions(cx - 130, 108, 120, 20).build());
        pruneButton = addDrawableChild(ButtonWidget.builder(Text.literal("Test & Prune"), b -> testAndPrune()).dimensions(cx - 4, 108, 164, 20).build());

        useButton = addDrawableChild(ButtonWidget.builder(Text.literal("Use Selected"), b -> {
            if (selectedIndex >= 0 && selectedIndex < OblivionClient.get().proxyManager.getProxies().size()) {
                OblivionClient.get().proxyManager.use(selectedIndex);
                setStatus("Using proxy: " + OblivionClient.get().proxyManager.getProxies().get(selectedIndex).name);
            }
        }).dimensions(cx - 130, 132, 130, 20).build());

        removeButton = addDrawableChild(ButtonWidget.builder(Text.literal("Remove Selected"), b -> {
            if (selectedIndex >= 0 && selectedIndex < OblivionClient.get().proxyManager.getProxies().size()) {
                String name = OblivionClient.get().proxyManager.getProxies().get(selectedIndex).name;
                OblivionClient.get().proxyManager.remove(selectedIndex);
                selectedIndex = -1;
                setStatus("Removed proxy: " + name);
            }
        }).dimensions(cx + 6, 132, 154, 20).build());

        statusLabel = addDrawableChild(ButtonWidget.builder(Text.literal(""), b -> {}).dimensions(cx - 130, 4, 260, 20).build());
        statusLabel.active = false;

        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), b -> close()).dimensions(cx - 40, this.height - 28, 80, 20).build());

        int rowHeight = 20;
        int visibleRows = Math.max(1, (this.height - (this.listTop + 40)) / rowHeight);
        for (int i = 0; i < visibleRows; i++) {
            final int row = i;
            ButtonWidget rb = addDrawableChild(ButtonWidget.builder(Text.literal(""), b -> {
                int idx = scrollOffset + row;
                if (idx >= 0 && idx < OblivionClient.get().proxyManager.getProxies().size()) selectedIndex = idx;
            }).dimensions(cx - 130, this.listTop + i * rowHeight, 290, 18).build());
            rowButtons.add(rb);
        }

        refreshUi();
    }

    private void addProxy() {
        String name = nameField.getText().trim();
        String host = hostField.getText().trim();
        String portRaw = portField.getText().trim();
        String user = userField.getText().trim();
        String pass = passField.getText();

        int port;
        try {
            port = Integer.parseInt(portRaw);
        } catch (Exception e) {
            setStatus("Invalid port");
            return;
        }

        boolean ok = OblivionClient.get().proxyManager.add(new ProxyManager.ProxyEntry(name, currentType, host, port, user, pass));
        if (!ok) {
            setStatus("Failed to add proxy (duplicate/invalid)");
            return;
        }

        nameField.setText("");
        hostField.setText("");
        portField.setText("");
        userField.setText("");
        passField.setText("");
        selectedIndex = OblivionClient.get().proxyManager.getProxies().size() - 1;
        setStatus("Added proxy: " + name);
    }

    private void setStatus(String msg) {
        status = msg;
    }

    private void fetchFastProxies() {
        if (busy) return;
        busy = true;
        setStatus("Fetching fast proxies...");

        Thread t = new Thread(() -> {
            ProxyManager.FetchReport r = OblivionClient.get().proxyManager.fetchFastOnlineProxies(60, 1800);
            if (this.client != null) {
                this.client.execute(() -> {
                    busy = false;
                    setStatus("Fetched SOCKS5 only: " + r.fetchedUnique() + ", added " + r.added() + ", skipped " + r.skipped());
                    refreshUi();
                });
            } else {
                busy = false;
            }
        }, "Oblivion-ProxyFetch");
        t.setDaemon(true);
        t.start();
    }

    private void testAndPrune() {
        if (busy) return;
        busy = true;
        setStatus("Testing proxies and pruning...");

        Thread t = new Thread(() -> {
            ProxyManager.PruneReport r = OblivionClient.get().proxyManager.pruneBadAndSlow(1600, 1.35);
            if (this.client != null) {
                this.client.execute(() -> {
                    busy = false;
                    setStatus(
                        "Total " + r.total() + ", alive " + r.alive() +
                            ", dead removed " + r.removedDead() +
                            ", slow removed " + r.removedSlow() +
                            ", avg " + String.format("%.0fms", r.averageMs())
                    );
                    if (selectedIndex >= OblivionClient.get().proxyManager.getProxies().size()) selectedIndex = -1;
                    refreshUi();
                });
            } else {
                busy = false;
            }
        }, "Oblivion-ProxyPrune");
        t.setDaemon(true);
        t.start();
    }

    private void refreshUi() {
        List<ProxyManager.ProxyEntry> proxies = OblivionClient.get().proxyManager.getProxies();
        int active = OblivionClient.get().proxyManager.getActiveIndex();

        boolean selectedValid = selectedIndex >= 0 && selectedIndex < proxies.size();
        useButton.active = selectedValid && !busy;
        removeButton.active = selectedValid && !busy;
        fetchButton.active = !busy;
        pruneButton.active = !busy;
        typeButton.active = !busy;
        nameField.setEditable(!busy);
        hostField.setEditable(!busy);
        portField.setEditable(!busy);
        userField.setEditable(!busy);
        passField.setEditable(!busy);

        statusLabel.setMessage(Text.literal(status));
        statusLabel.visible = !status.isBlank();

        int rowHeight = 20;
        int visibleRows = Math.max(1, (this.height - (this.listTop + 40)) / rowHeight);
        int maxScroll = Math.max(0, proxies.size() - visibleRows);
        if (scrollOffset > maxScroll) scrollOffset = maxScroll;
        if (scrollOffset < 0) scrollOffset = 0;

        for (int i = 0; i < rowButtons.size(); i++) {
            ButtonWidget row = rowButtons.get(i);
            int idx = scrollOffset + i;
            if (idx < proxies.size()) {
                ProxyManager.ProxyEntry p = proxies.get(idx);
                String marker = idx == selectedIndex ? "> " : "  ";
                String activeMarker = idx == active ? " [ACTIVE]" : "";
                row.visible = true;
                row.active = true;
                row.setMessage(Text.literal(marker + p.name + " | " + p.type + " | " + p.host + ":" + p.port + activeMarker));
            } else {
                row.visible = false;
                row.active = false;
                row.setMessage(Text.literal(""));
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        refreshUi();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int rowHeight = 20;
        int visibleRows = Math.max(1, (this.height - (this.listTop + 40)) / rowHeight);
        int maxScroll = Math.max(0, OblivionClient.get().proxyManager.getProxies().size() - visibleRows);
        if (verticalAmount < 0) scrollOffset = Math.min(maxScroll, scrollOffset + 1);
        if (verticalAmount > 0) scrollOffset = Math.max(0, scrollOffset - 1);
        refreshUi();
        return true;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 14, 0xFFFFFFFF);
        context.fill(this.width / 2 - 134, this.listTop - 2, this.width / 2 + 164, this.height - 40, 0x55000000);
        context.drawTextWithShadow(this.textRenderer, "Saved Proxies", this.width / 2 - 130, this.listTop - 14, 0xFFFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        if (this.client != null) this.client.setScreen(this.parent);
    }
}
