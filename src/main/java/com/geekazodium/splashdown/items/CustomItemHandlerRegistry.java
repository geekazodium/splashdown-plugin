/*
 * Copyright © Geekazodium 2023.
 * This file is released under GPLv3. See LICENSE for full license details.
 */
package com.geekazodium.splashdown.items;

import com.geekazodium.splashdown.SplashDown;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.NamespacedKey;

public class CustomItemHandlerRegistry {
    public static final NamespacedKey ITEM_ID_KEY = new NamespacedKey(SplashDown.getInstance(), "item_id");
    private final List<CustomItemHandler> customItemHandlers;
    private Map<String, Integer> stringIdReferences;

    public CustomItemHandlerRegistry() {
        customItemHandlers = new ArrayList<>();
    }

    public void registerItem(CustomItemHandler itemHandler) {
        this.customItemHandlers.add(itemHandler);
    }

    public void initIdStrings() {
        this.stringIdReferences = new HashMap<>();
        List<CustomItemHandler> itemHandlers = this.customItemHandlers;
        for (int i = 0; i < itemHandlers.size(); i++) {
            CustomItemHandler customItemHandler = itemHandlers.get(i);
            stringIdReferences.put(customItemHandler.getIdString(), i);
            stringIdReferences.put(customItemHandler.getCanonicalName(), i);
        }
    }

    public CustomItemHandler getWeaponHandler(int id) {
        return customItemHandlers.get(id);
    }

    public CustomItemHandler getWeaponHandlerFromString(String name) {
        return getWeaponHandler(getHandlerId(name));
    }

    public int getHandlerId(String name) {
        return this.stringIdReferences.get(name);
    }
}
