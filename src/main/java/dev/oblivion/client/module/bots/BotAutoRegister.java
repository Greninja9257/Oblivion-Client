package dev.oblivion.client.module.bots;

import com.google.gson.JsonObject;
import dev.oblivion.client.setting.impl.BoolSetting;
import dev.oblivion.client.setting.impl.StringSetting;

public final class BotAutoRegister extends BotModule {
    private final StringSetting password = settings.getDefaultGroup().add(
        new StringSetting.Builder().name("Password").description("Password used for /register and /login.").defaultValue("oblivion123").build()
    );

    private final StringSetting registerFormat = settings.getDefaultGroup().add(
        new StringSetting.Builder().name("Register Format").description("Example: /register {p} {p}").defaultValue("/register {p} {p}").build()
    );

    private final StringSetting loginFormat = settings.getDefaultGroup().add(
        new StringSetting.Builder().name("Login Format").description("Example: /login {p}").defaultValue("/login {p}").build()
    );

    private final BoolSetting enabledMode = settings.getDefaultGroup().add(
        new BoolSetting.Builder().name("Enable Mode").description("True=start auto-register, false=stop it.").defaultValue(true).build()
    );

    public BotAutoRegister() {
        super("BotAutoRegister", "Starts/stops bot auto-register + auto-login logic.");
    }

    @Override
    protected void onEnable() {
        JsonObject payload = createBasePayload("auto_register");
        payload.addProperty("enabled", enabledMode.get());
        payload.addProperty("password", password.get());
        payload.addProperty("registerFormat", registerFormat.get());
        payload.addProperty("loginFormat", loginFormat.get());
        sendAndReport(payload);
        disable();
    }
}
