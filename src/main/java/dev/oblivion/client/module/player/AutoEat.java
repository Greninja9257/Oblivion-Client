package dev.oblivion.client.module.player;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.IntSetting;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class AutoEat extends Module {

    private final IntSetting hungerThreshold = settings.getDefaultGroup().add(
        new IntSetting.Builder()
            .name("Hunger Threshold")
            .description("Start eating when hunger is at or below this level.")
            .defaultValue(14)
            .range(1, 19)
            .build()
    );

    private boolean eating = false;
    private int previousSlot = -1;

    public AutoEat() {
        super("AutoEat", "Automatically eats food when hungry.", Category.PLAYER);
    }

    @Override
    protected void onDisable() {
        stopEating();
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null) return;
        if (mc.player.getAbilities().creativeMode) return;

        int hunger = mc.player.getHungerManager().getFoodLevel();

        if (hunger <= hungerThreshold.get()) {
            int foodSlot = findFoodInHotbar();
            if (foodSlot == -1) return;

            if (!eating) {
                previousSlot = mc.player.getInventory().selectedSlot;
                mc.player.getInventory().selectedSlot = foodSlot;
                eating = true;
            }

            mc.options.useKey.setPressed(true);
        } else if (eating) {
            stopEating();
        }
    }

    private void stopEating() {
        if (eating) {
            mc.options.useKey.setPressed(false);
            if (previousSlot != -1 && mc.player != null) {
                mc.player.getInventory().selectedSlot = previousSlot;
            }
            previousSlot = -1;
            eating = false;
        }
    }

    private int findFoodInHotbar() {
        if (mc.player == null) return -1;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty()) {
                FoodComponent food = stack.get(DataComponentTypes.FOOD);
                if (food != null) {
                    return i;
                }
            }
        }
        return -1;
    }
}
