package com.geekazodium.splashdown;

import com.destroystokyo.paper.event.player.PlayerConnectionCloseEvent;
import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaterniond;

import java.util.Collection;
import java.util.function.Predicate;

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
        CollisionBox collisionBox = new CollisionBox(
            new Vector(0,0,0),
            new Vector(0.5,0.5,1),
            new Vector(0.2,0.2,20),
            new Quaterniond(1,0,0,0)
        );
        Collection<Entity> nearbyEntities = getNearbyDamageableEntities(eyeLocation, collisionBox, 64);
        collisionBox.updateCollider(eyeLocation);
        Entity entityHit = getNearestCollidingEntity(eyeLocation, collisionBox, nearbyEntities);
        if(entityHit!=null){
            if(collisionBox.isCollidingSkull(entityHit)){
                ((LivingEntity) entityHit).damage(100);
            }else {

            }
        }
    }

    @NotNull
    private Collection<Entity> getNearbyDamageableEntities(Location eyeLocation, CollisionBox collisionBox,int range) {
        World world = player.getWorld();
        collisionBox.renderOutline(world);
        Predicate<Entity> damageablePredicate = entity -> entity != player && entity instanceof LivingEntity;
        return world.getNearbyEntities(
            eyeLocation,
            range, range, range,
            damageablePredicate
        );
    }

    public static Entity getNearestCollidingEntity(Location hitboxLocation, CollisionBox collisionBox, Collection<Entity> nearbyEntities) {
        Entity nearestCollidingEntity = null;
        double nearestDistance = -1;
        Vector hitboxLocationVector = hitboxLocation.toVector();
        for (Entity entity : nearbyEntities) {
            if (collisionBox.isColliding(entity)) {
                double distanceSquared = entity.getBoundingBox().getCenter().distanceSquared(hitboxLocationVector);
                if (distanceSquared < nearestDistance || nearestDistance == -1) {
                    nearestDistance = distanceSquared;
                    nearestCollidingEntity = entity;
                }
            }
        }
        return nearestCollidingEntity;
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
