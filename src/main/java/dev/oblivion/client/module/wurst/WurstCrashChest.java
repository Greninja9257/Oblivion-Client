package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.IntSetting;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.screen.slot.SlotActionType;

public final class WurstCrashChest extends Module {
    private final IntSetting clicksPerTick = settings.getDefaultGroup().add(
        new IntSetting.Builder().name("Clicks/Tick").description("Packet flood size").defaultValue(20).range(1, 200).build()
    );

    public WurstCrashChest() {
        super("CrashChest", "Rapidly clicks chest slots (stress-testing).", Category.MISC);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.interactionManager == null) return;
        if (!(mc.currentScreen instanceof GenericContainerScreen)) return;

        var handler = mc.player.currentScreenHandler;
        int slots = Math.max(1, handler.slots.size() - 36);

        for (int i = 0; i < clicksPerTick.get(); i++) {
            int slot = i % slots;
            mc.interactionManager.clickSlot(handler.syncId, slot, 0, SlotActionType.QUICK_MOVE, mc.player);
        }
    }
}
