package dev.oblivion.client.mixin;

import dev.oblivion.client.OblivionClient;
import dev.oblivion.client.proxy.ProxyManager;
import io.netty.channel.Channel;
import io.netty.handler.proxy.HttpProxyHandler;
import io.netty.handler.proxy.ProxyHandler;
import io.netty.handler.proxy.Socks5ProxyHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.InetSocketAddress;

@Mixin(targets = "net.minecraft.network.ClientConnection$1")
public class MixinClientConnectionInitializer {

    @Inject(method = "initChannel", at = @At("HEAD"))
    private void onInitChannel(Channel channel, CallbackInfo ci) {
        try {
            ProxyManager.ProxyEntry proxy = OblivionClient.get().proxyManager.getActive();
            if (proxy == null) return;

            InetSocketAddress proxyAddress = new InetSocketAddress(proxy.host, proxy.port);
            ProxyHandler handler;

            if (proxy.type == ProxyManager.ProxyType.SOCKS5) {
                if (!proxy.username.isBlank()) handler = new Socks5ProxyHandler(proxyAddress, proxy.username, proxy.password);
                else handler = new Socks5ProxyHandler(proxyAddress);
            } else {
                if (!proxy.username.isBlank()) handler = new HttpProxyHandler(proxyAddress, proxy.username, proxy.password);
                else handler = new HttpProxyHandler(proxyAddress);
            }

            channel.pipeline().addFirst("oblivion_proxy", handler);
        } catch (Throwable t) {
            OblivionClient.LOGGER.error("Failed to inject proxy handler", t);
        }
    }
}
