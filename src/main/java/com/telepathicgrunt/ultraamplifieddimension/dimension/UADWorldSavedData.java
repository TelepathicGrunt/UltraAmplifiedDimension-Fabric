package com.telepathicgrunt.ultraamplifieddimension.dimension;

import com.mojang.datafixers.util.Pair;
import com.telepathicgrunt.ultraamplifieddimension.UltraAmplifiedDimension;
import com.telepathicgrunt.ultraamplifieddimension.blocks.AmplifiedPortalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UADWorldSavedData extends PersistentState {

    public static final String DATA_KEY = UltraAmplifiedDimension.MODID + ":delayed_teleportation";
    private List<TeleportEntry> entries = new ArrayList<>();
    private List<SpawnParticles> particles = new ArrayList<>();

    public UADWorldSavedData() {
        super(DATA_KEY);
    }

    public static UADWorldSavedData get(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(UADWorldSavedData::new, DATA_KEY);
    }

    /**
     * Does *not* create worlds that don't already exist
     * So they should be created by the thing that schedules the tick, if possible
     * @param world The world that is being ticked and contains a data instance
     */
    public static void tick(ServerWorld world) {
        MinecraftServer server = world.getServer();
        UADWorldSavedData data = get(world);

        List<TeleportEntry> list = data.entries;
        data.entries = new ArrayList<>();

        List<SpawnParticles> particleList = data.particles;
        data.particles = new ArrayList<>();

        // Delay particle spawning by one tick after teleportation so the player can see their particles
        for (SpawnParticles entry : particleList) {
            ServerWorld targetWorld = server.getWorld(entry.targetWorld);
            if (targetWorld != null) {
                AmplifiedPortalBlock.createLotsOfParticles(targetWorld, entry.targetVec, targetWorld.random);
            }
        }

        for (TeleportEntry entry : list) {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(entry.playerUUID);
            ServerWorld targetWorld = server.getWorld(entry.targetWorld);
            if (player != null && targetWorld != null && player.world == world) {
                ChunkPos playerChunkPos = new ChunkPos(player.getBlockPos());
                targetWorld.getChunkManager().addTicket(ChunkTicketType.POST_TELEPORT, playerChunkPos, 1, player.getEntityId());

                player.fallDistance = 0;
                player.prevY = 0;
                player.teleport(
                        targetWorld,
                        entry.targetVec.getX(),
                        entry.targetVec.getY() + 0.2D,
                        entry.targetVec.getZ(),
                        entry.targetLook.getFirst(),
                        entry.targetLook.getSecond());

                // -0.5 on x and z to counter the builtin 0.5 offset in AmplifiedPortalBlock.createLotsOfParticles
                data.addParticle(entry.targetWorld, new Vec3d(entry.targetVec.x - 0.5d, entry.targetVec.y, entry.targetVec.z - 0.5d));
            }
        }
    }

    public void addPlayer(PlayerEntity player, RegistryKey<World> destination, Vec3d targetVec, Pair<Float, Float> targetLook) {
        this.entries.add(new TeleportEntry(PlayerEntity.getUuidFromProfile(player.getGameProfile()), destination, targetVec, targetLook));
    }

    public void addParticle(RegistryKey<World> destination, Vec3d targetVec) {
        this.particles.add(new SpawnParticles(destination, targetVec));
    }

    @Override
    public void fromTag(CompoundTag nbt) {
    }

    @Override
    public CompoundTag toTag(CompoundTag compound) {
        return compound;
    }

    static class TeleportEntry {
        final UUID playerUUID;
        final RegistryKey<World> targetWorld;
        final Vec3d targetVec;
        final Pair<Float, Float> targetLook;

        public TeleportEntry(UUID playerUUID, RegistryKey<World> targetWorld, Vec3d targetVec, Pair<Float, Float> targetLook) {
            this.playerUUID = playerUUID;
            this.targetWorld = targetWorld;
            this.targetVec = targetVec;
            this.targetLook = targetLook;
        }
    }

    static class SpawnParticles {
        final RegistryKey<World> targetWorld;
        final Vec3d targetVec;

        public SpawnParticles(RegistryKey<World> targetWorld, Vec3d targetVec) {
            this.targetWorld = targetWorld;
            this.targetVec = targetVec;
        }
    }
}