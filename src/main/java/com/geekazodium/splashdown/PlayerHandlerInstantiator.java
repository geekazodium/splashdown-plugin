/*
 * Copyright © Geekazodium 2023.
 * This file is released under GPLv3. See LICENSE for full license details.
 */
package com.geekazodium.splashdown;

import com.destroystokyo.paper.event.player.PlayerConnectionCloseEvent;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

import io.papermc.paper.event.player.PlayerArmSwingEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerHandlerInstantiator implements Listener {
    public static final Logger LOGGER = SplashDown.LOGGER;
    private final HashMap<UUID, PlayerHandler> playerHandlers;

    public PlayerHandlerInstantiator() {
        this.playerHandlers = new HashMap<>();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerHandler handler = new PlayerHandler(player);
        playerHandlers.put(player.getUniqueId(), handler);
    }

    @EventHandler
    public void onPlayerLeave(PlayerConnectionCloseEvent event) {
        PlayerHandler handler = playerHandlers.remove(event.getPlayerUniqueId());
        if (handler == null) {
            LOGGER.warning("attempted to remove a nonexistent player -> name:" + event.getPlayerName() + ", uuid:"
                    + event.getPlayerUniqueId());
            return;
        }
        handler.onDisconnect(event);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        PlayerHandler playerHandler = playerHandlers.get(event.getPlayer().getUniqueId());
        if (event.getAction().isRightClick()) {
            playerHandler.onRightClick(event);
            return;
        }
    }

    @EventHandler
    public void onPlayerLeftClick(PlayerArmSwingEvent event){
        PlayerHandler playerHandler = playerHandlers.get(event.getPlayer().getUniqueId());
        playerHandler.onLeftClick(event);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Bukkit.getServer().sendMessage(nonNullDeathMessage(event));
        Player player = event.getPlayer();
        PlayerHandler playerHandler = playerHandlers.get(player.getUniqueId());
        playerHandler.onPlayerDeath(event);
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerAnimationEvent(PlayerAnimationEvent event){
        Player player = event.getPlayer();
        PlayerHandler playerHandler = playerHandlers.get(player.getUniqueId());
        playerHandler.onAnimation(event);
    }

    @NotNull
    private Component nonNullDeathMessage(PlayerDeathEvent event) {
        Component component = event.deathMessage();
        if (component == null) {
            LOGGER.warning("Death message is null for death event of player: "
                    + event.getPlayer().getName());
            return Component.text("");
        }
        return component;
    }
}
