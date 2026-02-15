package dev.oblivion.client.gui.hud.elements;

import dev.oblivion.client.gui.hud.HudElement;
import dev.oblivion.client.gui.theme.Theme;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

public class ArmorElement extends HudElement {

    public ArmorElement() {
        super("Armor", -1, -1); // Bottom-right area by default
    }

    @Override
    public void render(DrawContext context) {
        if (mc.player == null) return;

        int screenW = mc.getWindow().getScaledWidth();
        int screenH = mc.getWindow().getScaledHeight();

        width = 80;
        height = 68;

        int drawX = x < 0 ? screenW / 2 + 10 : x;
        int drawY = y < 0 ? screenH - height - 40 : y;

        EquipmentSlot[] slots = {
                EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
        };

        for (int i = 0; i < slots.length; i++) {
            ItemStack stack = mc.player.getEquippedStack(slots[i]);
            if (stack.isEmpty()) continue;

            int itemY = drawY + i * 17;
            context.drawItem(stack, drawX, itemY);

            if (stack.getMaxDamage() > 0) {
                int durability = stack.getMaxDamage() - stack.getDamage();
                float ratio = (float) durability / stack.getMaxDamage();
                int color;
                if (ratio > 0.5f) color = Theme.ACCENT_ENABLED;
                else if (ratio > 0.25f) color = Theme.NOTIFY_WARNING;
                else color = Theme.NOTIFY_DISABLED;

                String durText = durability + "";
                context.drawText(mc.textRenderer, durText, drawX + 18, itemY + 5, color, true);
            }
        }
    }
}
