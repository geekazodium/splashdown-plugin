/*
 * Copyright © Geekazodium 2023.
 * This file is released under GPLv3. See LICENSE for full license details.
 */
package com.geekazodium.splashdown.items.ability;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BubbleDashAbilityHandler extends AbilityItemHandler {
    @Override
    public String getIdString() {
        return "ability_bubble_dash";
    }

    @Override
    public String getCanonicalName() {
        return "Bubble Dash";
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = new ItemStack(Material.BREWER_POTTERY_SHERD, 1);
        return item;
    }
}
