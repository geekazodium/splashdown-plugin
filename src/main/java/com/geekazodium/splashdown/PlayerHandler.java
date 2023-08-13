/*
 * Copyright © Geekazodium 2023.
 * This file is released under GPLv3. See LICENSE for full license details.
 */
package com.geekazodium.splashdown;

import static com.geekazodium.splashdown.WeaponItemHandlerRegistry.ITEM_ID_KEY;

import com.destroystokyo.paper.event.player.PlayerConnectionCloseEvent;
import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import java.util.Collection;
import java.util.function.Predicate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class PlayerHandler implements Listener {
    public static final int MAX_FOOD_LEVEL = 40;
    private final Player player;

    public PlayerHandler(Player player) {
        this.player = player;
        Bukkit.getServer().getPluginManager().registerEvents(this, SplashDown.getInstance());
    }

    private int getItemId(ItemStack item) {
        if (item == null) return -1;
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) return -1;
        PersistentDataContainer itemDataContainer = itemMeta.getPersistentDataContainer();
        Integer integer = itemDataContainer.get(ITEM_ID_KEY, PersistentDataType.INTEGER);
        if (integer == null) return -1;
        return integer;
    }

    public void onRightClick(PlayerInteractEvent event) {
        int itemId = getItemId(player.getInventory().getItemInMainHand());
        if (itemId != -1) {
            WeaponItemHandler weaponHandler =
                    SplashDown.getInstance().weaponItemHandlerRegistry.getWeaponHandler(itemId);
            weaponHandler.onRightClick(this, event);

            //            Location eyeLocation = player.getEyeLocation();
            //            CollisionBox collisionBox = new CollisionBox(
            //                new Vector(0,0,0),
            //                new Vector(0.5,0.5,1),
            //                new Vector(0.2,0.2,64-1),
            //                new Quaterniond(1,0,0,0)
            //            );
            //            Collection<Entity> nearbyEntities = getNearbyDamageableEntities(eyeLocation, collisionBox,
            // 64);
            //            collisionBox.updateCollider(eyeLocation);
            //            Entity entityHit = getNearestCollidingEntity(eyeLocation, collisionBox, nearbyEntities);
            //            if(entityHit!=null){
            //                if(collisionBox.isCollidingSkull(entityHit)){
            //                    ((LivingEntity) entityHit).damage(100);
            //                }else {
            //                    ((LivingEntity) entityHit).damage(5);
            //                }
            //            }
        }
    }

    @NotNull
    private Collection<Entity> getNearbyDamageableEntities(Location eyeLocation, CollisionBox collisionBox, int range) {
        World world = player.getWorld();
        collisionBox.renderOutline(world);
        Predicate<Entity> damageablePredicate = entity -> entity != player && entity instanceof LivingEntity;
        return world.getNearbyEntities(eyeLocation, range, range, range, damageablePredicate);
    }

    public static Entity getNearestCollidingEntity(
            Location hitboxLocation, CollisionBox collisionBox, Collection<Entity> nearbyEntities) {
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

    public void onLeftClick(PlayerInteractEvent event) {}

    @EventHandler
    public void onTickStart(ServerTickStartEvent event) {}

    @EventHandler
    public void onTickEnd(ServerTickEndEvent event) {}

    public void onDisconnect(PlayerConnectionCloseEvent event) {
        HandlerList.unregisterAll(this);
    }

    public void onPlayerDeath(PlayerDeathEvent event) {
        player.teleportAsync(player.getWorld().getSpawnLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
        player.setFoodLevel(MAX_FOOD_LEVEL);
    }

    public Player getPlayer() {
        return this.player;
    }
}
