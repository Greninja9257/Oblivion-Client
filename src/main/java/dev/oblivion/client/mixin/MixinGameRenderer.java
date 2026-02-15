package dev.oblivion.client.mixin;

import dev.oblivion.client.OblivionClient;
import dev.oblivion.client.module.render.NoRender;
import dev.oblivion.client.module.wurst.WurstNoHurtcam;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    @Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
    private void onBobView(CallbackInfo ci) {
        NoRender noRender = OblivionClient.get().moduleManager.get(NoRender.class);
        if (noRender != null && noRender.isEnabled() && noRender.hurtCam()) {
            ci.cancel();
            return;
        }

        WurstNoHurtcam noHurtcam = OblivionClient.get().moduleManager.get(WurstNoHurtcam.class);
        if (noHurtcam != null && noHurtcam.isEnabled()) {
            ci.cancel();
        }
    }
}
