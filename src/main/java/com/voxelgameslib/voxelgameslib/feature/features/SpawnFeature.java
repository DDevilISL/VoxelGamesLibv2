package com.voxelgameslib.voxelgameslib.feature.features;

import com.google.gson.annotations.Expose;
import com.voxelgameslib.voxelgameslib.event.GameEvent;
import com.voxelgameslib.voxelgameslib.event.events.game.GameJoinEvent;
import com.voxelgameslib.voxelgameslib.feature.AbstractFeature;
import com.voxelgameslib.voxelgameslib.feature.Feature;
import com.voxelgameslib.voxelgameslib.feature.FeatureInfo;
import com.voxelgameslib.voxelgameslib.map.Map;
import com.voxelgameslib.voxelgameslib.map.Marker;
import com.voxelgameslib.voxelgameslib.map.Vector3D;
import com.voxelgameslib.voxelgameslib.user.User;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerRespawnEvent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@FeatureInfo(name = "SpawnFeature", author = "MiniDigger", version = "1.0",
        description = "Handles (re)spawning")
public class SpawnFeature extends AbstractFeature {

    @Expose
    private boolean isRespawn = true;
    @Expose
    private boolean isInitialSpawn = true;

    private List<Vector3D> spawns = new ArrayList<>();
    private Map map;

    @Override
    public void start() {
        map = getPhase().getFeature(MapFeature.class).getMap();
        for (Marker marker : map.getMarkers()) {
            if (marker.getData().startsWith("spawn")) {
                spawns.add(marker.getLoc());
            }
        }
        if (isInitialSpawn) {
            for (User user : getPhase().getGame().getPlayers()) {
                user.getPlayer().teleport(getSpawn(user.getPlayer().getUniqueId()));
            }
        }
    }

    /**
     * Generates a spawn location for that user
     *
     * @param id the uuid user which wants to spawn
     * @return the location he should spawn
     */
    public Location getSpawn(@Nonnull UUID id) {
        //TODO super fancy spawn algorithm
        return spawns.get(ThreadLocalRandom.current().nextInt(spawns.size()))
                .toLocation(map.getLoadedName(getPhase().getGame().getUuid()));
    }

    @SuppressWarnings("JavaDoc")
    @GameEvent
    public void onRespawn(PlayerRespawnEvent e) {
        e.setRespawnLocation(getSpawn(e.getPlayer().getUniqueId()));
    }

    @SuppressWarnings("JavaDoc")
    @GameEvent
    public void onJoin(GameJoinEvent e) {
        e.getUser().getPlayer().teleport(getSpawn(e.getUser().getUuid()));
    }

    @Override
    public void stop() {

    }

    @Override
    public void tick() {

    }

    @Override
    public void init() {

    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<? extends Feature>[] getDependencies() {
        return new Class[]{MapFeature.class};
    }

    /**
     * @param respawn if true, players will respawn after they died
     */
    public void setRespawn(boolean respawn) {
        isRespawn = respawn;
    }

    /**
     * @return if true, players will respawn after they died
     */
    public boolean isRespawn() {
        return isRespawn;
    }

    /**
     * @return if true, players will be spawned at the start of this phase
     */
    public boolean isInitialSpawn() {
        return isInitialSpawn;
    }

    /**
     * @param initialSpawn if true, players will be spawned at the start of this phase
     */
    public void setInitialSpawn(boolean initialSpawn) {
        isInitialSpawn = initialSpawn;
    }
}