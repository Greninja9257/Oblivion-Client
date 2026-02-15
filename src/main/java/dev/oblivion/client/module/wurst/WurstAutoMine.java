package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.IntSetting;
import net.minecraft.util.hit.BlockHitResult;

public final class WurstAutoMine extends Module {
    private final IntSetting delay = settings.getDefaultGroup().add(
        new IntSetting.Builder().name("Delay").description("Ticks between mine actions").defaultValue(1).range(0, 20).build()
    );

    private int cooldown;

    public WurstAutoMine() {
        super("AutoMine", "Automatically mines the looked-at block.", Category.WORLD);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;
        if (cooldown > 0) {
            cooldown--;
            return;
        }

        if (mc.crosshairTarget instanceof BlockHitResult hit) {
            if (!mc.world.isAir(hit.getBlockPos())) {
                mc.interactionManager.attackBlock(hit.getBlockPos(), hit.getSide());
                mc.player.swingHand(net.minecraft.util.Hand.MAIN_HAND);
                cooldown = delay.get();
            }
        }
    }
}
