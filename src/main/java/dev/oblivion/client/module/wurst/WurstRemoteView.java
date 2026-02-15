package dev.oblivion.client.module.wurst;

import dev.oblivion.client.OblivionClient;
import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import net.minecraft.entity.Entity;

public final class WurstRemoteView extends Module {
    private Entity tracked;

    public WurstRemoteView() {
        super("RemoteView", "Tracks nearest entity using freecam positioning.", Category.RENDER);
    }

    @Override
    protected void onEnable() {
        tracked = null;
        var freecam = OblivionClient.get().moduleManager.get("Freecam");
        if (freecam != null && !freecam.isEnabled()) freecam.enable();
    }

    @Override
    protected void onDisable() {
        var freecam = OblivionClient.get().moduleManager.get("Freecam");
        if (freecam != null && freecam.isEnabled()) freecam.disable();
        tracked = null;
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null) return;

        if (tracked == null || !tracked.isAlive()) {
            double best = Double.MAX_VALUE;
            for (Entity e : mc.world.getEntities()) {
                if (e == mc.player) continue;
                double d = mc.player.squaredDistanceTo(e);
                if (d < best) {
                    best = d;
                    tracked = e;
                }
            }
        }

        if (tracked != null) {
            mc.player.setPosition(tracked.getPos());
        }
    }
}
