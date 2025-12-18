package net.fabricmc.fabric.dimension.api.v1.util;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("all")
public class MathUtils {
    public static double roundToDecimal(double n, double point) {
        return point * Math.round(n / point);
    }

    public static int getRandomInt(int from, int to) {
        return ThreadLocalRandom.current().nextInt(from, to + 1);
    }

    public static double getRandomDouble(double from, double to) {
        return ThreadLocalRandom.current().nextDouble(from, to);
    }

    public static boolean getRandomBoolean() {
        return ThreadLocalRandom.current().nextBoolean();
    }
}
