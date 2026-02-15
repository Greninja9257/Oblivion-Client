package dev.oblivion.client.module.world;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.util.ChatUtil;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class AutoBuild extends Module {

    private final List<BlockPos> schematic = new ArrayList<>();
    private int currentIndex = 0;

    public AutoBuild() {
        super("AutoBuild", "Automatically places blocks from a saved schematic pattern.", Category.WORLD);
    }

    @Override
    protected void onEnable() {
        currentIndex = 0;
        if (schematic.isEmpty()) {
            ChatUtil.warning("No schematic loaded. Use .autobuild to set up a pattern.");
        }
    }

    @Override
    protected void onDisable() {
        currentIndex = 0;
    }

    /**
     * Sets the schematic to build. Each BlockPos is relative to the player's position
     * when the module is enabled.
     */
    public void setSchematic(List<BlockPos> positions) {
        schematic.clear();
        schematic.addAll(positions);
        currentIndex = 0;
    }

    public List<BlockPos> getSchematic() {
        return schematic;
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;
        if (schematic.isEmpty() || currentIndex >= schematic.size()) return;

        // Ensure player is holding a block
        if (!(mc.player.getMainHandStack().getItem() instanceof BlockItem)) return;

        BlockPos origin = mc.player.getBlockPos();
        BlockPos target = origin.add(schematic.get(currentIndex));

        if (!mc.world.getBlockState(target).isAir()) {
            // Block already placed or occupied, skip
            currentIndex++;
            return;
        }

        // Find a valid adjacent face to place against
        Direction placeDir = findPlaceDirection(target);
        if (placeDir == null) {
            currentIndex++;
            return;
        }

        BlockPos neighbor = target.offset(placeDir);
        BlockHitResult hitResult = new BlockHitResult(
            Vec3d.ofCenter(neighbor),
            placeDir.getOpposite(),
            neighbor,
            false
        );

        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hitResult);
        currentIndex++;

        if (currentIndex >= schematic.size()) {
            ChatUtil.success("AutoBuild complete.");
            disable();
        }
    }

    private Direction findPlaceDirection(BlockPos target) {
        for (Direction dir : Direction.values()) {
            BlockPos adjacent = target.offset(dir);
            if (!mc.world.getBlockState(adjacent).isAir()) {
                return dir;
            }
        }
        return null;
    }
}
