package com.bp389.outbreak.ai.entities;

import com.bp389.outbreak.ai.entities.zombie.EnhancedZombie;
import net.minecraft.server.v1_8_R3.*;
import net.minecraft.server.v1_8_R3.BiomeBase.BiomeMeta;
import org.bukkit.entity.EntityType;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * Register all NMS entites
 * 
 * @author BlackPhantom
 * 
 */
public enum CustomEntityType {

	ZOMBIE("Zombie", 54, EntityType.ZOMBIE, EntityZombie.class, EnhancedZombie.class),
	SKELETON("Skeleton", 51, EntityType.SKELETON, EntitySkeleton.class, UnspawnableSkeleton.class),
	SPIDER("Spider", 52, EntityType.SPIDER, EntitySpider.class, UnspawnableSpider.class),
	CREEPER("Creeper", 50, EntityType.CREEPER, EntityCreeper.class, UnspawnableCreeper.class),
	ENDERMAN("Enderman", 58, EntityType.ENDERMAN, EntityEnderman.class, UnspawnableEnderman.class),
	WITCH("Witch", 66, EntityType.WITCH, EntityWitch.class, UnspawnableWitch.class),
	SLIME("Slime", 55, EntityType.SLIME, EntitySlime.class, UnspawnableSlime.class),
	CAVE_SPIDER("CaveSpider", 59, EntityType.CAVE_SPIDER, EntityCaveSpider.class, UnspawnableCaveSpider.class);

	private String name;
	private int id;
	private EntityType entityType;
	private Class<? extends EntityInsentient> nmsClass;
	private Class<? extends EntityInsentient> customClass;

	private CustomEntityType(final String name, final int id, final EntityType entityType, final Class<? extends EntityInsentient> nmsClass,
                             final Class<? extends EntityInsentient> customClass) {
		this.name = name;
		this.id = id;
		this.entityType = entityType;
		this.nmsClass = nmsClass;
		this.customClass = customClass;
	}

	public String getName() {
		return this.name;
	}

	public int getID() {
		return this.id;
	}

	public EntityType getEntityType() {
		return this.entityType;
	}

	public Class<? extends EntityInsentient> getNMSClass() {
		return this.nmsClass;
	}

	public Class<? extends EntityInsentient> getCustomClass() {
		return this.customClass;
	}

	/**
	 * Register our entities.
	 */
	public static void registerEntities() {
		for(final CustomEntityType entity : CustomEntityType.values())
			/* Get our entities */
			CustomEntityType.a(entity.getCustomClass(), entity.getName(), entity.getID());
		/* Get all biomes on the server */
		BiomeBase[] biomes;
		try {
			biomes = (BiomeBase[]) CustomEntityType.getPrivateStatic(BiomeBase.class, "biomes");
		} catch(final Exception exc) {
			return;
		}
		for(final BiomeBase biomeBase : biomes) {
			if(biomeBase == null)
				break;
			for(final String field : new String[] { "at", "au", "av", "aw" })
				// Lists that hold all entity types
				try {
					final Field list = BiomeBase.class.getDeclaredField(field);
					list.setAccessible(true);
					@SuppressWarnings("unchecked")
					final List<BiomeMeta> mobList = (List<BiomeMeta>) list.get(biomeBase);

					for(final BiomeMeta meta : mobList)
						for(final CustomEntityType entity : CustomEntityType.values())
							if(entity.getNMSClass().equals(meta.b)) /*
							 * Test if
							 * the
							 * entity
							 * has the
							 * custom
							 * entity
							 * type
							 */
								meta.b = entity.getCustomClass(); // Set it's
					// meta to our
					// custom
					// class's
					// meta
				} catch(final Exception e) {
					e.printStackTrace();
				}
		}
	}

	/**
	 * Unregister our entities to prevent memory leaks. Call on disable.
	 */
	@SuppressWarnings("rawtypes")
	public static void unregisterEntities() {
		for(final CustomEntityType entity : CustomEntityType.values()) {
			// Remove our class references.
			try {
				((Map) CustomEntityType.getPrivateStatic(EntityTypes.class, "d")).remove(entity.getCustomClass());
			} catch(final Exception e) {
				e.printStackTrace();
			}

			try {
				((Map) CustomEntityType.getPrivateStatic(EntityTypes.class, "f")).remove(entity.getCustomClass());
			} catch(final Exception e) {
				e.printStackTrace();
			}
		}

		for(final CustomEntityType entity : CustomEntityType.values())
			try {
				CustomEntityType.a(entity.getNMSClass(), entity.getName(), entity.getID());
			} catch(final Exception e) {
				e.printStackTrace();
			}

		BiomeBase[] biomes;
		try {
			biomes = (BiomeBase[]) CustomEntityType.getPrivateStatic(BiomeBase.class, "biomes"); /*
			 * Get
			 * all
			 * biomes
			 * again
			 */
		} catch(final Exception exc) {
			return;
		}
		for(final BiomeBase biomeBase : biomes) {
			if(biomeBase == null)
				break;

			for(final String field : new String[] { "at", "au", "av", "aw" })
				/* The entity list */
				try {
					final Field list = BiomeBase.class.getDeclaredField(field);
					list.setAccessible(true);
					@SuppressWarnings("unchecked")
					final List<BiomeMeta> mobList = (List<BiomeMeta>) list.get(biomeBase);

					for(final BiomeMeta meta : mobList)
						for(final CustomEntityType entity : CustomEntityType.values())
							if(entity.getCustomClass().equals(meta.b))
								meta.b = entity.getNMSClass(); /*
								 * Set the
								 * entities meta
								 * back to the
								 * NMS one
								 */
				} catch(final Exception e) {
					e.printStackTrace();
				}
		}
	}

	/**
	 * A convenience method.
	 * 
	 * @param clazz
	 *            The class.
	 * @param f
	 *            The string representation of the private static field.
	 * @return The object found
	 * @throws Exception
	 *             if unable to get the object.
	 */
	@SuppressWarnings("rawtypes")
	private static Object getPrivateStatic(final Class clazz, final String f) throws Exception {
		final Field field = clazz.getDeclaredField(f);
		field.setAccessible(true);
		return field.get(null);
	}

	/*
	 * Since 1.7.2 added a check in their entity registration, simply bypass it
	 * and write to the maps ourself.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void a(final Class paramClass, final String paramString, final int paramInt) {
		try {
			((Map) CustomEntityType.getPrivateStatic(EntityTypes.class, "c")).put(paramString, paramClass);
			((Map) CustomEntityType.getPrivateStatic(EntityTypes.class, "d")).put(paramClass, paramString);
			((Map) CustomEntityType.getPrivateStatic(EntityTypes.class, "e")).put(Integer.valueOf(paramInt), paramClass);
			((Map) CustomEntityType.getPrivateStatic(EntityTypes.class, "f")).put(paramClass, Integer.valueOf(paramInt));
			((Map) CustomEntityType.getPrivateStatic(EntityTypes.class, "g")).put(paramString, Integer.valueOf(paramInt));
		} catch(final Exception exc) {
			// Unable to register the new class.
		}
	}
}