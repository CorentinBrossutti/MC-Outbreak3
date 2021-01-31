package com.bp389.outbreak.ai.entities;

import net.minecraft.server.v1_8_R3.EntityEnderman;
import net.minecraft.server.v1_8_R3.World;

public class UnspawnableEnderman extends EntityEnderman {

	public UnspawnableEnderman(final World world) {
		super(world);
	}

	@Override
	public boolean bR() {
		return false;
	}
}
