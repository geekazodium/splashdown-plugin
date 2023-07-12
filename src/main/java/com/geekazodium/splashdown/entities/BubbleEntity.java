package com.geekazodium.splashdown.entities;

import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public class BubbleEntity extends Snowball {

    public BubbleEntity(Level world, double x, double y, double z) {
        super(world, x, y, z);
    }

    public BubbleEntity(Location location, LivingEntity shooter, double speed) {
        this(
            ((CraftWorld) location.getWorld()).getHandle().getLevel(),
            location.x(),
            location.y(),
            location.z()
        );
        this.invulnerableTime = 5;
        this.setOwner(((CraftEntity) shooter).getHandle());
        Vector direction = location.getDirection();
        this.setDeltaMovement(direction.getX()*speed, direction.getY()*speed, direction.getZ()*speed);
    }

    @Override
    public void tick() {
        super.tick();
        if(this.invulnerableTime>0)this.invulnerableTime--;
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        CraftEntity bukkitEntity = entity.getBukkitEntity();
        if(bukkitEntity instanceof LivingEntity livingEntity){
            livingEntity.damage(10);
        }
    }

    @Override
    public void preOnHit(HitResult preHitResult) {
        HitResult.Type type = preHitResult.getType();
        if(type == HitResult.Type.ENTITY){
            if(this.getOwner() == (((EntityHitResult) preHitResult).getEntity())) {
                if (this.invulnerableTime > 0) return;
            }
        }
        super.preOnHit(preHitResult);
    }
}
