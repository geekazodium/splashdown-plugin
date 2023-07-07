package com.geekazodium.splashdown;

import com.destroystokyo.paper.event.player.PlayerConnectionCloseEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

public class PlayerHandlerInstantiator implements Listener {
    public static final Logger LOGGER = SplashDown.LOGGER;
    private final HashMap<UUID, PlayerHandler> playerHandlers;

    public PlayerHandlerInstantiator(){
        this.playerHandlers = new HashMap<>();
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        PlayerHandler handler = new PlayerHandler(player);
        playerHandlers.put(player.getUniqueId(),handler);
    }

    @EventHandler
    public void onPlayerLeave(PlayerConnectionCloseEvent event){
        PlayerHandler handler = playerHandlers.remove(event.getPlayerUniqueId());
        if(handler == null) {
            LOGGER.warning("attempted to remove a nonexistent player -> name:"+event.getPlayerName()+", uuid:"+event.getPlayerUniqueId());
            return;
        }
        handler.onDisconnect(event);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        PlayerHandler playerHandler = playerHandlers.get(event.getPlayer().getUniqueId());
        if(event.getAction().isRightClick()){
            playerHandler.onRightClick(event);
            return;
        }
        if(event.getAction().isLeftClick()){
            playerHandler.onLeftClick(event);
        }
    }
}
