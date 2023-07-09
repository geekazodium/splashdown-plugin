package com.geekazodium.splashdown;

import com.geekazodium.splashdown.util.ParticleUtil;
import it.unimi.dsi.fastutil.Pair;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaterniond;

import java.util.ArrayList;
import java.util.List;

import static com.geekazodium.splashdown.CollisionUtil.applyRotationMatrix;
import static com.geekazodium.splashdown.CollisionUtil.getRotationMatrix;
import static java.lang.Math.toRadians;

public class CollisionBox {
    public static final byte COPY_YAW = 1;
    public static final byte COPY_PITCH = 1 << 1;
    public static boolean debugRenderEnabled = false;
    public Quaterniond finalRotation;
    public Vector pos = new Vector(0, 0, 0);
    public Vector offset;
    public Vector center;
    public Vector size;
    public Quaterniond rotation;
    private final byte copyRotationType;
    private final CollisionUtil.OBB collider;
    private Vector effectiveOffset;
    private Color debugColor;

    public CollisionBox(Vector offset,
                        Vector center,
                        Vector size,
                        Quaterniond rotation) {
        this(offset,center,size,rotation, (byte) (COPY_YAW|COPY_PITCH));
    }

    public CollisionBox(Vector offset,
                        Vector center,
                        Vector size,
                        Quaterniond rotation,
                        byte copyRotationType) {
        this(offset, center, size, rotation, Color.RED, copyRotationType);
    }

    public CollisionBox(Vector offset,
                        Vector center,
                        Vector size,
                        Quaterniond rotation,
                        Color debugColor,
                        byte copyRotationType) {
        this.offset = offset;
        this.center = center;
        this.size = size;
        this.rotation = rotation;
        this.debugColor = debugColor;
        this.collider = new CollisionUtil.OBB(this, rotation);
        this.copyRotationType = copyRotationType;
    }

    public static CollisionBox fromBoundingBox(BoundingBox boundingBox) {
        return new CollisionBox(
                new Vector(0,0,0),
                new Vector(0.5,1,0.5),
                new Vector(boundingBox.getWidthX(),boundingBox.getHeight(), boundingBox.getWidthZ()),
                quaternionIdentity(),
                Color.GREEN,
                (byte)0
        );
    }

    public boolean isColliding(CollisionBox otherHitbox) {
        return CollisionUtil.getCollision(collider, otherHitbox.getCollider());
    }

    public boolean isColliding(Entity entity){
        BoundingBox entityBoundingBox = entity.getBoundingBox();
        CollisionBox entityHurtBox = CollisionBox.fromBoundingBox(entityBoundingBox);
        entityHurtBox.updateCollider(entity.getLocation());
        if(debugRenderEnabled == true)entityHurtBox.renderOutline(entity.getWorld());
        return CollisionUtil.getCollision(collider,entityHurtBox.getCollider());
    }

    public boolean isCollidingSkull(Entity entity){
        BoundingBox entityBoundingBox = entity.getBoundingBox();
        double eyeHeight = ((LivingEntity) entity).getEyeHeight();
        BoundingBox entityHeadBox = entityBoundingBox.clone().expand(0,(entityBoundingBox.getHeight()-eyeHeight*2)/2,0);
        CollisionBox entityHurtBox = CollisionBox.fromBoundingBox(entityHeadBox);
        entityHurtBox.debugColor = Color.YELLOW;
        entityHurtBox.updateCollider(entity.getLocation().add(0, eyeHeight*2-entityBoundingBox.getHeight(),0));
        if(debugRenderEnabled == true)entityHurtBox.renderOutline(entity.getWorld());
        return CollisionUtil.getCollision(collider,entityHurtBox.getCollider());
    }

    public CollisionUtil.OBB getCollider() {
        return this.collider;
    }

    private void updateCollider(Vector pos, Quaterniond rotation) {
        updateRotation(rotation);
        updateEffectiveOffset(rotation);
        updatePos(pos);
    }

    public void updatePos(Vector pos) {
        this.pos = pos.clone();
        this.collider.updatePos(this);
    }

    public void updateEffectiveOffset(Quaterniond rotation) {
        Vector[] m = getRotationMatrix(this.rotation);
        Vector o = offset.clone().add(applyRotationMatrix(new Vector(0.5, 0.5, 0.5)
                .subtract(center)
                .multiply(size), m));
        m = getRotationMatrix(rotation);
        this.effectiveOffset = applyRotationMatrix(o, m);
    }

    public void updateCollider(Vector pos,float pitch,float yaw){
        this.updateCollider(
                pos,
                quaternionIdentity().rotationYXZ(
                        ((this.copyRotationType & COPY_YAW) != 0) ? (float) toRadians(-yaw) : 0,
                        ((this.copyRotationType & COPY_PITCH) != 0) ?(float) toRadians(pitch) : 0,
                        0
                )
        );
    }

    @NotNull
    private static Quaterniond quaternionIdentity() {
        return new Quaterniond(1, 0, 0, 0);
    }

    public void updateRotation(Quaterniond rotation) {
        this.finalRotation = new Quaterniond(rotation);
        this.finalRotation.mul(this.rotation);
        this.collider.setRotationAxis(getRotationMatrix(finalRotation));
    }

    public Vector centerPoint() {
        if (this.effectiveOffset == null) {
            this.effectiveOffset = this.offset.clone();
        }
        return effectiveOffset.clone().add(pos);
    }

    public List<Pair<Vector,Vector>> getOutline() {
        Vector pos = centerPoint();
        Vector[] points = new Vector[]{
                new Vector(0, 0, 0), new Vector(1, 0, 0),
                new Vector(0, 1, 0), new Vector(1, 1, 0),
                new Vector(0, 0, 1), new Vector(1, 0, 1),
                new Vector(0, 1, 1), new Vector(1, 1, 1)
        };
        Pair<Vector,Vector>[] lines = new Pair[]{//ToDo add line class to store data about each line of hitbox outline
                //x
                Pair.of(points[0],points[1]),
                Pair.of(points[2],points[3]),
                Pair.of(points[4],points[5]),
                Pair.of(points[6],points[7]),
                //y
                Pair.of(points[0],points[2]),
                Pair.of(points[1],points[3]),
                Pair.of(points[4],points[6]),
                Pair.of(points[5],points[7]),
                //z
                Pair.of(points[0],points[4]),
                Pair.of(points[1],points[5]),
                Pair.of(points[2],points[6]),
                Pair.of(points[3],points[7])
        };
        List<Pair<Vector,Vector>> r = new ArrayList<>();
        if (finalRotation == null) {
            finalRotation = quaternionIdentity();
        }
        for (Pair<Vector,Vector> line: lines) {
            Vector point = line.left();
            Vector vec = applyRotationMatrix(new Vector(
                    (point.getX() - 0.5d) * size.getX(),
                    (point.getY() - 0.5d) * size.getY(),
                    (point.getZ() - 0.5d) * size.getZ()
            ), getRotationMatrix(this.finalRotation)).clone().add(pos);
            Vector point2 = line.right();
            Vector vec2 = applyRotationMatrix(new Vector(
                    (point2.getX() - 0.5d) * size.getX(),
                    (point2.getY() - 0.5d) * size.getY(),
                    (point2.getZ() - 0.5d) * size.getZ()
            ), getRotationMatrix(this.finalRotation)).clone().add(pos);
            r.add(Pair.of(vec,vec2));
        }
        return r;
    }

    public void renderOutline(World world){
        if(debugRenderEnabled == false)return;
        for (Pair<Vector, Vector> pair: this.getOutline()) {
            Vector p1 = pair.left();
            Vector p2 = pair.right();
            for (Player player:world.getPlayers()) {
                ParticleUtil.line(
                        player,
                        Particle.DUST_COLOR_TRANSITION,
                        1,
                        new Particle.DustTransition(debugColor,debugColor,0.6f),
                        getLocationForVec(world, p1),
                        getLocationForVec(world, p2),
                        (int)Math.max(2,p1.distance(p2)*5),
                        0,0,0,
                        0
                );
            }
        }
    }
    private static Location getLocationForVec(World world,Vector vector){
        return new Location(world, vector.getX(), vector.getY(), vector.getZ());
    }

    public void updateCollider(Location location) {
        updateCollider(
                new Vector(location.getX(),location.getY(),location.getZ()),
                location.getPitch(),
                location.getYaw()
        );
    }
}
