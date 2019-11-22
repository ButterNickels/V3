/*
 * Copyright (c) 2019, ganom <https://github.com/Ganom>
 * Copyright (c) 2019, TomC <https://github.com/tomcylke>
 * All rights reserved.
 * Licensed under GPL3, see LICENSE for the full scope.
 */
package net.runelite.client.plugins.OneClick.src.main.java.net.runelite.client.plugins.externals.oneclick;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Types
{
	DARTS("Darts"),
	FIREMAKING("Firemaking"),
	BIRDHOUSES("Birdhouses"),
	HERB_TAR("Herb Tar"),
	LAVA_RUNES("Lava Runes"),
	STEAM_RUNES("Steam Runes"),
	SMOKE_RUNES("Smoke Runes"),
	HIGH_ALCH("High Alch"),
	DWARF_CANNON("Dwarf Cannon"),
	BONES("Bones"),
	KARAMBWANS("Karambwans"),
	DARK_ESSENCE("Dark Essence"),
	BRUMA_ROOT("Bruma Roots"),
	TIARA("Tiara"),
	TROUBLEBREWING("Trouble Brewing"),
	TITHE_FARM("Tithe Farming"),
	LEATHER("Dragon Hide Leather"),
	SUPER_HEAT("SuperHeat"),
	MEDPACK("Shayzien Medpack"),
	NONE("None");

	private String name;

	@Override
	public String toString()
	{
		return getName();
	}
}
