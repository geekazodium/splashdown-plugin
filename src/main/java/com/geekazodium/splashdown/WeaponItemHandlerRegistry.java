/*
 * Copyright © Geekazodium 2023.
 * This file is released under GPLv3. See LICENSE for full license details.
 */
package com.geekazodium.splashdown;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.NamespacedKey;

public class WeaponItemHandlerRegistry {
    public static final NamespacedKey ITEM_ID_KEY = new NamespacedKey(SplashDown.getInstance(), "item_id");
    private final List<WeaponItemHandler> weaponItemHandlers;
    private Map<String, Integer> stringIdReferences;

    public WeaponItemHandlerRegistry() {
        weaponItemHandlers = new ArrayList<>();
    }

    public void registerItem(WeaponItemHandler itemHandler) {
        this.weaponItemHandlers.add(itemHandler);
    }

    public void initIdStrings() {
        this.stringIdReferences = new HashMap<>();
        List<WeaponItemHandler> itemHandlers = this.weaponItemHandlers;
        for (int i = 0; i < itemHandlers.size(); i++) {
            WeaponItemHandler weaponItemHandler = itemHandlers.get(i);
            stringIdReferences.put(weaponItemHandler.getIdString(), i);
            stringIdReferences.put(weaponItemHandler.getCanonicalName(), i);
        }
    }

    public WeaponItemHandler getWeaponHandler(int id) {
        return weaponItemHandlers.get(id);
    }

    public WeaponItemHandler getWeaponHandlerFromString(String name) {
        return getWeaponHandler(getHandlerId(name));
    }

    public int getHandlerId(String name) {
        return this.stringIdReferences.get(name);
    }
}
