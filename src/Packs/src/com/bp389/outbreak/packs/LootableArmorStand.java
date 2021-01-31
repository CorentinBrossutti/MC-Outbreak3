package com.bp389.outbreak.packs;

import com.bp389.outbreak.util.MathUtil;
import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import java.util.function.Consumer;

public final class LootableArmorStand extends EntityArmorStand {

    private Location baseLocation;
    private LootPackPoint point;
    private boolean spawned;

    /**
     * @param server Main server to create entity
     * @param point The loot point
     */
    public LootableArmorStand(LootPackPoint point) {
        super(((CraftWorld)point.getLocation().getWorld()).getHandle());

        this.baseLocation = point.getLocation();
        this.point = point;


        setBasePlate(false);
        setArms(true);
        //when going through NMS somehow true means no gravity
        setGravity(true);
        setInvisible(true);
    }

    public Location getBaseLocation() {
        return baseLocation;
    }

    public LootPackPoint getPoint() {
        return point;
    }

    public void setLoot(ItemStack is){
        EulerAngle tempAng = EulerAngle.ZERO;
        boolean b = false;
        switch(is.getType()){
            case BOW:
            case IRON_AXE:
            case IRON_HOE:
            case IRON_PICKAXE:
            case IRON_SPADE:
            case IRON_SWORD:
            case WOOD_SPADE:
            case WOOD_AXE:
            case WOOD_HOE:
            case WOOD_PICKAXE:
            case WOOD_SWORD:
            case STONE_AXE:
            case STONE_HOE:
            case STONE_PICKAXE:
            case STONE_SPADE:
            case STONE_SWORD:
            case GOLD_AXE:
            case GOLD_PICKAXE:
            case GOLD_SPADE:
            case GOLD_SWORD:
            case GOLD_HOE:
            case DIAMOND_AXE:
            case DIAMOND_HOE:
            case DIAMOND_PICKAXE:
            case DIAMOND_SPADE:
            case DIAMOND_SWORD:
                b = true;
                break;
            default:
                break;
        }

        if (b) {
            if (is.getType() == Material.BOW) {
                setPosition(baseLocation.getX() + 1.35, baseLocation.getY() - 0.3, baseLocation.getZ() + 0.6);
                tempAng = new EulerAngle(0D, MathUtil.degreesToRadians(10D), MathUtil.degreesToRadians(92.5));
            } else {
                setPosition(baseLocation.getX() + 1.425, baseLocation.getY() - 0.4, baseLocation.getZ() + 0.2);
                tempAng = new EulerAngle(0D, 0D, MathUtil.degreesToRadians(90D));
            }
        } else
            setPosition(baseLocation.getX() + 0.9, baseLocation.getY() + 0.225, baseLocation.getZ() + 0.225);

        setRightArmPose(MathUtil.eulerToNMS(tempAng));
        setEquipment(0, CraftItemStack.asNMSCopy(is));

        if (!spawned) {
            spawned =  true;
            getWorld().addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
        }
    }
}
