package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import net.minecraft.entity.player.PlayerModelPart;

public final class WurstSkinDerp extends Module {
    private int tick;

    public WurstSkinDerp() {
        super("SkinDerp", "Randomly toggles player model parts.", Category.MISC);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (++tick % 8 != 0) return;

        for (PlayerModelPart part : PlayerModelPart.values()) {
            boolean state = Math.random() > 0.5;
            mc.options.setPlayerModelPart(part, state);
        }
    }

    @Override
    protected void onDisable() {
        for (PlayerModelPart part : PlayerModelPart.values()) {
            mc.options.setPlayerModelPart(part, true);
        }
    }
}
