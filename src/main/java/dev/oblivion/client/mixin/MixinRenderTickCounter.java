package dev.oblivion.client.mixin;

import dev.oblivion.client.OblivionClient;
import dev.oblivion.client.module.world.Timer;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderTickCounter.Dynamic.class)
public class MixinRenderTickCounter {

    @Inject(method = "getTickDelta", at = @At("RETURN"), cancellable = true)
    private void onGetTickDelta(boolean bl, CallbackInfoReturnable<Float> cir) {
        Timer timer = OblivionClient.get().moduleManager.get(Timer.class);
        if (timer != null && timer.isEnabled()) {
            cir.setReturnValue(cir.getReturnValue() * timer.getMultiplier());
        }
    }
}
