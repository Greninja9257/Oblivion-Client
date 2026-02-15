package dev.oblivion.client.mixin;

import dev.oblivion.client.OblivionClient;
import dev.oblivion.client.event.events.MovementEvent;
import dev.oblivion.client.module.render.Freecam;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class MixinClientPlayerEntity {

    @Inject(method = "sendMovementPackets", at = @At("HEAD"), cancellable = true)
    private void onSendMovementPre(CallbackInfo ci) {
        MovementEvent.Pre event = new MovementEvent.Pre();
        OblivionClient.get().eventBus.post(event);
        if (event.isCancelled()) {
            ci.cancel();
            return;
        }

        Freecam freecam = OblivionClient.get().moduleManager.get(Freecam.class);
        if (freecam != null && freecam.isEnabled()) {
            ci.cancel();
        }
    }

    @Inject(method = "sendMovementPackets", at = @At("TAIL"))
    private void onSendMovementPost(CallbackInfo ci) {
        OblivionClient.get().eventBus.post(new MovementEvent.Post());
    }

}
