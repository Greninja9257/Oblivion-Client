package dev.oblivion.client.mixin;

import dev.oblivion.client.OblivionClient;
import dev.oblivion.client.module.movement.Jesus;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FluidBlock.class)
public class MixinFluidBlock {

    @Inject(method = "getCollisionShape", at = @At("HEAD"), cancellable = true)
    private void onGetCollision(BlockState state, BlockView world, BlockPos pos,
                                ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        Jesus jesus = OblivionClient.get().moduleManager.get(Jesus.class);
        if (jesus != null && jesus.isEnabled() && jesus.isSolidMode()) {
            cir.setReturnValue(VoxelShapes.fullCube());
        }
    }
}
