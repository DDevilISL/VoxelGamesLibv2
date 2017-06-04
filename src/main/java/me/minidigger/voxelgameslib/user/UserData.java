package me.minidigger.voxelgameslib.user;

import com.google.gson.annotations.Expose;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.inject.Inject;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import jskills.Rating;
import lombok.Getter;
import lombok.Setter;
import me.minidigger.voxelgameslib.game.GameMode;
import me.minidigger.voxelgameslib.lang.Locale;
import me.minidigger.voxelgameslib.persistence.PersistenceHandler;
import me.minidigger.voxelgameslib.role.Role;

/**
 * The data for a user
 */
@Getter
@Setter
public class UserData implements Serializable {

  @Inject
  private PersistenceHandler persistenceHandler;

  @Expose
  private UUID uuid;

  @Expose
  private Role role = Role.DEFAULT;

  @Expose
  private Locale locale = Locale.ENGLISH;

  @Expose
  private Map<String, Rating> ratings = new HashMap<>();

  @Expose
  private String displayName = "<error>";

  @Expose
  private String prefix = "{\"text\":\"\"}";
  @Expose
  private String suffix = "{\"text\":\"\"}";

  /**
   * Creates a new userdata object
   *
   * @param id the uuid of the user
   */
  public UserData(UUID id) {
    this.uuid = id;
  }

  /**
   * @param mode the mode to get the rating for
   * @return the rating of this player for gamemode mode. will return default values if not present
   */
  public Rating getRating(GameMode mode) {
    Rating rating = ratings.get(mode.getName());
    if (rating == null) {
      rating = mode.getDefaultRating();
      // no need to save here
    }
    return rating;
  }

  /**
   * Saves a rating for this users. will override existing ratings
   *
   * @param mode the mode the rating was achieved in
   * @param rating the new rating
   */
  public void saveRating(GameMode mode, Rating rating) {
    ratings.put(mode.getName(), rating);
    persistenceHandler.getProvider().saveUserData(this);
  }

  /**
   * @return all ratings for this player
   */
  public Map<String, Rating> getRatings() {
    return ratings;
  }
}
