/*
 * Copyright © Geekazodium 2023.
 * This file is released under GPLv3. See LICENSE for full license details.
 */
package com.geekazodium.splashdown.items;

import com.geekazodium.splashdown.CollisionUtil;
import com.geekazodium.splashdown.PlayerHandler;
import com.geekazodium.splashdown.entities.BubbleEntity;
import com.geekazodium.splashdown.util.RandomUtil;
import io.papermc.paper.event.player.PlayerArmSwingEvent;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.joml.Quaterniond;

public class BubbleGunHandler extends WeaponItemHandler {
    @Override
    public String getIdString() {
        return "bubble_gun";
    }

    @Override
    public String getCanonicalName() {
        return "BubbleGun";
    }

    @Override
    public void onLeftClick(PlayerHandler playerHandler, PlayerArmSwingEvent event, ItemStack itemStack) {
        if (!ifSetCooldown(playerHandler, itemStack, 20)) return;
        Player player = playerHandler.getPlayer();
        Location eyeLocation = player.getEyeLocation();
        CraftWorld world = (CraftWorld) player.getWorld();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            Quaterniond offset = new Quaterniond(1, 0, 0, 0)
                    .rotateY(RandomUtil.getBoxMullerNormLength(random) / 12)
                    .rotateLocalX(RandomUtil.getBoxMullerNormRotation(random));
            offset.rotateLocalZ(-Math.toRadians(eyeLocation.getPitch()))
                    .rotateLocalY(-Math.toRadians(eyeLocation.getYaw() + 90));
            Vector finalDirectionVector =
                    CollisionUtil.applyRotationMatrix(new Vector(1, 0, 0), CollisionUtil.getRotationMatrix(offset));
            spawnBubbleEntity(player, eyeLocation, finalDirectionVector, world);
        }
        event.setCancelled(true);
    }

    private void spawnBubbleEntity(Player player, Location eyeLocation, Vector deltaMovement, CraftWorld world) {
        world.addEntityToWorld(
                new BubbleEntity(eyeLocation, deltaMovement.clone().multiply(2), player),
                CreatureSpawnEvent.SpawnReason.COMMAND);
    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(Material.IRON_HOE);
    }
}
