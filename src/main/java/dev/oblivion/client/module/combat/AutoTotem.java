package dev.oblivion.client.module.combat;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

public class AutoTotem extends Module {

    private final DoubleSetting healthThreshold = settings.getDefaultGroup().add(
        new DoubleSetting.Builder()
            .name("Health Threshold")
            .description("Equip totem when health falls below this value.")
            .defaultValue(36.0)
            .range(1.0, 36.0)
            .build()
    );

    public AutoTotem() {
        super("AutoTotem", "Automatically equips a totem of undying in offhand.", Category.COMBAT);
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.interactionManager == null) return;

        // Already holding a totem in offhand
        if (mc.player.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING) return;

        // Only equip if health is below threshold
        float health = mc.player.getHealth() + mc.player.getAbsorptionAmount();
        if (health > healthThreshold.get()) return;

        // Find totem in inventory
        int totemSlot = findTotemSlot();
        if (totemSlot == -1) return;

        // Move totem to offhand (slot 45)
        // Pick up the totem
        mc.interactionManager.clickSlot(
            mc.player.currentScreenHandler.syncId,
            totemSlot,
            0,
            SlotActionType.PICKUP,
            mc.player
        );

        // Place in offhand slot (45)
        mc.interactionManager.clickSlot(
            mc.player.currentScreenHandler.syncId,
            45,
            0,
            SlotActionType.PICKUP,
            mc.player
        );

        // If there was an item in offhand, put it back where the totem was
        if (!mc.player.currentScreenHandler.getCursorStack().isEmpty()) {
            mc.interactionManager.clickSlot(
                mc.player.currentScreenHandler.syncId,
                totemSlot,
                0,
                SlotActionType.PICKUP,
                mc.player
            );
        }
    }

    private int findTotemSlot() {
        // Search hotbar (slots 36-44 in screen handler)
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).getItem() == Items.TOTEM_OF_UNDYING) {
                return 36 + i;
            }
        }

        // Search main inventory (slots 9-35 in screen handler)
        for (int i = 9; i < 36; i++) {
            if (mc.player.getInventory().getStack(i).getItem() == Items.TOTEM_OF_UNDYING) {
                return i;
            }
        }

        return -1;
    }
}
