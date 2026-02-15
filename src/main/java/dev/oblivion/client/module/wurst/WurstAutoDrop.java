package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.BoolSetting;
import dev.oblivion.client.setting.impl.IntSetting;
import net.minecraft.item.ItemStack;

public final class WurstAutoDrop extends Module {
    private final IntSetting interval = settings.getDefaultGroup().add(
        new IntSetting.Builder().name("Interval").description("Ticks between drops").defaultValue(8).range(1, 100).build()
    );

    private final BoolSetting dropStack = settings.getDefaultGroup().add(
        new BoolSetting.Builder().name("Drop Stack").description("Drop full stack instead of single item").defaultValue(false).build()
    );

    private int ticks;

    public WurstAutoDrop() {
        super("AutoDrop", "Automatically drops selected hotbar item.", Category.PLAYER);
    }

    @Override
    protected void onEnable() {
        ticks = 0;
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null) return;

        ticks++;
        if (ticks < interval.get()) return;
        ticks = 0;

        ItemStack selected = mc.player.getMainHandStack();
        if (selected.isEmpty()) return;

        mc.player.dropSelectedItem(dropStack.get());
    }
}
