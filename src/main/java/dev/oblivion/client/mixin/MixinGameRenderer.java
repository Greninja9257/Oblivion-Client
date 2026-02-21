package dev.oblivion.client.mixin;

import dev.oblivion.client.OblivionClient;
import dev.oblivion.client.module.render.NoRender;
import dev.oblivion.client.module.render.Zoom;
import dev.oblivion.client.module.wurst.WurstNoHurtcam;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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

    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void onGetFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Float> cir) {
        Zoom zoom = OblivionClient.get().moduleManager.get(Zoom.class);
        if (zoom != null && zoom.isEnabled()) {
            float fov = cir.getReturnValue();
            double zoomFactor = zoom.getFactor();
            if (zoomFactor > 0) {
                cir.setReturnValue((float) (fov / zoomFactor));
            }
        }
    }
}
