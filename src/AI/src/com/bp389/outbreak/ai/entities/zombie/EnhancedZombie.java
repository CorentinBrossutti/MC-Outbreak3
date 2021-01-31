package com.bp389.outbreak.ai.entities.zombie;

import com.bp389.outbreak.ai.VirtualSpawner;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Base class for new zombies
 */
public class EnhancedZombie extends EntityZombie {
    private static final double BASE_SPEED = 0.23000000417232513D;
    public static JavaPlugin PLUGIN;

    private boolean fromPlayer;
    private boolean hasMoveOrder;
    private VirtualSpawner attachedSpawner;
    private BukkitRunnable terminateMoveOrderRunnable;
    private ZombieArchetype archetype;


    public EnhancedZombie(World world) {
        super(world);
    }

    /**
     *
     * @return Whether this zombie has been revived from a player
     */
    public boolean isFromPlayer() {
        return fromPlayer;
    }

    /**
     *
     * @return The archetype of this zombie
     */
    public ZombieArchetype getArchetype() {
        return archetype;
    }

    /**
     *
     * @return The attached spawner of this zombie. Can be null.
     */
    public VirtualSpawner getAttachedSpawner() {
        return attachedSpawner;
    }

    /**
     * Moves the zombie to a given target location with default speed.
     * If the zombie already has a move order, then does nothing
     */
    public void move(Location target){
        move(target, 1);
    }

    /**
     * Moves the zombie to a given target with custom speed
     * If the zombie already has a move order, then does nothing
     * @param speedMult Speed multiplier compared to outbreak's standard (archetype ignored)
     */
    public void move(Location target, double speedMult){
        move(target, speedMult, false);
    }

    /**
     * Moves the zombie to a given target with custom speed
     * @param speedMult Speed multiplier compared to outbreak's standard (archetype ignored)
     * @param overruleCurrent Whether the override any currently running move order
     */
    public void move(Location target, double speedMult, boolean overruleCurrent){
        if(hasMoveOrder && !overruleCurrent)
            return;

        getNavigation().a(target.getX(), target.getY(), target.getZ(), BASE_SPEED * Config.Characteristics.SPEED * speedMult * 2.5);
        hasMoveOrder = true;
        if (terminateMoveOrderRunnable != null)
            terminateMoveOrderRunnable.cancel();
        terminateMoveOrderRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                hasMoveOrder = false;
            }
        };
        //Consider the move order done by calculating the number of ticks to move to the destination with a 15% margin
        terminateMoveOrderRunnable.runTaskLater(PLUGIN, Double.valueOf(
                (target.distance(getBukkitLocation()) / speedMult) * 1.15).longValue());
    }

    /**
     *
     * @return Whether this zombie is standing under sunlight
     */
    protected boolean isUnderSunlight(){
        //copy pasted from superclass, allows to know if the zombie is under the sun
        float f = c(1.0f);
        BlockPosition blockposition = new BlockPosition(this.locX, (double)Math.round(this.locY), this.locZ);
        return f > 0.5f && random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && this.world.i(blockposition);
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();

        archetype = ZombieArchetype.randomWeighted();

        // archetypes
        getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(Config.Detection.SPRINT * 1.5);
        getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(
                Config.Characteristics.SPEED * BASE_SPEED * archetype.speedmult);
        getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(archetype.damage);
        getAttributeInstance(GenericAttributes.maxHealth).setValue(archetype.health);

        setVillager(archetype.villager);
    }

    @Override
    public void die(DamageSource damagesource) {
        super.die(damagesource);

        if(fromPlayer && getEquipment(0) != null)
            //method a(itemstack, float) drops an item from this entity
            //the float is Y offset of the entity
            this.a(getEquipment(0), 0);

        if(attachedSpawner != null)
            attachedSpawner.removeZombie(this, true);
    }

    // TODO: 16/08/2019 DROPS
    @Override
    protected void getRareDrop() {
        switch(random.nextInt(5)) {
            case 0:
                a(Items.IRON_INGOT, 1);
                break;
            case 1:
                a(Items.CARROT, 1);
                break;
            case 2:
                a(Items.POTATO, 1);
                break;
            case 3:
                a(Items.REDSTONE, 1);
                break;
            case 4:
                a(Items.SUGAR, 1);
        }
    }

    @Override
    public void setBaby(final boolean arg0) {}

    @Override
    public boolean isBaby() {
        return false;
    }

    @Override
    public void setOnFire(final int i) {
        if(archetype.fearsSunlight)
            super.setOnFire(i);
    }

    @Override
    public int getExpReward() {
        return 0;
    }

    // handles pathfinding
    @Override
    protected void n() {
        targetSelector.a(2, new EnhancedPlayerTargetPathfinderGoal(this, Config.Detection.NEED_SIGHT));
        goalSelector.a(4, new PathfinderGoalOpenDoor(this, true));
    }

    //canSpawn method
    @Override
    public boolean bR() {
        attachedSpawner = VirtualSpawner.getNearbySpawner(getBukkitLocation());
        if(attachedSpawner == null && random.nextInt(Config.Spawn.ATTENUATION_FACTOR) == 0 && super.bR())
            return true;
        else if(attachedSpawner != null && attachedSpawner.canSpawnMore() && random.nextInt(attachedSpawner.getFactor()) == 0
            && (!archetype.fearsSunlight || !isUnderSunlight())){
            attachedSpawner.addZombie(this);
            persistent = true;
            return true;
        }
        return false;
    }

    //returns whether the light is valid to spawn at the current entity position
    @Override
    protected boolean n_() {
        if(isUnderSunlight() && (archetype.fearsSunlight || !Config.Spawn.DAYLIGHT_SPAWN))
            return false;

        //taken from EntityMonster
        BlockPosition blockposition2 = new BlockPosition(this.locX, this.getBoundingBox().b, this.locZ);
        int i = this.world.getLightLevel(blockposition2);
        if (this.world.R()) {
            int j = this.world.ab();
            this.world.c(10);
            i = this.world.getLightLevel(blockposition2);
            this.world.c(j);
        }

        return i <= this.random.nextInt(Config.Spawn.DAYLIGHT_SPAWN ? 16 : 8);
    }

    //method used when the zombie attacks another entity
    @Override
    public boolean r(Entity entity) {
        boolean b = false;
        if (entity instanceof EntityPlayer) {
            EntityPlayer human = (EntityPlayer) entity;

            if ((human.getHealth() - getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).getValue()) <= 0) {
                //loosely based on vanilla villager infection
                EnhancedZombie zombie = new EnhancedZombie(world);
                //this method copies the location and angles from the given entity
                zombie.m(human);
                zombie.prepare(this.world.E(new BlockPosition(zombie)), (GroupDataEntity)null);
                zombie.setSourcePlayer(human);
                //needs to put it in this order to allow items to be picked up from inventory
                if (super.r(human) && human.dead) {
                    world.addEntity(zombie, CreatureSpawnEvent.SpawnReason.INFECTION);
                    //plays infection sound
                    world.a((EntityHuman)null, 1016, new BlockPosition((int)this.locX, (int)this.locY, (int)this.locZ), 0);
                    b = true;
                }
            }
            else
                b = super.r(human);
        }
        else
            b = super.r(entity);
        return b;
    }

    @Override
    protected void a(final DifficultyDamageScaler dds) {
        super.a(dds);
        if(this.random.nextFloat() < (this.world.getDifficulty() == EnumDifficulty.HARD ? 0.05F : 0.01F)) {
            final int i = this.random.nextInt(3);
            if(i == 0)
                this.setEquipment(0, new ItemStack(Items.STONE_SWORD));
        }
    }


    /*
	 * 0 = weapon (only for non players, use item in hand otherwise) 1 = Bottes 2 = Pantalon 3 = Plastron 4 = Casque
	 */
    /**
     * Define the zombie as being an infected player
     * Gives the zombie the player's equipment and name
     * @param from The source player
     */
    public void setSourcePlayer(EntityPlayer from) {
        ItemStack helmet = from.getEquipment(4), chestplate = from.getEquipment(3), pants = from.getEquipment(2),
                boots = from.getEquipment(1), weapon = from.inventory.getItemInHand();
        if(helmet != null)
            setEquipment(4, helmet);
        if(chestplate != null)
            setEquipment(3, chestplate);
        if(pants != null)
            setEquipment(2, pants);
        if(boots != null)
            setEquipment(1, boots);
        if(weapon != null)
            setEquipment(0, weapon);

        setCustomName("§5§m" + from.getName());
        setCustomNameVisible(true);
        fromPlayer = true;
    }

    /**
     *
     * @return The exact Bukkit API Location
     */
    public Location getBukkitLocation() {
        return new Location(this.world.getWorld(), this.locX, this.locY, this.locZ, this.yaw, this.pitch);
    }

    public static final class Config {

        public static void setValues(ConfigurationSection zombiesSection){
            Detection.NEED_SIGHT = zombiesSection.getBoolean("detection.need_sight", true);
            Detection.SNEAK = zombiesSection.getInt("detection.sneak", 7);
            Detection.NORMAL = zombiesSection.getInt("detection.normal", 13);
            Detection.SPRINT = zombiesSection.getInt("detection.sprint", 23);

            Spawn.ATTENUATION_FACTOR = zombiesSection.getInt("spawn.attenuation_factor", 25);
            Spawn.ON_PLAYER_DEATH = zombiesSection.getBoolean("spawn.on_player_death", true);
            Spawn.DAYLIGHT_SPAWN = zombiesSection.getBoolean("spawn.daylight_spawn", false);

            Characteristics.SPEED = zombiesSection.getDouble("characteristics.speed", 1.455);
            Characteristics.DAMAGE = zombiesSection.getDouble("characteristics.damage", 3.0);
        }

        public static final class Detection {
            public static boolean NEED_SIGHT;
            public static int SNEAK,
                NORMAL,
                SPRINT;
        }

        public static final class Spawn {
            public static int ATTENUATION_FACTOR;
            public static boolean ON_PLAYER_DEATH,
                DAYLIGHT_SPAWN;
        }

        public static final class Characteristics {
            public static double SPEED;
            public static double DAMAGE;
        }
    }
}