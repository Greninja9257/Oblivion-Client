package dev.oblivion.client.gui.screen;

import dev.oblivion.client.OblivionClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class AltsScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget nameField;
    private ButtonWidget loggedInLabel;
    private ButtonWidget statusLabel;
    private ButtonWidget useSelectedButton;
    private ButtonWidget removeSelectedButton;
    private final List<ButtonWidget> rowButtons = new ArrayList<>();
    private int selectedIndex = -1;
    private int scrollOffset = 0;
    private String status = "";
    private int statusColor = 0xFFAAAAAA;
    private int statusTicks = 0;
    private int listTop = 110;

    public AltsScreen(Screen parent) {
        super(Text.literal("Alts"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int top = 40;
        int rowHeight = 20;
        int visibleRows = Math.max(1, (this.height - (this.listTop + 40)) / rowHeight);

        this.nameField = new TextFieldWidget(this.textRenderer, centerX - 110, top, 220, 20, Text.literal("Alt Name"));
        this.nameField.setMaxLength(32);
        this.addDrawableChild(this.nameField);
        this.setFocused(this.nameField);
        this.nameField.setFocused(true);

        this.loggedInLabel = this.addDrawableChild(ButtonWidget.builder(Text.literal("Logged in as:"), b -> {})
            .dimensions(6, 4, 260, 20).build());
        this.loggedInLabel.active = false;

        this.statusLabel = this.addDrawableChild(ButtonWidget.builder(Text.literal(""), b -> {})
            .dimensions(centerX - 130, 4, 260, 20).build());
        this.statusLabel.active = false;

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Add"), button -> {
            addAltFromInput();
        }).dimensions(centerX - 110, top + 26, 70, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Add Current"), button -> {
            if (this.client != null) {
                String name = this.client.getSession().getUsername();
                boolean added = OblivionClient.get().accountManager.addOffline(name);
                selectAndScrollTo(name);
                setStatus(added ? "Added current session: " + name : "Already exists: " + name, added ? 0xFF55FF55 : 0xFFFFFF55);
            }
        }).dimensions(centerX - 34, top + 26, 100, 20).build());

        this.useSelectedButton = this.addDrawableChild(ButtonWidget.builder(Text.literal("Use Selected"), button -> {
            List<String> accounts = OblivionClient.get().accountManager.getOfflineAccounts();
            if (selectedIndex >= 0 && selectedIndex < accounts.size()) {
                OblivionClient.get().accountManager.useOffline(accounts.get(selectedIndex));
                setStatus("Switched to: " + accounts.get(selectedIndex), 0xFF55FF55);
            }
        }).dimensions(centerX + 72, top + 26, 100, 20).build());

        this.removeSelectedButton = this.addDrawableChild(ButtonWidget.builder(Text.literal("Remove Selected"), button -> {
            List<String> accounts = OblivionClient.get().accountManager.getOfflineAccounts();
            if (selectedIndex >= 0 && selectedIndex < accounts.size()) {
                String removed = accounts.get(selectedIndex);
                OblivionClient.get().accountManager.removeOffline(accounts.get(selectedIndex));
                selectedIndex = -1;
                setStatus("Removed: " + removed, 0xFFFF5555);
            }
        }).dimensions(centerX + 72, top + 50, 100, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Done"), button -> {
            this.close();
        }).dimensions(centerX - 40, this.height - 28, 80, 20).build());

        this.useSelectedButton.active = false;
        this.removeSelectedButton.active = false;

        this.rowButtons.clear();
        for (int i = 0; i < visibleRows; i++) {
            final int row = i;
            ButtonWidget rowButton = this.addDrawableChild(ButtonWidget.builder(Text.literal(""), button -> {
                int index = scrollOffset + row;
                List<String> accounts = OblivionClient.get().accountManager.getOfflineAccounts();
                if (index >= 0 && index < accounts.size()) {
                    selectedIndex = index;
                }
            }).dimensions(centerX - 110, this.listTop + i * rowHeight, 220, 18).build());
            rowButtons.add(rowButton);
        }

        refreshUi();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int rowHeight = 18;
        List<String> accounts = OblivionClient.get().accountManager.getOfflineAccounts();
        int visibleRows = Math.max(1, (this.height - (this.listTop + 40)) / rowHeight);
        int start = Math.min(scrollOffset, Math.max(0, accounts.size() - visibleRows));
        int end = Math.min(accounts.size(), start + visibleRows);

        for (int i = start; i < end; i++) {
            int y = this.listTop + (i - start) * rowHeight;
            if (mouseY < y || mouseY > y + rowHeight) continue;
            if (mouseX >= this.width / 2 - 110 && mouseX <= this.width / 2 + 110) {
                selectedIndex = i;
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void tick() {
        super.tick();
        if (statusTicks > 0) statusTicks--;
        refreshUi();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 257 || keyCode == 335) { // Enter / keypad Enter
            addAltFromInput();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        List<String> accounts = OblivionClient.get().accountManager.getOfflineAccounts();
        int rowHeight = 18;
        int visibleRows = Math.max(1, (this.height - (this.listTop + 40)) / rowHeight);
        int maxScroll = Math.max(0, accounts.size() - visibleRows);
        if (verticalAmount < 0) scrollOffset = Math.min(maxScroll, scrollOffset + 1);
        if (verticalAmount > 0) scrollOffset = Math.max(0, scrollOffset - 1);
        refreshUi();
        return true;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 28, 0xFFFFFFFF);

        List<String> accounts = OblivionClient.get().accountManager.getOfflineAccounts();

        context.fill(this.width / 2 - 114, this.listTop - 2, this.width / 2 + 114, this.height - 40, 0x55000000);
        context.drawTextWithShadow(this.textRenderer, "Saved Alts", this.width / 2 - 110, this.listTop - 14, 0xFFFFFFFF);
        context.drawTextWithShadow(this.textRenderer, "Total: " + accounts.size(), this.width / 2 + 40, this.listTop - 14, 0xFFAAAAAA);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    private void addAltFromInput() {
        String name = this.nameField.getText().trim();
        if (name.isEmpty()) {
            setStatus("Enter an alt name first.", 0xFFFF5555);
            return;
        }

        boolean exists = OblivionClient.get().accountManager.getOfflineAccounts().stream()
            .anyMatch(a -> a.equalsIgnoreCase(name));
        if (exists) {
            selectAndScrollTo(name);
            setStatus("Alt already exists: " + name, 0xFFFFFF55);
            return;
        }

        OblivionClient.get().accountManager.addOffline(name);
        selectAndScrollTo(name);
        this.nameField.setText("");
        setStatus("Added alt: " + name, 0xFF55FF55);
    }

    private void setStatus(String text, int color) {
        this.status = text;
        this.statusColor = color;
        this.statusTicks = 100;
    }

    private void refreshUi() {
        List<String> accounts = OblivionClient.get().accountManager.getOfflineAccounts();
        int rowHeight = 20;
        int visibleRows = Math.max(1, (this.height - (this.listTop + 40)) / rowHeight);
        int maxScroll = Math.max(0, accounts.size() - visibleRows);
        if (scrollOffset > maxScroll) scrollOffset = maxScroll;
        if (scrollOffset < 0) scrollOffset = 0;

        boolean selectedValid = selectedIndex >= 0 && selectedIndex < accounts.size();
        this.useSelectedButton.active = selectedValid;
        this.removeSelectedButton.active = selectedValid;

        String active = OblivionClient.get().accountManager.getActiveOfflineName();
        String logged = this.client == null ? "<unknown>" : this.client.getSession().getUsername();
        this.loggedInLabel.setMessage(Text.literal("Logged in as: " + logged));
        this.statusLabel.visible = statusTicks > 0 && !status.isEmpty();
        this.statusLabel.setMessage(Text.literal(status));

        for (int i = 0; i < rowButtons.size(); i++) {
            ButtonWidget row = rowButtons.get(i);
            int index = scrollOffset + i;
            if (index < accounts.size()) {
                String name = accounts.get(index);
                String marker = index == selectedIndex ? "> " : "  ";
                String activeMarker = name.equals(active) ? " [ACTIVE]" : "";
                row.visible = true;
                row.active = true;
                row.setMessage(Text.literal(marker + name + activeMarker));
            } else {
                row.visible = false;
                row.active = false;
                row.setMessage(Text.literal(""));
            }
        }
    }

    private void selectAndScrollTo(String name) {
        List<String> accounts = OblivionClient.get().accountManager.getOfflineAccounts();
        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).equalsIgnoreCase(name)) {
                selectedIndex = i;
                int rowHeight = 18;
                int visibleRows = Math.max(1, (this.height - (this.listTop + 40)) / rowHeight);
                int maxScroll = Math.max(0, accounts.size() - visibleRows);
                if (selectedIndex < scrollOffset) scrollOffset = selectedIndex;
                if (selectedIndex >= scrollOffset + visibleRows) scrollOffset = selectedIndex - visibleRows + 1;
                if (scrollOffset > maxScroll) scrollOffset = maxScroll;
                if (scrollOffset < 0) scrollOffset = 0;
                return;
            }
        }
    }
}
