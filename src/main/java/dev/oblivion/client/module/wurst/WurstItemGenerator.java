package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.StringSetting;
import net.minecraft.text.Text;

public final class WurstItemGenerator extends Module {
    private final StringSetting targetPlayer = settings.getDefaultGroup().add(
        new StringSetting.Builder()
            .name("Target Player")
            .description("Player name to receive the item (required).")
            .defaultValue("")
            .build()
    );
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

        String target = targetPlayer.get().trim();
        String self = mc.player.getName().getString();
        if (target.isEmpty()) {
            mc.player.sendMessage(Text.literal("\u00a7c[ItemGenerator] Set 'Target Player' first."), false);
            ran = true;
            disable();
            return;
        }
        if (target.equalsIgnoreCase(self)) {
            mc.player.sendMessage(Text.literal("\u00a7c[ItemGenerator] Target must be another player (not yourself)."), false);
            ran = true;
            disable();
            return;
        }

        mc.player.networkHandler.sendChatCommand("give " + target + " " + itemId.get() + " 64");
        ran = true;
        disable();
    }
}
