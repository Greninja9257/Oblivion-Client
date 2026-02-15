package dev.oblivion.client.module.combat;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.BoolSetting;
import dev.oblivion.client.setting.impl.DoubleSetting;
import dev.oblivion.client.util.PlayerUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.Comparator;
import java.util.List;

public class CrystalAura extends Module {

    private final DoubleSetting placeRange = settings.getDefaultGroup().add(
        new DoubleSetting.Builder()
            .name("Place Range")
            .description("Range to place end crystals.")
            .defaultValue(4.5)
            .range(1.0, 6.0)
            .build()
    );

    private final DoubleSetting breakRange = settings.getDefaultGroup().add(
        new DoubleSetting.Builder()
            .name("Break Range")
            .description("Range to break end crystals.")
            .defaultValue(4.5)
            .range(1.0, 6.0)
            .build()
    );

    private final BoolSetting autoPlace = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Auto Place")
            .description("Automatically place end crystals near targets.")
            .defaultValue(true)
            .build()
    );

    private final BoolSetting autoBreak = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Auto Break")
            .description("Automatically break nearby end crystals.")
            .defaultValue(true)
            .build()
    );

    private final BoolSetting targetPlayers = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Target Players")
            .description("Target players for crystal placement.")
            .defaultValue(true)
            .build()
    );

    public CrystalAura() {
        super("CrystalAura", "Automatically places and breaks end crystals.", Category.COMBAT);
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;

        if (autoBreak.get()) {
            breakCrystals();
        }

        if (autoPlace.get()) {
            placeCrystals();
        }
    }

    private void breakCrystals() {
        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof EndCrystalEntity crystal)) continue;
            if (PlayerUtil.distanceTo(crystal) > breakRange.get()) continue;

            float[] rots = PlayerUtil.getRotationsTo(crystal);
            mc.player.setYaw(rots[0]);
            mc.player.setPitch(rots[1]);

            mc.interactionManager.attackEntity(mc.player, crystal);
            mc.player.swingHand(Hand.MAIN_HAND);
            break; // One crystal per tick
        }
    }

    private void placeCrystals() {
        if (mc.player.getMainHandStack().getItem() != Items.END_CRYSTAL
            && mc.player.getOffHandStack().getItem() != Items.END_CRYSTAL) {
            return;
        }

        Hand crystalHand = mc.player.getMainHandStack().getItem() == Items.END_CRYSTAL
            ? Hand.MAIN_HAND : Hand.OFF_HAND;

        PlayerEntity target = findTarget();
        if (target == null) return;

        BlockPos bestPos = findBestPlacement(target);
        if (bestPos == null) return;

        float[] rots = PlayerUtil.getRotationsTo(Vec3d.ofCenter(bestPos));
        mc.player.setYaw(rots[0]);
        mc.player.setPitch(rots[1]);

        BlockHitResult hitResult = new BlockHitResult(
            Vec3d.ofCenter(bestPos),
            Direction.UP,
            bestPos,
            false
        );

        mc.interactionManager.interactBlock(mc.player, crystalHand, hitResult);
        mc.player.swingHand(crystalHand);
    }

    private PlayerEntity findTarget() {
        PlayerEntity closest = null;
        double closestDist = placeRange.get() + 12.0; // Extra range for placement near target

        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof PlayerEntity player)) continue;
            if (!PlayerUtil.isValid(player)) continue;
            if (!targetPlayers.get()) continue;

            double dist = PlayerUtil.distanceTo(player);
            if (dist < closestDist) {
                closestDist = dist;
                closest = player;
            }
        }

        return closest;
    }

    private BlockPos findBestPlacement(PlayerEntity target) {
        BlockPos playerPos = mc.player.getBlockPos();
        BlockPos bestPos = null;
        double bestDist = Double.MAX_VALUE;

        for (int x = -5; x <= 5; x++) {
            for (int y = -3; y <= 3; y++) {
                for (int z = -5; z <= 5; z++) {
                    BlockPos pos = playerPos.add(x, y, z);

                    if (!canPlaceCrystal(pos)) continue;

                    double playerDist = Math.sqrt(pos.getSquaredDistance(mc.player.getPos()));
                    if (playerDist > placeRange.get()) continue;

                    double targetDist = Math.sqrt(pos.getSquaredDistance(target.getPos()));
                    if (targetDist < bestDist) {
                        bestDist = targetDist;
                        bestPos = pos;
                    }
                }
            }
        }

        return bestPos;
    }

    private boolean canPlaceCrystal(BlockPos pos) {
        BlockState baseBlock = mc.world.getBlockState(pos);
        if (!baseBlock.isOf(Blocks.OBSIDIAN) && !baseBlock.isOf(Blocks.BEDROCK)) return false;

        BlockPos above = pos.up();
        if (!mc.world.getBlockState(above).isAir()) return false;

        BlockPos above2 = pos.up(2);
        if (!mc.world.getBlockState(above2).isAir()) return false;

        // Check no entities blocking placement
        Box placementBox = new Box(above);
        if (!mc.world.getOtherEntities(null, placementBox).isEmpty()) return false;

        return true;
    }
}
