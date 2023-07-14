package com.geekazodium.splashdown;

import com.geekazodium.splashdown.entities.BubbleEntity;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class BubbleGunHandler implements WeaponItemHandler{
    @Override
    public String getIdString() {
        return "bubble_gun";
    }

    @Override
    public String getCanonicalName() {
        return "BubbleGun";
    }

    @Override
    public void onRightClick(PlayerHandler playerHandler, PlayerInteractEvent event) {
        Player player = playerHandler.getPlayer();
        ((CraftWorld) player.getWorld()).addEntityToWorld(
            new BubbleEntity(player.getEyeLocation(),player,2),
            CreatureSpawnEvent.SpawnReason.EGG
        );
        event.setCancelled(true);
    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(Material.IRON_HOE);
    }
}
