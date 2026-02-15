package dev.oblivion.client.mixin;

import dev.oblivion.client.OblivionClient;
import dev.oblivion.client.module.render.Fullbright;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightmapTextureManager.class)
public class MixinLightmapTextureManager {

    @Inject(method = "update", at = @At("HEAD"), cancellable = true)
    private void onUpdate(float delta, CallbackInfo ci) {
        Fullbright fullbright = OblivionClient.get().moduleManager.get(Fullbright.class);
        if (fullbright != null && fullbright.isEnabled()) {
            // Fullbright handles gamma in its own tick handler
        }
    }
}
