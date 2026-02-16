package dev.oblivion.client.module.movement;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.EnumSetting;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;

public class Sneak extends Module {

    public enum Mode { VANILLA, PACKET }

    private final EnumSetting<Mode> mode = settings.getDefaultGroup().add(
        new EnumSetting.Builder<Mode>()
            .name("Mode")
            .description("Sneak mode.")
            .defaultValue(Mode.VANILLA)
            .build()
    );

    public Sneak() {
        super("Sneak", "Automatically sneaks.", Category.MOVEMENT);
    }

    @Override
    protected void onEnable() {
        if (mc.player == null) return;
        if (mode.get() == Mode.PACKET) {
            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
        }
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player == null) return;
        if (mode.get() == Mode.VANILLA) {
            mc.options.sneakKey.setPressed(true);
        }
    }

    @Override
    protected void onDisable() {
        if (mc.player == null) return;
        if (mode.get() == Mode.VANILLA) {
            mc.options.sneakKey.setPressed(false);
        } else {
            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
        }
    }
}
