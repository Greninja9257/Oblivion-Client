package dev.oblivion.client.mixin;

import dev.oblivion.client.OblivionClient;
import dev.oblivion.client.event.events.KeyEvent;
import dev.oblivion.client.gui.screen.ClickGuiScreen;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class MixinKeyboard {

    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    private void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (action == GLFW.GLFW_PRESS &&
            key == GLFW.GLFW_KEY_RIGHT_SHIFT &&
            mc.currentScreen == null) {
            mc.setScreen(new ClickGuiScreen());
            ci.cancel();
            return;
        }

        if (mc.currentScreen != null) return;
        if (action == GLFW.GLFW_PRESS) {
            KeyEvent event = OblivionClient.get().eventBus.post(new KeyEvent(key, action));
            if (!event.isCancelled()) {
                OblivionClient.get().moduleManager.onKeyPress(key);
            }
        }
    }
}
