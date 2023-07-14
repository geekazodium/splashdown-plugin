package com.geekazodium.splashdown.commands;

import com.geekazodium.splashdown.SplashDown;
import com.geekazodium.splashdown.WeaponItemHandler;
import com.geekazodium.splashdown.WeaponItemHandlerRegistry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemCommandExecutor implements CommandExecutor {
    //test code to make sure command is actually running
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return false;
        WeaponItemHandlerRegistry weaponItemHandlerRegistry = SplashDown.getInstance().weaponItemHandlerRegistry;
        if(args.length!=1)return false;
        String itemString = args[0];
        int handlerId = weaponItemHandlerRegistry.getHandlerId(itemString);
        WeaponItemHandler weaponItemHandler = weaponItemHandlerRegistry.getWeaponHandler(handlerId);
        ItemStack item = weaponItemHandler.getItem(handlerId);
        player.getInventory().addItem(item);
        return true;
    }
}
