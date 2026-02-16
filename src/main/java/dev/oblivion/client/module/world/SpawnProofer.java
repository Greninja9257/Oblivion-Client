package dev.oblivion.client.module.world;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.IntSetting;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;

public class SpawnProofer extends Module {

    private final IntSetting range = settings.getDefaultGroup().add(
        new IntSetting.Builder()
            .name("Range")
            .description("Range to spawn-proof.")
            .defaultValue(4)
            .range(1, 8)
            .build()
    );

    public SpawnProofer() {
        super("SpawnProofer", "Automatically places torches on dark areas to prevent mob spawning.", Category.WORLD);
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;

        int torchSlot = findTorchSlot();
        if (torchSlot == -1) return;

        BlockPos playerPos = mc.player.getBlockPos();
        int r = range.get();

        for (BlockPos pos : BlockPos.iterateOutwards(playerPos, r, r, r)) {
            if (!mc.world.getBlockState(pos).isAir()) continue;
            if (mc.world.getBlockState(pos.down()).isAir()) continue;

            int lightLevel = mc.world.getLightLevel(LightType.BLOCK, pos);
            if (lightLevel > 0) continue;

            int prev = mc.player.getInventory().selectedSlot;
            mc.player.getInventory().selectedSlot = torchSlot;

            BlockHitResult hit = new BlockHitResult(Vec3d.ofCenter(pos.down()), Direction.UP, pos.down(), false);
            mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hit);

            mc.player.getInventory().selectedSlot = prev;
            return; // One per tick
        }
    }

    private int findTorchSlot() {
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).getItem() == Items.TORCH) return i;
            if (mc.player.getInventory().getStack(i).getItem() == Items.SOUL_TORCH) return i;
        }
        return -1;
    }
}
