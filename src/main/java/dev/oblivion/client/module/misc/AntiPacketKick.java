package dev.oblivion.client.module.misc;

import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;

public class AntiPacketKick extends Module {

    public AntiPacketKick() {
        super("AntiPacketKick", "Prevents being kicked for sending too many packets.", Category.MISC);
    }

    // This module works through integration with packet sending code.
    // When enabled, outgoing packets are rate-limited to avoid server kicks.
}
