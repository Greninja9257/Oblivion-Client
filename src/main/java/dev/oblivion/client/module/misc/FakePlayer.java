package dev.oblivion.client.module.misc;

import com.mojang.authlib.GameProfile;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;

import java.util.UUID;

public class FakePlayer extends Module {
    private static final int FAKE_ID = -1337420;

    public FakePlayer() {
        super("FakePlayer", "Spawns a local-only fake player.", Category.MISC);
    }

    @Override
    protected void onEnable() {
        if (mc.player == null || mc.world == null) return;
        if (mc.world.getEntityById(FAKE_ID) != null) return;

        GameProfile profile = new GameProfile(UUID.randomUUID(), mc.player.getName().getString() + "_fake");
        OtherClientPlayerEntity fake = new OtherClientPlayerEntity(mc.world, profile);
        fake.copyPositionAndRotation(mc.player);
        fake.setHealth(mc.player.getHealth());
        fake.setVelocity(mc.player.getVelocity());
        fake.setPose(mc.player.getPose());
        fake.setYaw(mc.player.getYaw());
        fake.setPitch(mc.player.getPitch());
        fake.setHeadYaw(mc.player.getHeadYaw());
        fake.bodyYaw = mc.player.bodyYaw;
        fake.prevBodyYaw = mc.player.prevBodyYaw;

        fake.setId(FAKE_ID);
        mc.world.addEntity(fake);
    }

    @Override
    protected void onDisable() {
        if (mc.world == null) return;
        Entity existing = mc.world.getEntityById(FAKE_ID);
        if (existing != null) {
            mc.world.removeEntity(FAKE_ID, Entity.RemovalReason.DISCARDED);
        }
    }
}
