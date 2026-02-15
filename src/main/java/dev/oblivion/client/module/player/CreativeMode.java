package dev.oblivion.client.module.player;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import net.minecraft.world.GameMode;

public class CreativeMode extends Module {
    private GameMode savedGameMode;
    private boolean savedInvulnerable;
    private boolean savedFlying;
    private boolean savedAllowFlying;
    private boolean savedCreativeMode;
    private boolean savedAllowModifyWorld;
    private float savedFlySpeed;
    private float savedWalkSpeed;
    private boolean saved = false;

    public CreativeMode() {
        super("CreativeMode", "Client-side creative abilities spoof.", Category.PLAYER);
    }

    @Override
    protected void onEnable() {
        if (mc.player == null || mc.interactionManager == null) return;
        savedGameMode = mc.interactionManager.getCurrentGameMode();
        saveIfNeeded();
        mc.interactionManager.setGameMode(GameMode.CREATIVE);
        applyCreativeAbilities();
    }

    @Override
    protected void onDisable() {
        if (mc.interactionManager != null) {
            mc.interactionManager.setGameMode(savedGameMode == null ? GameMode.SURVIVAL : savedGameMode);
        }
        restoreAbilities();
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.interactionManager == null) return;
        if (mc.interactionManager.getCurrentGameMode() != GameMode.CREATIVE) {
            mc.interactionManager.setGameMode(GameMode.CREATIVE);
        }
        saveIfNeeded();
        applyCreativeAbilities();
    }

    private void saveIfNeeded() {
        if (mc.player == null || saved) return;
        var abilities = mc.player.getAbilities();
        savedInvulnerable = abilities.invulnerable;
        savedFlying = abilities.flying;
        savedAllowFlying = abilities.allowFlying;
        savedCreativeMode = abilities.creativeMode;
        savedAllowModifyWorld = abilities.allowModifyWorld;
        savedFlySpeed = abilities.getFlySpeed();
        savedWalkSpeed = abilities.getWalkSpeed();
        saved = true;
    }

    private void applyCreativeAbilities() {
        if (mc.player == null) return;
        var abilities = mc.player.getAbilities();
        abilities.invulnerable = true;
        abilities.allowFlying = true;
        abilities.flying = true;
        abilities.creativeMode = true;
        abilities.allowModifyWorld = true;
        if (abilities.getFlySpeed() < 0.05f) abilities.setFlySpeed(0.05f);
        mc.player.sendAbilitiesUpdate();
    }

    private void restoreAbilities() {
        if (mc.player == null || !saved) return;
        var abilities = mc.player.getAbilities();
        abilities.invulnerable = savedInvulnerable;
        abilities.flying = savedFlying;
        abilities.allowFlying = savedAllowFlying;
        abilities.creativeMode = savedCreativeMode;
        abilities.allowModifyWorld = savedAllowModifyWorld;
        abilities.setFlySpeed(savedFlySpeed);
        abilities.setWalkSpeed(savedWalkSpeed);
        mc.player.sendAbilitiesUpdate();
        saved = false;
    }
}
