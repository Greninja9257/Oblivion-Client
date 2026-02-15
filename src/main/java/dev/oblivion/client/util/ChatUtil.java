package dev.oblivion.client.util;

import dev.oblivion.client.OblivionClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ChatUtil {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    private static MutableText prefix() {
        return Text.literal("")
            .append(Text.literal("[").formatted(Formatting.DARK_GRAY))
            .append(Text.literal(OblivionClient.NAME).formatted(Formatting.DARK_PURPLE))
            .append(Text.literal("] ").formatted(Formatting.DARK_GRAY));
    }

    public static void info(String message) {
        send(prefix().append(Text.literal(message).formatted(Formatting.GRAY)));
    }

    public static void success(String message) {
        send(prefix().append(Text.literal(message).formatted(Formatting.GREEN)));
    }

    public static void error(String message) {
        send(prefix().append(Text.literal(message).formatted(Formatting.RED)));
    }

    public static void warning(String message) {
        send(prefix().append(Text.literal(message).formatted(Formatting.YELLOW)));
    }

    public static void send(Text text) {
        if (mc.player != null) {
            mc.player.sendMessage(text, false);
        }
    }
}
