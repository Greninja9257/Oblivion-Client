package dev.oblivion.client.module.misc;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;
import net.minecraft.text.Text;

public class AutoDisconnect extends Module {
    private final DoubleSetting health = settings.getDefaultGroup().add(
        new DoubleSetting.Builder().name("Health").description("Disconnect below this health.").defaultValue(5.0).min(1.0).max(19.0).build()
    );

    public AutoDisconnect() {
        super("AutoDisconnect", "Disconnect when health gets low.", Category.MISC);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null) return;
        if (mc.player.getHealth() <= health.get()) {
            if (mc.world != null) mc.world.disconnect();
            if (mc.getNetworkHandler() != null) mc.getNetworkHandler().getConnection().disconnect(Text.literal("AutoDisconnect"));
        }
    }
}
