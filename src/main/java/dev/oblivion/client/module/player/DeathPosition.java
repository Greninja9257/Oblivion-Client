package dev.oblivion.client.module.player;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.util.ChatUtil;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class DeathPosition extends Module {

    private final List<Vec3d> deathPositions = new ArrayList<>();
    private boolean wasDead = false;

    public DeathPosition() {
        super("DeathPosition", "Remembers and displays where you died.", Category.PLAYER);
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player == null) return;

        if (mc.player.isDead() && !wasDead) {
            Vec3d pos = mc.player.getPos();
            deathPositions.add(pos);
            ChatUtil.info(String.format("Death at: %.0f, %.0f, %.0f", pos.x, pos.y, pos.z));
        }
        wasDead = mc.player.isDead();
    }

    public List<Vec3d> getDeathPositions() { return deathPositions; }

    @Override
    protected void onDisable() {
        deathPositions.clear();
        wasDead = false;
    }
}
