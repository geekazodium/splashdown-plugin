/*
 * Copyright © Geekazodium 2023.
 * This file is released under GPLv3. See LICENSE for full license details.
 */
package com.geekazodium.splashdown.items;

import com.geekazodium.splashdown.PlayerHandler;
import io.papermc.paper.event.player.PlayerArmSwingEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public interface CustomItemHandler {
    String getIdString();

    String getCanonicalName();

    void onRightClick(PlayerHandler playerHandler, PlayerInteractEvent event);

    default ItemStack getItem(int id) {
        ItemStack item = this.getItem();
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.getPersistentDataContainer()
                .set(CustomItemHandlerRegistry.ITEM_ID_KEY, PersistentDataType.INTEGER, id);
        item.setItemMeta(itemMeta);
        return item;
    }

    ItemStack getItem();

    void onLeftClick(PlayerHandler playerHandler, PlayerArmSwingEvent event, ItemStack itemStack);
}
