/*
 * Copyright © Geekazodium 2023.
 * This file is released under GPLv3. See LICENSE for full license details.
 */
package com.geekazodium.splashdown;

import com.geekazodium.splashdown.commands.ItemCommandExecutor;
import java.util.logging.Logger;

import com.geekazodium.splashdown.items.BubbleGunHandler;
import com.geekazodium.splashdown.items.CustomItemHandlerRegistry;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class SplashDown extends JavaPlugin implements Listener {
    private static SplashDown instance;
    public static final Logger LOGGER = Bukkit.getServer().getLogger();
    private PlayerHandlerInstantiator playerHandlerInstantiator;

    public CustomItemHandlerRegistry customItemHandlerRegistry;

    @Override
    public void onEnable() {
        setInstance(this);
        CollisionBox.debugRenderEnabled = true;
        playerHandlerInstantiator = new PlayerHandlerInstantiator();
        Bukkit.getPluginManager().registerEvents(playerHandlerInstantiator, this);
        initWeaponItemHandlerRegistry();

        getServer().getPluginCommand("item").setExecutor(new ItemCommandExecutor());
    }

    private void initWeaponItemHandlerRegistry() {
        customItemHandlerRegistry = new CustomItemHandlerRegistry();
        customItemHandlerRegistry.registerItem(new BubbleGunHandler());
        customItemHandlerRegistry.initIdStrings();
    }

    private void setInstance(SplashDown instance) {
        SplashDown.instance = instance;
    }

    public static SplashDown getInstance() {
        return instance;
    }
}
