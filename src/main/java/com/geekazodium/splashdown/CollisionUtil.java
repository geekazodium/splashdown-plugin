/*
 * Copyright © Geekazodium 2023.
 * This file is released under GPLv3. See LICENSE for full license details.
 */
package com.geekazodium.splashdown;

import org.bukkit.util.Vector;
import org.joml.Quaterniond;

public class CollisionUtil {
    public static Vector[] getRotationMatrix(Quaterniond q) {
        double w = q.w;
        double x = q.x;
        double y = q.y;
        double z = q.z;
        double n = w * w + x * x + y * y + z * z;
        double s = n == 0 ? 0 : 2 / n;
        return new Vector[] {
            new Vector(1 - s * (y * y + z * z), s * (x * y - w * z), s * (x * z + w * y)),
            new Vector(s * (x * y + w * z), 1 - s * (x * x + z * z), s * (y * z - w * x)),
            new Vector(s * (x * z - w * y), s * (y * z + w * x), 1 - s * (x * x + y * y))
        };
    }

    public static Vector applyRotationMatrix(Vector vec, Vector[] m) {
        return new Vector(
                vec.getX() * m[0].getX() + vec.getY() * m[0].getY() + vec.getZ() * m[0].getZ(),
                vec.getX() * m[1].getX() + vec.getY() * m[1].getY() + vec.getZ() * m[1].getZ(),
                vec.getX() * m[2].getX() + vec.getY() * m[2].getY() + vec.getZ() * m[2].getZ());
    }

    public static boolean getSeparatingPlane(Vector RPos, Vector Plane, OBB box1, OBB box2) {
        return (Math.abs(RPos.clone().dot(Plane))
                > (Math.abs((box1.AxisX.clone().multiply(box1.Half_size.getX())).dot(Plane))
                        + Math.abs((box1.AxisY.clone().multiply(box1.Half_size.getY())).dot(Plane))
                        + Math.abs((box1.AxisZ.clone().multiply(box1.Half_size.getZ())).dot(Plane))
                        + Math.abs((box2.AxisX.clone().multiply(box2.Half_size.getX())).dot(Plane))
                        + Math.abs((box2.AxisY.clone().multiply(box2.Half_size.getY())).dot(Plane))
                        + Math.abs((box2.AxisZ.clone().multiply(box2.Half_size.getZ())).dot(Plane))));
    }

    public static boolean getCollision(OBB box1, OBB box2) {
        Vector RPos = box2.Pos.clone().subtract(box1.Pos);
        return !(getSeparatingPlane(RPos, box1.AxisX, box1, box2)
                || getSeparatingPlane(RPos, box1.AxisY, box1, box2)
                || getSeparatingPlane(RPos, box1.AxisZ, box1, box2)
                || getSeparatingPlane(RPos, box2.AxisX, box1, box2)
                || getSeparatingPlane(RPos, box2.AxisY, box1, box2)
                || getSeparatingPlane(RPos, box2.AxisZ, box1, box2)
                || getSeparatingPlane(RPos, box1.AxisX.clone().crossProduct(box2.AxisX), box1, box2)
                || getSeparatingPlane(RPos, box1.AxisX.clone().crossProduct(box2.AxisY), box1, box2)
                || getSeparatingPlane(RPos, box1.AxisX.clone().crossProduct(box2.AxisZ), box1, box2)
                || getSeparatingPlane(RPos, box1.AxisY.clone().crossProduct(box2.AxisX), box1, box2)
                || getSeparatingPlane(RPos, box1.AxisY.clone().crossProduct(box2.AxisY), box1, box2)
                || getSeparatingPlane(RPos, box1.AxisY.clone().crossProduct(box2.AxisZ), box1, box2)
                || getSeparatingPlane(RPos, box1.AxisZ.clone().crossProduct(box2.AxisX), box1, box2)
                || getSeparatingPlane(RPos, box1.AxisZ.clone().crossProduct(box2.AxisY), box1, box2)
                || getSeparatingPlane(RPos, box1.AxisZ.clone().crossProduct(box2.AxisZ), box1, box2));
    }

    public static class OBB {
        public Vector Pos, AxisX, AxisY, AxisZ, Half_size;

        public OBB(CollisionBox hitbox, Quaterniond rotation) {
            updatePos(hitbox);
            updateRotationMatrix(rotation);
        }

        public void updatePos(CollisionBox hitbox) {
            this.Half_size = hitbox.size.clone().multiply(0.5);
            this.Pos = hitbox.centerPoint();
        }

        public void updateRotationMatrix(Quaterniond q) {
            Vector[] tmp = getRotationMatrix(q);
            setRotationAxis(tmp);
        }

        public void setRotationAxis(Vector[] m) {
            AxisX = new Vector(m[0].getX(), m[1].getX(), m[2].getX());
            AxisY = new Vector(m[0].getY(), m[1].getY(), m[2].getY());
            AxisZ = new Vector(m[0].getZ(), m[1].getZ(), m[2].getZ());
        }
    }
}
