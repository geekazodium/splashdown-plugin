package com.geekazodium.splashdown;

import com.geekazodium.splashdown.util.ParticleUtil;
import it.unimi.dsi.fastutil.Pair;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
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
    public Quaterniond finalRotation;
    public Vector pos = new Vector(0, 0, 0);
    public Vector offset;
    public Vector center;
    public Vector size;
    public Quaterniond rotation;
    private byte copyRotationType = 3;
    private final CollisionUtil.OBB collider;
    private Vector effectiveOffset;

    public CollisionBox(Vector offset,
                        Vector center,
                        Vector size,
                        Quaterniond rotation) {
        this.offset = offset;
        this.center = center;
        this.size = size;
        this.rotation = rotation;
        collider = new CollisionUtil.OBB(this, rotation);
    }

    public CollisionBox(Vector offset,
                        Vector center,
                        Vector size,
                        Quaterniond rotation,
                        byte copyRotationType) {
        this(offset, center, size, rotation);
        this.copyRotationType = copyRotationType;
    }

    public static CollisionBox fromBoundingBox(BoundingBox boundingBox) {
        return new CollisionBox(
                new Vector(0,0,0),
                new Vector(0.5,0.5,0.5),
                new Vector(boundingBox.getWidthX(),boundingBox.getHeight(), boundingBox.getWidthZ()),
            quaternionIdentity(),
                (byte)0
        );
    }

    public boolean isColliding(CollisionBox otherHitbox) {
        return CollisionUtil.getCollision(collider, otherHitbox.getCollider());
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
                        this.copyRotationType==1||this.copyRotationType==3?(float) toRadians(-yaw):0,
                        this.copyRotationType==2||this.copyRotationType==3?(float) toRadians(pitch):0,
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
        for (Pair<Vector, Vector> pair: this.getOutline()) {
            Vector p1 = pair.left();
            Vector p2 = pair.right();
            for (Player player:world.getPlayers()) {
                ParticleUtil.line(
                        player,
                        Particle.REDSTONE,
                        1,
                        new Particle.DustOptions(Color.RED,1),
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
