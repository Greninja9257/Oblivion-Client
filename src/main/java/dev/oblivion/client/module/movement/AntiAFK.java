package dev.oblivion.client.module.movement;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.BoolSetting;
import dev.oblivion.client.setting.impl.IntSetting;

public class AntiAFK extends Module {

    private final BoolSetting spin = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Spin")
            .description("Spin the player.")
            .defaultValue(true)
            .build()
    );

    private final BoolSetting jump = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Jump")
            .description("Periodically jump.")
            .defaultValue(true)
            .build()
    );

    private final BoolSetting swing = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Swing")
            .description("Swing hand periodically.")
            .defaultValue(false)
            .build()
    );

    private final IntSetting interval = settings.getDefaultGroup().add(
        new IntSetting.Builder()
            .name("Interval")
            .description("Ticks between actions.")
            .defaultValue(40)
            .range(10, 200)
            .build()
    );

    private int ticks = 0;

    public AntiAFK() {
        super("AntiAFK", "Prevents you from being kicked for being AFK.", Category.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player == null) return;

        ticks++;

        if (spin.get()) {
            mc.player.setYaw(mc.player.getYaw() + 1.0f);
        }

        if (ticks % interval.get() == 0) {
            if (jump.get() && mc.player.isOnGround()) {
                mc.player.jump();
            }
            if (swing.get()) {
                mc.player.swingHand(net.minecraft.util.Hand.MAIN_HAND);
            }
        }
    }

    @Override
    protected void onDisable() {
        ticks = 0;
    }
}
