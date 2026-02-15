package dev.oblivion.client.module.misc;

import dev.oblivion.client.OblivionClient;
import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.util.ChatUtil;
import org.lwjgl.glfw.GLFW;

public class MiddleClickFriend extends Module {
    private boolean wasPressed;

    public MiddleClickFriend() {
        super("MiddleClickFriend", "Toggle friend status by middle-clicking players.", Category.MISC);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.currentScreen != null) return;

        long handle = mc.getWindow().getHandle();
        boolean pressed = GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_MIDDLE) == GLFW.GLFW_PRESS;

        if (pressed && !wasPressed && mc.targetedEntity != null) {
            String name = mc.targetedEntity.getName().getString();
            if (OblivionClient.get().friendManager.isFriend(name)) {
                OblivionClient.get().friendManager.remove(name);
                ChatUtil.info("Removed friend: " + name);
            } else {
                OblivionClient.get().friendManager.add(name);
                ChatUtil.info("Added friend: " + name);
            }
        }

        wasPressed = pressed;
    }
}
