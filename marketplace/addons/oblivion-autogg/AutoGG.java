import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.BoolSetting;
import dev.oblivion.client.setting.impl.IntSetting;
import dev.oblivion.client.setting.impl.StringSetting;
import net.minecraft.entity.player.PlayerEntity;

import java.util.HashSet;
import java.util.Set;

public class AutoGG extends Module {

    private final StringSetting message = settings.getDefaultGroup().add(
        new StringSetting.Builder()
            .name("Message")
            .description("Message to send after a kill.")
            .defaultValue("gg")
            .build()
    );

    private final IntSetting delayTicks = settings.getDefaultGroup().add(
        new IntSetting.Builder()
            .name("Delay")
            .description("Ticks to wait before sending (20 = 1 second).")
            .defaultValue(10)
            .range(0, 100)
            .build()
    );

    private final BoolSetting onlyPlayers = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Players Only")
            .description("Only trigger on player kills, not mobs.")
            .defaultValue(true)
            .build()
    );

    private final Set<Integer> trackedEntities = new HashSet<>();
    private int sendTimer = -1;

    public AutoGG() {
        super("AutoGG", "Sends 'gg' in chat after you kill a player.", Category.MISC);
    }

    @Override
    protected void onEnable() {
        trackedEntities.clear();
        sendTimer = -1;
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null) return;

        // Check if we have a pending message to send
        if (sendTimer >= 0) {
            sendTimer--;
            if (sendTimer < 0) {
                mc.player.networkHandler.sendChatMessage(message.get());
            }
            return;
        }

        // Track nearby players and detect when they die
        for (var entity : mc.world.getEntities()) {
            if (onlyPlayers.get() && !(entity instanceof PlayerEntity)) continue;
            if (entity == mc.player) continue;
            if (!entity.isAlive()) continue;

            if (mc.player.distanceTo(entity) <= 6.0) {
                trackedEntities.add(entity.getId());
            }
        }

        // Check if any tracked entity died (no longer alive or removed)
        var iterator = trackedEntities.iterator();
        while (iterator.hasNext()) {
            int id = iterator.next();
            var entity = mc.world.getEntityById(id);
            if (entity == null || !entity.isAlive()) {
                iterator.remove();
                // Entity died near us — likely our kill
                if (entity == null || mc.player.distanceTo(entity) <= 8.0) {
                    sendTimer = delayTicks.get();
                    break;
                }
            }
        }
    }
}
