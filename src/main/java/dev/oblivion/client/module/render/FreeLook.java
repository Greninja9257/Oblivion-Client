package dev.oblivion.client.module.render;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;

public class FreeLook extends Module {

    private float savedYaw;
    private float savedPitch;
    private boolean active = false;

    public FreeLook() {
        super("FreeLook", "Allows you to look around without changing your movement direction.", Category.RENDER);
    }

    @Override
    protected void onEnable() {
        if (mc.player != null) {
            savedYaw = mc.player.getYaw();
            savedPitch = mc.player.getPitch();
            active = true;
        }
    }

    @Override
    protected void onDisable() {
        if (mc.player != null && active) {
            mc.player.setYaw(savedYaw);
            mc.player.setPitch(savedPitch);
        }
        active = false;
    }

    public float getSavedYaw() { return savedYaw; }
    public float getSavedPitch() { return savedPitch; }
    public boolean isActive() { return active; }
}
