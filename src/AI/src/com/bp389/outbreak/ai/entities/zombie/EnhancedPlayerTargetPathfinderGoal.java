package com.bp389.outbreak.ai.entities.zombie;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import net.minecraft.server.v1_8_R3.*;

import java.util.List;

public class EnhancedPlayerTargetPathfinderGoal extends PathfinderGoalNearestAttackableTarget<EntityHuman> {
    protected int unseenTicks;
    protected EnhancedZombie zombie;
    protected Predicate<EntityHuman> selector;

    public EnhancedPlayerTargetPathfinderGoal(EnhancedZombie zombie, boolean need_sight) {
        // integer = targetChance (0 = always)
        // first bool means whether sight should be checked (the zombie will stop chasing if he loses sight for more than a set amount of time)
        // second bool means it will only target easily reachable eligible entities
        super(zombie, EntityHuman.class, 0, need_sight, false, null);
        this.zombie = zombie;
        selector = human -> {
            double distanceSq = zombie.getBukkitLocation().distanceSquared(human.getBukkitEntity().getLocation());

            //calculation of camo and adding to total distance
            int camoVal = 0;
            if(human.getEquipment(3) != null)
                if(human.getEquipment(3).getItem() == Items.CHAINMAIL_CHESTPLATE)
                    camoVal += 15;
            if(human.getEquipment(1) != null)
                if(human.getEquipment(1).getItem() == Items.CHAINMAIL_BOOTS)
                    camoVal += 25;
            if(human.getEquipment(2) != null)
                if(human.getEquipment(2).getItem() == Items.CHAINMAIL_LEGGINGS)
                    camoVal += 25;
            if(human.getEquipment(4) != null)
                if(human.getEquipment(4).getItem() == Items.CHAINMAIL_HELMET)
                    camoVal += 10;
            if(camoVal != 0)
                distanceSq += distanceSq / 100 * camoVal;

            if(distanceSq < Math.pow(EnhancedZombie.Config.Detection.SNEAK, 2))
                return true;
            else if(distanceSq < Math.pow(EnhancedZombie.Config.Detection.NORMAL, 2) && !human.isSneaking())
                return true;
            else if(distanceSq < Math.pow(EnhancedZombie.Config.Detection.SPRINT, 2) && human.isSprinting())
                return true;
            return false;
        };
    }

    @Override
    public boolean a() {
        //copy pasted from superclass. Target chance removed since not used.
        // Gets the nearest eligible player
        double d0 = this.f();
        List list = this.e.world.a(this.a, this.e.getBoundingBox().grow(d0, 4.0D, d0),
                                   Predicates.and(IEntitySelector.d, selector));
        list.sort(this.b);
        if (list.isEmpty())
            return false;
        else {
            this.d = (EntityLiving)list.get(0);
            return true;
        }
    }

    // method is used to know if the pathfinder should continue executing
    @Override
    public boolean b() {
        // Copy pasted from PathfinderGoalTarget
        // In order to allow customization of unseen ticks
        EntityLiving var1 = this.e.getGoalTarget();
        if (var1 == null) {
            return false;
        } else if (!var1.isAlive()) {
            return false;
        } else {
            ScoreboardTeamBase var2 = this.e.getScoreboardTeam();
            ScoreboardTeamBase var3 = var1.getScoreboardTeam();
            if (var2 != null && var3 == var2) {
                return false;
            } else {
                double var4 = this.f();
                if (this.e.h(var1) > var4 * var4) {
                    return false;
                } else {
                    if (this.f) {
                        if (this.e.getEntitySenses().a(var1)) {
                            this.unseenTicks = 0;
                        } else if (++this.unseenTicks > zombie.getArchetype().persistence * 20) {
                            return false;
                        }
                    }

                    return !(var1 instanceof EntityHuman) || !((EntityHuman)var1).abilities.isInvulnerable;
                }
            }
        }
    }

    //when the pathfinder executes itself
    @Override
    public void c() {
        super.c();
        this.unseenTicks = 0;
    }
}
