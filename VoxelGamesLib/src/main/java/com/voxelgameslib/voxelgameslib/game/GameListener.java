package com.voxelgameslib.voxelgameslib.game;

import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.inject.Inject;

import com.voxelgameslib.voxelgameslib.VoxelGamesLib;
import com.voxelgameslib.voxelgameslib.event.events.game.GameJoinEvent;
import com.voxelgameslib.voxelgameslib.event.events.game.GamePostLeaveEvent;
import com.voxelgameslib.voxelgameslib.event.events.game.GamePreLeaveEvent;
import com.voxelgameslib.voxelgameslib.exception.UserException;
import com.voxelgameslib.voxelgameslib.feature.features.SpawnFeature;
import com.voxelgameslib.voxelgameslib.user.User;
import com.voxelgameslib.voxelgameslib.user.UserHandler;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@SuppressWarnings("JavaDoc")
public class GameListener implements Listener {

    private static final Logger log = Logger.getLogger(GameListener.class.getName());
    @Inject
    private GameHandler gameHandler;
    @Inject
    private UserHandler userHandler;
    @Inject
    private VoxelGamesLib voxelGamesLib;

    @EventHandler
    public void onLeave(@Nonnull PlayerQuitEvent event) {
        User user = userHandler.getUser(event.getPlayer().getUniqueId())
            .orElseThrow(() -> new UserException(
                "Unknown user " + event.getPlayer().getDisplayName() + "(" + event.getPlayer()
                    .getUniqueId() + ")"));
        user.setLeaving(true);
        gameHandler.getGames(event.getPlayer().getUniqueId(), true)
            .forEach((game -> game.leave(user, false)));
    }

    @EventHandler
    public void onL(@Nonnull GamePostLeaveEvent event) {
        log.finer(event.getUser().getRawDisplayName() + " left the game " + event.getGame()
            .getGameMode().getName());

        if (!event.getUser().isLeaving() && gameHandler.getDefaultGame() != null) {
            if (event.getGame().getUuid() != gameHandler.getDefaultGame().getUuid()) {
                gameHandler.getDefaultGame().join(event.getUser());
            }
        }
    }

    @EventHandler
    public void onJ(@Nonnull GameJoinEvent event) {
        log.finer(event.getUser().getRawDisplayName() + " joined the game " + event.getGame()
            .getGameMode().getName());
    }

    @EventHandler
    public void onJoin(@Nonnull PlayerJoinEvent event) {
        if (gameHandler.getDefaultGame() != null) {
            gameHandler.getDefaultGame().getActivePhase().getOptionalFeature(SpawnFeature.class).ifPresent(spawnFeature ->
                event.getPlayer().teleport(spawnFeature.getSpawn(event.getPlayer().getUniqueId())));
        }

        Bukkit.getScheduler().runTaskLater(voxelGamesLib, () -> {
            if (gameHandler.getDefaultGame() != null) {
                gameHandler.getDefaultGame().join(userHandler.getUser(event.getPlayer().getUniqueId()).orElseThrow(() ->
                    new UserException("Unknown user " + event.getPlayer().getDisplayName() + "(" + event.getPlayer().getUniqueId() + ")")));
            }
        }, 10);
    }
}
