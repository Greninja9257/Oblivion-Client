package dev.oblivion.client.bot;

import org.geysermc.mcprotocollib.network.Session;
import org.geysermc.mcprotocollib.network.event.session.SessionAdapter;
import org.geysermc.mcprotocollib.network.packet.Packet;
import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.geysermc.mcprotocollib.protocol.data.game.entity.type.EntityType;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.ClientboundLoginPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.ClientboundPlayerInfoRemovePacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.ClientboundPlayerInfoUpdatePacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.ClientboundEntityPositionSyncPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.ClientboundMoveEntityPosPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.ClientboundMoveEntityPosRotPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.ClientboundRemoveEntitiesPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.ClientboundTeleportEntityPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.player.ClientboundPlayerPositionPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.player.ClientboundSetHealthPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.spawn.ClientboundAddEntityPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.level.ServerboundAcceptTeleportationPacket;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BotWorldTracker extends SessionAdapter {

    public record TrackedEntity(int entityId, UUID uuid, double x, double y, double z, EntityType type) {
        public TrackedEntity withPosition(double x, double y, double z) {
            return new TrackedEntity(entityId, uuid, x, y, z, type);
        }
    }

    public record TrackedPlayer(UUID uuid, String name) {}

    private volatile double x, y, z;
    private volatile float yaw, pitch;
    private volatile int entityId;
    private volatile float health = 20f;
    private volatile float food = 20f;

    private final Map<Integer, TrackedEntity> entities = new ConcurrentHashMap<>();
    private final Map<UUID, TrackedPlayer> players = new ConcurrentHashMap<>();

    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }
    public float getYaw() { return yaw; }
    public float getPitch() { return pitch; }
    public int getEntityId() { return entityId; }
    public float getHealth() { return health; }
    public float getFood() { return food; }
    public Map<Integer, TrackedEntity> getEntities() { return entities; }
    public Map<UUID, TrackedPlayer> getPlayers() { return players; }

    public TrackedPlayer getPlayerByName(String name) {
        for (TrackedPlayer p : players.values()) {
            if (p.name().equalsIgnoreCase(name)) return p;
        }
        return null;
    }

    public TrackedEntity findEntityByUuid(UUID uuid) {
        for (TrackedEntity e : entities.values()) {
            if (uuid.equals(e.uuid())) return e;
        }
        return null;
    }

    public void clear() {
        entities.clear();
        players.clear();
        health = 20f;
        food = 20f;
    }

    @Override
    public void packetReceived(Session session, Packet packet) {
        if (packet instanceof ClientboundLoginPacket login) {
            entityId = login.getEntityId();
            entities.clear();

        } else if (packet instanceof ClientboundPlayerPositionPacket pos) {
            var p = pos.getPosition();
            x = p.getX();
            y = p.getY();
            z = p.getZ();
            yaw = pos.getYRot();
            pitch = pos.getXRot();
            session.send(new ServerboundAcceptTeleportationPacket(pos.getId()));

        } else if (packet instanceof ClientboundSetHealthPacket hp) {
            health = hp.getHealth();
            food = hp.getFood();

        } else if (packet instanceof ClientboundAddEntityPacket add) {
            entities.put(add.getEntityId(), new TrackedEntity(
                add.getEntityId(), add.getUuid(), add.getX(), add.getY(), add.getZ(), add.getType()
            ));

        } else if (packet instanceof ClientboundRemoveEntitiesPacket remove) {
            for (int id : remove.getEntityIds()) {
                entities.remove(id);
            }

        } else if (packet instanceof ClientboundEntityPositionSyncPacket sync) {
            TrackedEntity e = entities.get(sync.getId());
            if (e != null) {
                var pos = sync.getPosition();
                entities.put(sync.getId(), e.withPosition(pos.getX(), pos.getY(), pos.getZ()));
            }

        } else if (packet instanceof ClientboundTeleportEntityPacket tp) {
            TrackedEntity e = entities.get(tp.getId());
            if (e != null) {
                var pos = tp.getPosition();
                entities.put(tp.getId(), e.withPosition(pos.getX(), pos.getY(), pos.getZ()));
            }

        } else if (packet instanceof ClientboundMoveEntityPosPacket move) {
            TrackedEntity e = entities.get(move.getEntityId());
            if (e != null) {
                entities.put(move.getEntityId(), e.withPosition(
                    e.x() + move.getMoveX(), e.y() + move.getMoveY(), e.z() + move.getMoveZ()
                ));
            }

        } else if (packet instanceof ClientboundMoveEntityPosRotPacket move) {
            TrackedEntity e = entities.get(move.getEntityId());
            if (e != null) {
                entities.put(move.getEntityId(), e.withPosition(
                    e.x() + move.getMoveX(), e.y() + move.getMoveY(), e.z() + move.getMoveZ()
                ));
            }

        } else if (packet instanceof ClientboundPlayerInfoUpdatePacket info) {
            for (PlayerListEntry entry : info.getEntries()) {
                if (entry.getProfile() != null && entry.getProfile().getName() != null) {
                    players.put(entry.getProfileId(),
                        new TrackedPlayer(entry.getProfileId(), entry.getProfile().getName()));
                }
            }

        } else if (packet instanceof ClientboundPlayerInfoRemovePacket remove) {
            for (UUID uuid : remove.getProfileIds()) {
                players.remove(uuid);
            }
        }
    }
}
