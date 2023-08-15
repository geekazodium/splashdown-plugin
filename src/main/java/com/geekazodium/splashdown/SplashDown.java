/*
 * Copyright © Geekazodium 2023.
 * This file is released under GPLv3. See LICENSE for full license details.
 */
package com.geekazodium.splashdown;

import com.geekazodium.splashdown.commands.ItemCommandExecutor;
import com.geekazodium.splashdown.entities.BubbleEntity;
import com.geekazodium.splashdown.items.BubbleGunHandler;
import com.geekazodium.splashdown.items.CustomItemHandlerRegistry;
import com.geekazodium.splashdown.items.ability.BubbleDashAbilityHandler;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
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
        Bukkit.getPluginManager().registerEvents(this, this);

        initCustomItemRegistry();

        getServer().getPluginCommand("item").setExecutor(new ItemCommandExecutor());
    }

    private void initCustomItemRegistry() {
        customItemHandlerRegistry = new CustomItemHandlerRegistry();
        customItemHandlerRegistry.registerItem(new BubbleGunHandler());
        customItemHandlerRegistry.registerItem(new BubbleDashAbilityHandler());
        customItemHandlerRegistry.initIdStrings();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getHandlers().unregister(((Listener) this));
        constructCustomEntityDisguises(event.getPlayer().getWorld());
    }

    public void constructCustomEntityDisguises(World world) {
        BubbleEntity.constructDisguise(new Location(world, 0, 0, 0));
    }

    private void setInstance(SplashDown instance) {
        SplashDown.instance = instance;
    }

    public static SplashDown getInstance() {
        return instance;
    }
}
