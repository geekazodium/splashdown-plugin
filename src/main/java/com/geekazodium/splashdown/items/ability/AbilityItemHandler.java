/*
 * Copyright © Geekazodium 2023.
 * This file is released under GPLv3. See LICENSE for full license details.
 */
package com.geekazodium.splashdown.items.ability;

import com.geekazodium.splashdown.PlayerHandler;
import com.geekazodium.splashdown.items.CustomItemHandler;
import io.papermc.paper.event.player.PlayerArmSwingEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public abstract class AbilityItemHandler implements CustomItemHandler {
    @Override
    public void onRightClick(PlayerHandler playerHandler, PlayerInteractEvent event) {}

    @Override
    public void onLeftClick(PlayerHandler playerHandler, PlayerArmSwingEvent event, ItemStack itemStack) {}
}
