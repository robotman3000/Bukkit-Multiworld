package io.github.robotman3000.bukkit.multiworld.inventory;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BukkitInventories {
	private HashSet<BukkitInventory> unregisteredInventories = new HashSet<>();
	private HashSet<PlayerState> registeredInventories = new HashSet<>();
	
	protected Set<BukkitInventory> getInventories(){
		return unregisteredInventories;
	}
	
	protected HashSet<PlayerState> getRegisteredInventories() {
		return registeredInventories;
	}
	
	protected boolean createInventory(BukkitInventory inv){
		if(inv != null){
			return unregisteredInventories.add(inv);
		}
		return false;
	}
	
	protected boolean registerInventory(PlayerState beforeState, PlayerState afterState, UUID invID){
		Bukkit.getLogger().warning("Inv Maps Before Reg:");
		Bukkit.getLogger().warning("Reg: " + registeredInventories);
		Bukkit.getLogger().warning("UnReg: " + unregisteredInventories);
		Iterator<BukkitInventory> it = unregisteredInventories.iterator(); // Iterate over all aval invs
		while(it.hasNext()){
			BukkitInventory inv = it.next();
			if(inv.getInventoryId().equals(invID)){ // If its not the one we want then move on else do the task
				boolean var = registeredInventories.add(afterState); // Add the inventory to the registered list
				it.remove(); // Remove it from the unregistered list
				BukkitInventories.zeroPlayerInventory(afterState.getPlayer()); // Clear our slate
				updatePlayerInventory(afterState.getPlayer(), inv); // Give the player his new inventory
				Bukkit.getLogger().warning("Inv Maps After Reg:");
				Bukkit.getLogger().warning("Reg: " + registeredInventories);
				Bukkit.getLogger().warning("UnReg: " + unregisteredInventories);
				return var;
			}
		}
		return false; // We failed
	}
	
	protected boolean unregisterInventory(PlayerState beforeState, PlayerState afterState){
		Bukkit.getLogger().warning("Inv Maps Before UnReg:");
		Bukkit.getLogger().warning("Reg: " + registeredInventories);
		Bukkit.getLogger().warning("UnReg: " + unregisteredInventories);
		Iterator<PlayerState> it = registeredInventories.iterator();
		while(it.hasNext()){
			PlayerState rInv = it.next();
			if(rInv.getPlayer().getUniqueId().equals(beforeState.getPlayer().getUniqueId())){
				if(rInv.getWorld().getUID().equals(beforeState.getWorld().getUID())){
					if(rInv.getGamemode().equals(beforeState.getGamemode())){
						it.remove();
						BukkitInventory inv = new BukkitInventory(beforeState);
						BukkitInventories.zeroPlayerInventory(afterState.getPlayer());
						boolean var = unregisteredInventories.add(inv);
						Bukkit.getLogger().warning("Inv Maps After UnReg:");
						Bukkit.getLogger().warning("Reg: " + registeredInventories);
						Bukkit.getLogger().warning("UnReg: " + unregisteredInventories);
						return var;
					}
				}
			}
		}
		return false;
	}
	
/*	public static void updateInventoryContents(PlayerState playerState, UnRegisteredInventory inv) {
		inv.setCanFly(playerState.getPlayer().getAllowFlight());
		inv.setBedSpawnPoint(playerState.getPlayer().getBedSpawnLocation());
		inv.setCompassTarget(playerState.getPlayer().getCompassTarget());
		inv.setDisplayName(playerState.getPlayer().getDisplayName());
		inv.setExhaustion(playerState.getPlayer().getExhaustion());
		inv.setXpPoints(playerState.getPlayer().getExp());
		inv.setFallDistance(playerState.getPlayer().getFallDistance());
		inv.setFireTicks(playerState.getPlayer().getFireTicks());
		inv.setFlying(playerState.getPlayer().isFlying());
		inv.setFoodLevel(playerState.getPlayer().getFoodLevel());
		inv.setHealthPoints(playerState.getPlayer().getHealth());
		inv.setXpLevel(playerState.getPlayer().getLevel());
		inv.setRemainingAir(playerState.getPlayer().getRemainingAir());
		inv.setFoodSaturation(playerState.getPlayer().getSaturation());
		inv.setVelocity(playerState.getPlayer().getVelocity());
		inv.setArmorContents(playerState.getPlayer().getInventory().getArmorContents());
		inv.setInventoryContents(playerState.getPlayer().getInventory().getContents());
		inv.setEnderChest(playerState.getPlayer().getEnderChest().getContents());
		inv.setLocation(playerState.getPlayer().getLocation());
	}*/
	
	private static void zeroPlayerInventory(Player player) {
		// TODO: Finish adding all the things that need to be cleared
		player.getActivePotionEffects().clear();
		player.getEnderChest().clear();
		player.getEquipment().clear();
		player.getInventory().clear();
	}

	public static void updatePlayerInventory(Player player, BukkitInventory inventory){
		player.setAllowFlight(inventory.canFly());
		player.setBedSpawnLocation(inventory.getBedSpawnPoint());
		player.setCompassTarget(inventory.getCompassTarget());
		player.setDisplayName(inventory.getDisplayName());
		player.setExhaustion(inventory.getExhaustion());
		player.setExp(inventory.getXpPoints());
		player.setFallDistance(inventory.getFallDistance());
		player.setFireTicks(inventory.getFireTicks());
		player.setFlying(inventory.isFlying());
		player.setFoodLevel(inventory.getFoodLevel());
		player.setHealth(inventory.getHealthPoints());
		player.setLevel(inventory.getXpLevel());
		player.setRemainingAir(inventory.getRemainingAir());
		player.setSaturation(inventory.getFoodSaturation());
		player.setVelocity(inventory.getVelocity());
		player.getInventory().setArmorContents(inventory.getArmorContents());
		player.getInventory().setContents(inventory.getInventoryContents());
		player.getEnderChest().setContents(inventory.getEnderChest());
		//player.teleport(inventory.getLocation());
	}
	
	public UUID queryAvalInventories(PlayerState playerState){
		for(BukkitInventory inv : unregisteredInventories){
			if(inv.getPlayerState().equals(playerState)){
				return inv.getInventoryId();
			}
		}
		return null;
	}

	public static BukkitInventory generateBlankConfig(PlayerState playerState) {
		return new BukkitInventory(playerState);
	}
}
