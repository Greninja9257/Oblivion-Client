package dev.oblivion.client.gui.hud;

import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public abstract class HudElement {
    protected static final MinecraftClient mc = MinecraftClient.getInstance();

    public final String name;
    protected int x, y, width, height;
    protected boolean visible = true;
    protected boolean dragging = false;
    private int dragOffsetX, dragOffsetY;

    protected HudElement(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public abstract void render(DrawContext context);

    public void onMouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && isHovered(mouseX, mouseY)) {
            dragging = true;
            dragOffsetX = (int) mouseX - x;
            dragOffsetY = (int) mouseY - y;
        }
    }

    public void onMouseDragged(double mouseX, double mouseY) {
        if (dragging) {
            x = (int) mouseX - dragOffsetX;
            y = (int) mouseY - dragOffsetY;
            // Clamp to screen bounds
            if (mc.getWindow() != null) {
                int screenW = mc.getWindow().getScaledWidth();
                int screenH = mc.getWindow().getScaledHeight();
                x = Math.max(0, Math.min(screenW - width, x));
                y = Math.max(0, Math.min(screenH - height, y));
            }
        }
    }

    public void onMouseReleased() {
        dragging = false;
    }

    public boolean isHovered(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("x", x);
        json.addProperty("y", y);
        json.addProperty("visible", visible);
        return json;
    }

    public void fromJson(JsonObject json) {
        if (json.has("x")) x = json.get("x").getAsInt();
        if (json.has("y")) y = json.get("y").getAsInt();
        if (json.has("visible")) visible = json.get("visible").getAsBoolean();
    }
}
