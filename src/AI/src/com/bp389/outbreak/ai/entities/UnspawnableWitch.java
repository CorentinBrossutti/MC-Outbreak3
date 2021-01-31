package com.bp389.outbreak.ai.entities;

import net.minecraft.server.v1_8_R3.EntityWitch;
import net.minecraft.server.v1_8_R3.World;

public class UnspawnableWitch extends EntityWitch {

	public UnspawnableWitch(final World arg0) {
		super(arg0);
	}

	@Override
	public boolean bR() {
		return false;
	}
}
