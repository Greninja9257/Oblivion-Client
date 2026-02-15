package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import net.minecraft.entity.Entity;

import java.util.HashSet;
import java.util.Set;

public final class WurstTrueSight extends Module {
    private final Set<Integer> forcedGlowing = new HashSet<>();

    public WurstTrueSight() {
        super("TrueSight", "Reveals invisible entities by forcing glow.", Category.RENDER);
    }

    @Override
    protected void onDisable() {
        if (mc.world == null) return;
        for (Entity entity : mc.world.getEntities()) {
            if (forcedGlowing.contains(entity.getId())) {
                entity.setGlowing(false);
            }
        }
        forcedGlowing.clear();
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.world == null) return;

        for (Entity entity : mc.world.getEntities()) {
            if (entity == mc.player) continue;
            if (entity.isInvisible()) {
                entity.setGlowing(true);
                forcedGlowing.add(entity.getId());
            }
        }
    }
}
