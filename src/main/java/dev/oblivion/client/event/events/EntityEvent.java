package dev.oblivion.client.event.events;

import dev.oblivion.client.event.Event;
import net.minecraft.entity.Entity;

public class EntityEvent extends Event {
    private final Entity entity;

    public EntityEvent(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() { return entity; }

    public static class Add extends EntityEvent {
        public Add(Entity entity) { super(entity); }
    }

    public static class Remove extends EntityEvent {
        public Remove(Entity entity) { super(entity); }
    }
}
