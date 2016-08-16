package io.github.robotman3000.bukkit.multiworld.world;

import org.bukkit.World;

public interface PropertyList {

	String getPropertyValue(World world);

	boolean setPropertyValue(World world, String newValue);

	String name();

}
