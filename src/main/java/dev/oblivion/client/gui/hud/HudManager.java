package dev.oblivion.client.gui.hud;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.oblivion.client.OblivionClient;
import dev.oblivion.client.gui.hud.elements.*;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public class HudManager {
    private final List<HudElement> elements = new ArrayList<>();

    public void init() {
        elements.add(new WatermarkElement());
        elements.add(new CoordinatesElement());
        elements.add(new FpsElement());
        elements.add(new ServerInfoElement());
        elements.add(new ActiveModulesElement());
        elements.add(new ArmorElement());
    }

    public void render(DrawContext context) {
        var mc = OblivionClient.mc();
        if (mc == null || mc.textRenderer == null || mc.player == null) return;

        for (HudElement element : elements) {
            if (element.isVisible()) {
                element.render(context);
            }
        }

        // Render notifications
        OblivionClient.get().notificationManager.render(context);
    }

    public List<HudElement> getElements() {
        return elements;
    }

    public HudElement getElement(String name) {
        for (HudElement e : elements) {
            if (e.name.equalsIgnoreCase(name)) return e;
        }
        return null;
    }

    // Mouse events for HUD editor
    public void onMouseClicked(double mouseX, double mouseY, int button) {
        for (HudElement element : elements) {
            if (element.isVisible()) {
                element.onMouseClicked(mouseX, mouseY, button);
            }
        }
    }

    public void onMouseDragged(double mouseX, double mouseY) {
        for (HudElement element : elements) {
            element.onMouseDragged(mouseX, mouseY);
        }
    }

    public void onMouseReleased() {
        for (HudElement element : elements) {
            element.onMouseReleased();
        }
    }

    public JsonObject toJson() {
        JsonObject root = new JsonObject();
        for (HudElement element : elements) {
            root.add(element.name, element.toJson());
        }
        return root;
    }

    public void fromJson(JsonObject json) {
        if (json == null) return;
        for (HudElement element : elements) {
            JsonElement elementJson = json.get(element.name);
            if (elementJson != null && elementJson.isJsonObject()) {
                element.fromJson(elementJson.getAsJsonObject());
            }
        }
    }
}
