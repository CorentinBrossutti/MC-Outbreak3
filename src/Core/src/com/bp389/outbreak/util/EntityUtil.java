package com.bp389.outbreak.util;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.IEntitySelector;

import java.util.List;

public final class EntityUtil {

    /**
     * Returns a list of the living entities around another in a given radius
     */
    public static <T extends Entity> List<T> getLivingNearby(Entity source, double radius) {
        return (List<T>) getNearby(source, EntityLiving.class, radius);
    }

    /**
     * NMS-optimized way of getting entities near another
     * @param source Source entity
     * @param radius Radius
     * @param target Target entity class. Use Entity.class for all
     */
    public static <T extends Entity> List<T> getNearby(Entity source, Class<? extends T> target, double radius) {
        return getNearby(source, target, radius, null);
    }

    /**
     * NMS-optimized way of getting entities near another
     * @param source Source entity
     * @param radius Radius
     * @param target Target entity class. Use Entity.class for all
     * @param selector Can be null. Additional predicate to filter
     */
    public static <T extends Entity> List<T> getNearby(Entity source, Class<? extends T> target, double radius, Predicate<T> selector) {
        return source.world.a(target, source.getBoundingBox().grow(radius, radius, radius), selector == null ? IEntitySelector.d : Predicates.and(IEntitySelector.d, selector));
    }
}
