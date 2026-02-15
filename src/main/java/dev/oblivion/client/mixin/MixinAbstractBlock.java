package dev.oblivion.client.mixin;

import dev.oblivion.client.OblivionClient;
import dev.oblivion.client.module.render.Xray;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public class MixinAbstractBlock {

    @Inject(method = "isOpaqueFullCube", at = @At("HEAD"), cancellable = true)
    private void onIsOpaque(CallbackInfoReturnable<Boolean> cir) {
        Xray xray = OblivionClient.get().moduleManager.get(Xray.class);
        if (xray != null && xray.isEnabled()) {
            BlockState state = (BlockState) (Object) this;
            if (!xray.isVisibleBlock(state.getBlock())) {
                cir.setReturnValue(false);
            }
        }
    }
}
