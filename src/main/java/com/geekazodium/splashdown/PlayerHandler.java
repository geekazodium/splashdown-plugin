package com.geekazodium.splashdown;

import com.destroystokyo.paper.event.player.PlayerConnectionCloseEvent;
import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PlayerHandler implements Listener {

    public static final NamespacedKey ITEM_ID_KEY = new NamespacedKey(SplashDown.getInstance(), "item_id");
    private final Player player;
    private final CollisionBox hurtbox;

    public PlayerHandler(Player player){
        this.player = player;
        this.hurtbox = CollisionBox.fromBoundingBox(this.player.getBoundingBox());
        Bukkit.getServer().getPluginManager().registerEvents(this,SplashDown.getInstance());
    }

    private int getItemId(ItemStack item) {
        if(item == null) return -1;
        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer itemDataContainer = itemMeta.getPersistentDataContainer();
        Integer integer = itemDataContainer.get(ITEM_ID_KEY, PersistentDataType.INTEGER);
        if(integer == null) return -1;
        return integer;
    }

    public void onRightClick(PlayerInteractEvent event){
        Location eyeLocation = player.getEyeLocation();
        eyeLocation.createExplosion(10);
    }

    public void onLeftClick(PlayerInteractEvent event) {

    }

    @EventHandler
    public void onServerTick(ServerTickEndEvent event){
        hurtbox.updateCollider(player.getLocation());
    }

    public void onDisconnect(PlayerConnectionCloseEvent event){
        HandlerList.unregisterAll(this);
    }
}
