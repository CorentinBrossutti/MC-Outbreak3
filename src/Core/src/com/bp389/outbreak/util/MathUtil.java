package com.bp389.outbreak.util;

import net.minecraft.server.v1_8_R3.Vector3f;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.util.EulerAngle;

import java.util.Random;

public final class MathUtil {

    /**
     *
     * @param includeMax Whether to include the max number in the randomization
     * @return A random integer between min (included) and max (excluded / included)
     */
    public static int randomBetween(Random rng, int min, int max, boolean includeMax) {
        if(max == min)
            return min;

        return rng.nextInt(max - min) + min + (includeMax ? 1 : 0);
    }

    /**
     *
     * @param from
     *            L'angle en radian
     * @return La valeur de l'angle convertie en degrés
     */
    public static double radianToDegrees(final double from) {
        return 180 * from / Math.PI;
    }

    public static double degreesToRadians(final double from) {
        return Math.PI * from / 180;
    }

    public static Vector3f eulerToNMS(EulerAngle from) {
        return new Vector3f((float)Math.toDegrees(from.getX()), (float)Math.toDegrees(from.getY()), (float)Math.toDegrees(from.getZ()));
    }

    public static final class BlockFacing {

        public static final BlockFace[] axis = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
        public static final BlockFace[] radial = { BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST };

        public static BlockFace locationToFace(Location location) {
            return yawToFace(location.getYaw());
        }

        public static BlockFace yawToFace(float yaw) {
            return yawToFace(yaw, true);
        }

        public static BlockFace yawToFace(float yaw, boolean useSubCardinalDirections) {
            if (useSubCardinalDirections) {
                return radial[Math.round(yaw / 45f) & 0x7];
            } else {
                return axis[Math.round(yaw / 90f) & 0x3];
            }
        }
    }
}
