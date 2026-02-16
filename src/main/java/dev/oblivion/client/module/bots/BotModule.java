package dev.oblivion.client.module.bots;

import com.google.gson.JsonObject;
import dev.oblivion.client.OblivionClient;
import dev.oblivion.client.bot.BotBridgeManager;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.IntSetting;
import dev.oblivion.client.setting.impl.StringSetting;
import dev.oblivion.client.util.ChatUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public abstract class BotModule extends Module {

    private static Boolean nodeAvailable = null;

    private static boolean isNodeInstalled() {
        if (nodeAvailable != null) return nodeAvailable;
        try {
            ProcessBuilder pb = new ProcessBuilder("node", "--version");
            pb.redirectErrorStream(true);
            Process process = pb.start();
            try (BufferedReader ignored = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                // consume stream
            }
            if (!process.waitFor(4, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                nodeAvailable = false;
                return false;
            }
            nodeAvailable = process.exitValue() == 0;
        } catch (Exception e) {
            nodeAvailable = false;
        }
        return nodeAvailable;
    }

    protected final StringSetting bridgeEndpoint = settings.getDefaultGroup().add(
        new StringSetting.Builder()
            .name("Bridge Endpoint")
            .description("Mineflayer bridge HTTP endpoint.")
            .defaultValue(BotBridgeManager.DEFAULT_ENDPOINT)
            .build()
    );

    protected final StringSetting apiToken = settings.getDefaultGroup().add(
        new StringSetting.Builder()
            .name("API Token")
            .description("Optional bearer token for the bot bridge.")
            .defaultValue("")
            .build()
    );

    protected final IntSetting botAmount = settings.getDefaultGroup().add(
        new IntSetting.Builder()
            .name("Bot Amount")
            .description("How many bots this module command targets.")
            .defaultValue(1)
            .range(1, 200)
            .build()
    );

    protected BotModule(String name, String description) {
        super(name, description, Category.BOTS);
    }

    protected JsonObject createBasePayload(String action) {
        JsonObject payload = new JsonObject();
        payload.addProperty("action", action);
        payload.addProperty("count", botAmount.get());
        return payload;
    }

    protected void sendAndReport(JsonObject payload) {
        if (!isNodeInstalled()) {
            ChatUtil.error("Node.js is not installed! Bots require Node.js to function.");
            ChatUtil.warning("Install Node.js from https://nodejs.org/ and restart Minecraft.");
            return;
        }
        boolean ok = OblivionClient.get().botBridgeManager.sendCommand(bridgeEndpoint.get(), apiToken.get(), payload);
        if (ok) {
            ChatUtil.success(name + " command sent.");
        } else {
            ChatUtil.error(name + " command failed. Check bot bridge endpoint/API token.");
        }
    }
}
