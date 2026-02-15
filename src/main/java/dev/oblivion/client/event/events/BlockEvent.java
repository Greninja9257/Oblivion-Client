package dev.oblivion.client.event.events;

import dev.oblivion.client.event.Event;
import net.minecraft.util.math.BlockPos;

public class BlockEvent extends Event {
    private final BlockPos pos;

    public BlockEvent(BlockPos pos) {
        this.pos = pos;
    }

    public BlockPos getPos() { return pos; }

    public static class Break extends BlockEvent {
        public Break(BlockPos pos) { super(pos); }
    }

    public static class Place extends BlockEvent {
        public Place(BlockPos pos) { super(pos); }
    }
}
