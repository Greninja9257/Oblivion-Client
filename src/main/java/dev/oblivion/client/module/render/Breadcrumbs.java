package dev.oblivion.client.module.render;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.RenderEvent;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.IntSetting;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class Breadcrumbs extends Module {

    private final IntSetting maxPoints = settings.getDefaultGroup().add(
        new IntSetting.Builder()
            .name("Max Points")
            .description("Maximum trail points to keep.")
            .defaultValue(500)
            .range(50, 5000)
            .build()
    );

    private final List<Vec3d> trail = new ArrayList<>();

    public Breadcrumbs() {
        super("Breadcrumbs", "Leaves a trail behind you showing your path.", Category.RENDER);
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player == null) return;

        Vec3d pos = mc.player.getPos();
        if (trail.isEmpty() || trail.get(trail.size() - 1).squaredDistanceTo(pos) > 0.25) {
            trail.add(pos);
            while (trail.size() > maxPoints.get()) {
                trail.remove(0);
            }
        }
    }

    public List<Vec3d> getTrail() {
        return trail;
    }

    @Override
    protected void onDisable() {
        trail.clear();
    }
}
