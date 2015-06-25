package io.github.robotman3000.bukkit.multiworld;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * This class is a wraper for the Location class to fix a circular reference that caused gson to fail
 * @author Eric
 *
 */
public class FixedLocation {

	private int x;
	private int y;
	private int z;
	private UUID world;
	private float yaw;
	private float pitch;

	public FixedLocation(Location loc){
		if(loc == null){
			loc = getAWorld().getSpawnLocation();
		}
		this.x = loc.getBlockX();
		this.y = loc.getBlockY();
		this.z = loc.getBlockZ();
		this.yaw = loc.getYaw();
		this.pitch = loc.getPitch();
		this.world = loc.getWorld().getUID();
	}
	
	public Location getLocation(){
		return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
	}
	
	private World getAWorld(){
		return Bukkit.getServer().getWorlds().get(0);
	}
}
