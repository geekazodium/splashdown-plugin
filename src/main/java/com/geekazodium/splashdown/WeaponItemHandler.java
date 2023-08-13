/*
 * Copyright © Geekazodium 2023.
 * This file is released under GPLv3. See LICENSE for full license details.
 */
package com.geekazodium.splashdown;

import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public interface WeaponItemHandler {
    String getIdString();

    String getCanonicalName();

    void onRightClick(PlayerHandler playerHandler, PlayerInteractEvent event);

    default ItemStack getItem(int id) {
        ItemStack item = this.getItem();
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.getPersistentDataContainer()
                .set(WeaponItemHandlerRegistry.ITEM_ID_KEY, PersistentDataType.INTEGER, id);
        item.setItemMeta(itemMeta);
        return item;
    }

    ItemStack getItem();
}
