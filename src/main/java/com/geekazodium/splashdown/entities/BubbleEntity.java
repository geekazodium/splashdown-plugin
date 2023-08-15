/*
 * Copyright © Geekazodium 2023.
 * This file is released under GPLv3. See LICENSE for full license details.
 */
package com.geekazodium.splashdown.entities;

import java.lang.reflect.Field;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class BubbleEntity extends Snowball {
    private static Disguise disguise;
    private static final Field intervalField;

    static {
        intervalField = getReflectedIntervalField();
    }

    public BubbleEntity(Level world, double x, double y, double z) {
        super(world, x, y, z);
    }

    public BubbleEntity(Location location, Vector deltaMovement, LivingEntity shooter) {
        this(((CraftWorld) location.getWorld()).getHandle().getLevel(), location.x(), location.y(), location.z());
        //        World world = location.getWorld();
        //        if (disguise == null) {
        //            constructDisguise(location, world);
        //        }
        DisguiseAPI.disguiseEntity(this.getBukkitEntity(), disguise);
        this.invulnerableTime = 5;
        this.setOwner(((CraftEntity) shooter).getHandle());
        this.setDeltaMovement(deltaMovement.getX(), deltaMovement.getY(), deltaMovement.getZ());
    }

    public static void constructDisguise(Location location) {
        World world = location.getWorld();
        Item displayEntity = (Item) world.spawnEntity(location.add(0, 512, 0), EntityType.DROPPED_ITEM);
        displayEntity.setItemStack(new ItemStack(Material.BLUE_STAINED_GLASS));
        displayEntity.setCanPlayerPickup(false);
        displayEntity.setInvulnerable(true);
        disguise = DisguiseAPI.constructDisguise(displayEntity, true, true);
        displayEntity.remove();
    }

    private boolean modifiedUpdateInterval = false;

    @Override
    public void tick() {
        super.tick();
        World level = this.getBukkitEntity().getWorld();
        level.spawnParticle(
                Particle.DUST_COLOR_TRANSITION,
                this.getX(),
                this.getY(),
                this.getZ(),
                1,
                new Particle.DustTransition(Color.BLUE, Color.BLUE, 0.8f));
        if (tracker != null && !modifiedUpdateInterval) modifyUpdateInterval();
        if (this.invulnerableTime > 0) this.invulnerableTime--;
    }

    private void modifyUpdateInterval() {
        Field intervalField = getIntervalField();
        try {
            intervalField.setInt(this.tracker.serverEntity, 1);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        modifiedUpdateInterval = true;
    }

    @NotNull
    private static Field getReflectedIntervalField() {
        Field[] declaredFields = ServerEntity.class.getDeclaredFields();
        int seenFields = 0;
        int modifyOrdinal = 4;
        Field intervalField = null;
        for (Field declaredField : declaredFields) {
            if (modifyOrdinal == seenFields) {
                intervalField = declaredField;
                intervalField.setAccessible(true);
                break;
            }
            seenFields++;
        }
        assert intervalField != null;
        return intervalField;
    }

    public Field getIntervalField() {
        return intervalField;
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        CraftEntity bukkitEntity = entity.getBukkitEntity();
        if (bukkitEntity instanceof LivingEntity livingEntity) {
            livingEntity.damage(10);
        }
    }

    @Override
    public void preOnHit(HitResult preHitResult) {
        HitResult.Type type = preHitResult.getType();
        if (type == HitResult.Type.ENTITY) {
            if (this.getOwner() == (((EntityHitResult) preHitResult).getEntity())) {
                if (this.invulnerableTime > 0) return;
            }
        }
        super.preOnHit(preHitResult);
    }
}
