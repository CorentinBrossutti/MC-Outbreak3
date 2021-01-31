package com.bp389.outbreak.ai.entities;

import net.minecraft.server.v1_8_R3.EntitySkeleton;
import net.minecraft.server.v1_8_R3.World;

public class UnspawnableSkeleton extends EntitySkeleton {

	public UnspawnableSkeleton(final World world) {
		super(world);
	}

	@Override
	public boolean bR() {
		return false;
	}
}
