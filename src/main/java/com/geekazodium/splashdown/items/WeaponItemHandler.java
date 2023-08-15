/*
 * Copyright © Geekazodium 2023.
 * This file is released under GPLv3. See LICENSE for full license details.
 */
package com.geekazodium.splashdown.items;

import com.geekazodium.splashdown.PlayerHandler;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public abstract class WeaponItemHandler implements CustomItemHandler {
    @Override
    public void onRightClick(PlayerHandler playerHandler, PlayerInteractEvent event) {}

    protected boolean ifSetCooldown(PlayerHandler playerHandler, ItemStack thisItem, int cooldown) {
        Player player = playerHandler.getPlayer();
        Material itemMaterial = thisItem.getType();
        if (player.getCooldown(itemMaterial) > 0) return false;
        player.setCooldown(itemMaterial, cooldown);
        return true;
    }
}
