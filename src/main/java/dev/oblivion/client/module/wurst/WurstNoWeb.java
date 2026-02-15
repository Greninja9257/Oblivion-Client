package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import net.minecraft.util.math.BlockPos;

public final class WurstNoWeb extends Module {
    public WurstNoWeb() {
        super("NoWeb", "Prevents slowdown while inside cobwebs.", Category.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null) return;

        BlockPos pos = mc.player.getBlockPos();
        if (mc.world.getBlockState(pos).getBlock().getTranslationKey().contains("cobweb")) {
            mc.player.setVelocity(mc.player.getVelocity().x * 1.8, Math.max(mc.player.getVelocity().y, 0.08), mc.player.getVelocity().z * 1.8);
        }
    }
}
