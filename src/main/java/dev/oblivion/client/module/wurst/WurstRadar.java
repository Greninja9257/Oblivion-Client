package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.RenderEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.IntSetting;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class WurstRadar extends Module {
    private final IntSetting maxEntries = settings.getDefaultGroup().add(
        new IntSetting.Builder().name("Max Entries").description("Maximum players shown").defaultValue(8).range(1, 30).build()
    );

    public WurstRadar() {
        super("Radar", "Displays nearby players and distance on HUD.", Category.RENDER);
    }

    @EventHandler
    public void onHud(RenderEvent.Hud event) {
        if (mc.player == null || mc.world == null) return;

        List<PlayerEntity> players = new ArrayList<>();
        for (PlayerEntity p : mc.world.getPlayers()) {
            if (p != mc.player) players.add(p);
        }

        players.sort(Comparator.comparingDouble(p -> mc.player.squaredDistanceTo(p)));

        int x = 8;
        int y = 24;
        event.getContext().drawText(mc.textRenderer, "Radar", x, y, 0xFF00D1FF, true);
        y += 12;

        int shown = 0;
        for (PlayerEntity p : players) {
            if (shown >= maxEntries.get()) break;
            double dist = mc.player.distanceTo(p);
            String line = p.getName().getString() + " " + String.format("%.1fm", dist);
            event.getContext().drawText(mc.textRenderer, line, x, y, 0xFFFFFFFF, true);
            y += 10;
            shown++;
        }
    }
}
