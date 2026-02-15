package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.PacketEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.BoolSetting;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;

import java.util.ArrayList;
import java.util.List;

public final class WurstBlink extends Module {
    private final BoolSetting cancelActions = settings.getDefaultGroup().add(
        new BoolSetting.Builder().name("Cancel Actions").description("Also queue interaction/action packets").defaultValue(true).build()
    );

    private final List<Packet<?>> queued = new ArrayList<>();

    public WurstBlink() {
        super("Blink", "Temporarily halts movement packets then flushes them.", Category.MOVEMENT);
    }

    @Override
    protected void onEnable() {
        queued.clear();
    }

    @Override
    protected void onDisable() {
        if (mc.getNetworkHandler() == null) return;
        for (Packet<?> packet : queued) {
            mc.getNetworkHandler().sendPacket(packet);
        }
        queued.clear();
    }

    @EventHandler
    public void onSend(PacketEvent.Send event) {
        Packet<?> packet = event.getPacket();

        boolean movement = packet instanceof PlayerMoveC2SPacket;
        boolean action = packet instanceof PlayerInteractBlockC2SPacket
            || packet instanceof PlayerInteractItemC2SPacket
            || packet instanceof PlayerInteractEntityC2SPacket
            || packet instanceof ClientCommandC2SPacket;

        if (movement || (cancelActions.get() && action)) {
            queued.add(packet);
            event.cancel();
        }
    }
}
