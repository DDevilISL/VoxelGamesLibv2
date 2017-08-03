package com.voxelgameslib.voxelgameslib.chat;

import com.voxelgameslib.voxelgameslib.user.User;
import com.voxelgameslib.voxelgameslib.utils.ChatUtil;
import lombok.Getter;
import lombok.Setter;
import net.kyori.text.Component;
import net.kyori.text.TextComponent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * A chat channel represents a collection of listeners who are able to receive messages send into
 * this channel
 */
@Getter
@Setter
public class ChatChannel {

    private String identifier;
    private List<User> listeners;
    @Nullable
    private Component prefix;

    /**
     * Creates a new, empty channel with a prefix
     *
     * @param identifier a unique identifier for this channel
     */
    ChatChannel(String identifier, @Nullable Component prefix) {
        this.identifier = identifier;
        this.listeners = new ArrayList<>();
        this.prefix = prefix;
    }

    /**
     * Creates a new, empty channel
     *
     * @param identifier a unique identifier for this channel
     */
    ChatChannel(String identifier) {
        this(identifier, null);
    }

    /**
     * Adds a new listener to the channel
     * <p>
     * Listeners can hear messages, but messages they send will not be sent to this channel unless
     * it is their active channel.
     *
     * @param user the new listener
     */
    public void addListener(User user) {
        listeners.add(user);
    }

    /**
     * Removes a listener from the channel
     *
     * @param user the listener to remove
     */
    public void removeListener(User user) {
        listeners.remove(user);
    }

    /**
     * Sends a plaintext message to the channel's listeners
     *
     * @param byUser  sender
     * @param message message
     */
    public void sendMessage(User byUser, String message) {
        sendMessage(byUser, TextComponent.of(message));
    }

    /**
     * Sends a component message to the channel's listeners
     *
     * @param byUser  sender
     * @param message the message to send
     */
    public void sendMessage(User byUser, Component message) {
        listeners.forEach(listener -> listener.sendMessage(ChatUtil.formatChannelMessage(this, byUser.getDisplayName(), message)));
    }
}