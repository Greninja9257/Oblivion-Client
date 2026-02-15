package dev.oblivion.client.mixin;

import dev.oblivion.client.OblivionClient;
import dev.oblivion.client.module.movement.Step;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class MixinEntity {

    @Inject(method = "getStepHeight", at = @At("RETURN"), cancellable = true)
    private void onGetStepHeight(CallbackInfoReturnable<Float> cir) {
        Entity self = (Entity) (Object) this;
        if (self != MinecraftClient.getInstance().player) return;

        Step step = OblivionClient.get().moduleManager.get(Step.class);
        if (step != null && step.isEnabled()) {
            cir.setReturnValue(step.getStepHeight());
        }
    }
}
