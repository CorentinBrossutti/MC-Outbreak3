package com.bp389.outbreak.ai.entities;

import net.minecraft.server.v1_8_R3.EntityCreeper;
import net.minecraft.server.v1_8_R3.World;

public class UnspawnableCreeper extends EntityCreeper {

	public UnspawnableCreeper(final World world) {
		super(world);
	}

	@Override
	public boolean bR() {
		return false;
	}
}
