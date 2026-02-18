package dev.oblivion.client.module.movement;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.lwjgl.glfw.GLFW;

public class ClickTP extends Module {

    private final DoubleSetting range = settings.getDefaultGroup().add(
        new DoubleSetting.Builder()
            .name("Range")
            .description("Maximum teleport distance in blocks.")
            .defaultValue(100.0)
            .range(10.0, 300.0)
            .build()
    );

    private boolean wasPressed;

    public ClickTP() {
        super("ClickTP", "Right-click to teleport to where you're looking.", Category.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null || mc.currentScreen != null) return;

        long handle = mc.getWindow().getHandle();
        boolean pressed = GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;

        if (pressed && !wasPressed) {
            Vec3d eyePos = mc.player.getEyePos();
            Vec3d lookVec = mc.player.getRotationVec(1.0f);
            Vec3d endPos = eyePos.add(lookVec.multiply(range.get()));

            BlockHitResult hitResult = mc.world.raycast(new RaycastContext(
                eyePos, endPos,
                RaycastContext.ShapeType.OUTLINE,
                RaycastContext.FluidHandling.NONE,
                mc.player
            ));

            if (hitResult.getType() == HitResult.Type.BLOCK) {
                BlockPos blockPos = hitResult.getBlockPos();
                // Teleport on top of the block
                mc.player.setPosition(
                    blockPos.getX() + 0.5,
                    blockPos.getY() + 1.0,
                    blockPos.getZ() + 0.5
                );
            }
        }

        wasPressed = pressed;
    }
}
