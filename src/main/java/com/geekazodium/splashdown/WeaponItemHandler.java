package com.geekazodium.splashdown;

import static com.geekazodium.splashdown.WeaponItemHandlerRegistry.ITEM_ID_KEY;

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
        itemMeta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.INTEGER, id);
        item.setItemMeta(itemMeta);
        return item;
    }

    ItemStack getItem();
}
