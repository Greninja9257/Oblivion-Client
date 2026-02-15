package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.util.ChatUtil;

public final class WurstTemplateTool extends Module {
    private net.minecraft.util.math.BlockPos first;
    private net.minecraft.util.math.BlockPos second;

    public WurstTemplateTool() {
        super("TemplateTool", "Captures two template positions via left/right click state.", Category.WORLD);
    }

    @Override
    protected void onEnable() {
        first = null;
        second = null;
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.crosshairTarget == null) return;
        if (!(mc.crosshairTarget instanceof net.minecraft.util.hit.BlockHitResult hit)) return;

        if (mc.options.attackKey.isPressed() && first == null) {
            first = hit.getBlockPos().toImmutable();
            ChatUtil.info("Template point A set: " + first.toShortString());
        }

        if (mc.options.useKey.isPressed() && second == null) {
            second = hit.getBlockPos().toImmutable();
            ChatUtil.info("Template point B set: " + second.toShortString());
        }

        if (first != null && second != null) {
            ChatUtil.success("Template captured.");
            disable();
        }
    }
}
