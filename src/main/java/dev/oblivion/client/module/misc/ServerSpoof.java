package dev.oblivion.client.module.misc;

import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.StringSetting;
import dev.oblivion.client.setting.impl.BoolSetting;

public class ServerSpoof extends Module {

    private final BoolSetting spoofBrand = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Spoof Brand")
            .description("Spoof the client brand sent to the server.")
            .defaultValue(true)
            .build()
    );

    private final StringSetting brand = settings.getDefaultGroup().add(
        new StringSetting.Builder()
            .name("Brand")
            .description("Client brand to send.")
            .defaultValue("vanilla")
            .visible(spoofBrand::get)
            .build()
    );

    public ServerSpoof() {
        super("ServerSpoof", "Spoofs client information sent to the server.", Category.MISC);
    }

    public boolean shouldSpoofBrand() { return spoofBrand.get(); }
    public String getBrand() { return brand.get(); }
}
