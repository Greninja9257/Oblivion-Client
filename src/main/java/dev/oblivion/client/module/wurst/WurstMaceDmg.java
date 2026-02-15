package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;
import net.minecraft.item.Items;

public final class WurstMaceDmg extends Module {
    private final DoubleSetting fallBoost = settings.getDefaultGroup().add(
        new DoubleSetting.Builder().name("Fall Boost").description("Extra downward momentum for mace crits").defaultValue(0.12).range(0.0, 0.4).build()
    );

    public WurstMaceDmg() {
        super("MaceDmg", "Boosts downward momentum while using a mace.", Category.COMBAT);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null) return;
        if (mc.player.getMainHandStack().getItem() != Items.MACE) return;
        if (!mc.options.attackKey.isPressed()) return;

        mc.player.setVelocity(mc.player.getVelocity().x, mc.player.getVelocity().y - fallBoost.get(), mc.player.getVelocity().z);
    }
}
