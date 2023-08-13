/*
 * Copyright © Geekazodium 2023.
 * This file is released under GPLv3. See LICENSE for full license details.
 */
package com.geekazodium.splashdown;

import com.destroystokyo.paper.event.player.PlayerConnectionCloseEvent;
import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import java.util.Collection;
import java.util.function.Predicate;

import com.geekazodium.splashdown.items.CustomItemHandler;
import com.geekazodium.splashdown.items.CustomItemHandlerRegistry;
import io.papermc.paper.event.player.PlayerArmSwingEvent;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class PlayerHandler implements Listener {
    public static final int MAX_FOOD_LEVEL = 40;
    private final Player player;

    public PlayerHandler(Player player) {
        this.player = player;
        disableArmSwingAnimation();
        Bukkit.getServer().getPluginManager().registerEvents(this, SplashDown.getInstance());
    }

    private int getItemId(ItemStack item) {
        if (item == null) return -1;
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) return -1;
        PersistentDataContainer itemDataContainer = itemMeta.getPersistentDataContainer();
        Integer integer = itemDataContainer.get(CustomItemHandlerRegistry.ITEM_ID_KEY, PersistentDataType.INTEGER);
        if (integer == null) return -1;
        return integer;
    }

    public void onRightClick(PlayerInteractEvent event) {
        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        runIfItemIdExists(mainHandItem, itemId -> {
            CustomItemHandler itemHandler =
            SplashDown.getInstance().customItemHandlerRegistry.getWeaponHandler(itemId);
            itemHandler.onRightClick(this, event);
        });
    }

    public void onLeftClick(PlayerArmSwingEvent event) {
        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        runIfItemIdExists(mainHandItem, itemId -> {
            if(event.getHand() != EquipmentSlot.HAND)return;
            CustomItemHandler itemHandler =
                SplashDown.getInstance().customItemHandlerRegistry.getWeaponHandler(itemId);
            itemHandler.onLeftClick(this, mainHandItem, player.getInventory().getHeldItemSlot(), event);
        });
    }

    public void onAnimation(PlayerAnimationEvent event){
        event.setCancelled(true);
    }

    private void runIfItemIdExists(ItemStack itemStack,IfItemIdExists runIfHasId){
        int itemId = getItemId(itemStack);
        if (itemId != -1) {
            runIfHasId.ifHasItemId(itemId);
        }
    }

    @FunctionalInterface
    private interface IfItemIdExists{
        void ifHasItemId(int itemId);
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
        disableArmSwingAnimation();
    }

    private void disableArmSwingAnimation(){
        player.clearActivePotionEffects();
        player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING,Integer.MAX_VALUE,5,true));
    }

    public Player getPlayer() {
        return this.player;
    }
}
