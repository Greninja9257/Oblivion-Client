package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.StringSetting;

public final class WurstItemGenerator extends Module {
    private final StringSetting itemId = settings.getDefaultGroup().add(
        new StringSetting.Builder().name("Item ID").description("Item id for /give command").defaultValue("minecraft:stone").build()
    );

    private boolean ran;

    public WurstItemGenerator() {
        super("ItemGenerator", "Sends a /give command once when enabled.", Category.MISC);
    }

    @Override
    protected void onEnable() {
        ran = false;
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || ran) return;
        mc.player.networkHandler.sendChatCommand("give " + mc.player.getName().getString() + " " + itemId.get() + " 64");
        ran = true;
        disable();
    }
}
