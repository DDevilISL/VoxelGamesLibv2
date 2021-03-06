package com.voxelgameslib.voxelgameslib.feature;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;

import com.voxelgameslib.voxelgameslib.game.GameMode;
import com.voxelgameslib.voxelgameslib.map.MarkerDefinition;
import com.voxelgameslib.voxelgameslib.phase.Phase;
import com.voxelgameslib.voxelgameslib.tick.Tickable;

/**
 * A {@link Feature} is a piece of logic that is active during a {@link Phase}. It typically listens to certain events
 * and reacts based of that. A {@link Feature} is a small module that can be reused in a variety of {@link GameMode}s
 */
public interface Feature extends Tickable {

    /**
     * @return the {@link Phase} this {@link Feature} is attached too.
     */
    @Nonnull
    Phase getPhase();

    /**
     * sets the face that this feature is attached to
     *
     * @param phase the phase that this feature is attached to
     */
    void setPhase(@Nonnull Phase phase);

    /**
     * initialises this feature
     */
    void init();

    /**
     * @return the name of this feature
     */
    @Nonnull
    String getName();

    /**
     * @return all features this feature depends on. this feature will load before any dependency in this list and the
     * dependencies are required to be active
     */
    @Nonnull
    default List<Class<? extends Feature>> getDependencies() {
        return Collections.emptyList();
    }

    /**
     * @return all features this phase soft depends on. this feature will load before any dependency in this list, but
     * the dependencies are not required to be active
     */
    @Nonnull
    default List<Class<? extends Feature>> getSoftDependencies() {
        return Collections.emptyList();
    }

    /**
     * @return a list of all markers this feature uses
     */
    @Nonnull
    default MarkerDefinition[] getMarkers() {
        return new MarkerDefinition[0];
    }
}
