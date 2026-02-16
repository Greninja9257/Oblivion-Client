package dev.oblivion.client.module.misc;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.BoolSetting;
import dev.oblivion.client.setting.impl.IntSetting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class AutoClicker extends Module {

    private final IntSetting cps = settings.getDefaultGroup().add(
        new IntSetting.Builder()
            .name("CPS")
            .description("Clicks per second.")
            .defaultValue(10)
            .range(1, 20)
            .build()
    );

    private final BoolSetting rightClick = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Right Click")
            .description("Also auto right click.")
            .defaultValue(false)
            .build()
    );

    private long lastClickTime = 0;

    public AutoClicker() {
        super("AutoClicker", "Automatically clicks at a configurable rate.", Category.MISC);
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.interactionManager == null) return;

        long now = System.currentTimeMillis();
        long interval = 1000L / cps.get();

        if (now - lastClickTime < interval) return;
        lastClickTime = now;

        if (mc.options.attackKey.isPressed()) {
            if (mc.crosshairTarget instanceof EntityHitResult entityHit) {
                mc.interactionManager.attackEntity(mc.player, entityHit.getEntity());
            }
            mc.player.swingHand(Hand.MAIN_HAND);
        }

        if (rightClick.get() && mc.options.useKey.isPressed()) {
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
        }
    }
}
