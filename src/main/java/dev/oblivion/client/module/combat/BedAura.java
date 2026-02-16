package dev.oblivion.client.module.combat;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;
import net.minecraft.block.BedBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.Comparator;
import java.util.stream.StreamSupport;

public class BedAura extends Module {

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
            .description("Range to place beds.")
            .defaultValue(4.5)
            .range(1.0, 6.0)
            .build()
    );

    public BedAura() {
        super("BedAura", "Automatically uses beds to deal damage outside the overworld.", Category.COMBAT);
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;
        if (mc.world.getDimension().bedWorks()) return; // Only works outside overworld

        PlayerEntity target = findTarget();
        if (target == null) return;

        // Look for existing beds near target
        BlockPos targetPos = target.getBlockPos();
        for (BlockPos pos : BlockPos.iterateOutwards(targetPos, 3, 3, 3)) {
            if (mc.world.getBlockState(pos).getBlock() instanceof BedBlock) {
                interactBlock(pos);
                return;
            }
        }

        // Place a bed near target
        int bedSlot = findBedSlot();
        if (bedSlot == -1) return;

        BlockPos placePos = findPlacePos(targetPos);
        if (placePos == null) return;

        int prev = mc.player.getInventory().selectedSlot;
        mc.player.getInventory().selectedSlot = bedSlot;
        BlockHitResult hit = new BlockHitResult(Vec3d.ofCenter(placePos.down()), Direction.UP, placePos.down(), false);
        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hit);
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
            if (mc.world.getBlockState(pos).isReplaceable()
                && mc.world.getBlockState(pos.offset(Direction.NORTH)).isReplaceable()
                && !mc.world.getBlockState(pos.down()).isReplaceable()) {
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

    private int findBedSlot() {
        for (int i = 0; i < 9; i++) {
            net.minecraft.item.Item item = mc.player.getInventory().getStack(i).getItem();
            String name = net.minecraft.registry.Registries.ITEM.getId(item).getPath();
            if (name.endsWith("_bed")) return i;
        }
        return -1;
    }
}
