package dev.oblivion.client.module.combat;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.BoolSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.*;

public class AutoWeapon extends Module {

    private final BoolSetting preferSwords = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Prefer Swords")
            .description("Prefer swords over axes.")
            .defaultValue(true)
            .build()
    );

    private final BoolSetting switchBack = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Switch Back")
            .description("Switch back to previous slot after attacking.")
            .defaultValue(false)
            .build()
    );

    private int previousSlot = -1;
    private boolean switched = false;
    private int ticksSinceSwitch = 0;

    public AutoWeapon() {
        super("AutoWeapon", "Automatically switches to the best weapon when attacking.", Category.COMBAT);
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null) return;

        if (switched && switchBack.get()) {
            ticksSinceSwitch++;
            if (ticksSinceSwitch > 3 && previousSlot != -1) {
                mc.player.getInventory().selectedSlot = previousSlot;
                switched = false;
                previousSlot = -1;
                ticksSinceSwitch = 0;
            }
        }
    }

    public void onAttack(Entity target) {
        if (mc.player == null) return;

        int bestSlot = -1;
        double bestDamage = -1;
        boolean bestIsSword = false;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.isEmpty()) continue;
            Item item = stack.getItem();

            double damage = 0;
            boolean isSword = false;

            if (item instanceof SwordItem sword) {
                damage = sword.getComponents().get(net.minecraft.component.DataComponentTypes.ATTRIBUTE_MODIFIERS) != null ? 7 : 4;
                isSword = true;
            } else if (item instanceof AxeItem) {
                damage = 9;
            }

            if (damage > bestDamage || (damage == bestDamage && preferSwords.get() && isSword && !bestIsSword)) {
                bestDamage = damage;
                bestSlot = i;
                bestIsSword = isSword;
            }
        }

        if (bestSlot != -1 && bestSlot != mc.player.getInventory().selectedSlot) {
            previousSlot = mc.player.getInventory().selectedSlot;
            mc.player.getInventory().selectedSlot = bestSlot;
            switched = true;
            ticksSinceSwitch = 0;
        }
    }
}
