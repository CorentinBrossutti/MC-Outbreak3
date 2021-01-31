package com.bp389.outbreak.ai.entities;

import net.minecraft.server.v1_8_R3.EntityCaveSpider;
import net.minecraft.server.v1_8_R3.World;

public class UnspawnableCaveSpider extends EntityCaveSpider {

	public UnspawnableCaveSpider(final World arg0) {
		super(arg0);
	}

	@Override
	public boolean bR() {
		return false;
	}
}