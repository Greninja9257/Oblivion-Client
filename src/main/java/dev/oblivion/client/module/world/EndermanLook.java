package dev.oblivion.client.module.world;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.EndermanEntity;

public class EndermanLook extends Module {

    public EndermanLook() {
        super("EndermanLook", "Prevents endermen from becoming hostile when you look at them.", Category.WORLD);
    }

    // This module works through a mixin that modifies the enderman's isPlayerStaring check.
    // The module being enabled signals the mixin to always return false.

    public boolean shouldPreventAggro() {
        return isEnabled();
    }
}
