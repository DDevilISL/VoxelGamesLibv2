package me.minidigger.voxelgameslib.feature.features;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Optional;
import com.google.gson.annotations.Expose;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import me.minidigger.voxelgameslib.event.events.game.GameJoinEvent;
import me.minidigger.voxelgameslib.feature.AbstractFeature;
import me.minidigger.voxelgameslib.feature.Feature;
import me.minidigger.voxelgameslib.feature.FeatureInfo;
import me.minidigger.voxelgameslib.lang.Lang;
import me.minidigger.voxelgameslib.lang.LangKey;
import me.minidigger.voxelgameslib.map.MapInfo;
import me.minidigger.voxelgameslib.user.User;
import me.minidigger.voxelgameslib.world.WorldConfig;
import org.bukkit.event.EventHandler;

@FeatureInfo(name = "VoteFeature", author = "MiniDigger", version = "1.0",
    description = "Simple feature that lets ppl vote on maps")
public class VoteFeature extends AbstractFeature {

  @Expose
  private int maxMaps = 3;

  @Inject
  private WorldConfig config;

  private Map<UUID, Integer> votes = new HashMap<>();
  private Map<Integer, MapInfo> availableMaps = new HashMap<>();

  @Override
  public void start() {
    String mode = getPhase().getGame().getGameMode().getName();
    int id = 0;
    for (MapInfo info : config.maps) {
      if (info.getGamemodes().contains(mode)) {
        availableMaps.put(id++, info);
      }

      if (id == maxMaps - 1) {
        break;
      }
    }

    if (availableMaps.size() == 0) {
      getPhase().getGame().broadcastMessage(LangKey.VOTE_NO_MAPS_FOUND);
      getPhase().getGame().abortGame();
    }

    getPhase().getGame().getPlayers().forEach(this::sendVoteMessage);
  }

  @Override
  public void stop() {
    Map<Integer, Integer> votes = new HashMap<>();
    int max = -1;
    int maxMap = -1;
    for (Integer map : this.votes.values()) {
      int old = votes.getOrDefault(map, 0);
      old++;
      if (old > max) {
        max = old;
        maxMap = map;
      }
      votes.put(map, old);
    }

    MapInfo winner = availableMaps.get(maxMap);
    if (winner == null) {
      // use first map if nobody won
      winner = availableMaps.values().iterator().next();
    }

    getPhase().getGame().putGameData("map", winner);
    getPhase().getGame()
        .broadcastMessage(LangKey.VOTE_END, winner.getName(), winner.getAuthor(), max);
  }

  @Override
  public void tick() {

  }

  @Override
  public void init() {

  }

  /**
   * Sends the vote message to that user
   *
   * @param user the user that should receive the message
   */
  public void sendVoteMessage(User user) {
    Lang.msg(user, LangKey.VOTE_MESSAGE_TOP);
    for (int id : availableMaps.keySet()) {
      MapInfo info = availableMaps.get(id);
      Lang.msg(user, LangKey.VOTE_MESSAGE_MAP, id, info.getName(), info.getAuthor());
    }
    Lang.msg(user, LangKey.VOTE_MESSAGE_BOT);
  }

  @EventHandler
  public void onJoin(@Nonnull GameJoinEvent event) {
    if (event.getGame().getUuid().equals(getPhase().getGame().getUuid())) {
      sendVoteMessage(event.getUser());
    }
  }

  @CommandAlias("vote")
  public void vote(User sender, @Optional Integer map) {
    if (map == null) {
      sendVoteMessage(sender);
    } else {
      if (votes.containsKey(sender.getUuid())) {
        Lang.msg(sender, LangKey.VOTE_ALREADY_VOTED);
      } else {
        if (availableMaps.get(map) == null) {
          Lang.msg(sender, LangKey.VOTE_UNKNOWN_MAP, map);
          return;
        }

        votes.put(sender.getUuid(), map);
        Lang.msg(sender, LangKey.VOTE_SUBMITTED, map);
      }
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public Class<? extends Feature>[] getDependencies() {
    return new Class[0];
  }
}
