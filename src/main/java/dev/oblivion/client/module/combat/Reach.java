package dev.oblivion.client.module.combat;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;
import net.minecraft.entity.attribute.EntityAttributes;

public class Reach extends Module {

    private final DoubleSetting reach = settings.getDefaultGroup().add(
        new DoubleSetting.Builder()
            .name("Reach")
            .description("Attack and interaction reach distance.")
            .defaultValue(4.5)
            .range(3.0, 6.0)
            .build()
    );

    public Reach() {
        super("Reach", "Extends your attack and interaction reach.", Category.COMBAT);
    }

    private double oldBlockRange = -1;
    private double oldEntityRange = -1;

    @Override
    protected void onDisable() {
        if (mc.player == null) return;
        if (mc.player.getAttributeInstance(EntityAttributes.BLOCK_INTERACTION_RANGE) != null && oldBlockRange > 0) {
            mc.player.getAttributeInstance(EntityAttributes.BLOCK_INTERACTION_RANGE).setBaseValue(oldBlockRange);
        }
        if (mc.player.getAttributeInstance(EntityAttributes.ENTITY_INTERACTION_RANGE) != null && oldEntityRange > 0) {
            mc.player.getAttributeInstance(EntityAttributes.ENTITY_INTERACTION_RANGE).setBaseValue(oldEntityRange);
        }
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null) return;

        var blockAttr = mc.player.getAttributeInstance(EntityAttributes.BLOCK_INTERACTION_RANGE);
        var entityAttr = mc.player.getAttributeInstance(EntityAttributes.ENTITY_INTERACTION_RANGE);
        if (blockAttr == null || entityAttr == null) return;

        if (oldBlockRange < 0) oldBlockRange = blockAttr.getBaseValue();
        if (oldEntityRange < 0) oldEntityRange = entityAttr.getBaseValue();

        double value = reach.get();
        blockAttr.setBaseValue(value);
        entityAttr.setBaseValue(value);
    }
}
