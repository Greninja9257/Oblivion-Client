package dev.oblivion.client.bot;

import org.geysermc.mcprotocollib.network.Session;
import org.geysermc.mcprotocollib.network.event.session.SessionAdapter;
import org.geysermc.mcprotocollib.network.packet.Packet;
import org.geysermc.mcprotocollib.protocol.data.game.inventory.ContainerType;
import org.geysermc.mcprotocollib.protocol.data.game.item.ItemStack;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.inventory.ClientboundContainerClosePacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.inventory.ClientboundContainerSetContentPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.inventory.ClientboundContainerSetSlotPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.inventory.ClientboundOpenScreenPacket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BotInventoryTracker extends SessionAdapter {

    private volatile int openContainerId = 0;
    private volatile ContainerType openContainerType = null;
    private volatile int openContainerStateId = 0;
    private final Map<Integer, ItemStack> openContainerSlots = new ConcurrentHashMap<>();

    private volatile int playerInventoryStateId = 0;
    private final Map<Integer, ItemStack> playerInventorySlots = new ConcurrentHashMap<>();

    public boolean isContainerOpen() {
        return openContainerId != 0;
    }

    public int getOpenContainerId() {
        return openContainerId;
    }

    public ContainerType getOpenContainerType() {
        return openContainerType;
    }

    public int getOpenContainerStateId() {
        return openContainerStateId;
    }

    public Map<Integer, ItemStack> getOpenContainerSlots() {
        return openContainerSlots;
    }

    public int getOpenContainerSize() {
        if (openContainerType == null) return 0;
        return switch (openContainerType) {
            case GENERIC_9X1 -> 9;
            case GENERIC_9X2 -> 18;
            case GENERIC_9X3 -> 27;
            case GENERIC_9X4 -> 36;
            case GENERIC_9X5 -> 45;
            case GENERIC_9X6 -> 54;
            case GENERIC_3X3 -> 9;
            case SHULKER_BOX -> 27;
            case HOPPER -> 5;
            case FURNACE, BLAST_FURNACE, SMOKER -> 3;
            case BREWING_STAND -> 5;
            default -> 0;
        };
    }

    public int getPlayerInventoryStateId() {
        return playerInventoryStateId;
    }

    public Map<Integer, ItemStack> getPlayerInventorySlots() {
        return playerInventorySlots;
    }

    public void clear() {
        openContainerId = 0;
        openContainerType = null;
        openContainerStateId = 0;
        openContainerSlots.clear();
        playerInventoryStateId = 0;
        playerInventorySlots.clear();
    }

    @Override
    public void packetReceived(Session session, Packet packet) {
        if (packet instanceof ClientboundOpenScreenPacket open) {
            openContainerId = open.getContainerId();
            openContainerType = open.getType();
            openContainerSlots.clear();

        } else if (packet instanceof ClientboundContainerClosePacket close) {
            if (close.getContainerId() == openContainerId) {
                openContainerId = 0;
                openContainerType = null;
                openContainerStateId = 0;
                openContainerSlots.clear();
            }

        } else if (packet instanceof ClientboundContainerSetContentPacket content) {
            if (content.getContainerId() == 0) {
                playerInventoryStateId = content.getStateId();
                playerInventorySlots.clear();
                ItemStack[] items = content.getItems();
                for (int i = 0; i < items.length; i++) {
                    if (items[i] != null) playerInventorySlots.put(i, items[i]);
                }
            } else if (content.getContainerId() == openContainerId) {
                openContainerStateId = content.getStateId();
                openContainerSlots.clear();
                ItemStack[] items = content.getItems();
                for (int i = 0; i < items.length; i++) {
                    if (items[i] != null) openContainerSlots.put(i, items[i]);
                }
            }

        } else if (packet instanceof ClientboundContainerSetSlotPacket slot) {
            if (slot.getContainerId() == 0) {
                playerInventoryStateId = slot.getStateId();
                if (slot.getItem() == null) playerInventorySlots.remove(slot.getSlot());
                else playerInventorySlots.put(slot.getSlot(), slot.getItem());
            } else if (slot.getContainerId() == openContainerId) {
                openContainerStateId = slot.getStateId();
                if (slot.getItem() == null) openContainerSlots.remove(slot.getSlot());
                else openContainerSlots.put(slot.getSlot(), slot.getItem());
            }
        }
    }
}
