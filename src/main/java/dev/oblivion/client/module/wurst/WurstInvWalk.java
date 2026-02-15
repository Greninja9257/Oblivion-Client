package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import org.lwjgl.glfw.GLFW;

public final class WurstInvWalk extends Module {
    public WurstInvWalk() {
        super("InvWalk", "Allows movement while inventory/screens are open.", Category.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.currentScreen == null || mc.getWindow() == null) return;

        long handle = mc.getWindow().getHandle();
        mc.options.forwardKey.setPressed(GLFW.glfwGetKey(handle, mc.options.forwardKey.getDefaultKey().getCode()) == GLFW.GLFW_PRESS);
        mc.options.backKey.setPressed(GLFW.glfwGetKey(handle, mc.options.backKey.getDefaultKey().getCode()) == GLFW.GLFW_PRESS);
        mc.options.leftKey.setPressed(GLFW.glfwGetKey(handle, mc.options.leftKey.getDefaultKey().getCode()) == GLFW.GLFW_PRESS);
        mc.options.rightKey.setPressed(GLFW.glfwGetKey(handle, mc.options.rightKey.getDefaultKey().getCode()) == GLFW.GLFW_PRESS);
        mc.options.jumpKey.setPressed(GLFW.glfwGetKey(handle, mc.options.jumpKey.getDefaultKey().getCode()) == GLFW.GLFW_PRESS);
        mc.options.sneakKey.setPressed(GLFW.glfwGetKey(handle, mc.options.sneakKey.getDefaultKey().getCode()) == GLFW.GLFW_PRESS);
    }
}
