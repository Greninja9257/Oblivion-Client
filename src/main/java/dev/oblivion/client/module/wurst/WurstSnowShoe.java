package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import net.minecraft.block.Blocks;

public final class WurstSnowShoe extends Module {
    public WurstSnowShoe() {
        super("SnowShoe", "Prevents sinking/slowing on powder snow.", Category.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null) return;

        var pos = mc.player.getBlockPos();
        if (mc.world.getBlockState(pos).isOf(Blocks.POWDER_SNOW)) {
            mc.player.setVelocity(mc.player.getVelocity().x, Math.max(mc.player.getVelocity().y, 0.08), mc.player.getVelocity().z);
        }
    }
}
