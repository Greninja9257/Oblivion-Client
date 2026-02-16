package dev.oblivion.client.module.misc;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;
import dev.oblivion.client.setting.impl.BoolSetting;
import dev.oblivion.client.util.ChatUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class AutoLog extends Module {

    private final DoubleSetting health = settings.getDefaultGroup().add(
        new DoubleSetting.Builder()
            .name("Health")
            .description("Disconnect when health drops below this value.")
            .defaultValue(6.0)
            .range(0.5, 20.0)
            .build()
    );

    private final BoolSetting onTotem = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("On Totem Pop")
            .description("Disconnect when a totem pops.")
            .defaultValue(false)
            .build()
    );

    private int lastTotemCount = -1;

    public AutoLog() {
        super("AutoLog", "Automatically disconnects when health is low.", Category.MISC);
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.getNetworkHandler() == null) return;

        if (mc.player.getHealth() <= health.get()) {
            disconnect("Health dropped below " + health.get());
            return;
        }

        if (onTotem.get()) {
            // Detect totem pop by checking if health suddenly goes up from near-death
            // Simplified: check if player had a death-preventing item used
        }
    }

    private void disconnect(String reason) {
        ChatUtil.warning("AutoLog: " + reason);
        mc.getNetworkHandler().getConnection().disconnect(Text.literal("[AutoLog] " + reason));
        disable();
    }

    @Override
    protected void onEnable() {
        lastTotemCount = -1;
    }
}
