package com.geekazodium.splashdown.util;

import java.util.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.util.Vector;

public class RandomUtil {
    public static <T> T randomPickFromList(List<T> list, Random random) {
        return list.get(random.nextInt(list.size()));
    }

    public static <T> T randomPickFromList(Collection<T> list, Random random) {
        return randomPickFromList(list.stream().toList(), random);
    }

    public static int nextIntRandomSign(int bound, Random random) {
        return random.nextInt(bound) * (random.nextBoolean() ? 1 : -1);
    }

    public static double nextDoubleRandomSign(double bound, Random random) {
        return random.nextDouble(bound) * (random.nextBoolean() ? 1d : -1d);
    }

    public static <T> T randomPickFromWeighted(Map<T, Float> map, Random random) {
        float r = random.nextFloat();
        float n = 0;
        for (Map.Entry<T, Float> entry : map.entrySet()) {
            n += entry.getValue();
            if (r < n) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static Inventory generateChestLoot() { // TODO add loot tables and stuff
        return null;
    }

    public static Vector randomVec(double v, Random random) {
        return new Vector(
                nextDoubleRandomSign(v / 2d, random),
                nextDoubleRandomSign(v / 2d, random),
                nextDoubleRandomSign(v / 2d, random));
    }

    public static double getBoxMullerNormLength(Random random) {
        return Math.sqrt(-2 * Math.log(random.nextDouble()));
    }

    public static double getBoxMullerNormRotation(Random random) {
        return random.nextDouble() * Math.PI * 2;
    }
}
