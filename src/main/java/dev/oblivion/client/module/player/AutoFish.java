package dev.oblivion.client.module.player;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.IntSetting;
import net.minecraft.item.FishingRodItem;
import net.minecraft.util.Hand;

public class AutoFish extends Module {

    private final IntSetting castDelay = settings.getDefaultGroup().add(
        new IntSetting.Builder()
            .name("Cast Delay")
            .description("Ticks to wait before recasting.")
            .defaultValue(15)
            .range(5, 100)
            .build()
    );

    private boolean wasFishing = false;
    private int waitTicks = 0;

    public AutoFish() {
        super("AutoFish", "Automatically reels in and recasts your fishing rod.", Category.PLAYER);
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.interactionManager == null) return;
        if (!(mc.player.getMainHandStack().getItem() instanceof FishingRodItem)) return;

        if (waitTicks > 0) {
            waitTicks--;
            if (waitTicks == 0) {
                // Recast
                mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            }
            return;
        }

        if (mc.player.fishHook != null) {
            if (mc.player.fishHook.isInOpenWater() || mc.player.fishHook.getVelocity().y < -0.01) {
                // Check if we caught something
                boolean caught = mc.player.fishHook.getHookedEntity() != null
                    || mc.player.fishHook.getVelocity().y < -0.04;
                if (caught) {
                    // Reel in
                    mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                    waitTicks = castDelay.get();
                }
            }
            wasFishing = true;
        } else if (wasFishing) {
            wasFishing = false;
            waitTicks = castDelay.get();
        }
    }
}
