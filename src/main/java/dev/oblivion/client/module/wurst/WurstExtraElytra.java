package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;

public final class WurstExtraElytra extends Module {
    private final DoubleSetting boost = settings.getDefaultGroup().add(
        new DoubleSetting.Builder().name("Boost").description("Forward boost while gliding").defaultValue(0.08).range(0.0, 0.5).build()
    );

    public WurstExtraElytra() {
        super("ExtraElytra", "Adds speed control while fall-flying.", Category.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null) return;
        if (!mc.player.isGliding()) return;

        var look = mc.player.getRotationVec(1.0f);
        mc.player.setVelocity(
            mc.player.getVelocity().x + look.x * boost.get(),
            mc.player.getVelocity().y + look.y * boost.get() * 0.2,
            mc.player.getVelocity().z + look.z * boost.get()
        );
    }
}
