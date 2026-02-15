package dev.oblivion.client.event.events;

import dev.oblivion.client.event.Event;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;

public class RenderEvent extends Event {
    public static class Hud extends RenderEvent {
        private final DrawContext context;
        private final RenderTickCounter tickCounter;

        public Hud(DrawContext context, RenderTickCounter tickCounter) {
            this.context = context;
            this.tickCounter = tickCounter;
        }

        public DrawContext getContext() { return context; }
        public RenderTickCounter getTickCounter() { return tickCounter; }
    }

    public static class World extends RenderEvent {
        private final MatrixStack matrices;
        private final float tickDelta;

        public World(MatrixStack matrices, float tickDelta) {
            this.matrices = matrices;
            this.tickDelta = tickDelta;
        }

        public MatrixStack getMatrices() { return matrices; }
        public float getTickDelta() { return tickDelta; }
    }
}
