package com.voxelgameslib.voxelgameslib.phase;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.voxelgameslib.voxelgameslib.condition.VictoryCondition;
import com.voxelgameslib.voxelgameslib.exception.NoSuchFeatureException;
import com.voxelgameslib.voxelgameslib.feature.Feature;
import com.voxelgameslib.voxelgameslib.game.Game;
import com.voxelgameslib.voxelgameslib.tick.Tickable;

/**
 * A {@link Phase} is directly tied to a {@link Game}. A {@link Phase} is a collection of {@link Feature}. A {@link
 * Phase} is directly linked to the next {@link Phase}.
 */
public interface Phase extends Tickable {

    /**
     * Initialises this phase
     */
    void init();

    /**
     * sets the next phase
     *
     * @param nextPhase the new next phase
     */
    void setNextPhase(@Nonnull Phase nextPhase);

    /**
     * sets the game this phase is attached to
     *
     * @param game the game
     */
    void setGame(@Nonnull Game game);

    /**
     * Adds a new feature to this phase
     *
     * @param feature the new feature to add
     */
    void addFeature(@Nonnull Feature feature);

    /**
     * sets the name of this phase
     *
     * @param name the new name for this phase
     */
    void setName(@Nonnull String name);

    /**
     * @return the name of this {@link Phase}
     */
    @Nonnull
    String getName();

    /**
     * @return the {@link Game}, this {@link Phase} is tied to
     */
    @Nonnull
    Game getGame();

    /**
     * Searched for a instance of the specified class.
     *
     * @param clazz the class of the {@link Feature}
     * @param <T>   the type of the feature
     * @return the instance of the class, if present.
     * @throws NoSuchFeatureException if this phase doesn't has that feature registered
     */
    @Nonnull
    <T extends Feature> T getFeature(@Nonnull Class<T> clazz);

    /**
     * Searched for a instance of the specified class.
     *
     * @param clazz the class of the {@link Feature}
     * @param <T>   the type of the feature
     * @return the instance of the class, if present.
     */
    @Nonnull
    <T extends Feature> Optional<T> getOptionalFeature(@Nonnull Class<T> clazz);

    /**
     * @return a list with all {@link Feature}s that are present in this {@link Phase}
     */
    @Nonnull
    List<Feature> getFeatures();

    void addVictoryCondition(VictoryCondition victoryCondition);

    /**
     * @return the {@link Phase} that will follow after this {@link Phase} has ended.
     */
    @Nullable
    Phase getNextPhase();

    /**
     * sets if new players are allowed to join while this phase is active
     *
     * @param allowJoin if new players should join
     */
    void setAllowJoin(boolean allowJoin);

    /**
     * @return if new players are allowed to join while this phase is active
     */
    boolean allowJoin();

    /**
     * sets if new players are allowed to join this game to spectate (not play!)
     *
     * @param allowSpectate if new players should join and spectate
     */
    void setAllowSpectate(boolean allowSpectate);

    /**
     * @return if new players are allowed to join and spectate while this phase is active
     */
    boolean allowSpectate();

    /**
     * sets the status for this phases
     *
     * @param running true if the phase should be set to active
     */
    void setRunning(boolean running);

    /**
     * @return the current status of the phase. true if the phase is active
     */
    boolean isRunning();

    /**
     * if running: get the duration between enable and now.<br> else: get the duration this phase has run
     *
     * @return the duration
     */
    @Nonnull
    Duration getDuration();

    /**
     * Adds a tickable exclusive to the phase
     *
     * @param tickable the tickable object
     */
    void addTickable(@Nonnull UUID identifier, @Nonnull Tickable tickable);

    /**
     * Removes a tickable that is dependent on the phase
     *
     * @param identifier identifier of the tickable
     */
    void removeTickable(@Nonnull UUID identifier);

    /**
     * A list with all victory conditions that have to be met to be able to finish this game
     *
     * @return the list of victory conditions
     */
    List<VictoryCondition> getVictoryConditions();
}
