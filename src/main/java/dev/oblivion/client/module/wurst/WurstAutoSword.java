package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;

public final class WurstAutoSword extends Module {
    public WurstAutoSword() {
        super("AutoSword", "Always keeps the strongest sword selected.", Category.COMBAT);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null) return;

        int bestSlot = -1;
        int bestScore = -1;

        for (int i = 0; i < 9; i++) {
            var stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() instanceof SwordItem) {
                int score = swordScore(stack.getItem());
                if (score > bestScore) {
                    bestScore = score;
                    bestSlot = i;
                }
            }
        }

        if (bestSlot != -1) {
            mc.player.getInventory().selectedSlot = bestSlot;
        }
    }

    private int swordScore(net.minecraft.item.Item item) {
        if (item == Items.NETHERITE_SWORD) return 6;
        if (item == Items.DIAMOND_SWORD) return 5;
        if (item == Items.IRON_SWORD) return 4;
        if (item == Items.STONE_SWORD) return 3;
        if (item == Items.GOLDEN_SWORD) return 2;
        if (item == Items.WOODEN_SWORD) return 1;
        return 0;
    }
}
