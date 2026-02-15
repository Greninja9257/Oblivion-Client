package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.village.VillagerProfession;

public final class WurstAutoLibrarian extends Module {
    public WurstAutoLibrarian() {
        super("AutoLibrarian", "Automatically interacts with nearby librarian villagers.", Category.PLAYER);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;

        for (VillagerEntity villager : mc.world.getEntitiesByClass(VillagerEntity.class, mc.player.getBoundingBox().expand(4.5), v -> true)) {
            if (villager.getVillagerData().getProfession() == VillagerProfession.LIBRARIAN) {
                mc.interactionManager.interactEntity(mc.player, villager, net.minecraft.util.Hand.MAIN_HAND);
                return;
            }
        }
    }
}
