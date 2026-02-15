package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;
import net.minecraft.item.Items;

public final class WurstArrowDmg extends Module {
    private final DoubleSetting boost = settings.getDefaultGroup().add(
        new DoubleSetting.Builder().name("Boost").description("Arrow charge assist while drawing").defaultValue(0.08).range(0.0, 0.3).build()
    );

    public WurstArrowDmg() {
        super("ArrowDmg", "Increases bow shot effectiveness by stabilizing draw.", Category.COMBAT);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null) return;
        if (mc.player.getMainHandStack().getItem() != Items.BOW) return;
        if (!mc.player.isUsingItem()) return;

        mc.player.setPitch(Math.max(-89, Math.min(89, mc.player.getPitch() - boost.get().floatValue())));
    }
}
