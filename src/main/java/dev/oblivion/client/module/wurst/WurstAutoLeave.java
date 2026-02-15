package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;
import net.minecraft.text.Text;

public final class WurstAutoLeave extends Module {
    private final DoubleSetting health = settings.getDefaultGroup().add(
        new DoubleSetting.Builder().name("Health").description("Disconnect below this HP").defaultValue(8.0).range(1.0, 36.0).build()
    );

    public WurstAutoLeave() {
        super("AutoLeave", "Disconnects automatically on low health.", Category.MISC);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.getNetworkHandler() == null) return;

        double hp = mc.player.getHealth() + mc.player.getAbsorptionAmount();
        if (hp <= health.get()) {
            mc.getNetworkHandler().getConnection().disconnect(Text.literal("AutoLeave: low health"));
            disable();
        }
    }
}
