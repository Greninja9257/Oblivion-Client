package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public final class WurstFeedAura extends Module {
    private final DoubleSetting range = settings.getDefaultGroup().add(
        new DoubleSetting.Builder().name("Range").description("Feed range").defaultValue(4.0).range(1.0, 8.0).build()
    );

    public WurstFeedAura() {
        super("FeedAura", "Feeds nearby animals using held food.", Category.PLAYER);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;

        ItemStack held = mc.player.getMainHandStack();
        if (held.isEmpty()) return;

        for (AnimalEntity animal : mc.world.getEntitiesByClass(AnimalEntity.class, mc.player.getBoundingBox().expand(range.get()), a -> true)) {
            mc.interactionManager.interactEntity(mc.player, animal, Hand.MAIN_HAND);
            mc.player.swingHand(Hand.MAIN_HAND);
            return;
        }
    }
}
