package dev.oblivion.client.mixin;

import dev.oblivion.client.OblivionClient;
import dev.oblivion.client.module.player.FastBreak;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {

    @Shadow
    private int blockBreakingCooldown;

    @Inject(method = "updateBlockBreakingProgress", at = @At("HEAD"))
    private void onUpdateBreaking(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        FastBreak fastBreak = OblivionClient.get().moduleManager.get(FastBreak.class);
        if (fastBreak != null && fastBreak.isEnabled()) {
            blockBreakingCooldown = 0;
        }
    }
}
