package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public final class WurstTunneller extends Module {
    public WurstTunneller() {
        super("Tunneller", "Mines a 1x2 tunnel in front of the player.", Category.WORLD);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;

        Direction dir = mc.player.getHorizontalFacing();
        BlockPos feet = mc.player.getBlockPos().offset(dir);
        BlockPos head = feet.up();

        if (!mc.world.isAir(feet)) {
            mc.interactionManager.attackBlock(feet, dir);
        }
        if (!mc.world.isAir(head)) {
            mc.interactionManager.attackBlock(head, dir);
        }
    }
}
