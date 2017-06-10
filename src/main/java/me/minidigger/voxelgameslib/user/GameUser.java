package me.minidigger.voxelgameslib.user;

import com.google.gson.annotations.Expose;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import jskills.Rating;
import me.minidigger.voxelgameslib.config.GlobalConfig;
import me.minidigger.voxelgameslib.game.GameMode;
import me.minidigger.voxelgameslib.lang.Locale;
import me.minidigger.voxelgameslib.persistence.PersistenceHandler;
import me.minidigger.voxelgameslib.role.Permission;
import me.minidigger.voxelgameslib.role.Role;
import me.minidigger.voxelgameslib.utils.ChatUtil;
import net.kyori.text.Component;
import net.kyori.text.TextComponent;
import org.bukkit.entity.Player;

/**
 * abstract implementation of the user interface that deals with some stuff
 */
public class GameUser implements User {

  // TODO this call is a mess, clean it up

  @Inject
  private GlobalConfig config;
  @Inject
  private PersistenceHandler persistenceHandler;


  private Player player;

  @Expose
  private UUID uuid;

  @Expose
  private Role role = Role.DEFAULT;

  @Expose
  private Locale locale = Locale.ENGLISH;

  @Expose
  private Map<String, Rating> ratings = new HashMap<>();

  // combination of <prefix> <rawdisplayname> <suffix>
  private Component displayName;

  @Expose
  private String rawDisplayName;

  @Expose
  private Component prefix;
  @Expose
  private Component suffix;

  @Nonnull
  @Override
  public UUID getUuid() {
    return uuid;
  }

  @Override
  public void sendMessage(@Nonnull Component message) {
    ChatUtil.sendMessage(this, message);
  }

  @Override
  public boolean hasPermission(@Nonnull Permission perm) {
    if (config.useRoleSystem) {
      return getRole().hasPermission(perm);
    }
    return getPlayer().hasPermission(perm.getString());
  }

  @Override
  public Component getDisplayName() {
    if (displayName == null) {
      displayName = prefix.copy().append(new TextComponent(rawDisplayName)).append(suffix);
    }
    return displayName;
  }

  @Override
  public String getRawDisplayName() {
    return rawDisplayName;
  }

  @Override
  public Player getPlayer() {
    return player;
  }

  @Override
  public Locale getLocale() {
    return locale;
  }

  @Override
  public void setLocale(Locale locale) {
    this.locale = locale;
  }

  @Override
  public Role getRole() {
    return role;
  }

  @Override
  public void setRole(Role role) {
    this.role = role;
  }

  @Override
  public Rating getRating(GameMode mode) {
    Rating rating = ratings.get(mode.getName());
    if (rating == null) {
      rating = mode.getDefaultRating();
      // no need to save here
    }
    return rating;
  }

  @Override
  public void saveRating(GameMode mode, Rating rating) {
    ratings.put(mode.getName(), rating);
    persistenceHandler.getProvider().saveUser(this);
  }

  @Override
  public Map<String, Rating> getRatings() {
    return ratings;
  }

  @Override
  public void setPlayer(Player player) {
    this.player = player;
  }

  @Override
  public void setDisplayName(String displayName) {
    this.rawDisplayName = displayName;
    this.displayName = null; // regenerate full display name
  }

  @Override
  public double getPartialPlayPercentage() {
    return 1.0;
  }

  @Override
  public double getPartialUpdatePercentage() {
    return 1.0;
  }

  @Override
  public Component getPrefix() {
    return prefix;
  }

  @Override
  public Component getSuffix() {
    return suffix;
  }

  @Override
  public void setPrefix(Component prefix) {
    this.prefix = prefix;
  }

  @Override
  public void setSuffix(Component suffix) {
    this.suffix = suffix;
  }

  @Override
  public void setUuid(UUID uuid) {
    this.uuid = uuid;
  }
}