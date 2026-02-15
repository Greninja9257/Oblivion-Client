package dev.oblivion.client.module.render;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class Freecam extends Module {

    private final DoubleSetting speed = settings.getDefaultGroup().add(
            new DoubleSetting.Builder().name("Speed").description("Camera movement speed").defaultValue(2.0).min(0.5).max(5.0).build()
    );

    private double savedX, savedY, savedZ;
    private float savedYaw, savedPitch;
    private boolean savedFlying;

    // Current freecam camera position
    private double camX, camY, camZ;
    private float camYaw, camPitch;

    public Freecam() {
        super("Freecam", "Detach the camera and fly freely", Category.RENDER);
    }

    @Override
    protected void onEnable() {
        if (mc.player == null) return;

        // Save current player state
        savedX = mc.player.getX();
        savedY = mc.player.getY();
        savedZ = mc.player.getZ();
        savedYaw = mc.player.getYaw();
        savedPitch = mc.player.getPitch();
        savedFlying = mc.player.getAbilities().flying;

        // Initialize camera at player position
        camX = savedX;
        camY = savedY;
        camZ = savedZ;
        camYaw = savedYaw;
        camPitch = savedPitch;
    }

    @Override
    protected void onDisable() {
        if (mc.player == null) return;

        // Restore player to saved position
        mc.player.setPosition(savedX, savedY, savedZ);
        mc.player.setYaw(savedYaw);
        mc.player.setPitch(savedPitch);
        mc.player.getAbilities().flying = savedFlying;
        mc.player.setVelocity(Vec3d.ZERO);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null) return;

        // Prevent momentum from moving the player body while in freecam.
        mc.player.setVelocity(Vec3d.ZERO);

        // Update camera rotation from player input
        camYaw = mc.player.getYaw();
        camPitch = mc.player.getPitch();

        // Calculate movement based on key inputs
        double forward = 0;
        double strafe = 0;
        double vertical = 0;

        if (mc.options.forwardKey.isPressed()) forward += 1;
        if (mc.options.backKey.isPressed()) forward -= 1;
        if (mc.options.leftKey.isPressed()) strafe += 1;
        if (mc.options.rightKey.isPressed()) strafe -= 1;
        if (mc.options.jumpKey.isPressed()) vertical += 1;
        if (mc.options.sneakKey.isPressed()) vertical -= 1;

        double moveSpeed = speed.get() * 0.5;

        // Convert yaw to radians for directional movement
        double yawRad = Math.toRadians(camYaw);
        double pitchRad = Math.toRadians(camPitch);

        // Forward/backward movement (accounts for pitch for flying direction)
        camX += (-Math.sin(yawRad) * forward - Math.cos(yawRad) * (-strafe)) * moveSpeed;
        camZ += (Math.cos(yawRad) * forward - Math.sin(yawRad) * (-strafe)) * moveSpeed;
        camY += vertical * moveSpeed;

        // If moving forward/back, apply pitch-based vertical movement
        if (forward != 0) {
            camY += -Math.sin(pitchRad) * forward * moveSpeed;
        }

        // Keep local player entity at the freecam position so the camera remains stable.
        // Movement packets are cancelled in MixinClientPlayerEntity while Freecam is enabled.
        mc.player.setPosition(camX, camY, camZ);
    }

    public double getCamX() { return camX; }
    public double getCamY() { return camY; }
    public double getCamZ() { return camZ; }
    public float getCamYaw() { return camYaw; }
    public float getCamPitch() { return camPitch; }
}
