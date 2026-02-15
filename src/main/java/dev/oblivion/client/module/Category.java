package dev.oblivion.client.module;

public enum Category {
    COMBAT("Combat"),
    MOVEMENT("Movement"),
    RENDER("Render"),
    PLAYER("Player"),
    WORLD("World"),
    MISC("Misc"),
    BOTS("Bots");

    public final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }
}
