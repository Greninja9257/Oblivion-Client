package dev.oblivion.client.module.player;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.BoolSetting;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

public class AutoTool extends Module {

    private final BoolSetting switchBack = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Switch Back")
            .description("Switch back to the previous slot after mining.")
            .defaultValue(true)
            .build()
    );

    private int previousSlot = -1;
    private boolean switched = false;

    public AutoTool() {
        super("AutoTool", "Automatically switches to the best tool for the block you are mining.", Category.PLAYER);
    }

    @Override
    protected void onDisable() {
        if (switchBack.get() && switched && previousSlot != -1 && mc.player != null) {
            mc.player.getInventory().selectedSlot = previousSlot;
        }
        switched = false;
        previousSlot = -1;
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;

        if (mc.interactionManager.isBreakingBlock() && mc.crosshairTarget instanceof BlockHitResult blockHit) {
            if (blockHit.getType() == HitResult.Type.BLOCK) {
                BlockPos pos = blockHit.getBlockPos();
                BlockState state = mc.world.getBlockState(pos);

                int bestSlot = getBestToolSlot(state);
                if (bestSlot != -1 && bestSlot != mc.player.getInventory().selectedSlot) {
                    if (!switched) {
                        previousSlot = mc.player.getInventory().selectedSlot;
                        switched = true;
                    }
                    mc.player.getInventory().selectedSlot = bestSlot;
                }
            }
        } else if (switched && switchBack.get()) {
            if (previousSlot != -1) {
                mc.player.getInventory().selectedSlot = previousSlot;
            }
            switched = false;
            previousSlot = -1;
        }
    }

    private int getBestToolSlot(BlockState state) {
        int bestSlot = -1;
        float bestSpeed = 1.0f;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            float speed = stack.getMiningSpeedMultiplier(state);
            if (speed > bestSpeed) {
                bestSpeed = speed;
                bestSlot = i;
            }
        }

        return bestSlot;
    }
}
