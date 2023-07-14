package com.geekazodium.splashdown;

import com.geekazodium.splashdown.commands.ItemCommandExecutor;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class SplashDown extends JavaPlugin implements Listener {
    private static SplashDown instance;
    public static final Logger LOGGER = Bukkit.getServer().getLogger();
    private PlayerHandlerInstantiator playerHandlerInstantiator;

    public WeaponItemHandlerRegistry weaponItemHandlerRegistry;

    @Override
    public void onEnable() {
        setInstance(this);
        CollisionBox.debugRenderEnabled = true;
        playerHandlerInstantiator = new PlayerHandlerInstantiator();
        Bukkit.getPluginManager().registerEvents(playerHandlerInstantiator,this);
        initWeaponItemHandlerRegistry();

        getServer().getPluginCommand("item").setExecutor(new ItemCommandExecutor());
    }

    private void initWeaponItemHandlerRegistry() {
        weaponItemHandlerRegistry = new WeaponItemHandlerRegistry();
        weaponItemHandlerRegistry.registerItem(new BubbleGunHandler());
        weaponItemHandlerRegistry.initIdStrings();
    }

    private void setInstance(SplashDown instance) {
        SplashDown.instance = instance;
    }

    public static SplashDown getInstance(){
        return instance;
    }
}
