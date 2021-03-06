package com.voxelgameslib.voxelgameslib.stats;

import net.kyori.text.TextComponent;

import com.voxelgameslib.voxelgameslib.event.events.player.PlayerDecrementStatEvent;
import com.voxelgameslib.voxelgameslib.event.events.player.PlayerIncrementStatEvent;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class StatListener implements Listener {

    @EventHandler
    public void increment(PlayerIncrementStatEvent e) {
        if (!e.getStat().shouldAnnounce()) return;
        e.getUser().sendMessage(TextComponent.of("+" + e.getIncrementAmount() + " " + e.getStat().getDisplayName() + "(" + e.getNewVal() + ")"));
    }

    @EventHandler
    public void decrement(PlayerDecrementStatEvent e) {
        if (!e.getStat().shouldAnnounce()) return;
        e.getUser().sendMessage(TextComponent.of("-" + e.getDecrementAmount() + " " + e.getStat().getDisplayName() + "(" + e.getNewVal() + ")"));
    }
}
