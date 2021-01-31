package com.bp389.outbreak.ai.entities;

import net.minecraft.server.v1_8_R3.EntitySpider;
import net.minecraft.server.v1_8_R3.World;

public class UnspawnableSpider extends EntitySpider {

	public UnspawnableSpider(final World world) {
		super(world);
	}

	@Override
	public boolean bR() {
		return false;
	}
}
