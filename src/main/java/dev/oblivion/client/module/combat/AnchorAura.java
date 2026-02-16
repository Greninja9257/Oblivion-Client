package dev.oblivion.client.module.combat;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;
import dev.oblivion.client.setting.impl.BoolSetting;
import net.minecraft.block.Blocks;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Comparator;
import java.util.stream.StreamSupport;

public class AnchorAura extends Module {

    private final DoubleSetting range = settings.getDefaultGroup().add(
        new DoubleSetting.Builder()
            .name("Range")
            .description("Range to target players.")
            .defaultValue(4.5)
            .range(1.0, 6.0)
            .build()
    );

    private final DoubleSetting placeRange = settings.getDefaultGroup().add(
        new DoubleSetting.Builder()
            .name("Place Range")
            .description("Range to place anchors.")
            .defaultValue(4.5)
            .range(1.0, 6.0)
            .build()
    );

    private final DoubleSetting minDamage = settings.getDefaultGroup().add(
        new DoubleSetting.Builder()
            .name("Min Damage")
            .description("Minimum damage to deal.")
            .defaultValue(6.0)
            .range(0.0, 36.0)
            .build()
    );

    private final BoolSetting antiSelf = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Anti Self")
            .description("Prevent damaging yourself.")
            .defaultValue(true)
            .build()
    );

    public AnchorAura() {
        super("AnchorAura", "Automatically uses respawn anchors to deal damage in non-overworld dimensions.", Category.COMBAT);
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;
        if (mc.world.getDimension().bedWorks()) return; // Only works outside overworld

        PlayerEntity target = findTarget();
        if (target == null) return;

        // Look for existing charged anchors near target
        BlockPos targetPos = target.getBlockPos();
        for (BlockPos pos : BlockPos.iterateOutwards(targetPos, 3, 3, 3)) {
            if (mc.world.getBlockState(pos).getBlock() == Blocks.RESPAWN_ANCHOR) {
                int charges = mc.world.getBlockState(pos).get(RespawnAnchorBlock.CHARGES);
                if (charges > 0) {
                    // Detonate it
                    interactBlock(pos);
                    return;
                } else {
                    // Charge it with glowstone
                    int glowSlot = findItem(Items.GLOWSTONE);
                    if (glowSlot != -1) {
                        int prev = mc.player.getInventory().selectedSlot;
                        mc.player.getInventory().selectedSlot = glowSlot;
                        interactBlock(pos);
                        mc.player.getInventory().selectedSlot = prev;
                    }
                    return;
                }
            }
        }

        // Place a new anchor near target
        int anchorSlot = findItem(Items.RESPAWN_ANCHOR);
        if (anchorSlot == -1) return;

        BlockPos placePos = findPlacePos(targetPos);
        if (placePos == null) return;

        int prev = mc.player.getInventory().selectedSlot;
        mc.player.getInventory().selectedSlot = anchorSlot;
        interactBlock(placePos.down());
        mc.player.getInventory().selectedSlot = prev;
    }

    private PlayerEntity findTarget() {
        return StreamSupport.stream(mc.world.getEntities().spliterator(), false)
            .filter(e -> e instanceof PlayerEntity && e != mc.player && e.isAlive())
            .map(e -> (PlayerEntity) e)
            .filter(p -> mc.player.distanceTo(p) <= range.get())
            .min(Comparator.comparingDouble(p -> mc.player.distanceTo(p)))
            .orElse(null);
    }

    private BlockPos findPlacePos(BlockPos around) {
        for (BlockPos pos : BlockPos.iterateOutwards(around, 2, 2, 2)) {
            if (mc.world.getBlockState(pos).isReplaceable() && !mc.world.getBlockState(pos.down()).isReplaceable()) {
                if (mc.player.getBlockPos().getSquaredDistance(pos) <= placeRange.get() * placeRange.get()) {
                    return pos;
                }
            }
        }
        return null;
    }

    private void interactBlock(BlockPos pos) {
        BlockHitResult hit = new BlockHitResult(Vec3d.ofCenter(pos), Direction.UP, pos, false);
        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hit);
        mc.player.swingHand(Hand.MAIN_HAND);
    }

    private int findItem(net.minecraft.item.Item item) {
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).getItem() == item) return i;
        }
        return -1;
    }
}
