package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;

public final class WurstCreativeFlight extends Module {
    private boolean oldAllowFlying;
    private boolean oldFlying;

    public WurstCreativeFlight() {
        super("CreativeFlight", "Enables creative-style flight.", Category.MOVEMENT);
    }

    @Override
    protected void onEnable() {
        if (mc.player == null) return;
        oldAllowFlying = mc.player.getAbilities().allowFlying;
        oldFlying = mc.player.getAbilities().flying;
        mc.player.getAbilities().allowFlying = true;
    }

    @Override
    protected void onDisable() {
        if (mc.player == null) return;
        mc.player.getAbilities().allowFlying = oldAllowFlying;
        mc.player.getAbilities().flying = oldFlying;
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null) return;
        mc.player.getAbilities().allowFlying = true;
    }
}
