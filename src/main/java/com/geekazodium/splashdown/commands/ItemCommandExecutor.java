/*
 * Copyright © Geekazodium 2023.
 * This file is released under GPLv3. See LICENSE for full license details.
 */
package com.geekazodium.splashdown.commands;

import com.geekazodium.splashdown.SplashDown;
import com.geekazodium.splashdown.items.CustomItemHandler;
import com.geekazodium.splashdown.items.CustomItemHandlerRegistry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemCommandExecutor implements CommandExecutor {
    // test code to make sure command is actually running
    @Override
    public boolean onCommand(
            @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return false;
        CustomItemHandlerRegistry customItemHandlerRegistry = SplashDown.getInstance().customItemHandlerRegistry;
        if (args.length != 1) return false;
        String itemString = args[0];
        int handlerId = customItemHandlerRegistry.getHandlerId(itemString);
        CustomItemHandler customItemHandler = customItemHandlerRegistry.getWeaponHandler(handlerId);
        ItemStack item = customItemHandler.getItem(handlerId);
        player.getInventory().addItem(item);
        return true;
    }
}
