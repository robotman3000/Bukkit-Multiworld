package io.github.robotman3000.bukkit.multiworld.world;

import io.github.robotman3000.bukkit.multiworld.CommonLogic;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

public class BukkitWorlds {

	private HashMap<UUID, BukkitWorld> worldList = new HashMap<>();
	
	/**
	 * 
	 * @param worldToLoad
	 * @return True if the world was loaded or False if something went wrong
	 */
	protected boolean loadWorldToMemory(UUID worldToLoad){
		BukkitWorld theWorld = worldList.get(worldToLoad);
		if (theWorld != null){
			WorldCreator creator = theWorld.getWorldCreator();
			World world = Bukkit.createWorld(creator);
			theWorld.setUUID(world.getUID());
			WorldManager.updateConfigForWorld(world, theWorld);
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param worldToUnload
	 * @param saveChunks
	 * @return True if world was unloaded successfully and False if something went wrong
	 */
	protected boolean unloadWorldFromMemory(UUID worldToUnload, boolean saveChunks){
		BukkitWorld theWorld = worldList.get(worldToUnload);
		if (theWorld != null){
			World world = Bukkit.getWorld(theWorld.getUUID());
			if (world != null){
				return Bukkit.unloadWorld(world, saveChunks);
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param theNewWorld
	 * @return True if world was created successfully and False if something went wrong</br>
	 * Note: This does not load the world or register it with the server. This only adds the world the plugins world list
	 * 
	 */
	protected boolean createWorld(BukkitWorld theNewWorld){
		if(theNewWorld != null){
			UUID theUUID = theNewWorld.getUUID();
			if (theUUID == null){
				theUUID = UUID.randomUUID();
			}
			theNewWorld.setUUID(theUUID);
			worldList.put(theUUID, theNewWorld);
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param worldToDelete
	 * @param deleteWorldFolder
	 * @return True if world was deleted successfully and False if something went wrong
	 */
	protected boolean deleteWorld(UUID worldToDelete, boolean deleteWorldFolder){
		boolean canDeleteWorld = false;
		if(worldToDelete != null){
			for(World world : Bukkit.getWorlds()){
				if (world.getUID().equals(worldToDelete)){
					canDeleteWorld = true;
				}
			}
			
			if(canDeleteWorld){
				World world = Bukkit.getWorld(worldToDelete);
				File theWorldDir = Bukkit.getWorld(worldToDelete).getWorldFolder();
				// Always save chunks so that the world can be deleted properly
				boolean worldUnloaded = Bukkit.unloadWorld(world, true);
				if (worldUnloaded){
					if(deleteWorldFolder){
						if(theWorldDir != null){
							if(theWorldDir.isDirectory()){
								try {
									CommonLogic.dirDelete(theWorldDir);
									return true;
								} catch (IOException e) {
									e.printStackTrace();
									return false;
								}
							} else {
								// This should never run but just in case
								return theWorldDir.delete();
							}
						}
					} else {
						return true;	
					}
				}
			}
		}
		return false;
	}

	public UUID doesWorldNameExist(String string) {
		for (UUID uid : worldList.keySet()){
			if(worldList.get(uid).getWorldName().equalsIgnoreCase(string)){
				return uid;
			}
		}
		return null;
	}
}