package dev.oblivion.client.mixin;

import dev.oblivion.client.OblivionClient;
import dev.oblivion.client.module.movement.Fly;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {

    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    private void onTravel(Vec3d movementInput, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self != MinecraftClient.getInstance().player) return;

        Fly fly = OblivionClient.get().moduleManager.get(Fly.class);
        if (fly != null && fly.isEnabled()) {
            fly.handleTravel(movementInput);
            ci.cancel();
        }
    }
}
