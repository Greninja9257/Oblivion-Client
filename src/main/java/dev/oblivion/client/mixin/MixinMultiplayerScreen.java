package dev.oblivion.client.mixin;

import dev.oblivion.client.gui.screen.AltsScreen;
import dev.oblivion.client.gui.screen.ProxiesScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public abstract class MixinMultiplayerScreen extends Screen {

    protected MixinMultiplayerScreen(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Proxies"), button -> {
            this.client.setScreen(new ProxiesScreen((Screen) (Object) this));
        }).dimensions(this.width - 108, 6, 52, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Alts"), button -> {
            this.client.setScreen(new AltsScreen((Screen) (Object) this));
        }).dimensions(this.width - 54, 6, 48, 20).build());
    }
}
