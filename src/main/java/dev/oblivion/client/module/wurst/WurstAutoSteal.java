package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.IntSetting;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.screen.slot.SlotActionType;

public final class WurstAutoSteal extends Module {
    private final IntSetting delay = settings.getDefaultGroup().add(
        new IntSetting.Builder().name("Delay").description("Ticks between steals").defaultValue(2).range(0, 20).build()
    );

    private int ticks;

    public WurstAutoSteal() {
        super("AutoSteal", "Steals items from opened containers.", Category.PLAYER);
    }

    @Override
    protected void onEnable() {
        ticks = 0;
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.interactionManager == null) return;
        if (!(mc.currentScreen instanceof GenericContainerScreen)) return;

        ticks++;
        if (ticks < delay.get()) return;
        ticks = 0;

        var handler = mc.player.currentScreenHandler;
        int containerSlots = handler.slots.size() - 36;

        for (int i = 0; i < containerSlots; i++) {
            if (!handler.getSlot(i).getStack().isEmpty()) {
                mc.interactionManager.clickSlot(handler.syncId, i, 0, SlotActionType.QUICK_MOVE, mc.player);
                return;
            }
        }
    }
}
