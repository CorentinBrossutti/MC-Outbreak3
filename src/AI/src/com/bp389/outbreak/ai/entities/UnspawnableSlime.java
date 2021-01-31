package com.bp389.outbreak.ai.entities;

import net.minecraft.server.v1_8_R3.EntitySlime;
import net.minecraft.server.v1_8_R3.World;

public class UnspawnableSlime extends EntitySlime {

	public UnspawnableSlime(final World world) {
		super(world);
	}

	@Override
	public boolean bR() {
		return false;
	}
}
