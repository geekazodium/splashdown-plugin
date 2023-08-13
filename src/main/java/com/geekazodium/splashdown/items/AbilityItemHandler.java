package com.geekazodium.splashdown.items;

import com.geekazodium.splashdown.PlayerHandler;
import io.papermc.paper.event.player.PlayerArmSwingEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public abstract class AbilityItemHandler implements CustomItemHandler{
    @Override
    public void onRightClick(PlayerHandler playerHandler, PlayerInteractEvent event) {

    }

    @Override
    public void onLeftClick(PlayerHandler playerHandler, ItemStack mainHandItem, int heldItemSlot, PlayerArmSwingEvent event) {

    }
}
