package io.github.robotman3000.bukkit.spigotplus.mods.deathchest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import io.github.robotman3000.bukkit.spigotplus.api.JavaPluginFeature;

public class mod_DeathChest extends JavaPluginFeature implements Listener {
	//TODO: Make the generated chests disappear when items are collected
	private boolean attemptDoubleChest;
	private boolean generateChest;
	private boolean useInventoryChest;
	private boolean reportChestLoc;
	private final int SINGLE_CHEST_SIZE = 36;
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event){
		if(event.getEntity().hasPermission("spigotplus.deathchest." + event.getEntity().getGameMode())){
			if(!event.getKeepInventory()){
				Player player = event.getEntity();
				ItemStack[] inventoryContents = player.getInventory().getContents();
				boolean chestFound = this.generateChest;
				boolean isTrapped = false;
				boolean needsDoubleChest = this.attemptDoubleChest && (inventoryContents.length > SINGLE_CHEST_SIZE);
				
				if(this.useInventoryChest){
					for(ItemStack item : inventoryContents){
						if(item != null && !chestFound){
							if(!chestFound && (item.getType() == Material.CHEST || item.getType() == Material.TRAPPED_CHEST)){
								chestFound = true;
								
								isTrapped = (item.getType() == Material.TRAPPED_CHEST);
								
								int reqChestCount = (boolToInt(needsDoubleChest) + 1);
								if(item.getAmount() > reqChestCount){
									item.setAmount(item.getAmount() - reqChestCount);
								} else {
									player.getInventory().remove(item);
								}
							}
						}
					}
				}
				
				if(chestFound){
					Location loc = player.getLocation();
					
					if(loc.getBlockY() < 0){
						loc.setY(4);
					}
					
					if(reportChestLoc){
						player.sendMessage("Your things have been put into a chest located at: " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());
					}
					loc.getBlock().setType(isTrapped ? Material.TRAPPED_CHEST : Material.CHEST);
					Block blk = loc.getBlock();
					Chest chest = (Chest) blk.getState();
					
					Chest chest2 = null;
					if(needsDoubleChest){
						// TODO: Make this try to avoid replacing non-air blocks
						loc = loc.add(1, 0, 0);
						loc.getBlock().setType(isTrapped ? Material.TRAPPED_CHEST : Material.CHEST);
						Block block = loc.getBlock();
						chest2 = (Chest) block.getState();
					}
					
					
					List<ItemStack> list = new ArrayList<>();
					for(ItemStack item : player.getInventory().getContents()){
						if(item != null){
							list.add(item);
						}
					}
					
					Map<Integer, ItemStack> leftOvers = chest.getBlockInventory().addItem(list.toArray(new ItemStack[0]));
					
					if(chest2 != null && !leftOvers.isEmpty()){
						leftOvers = chest2.getBlockInventory().addItem(leftOvers.values().toArray(new ItemStack[0]));
					}
					event.getDrops().clear();
					event.getDrops().addAll(leftOvers.values());
				}
			}
		}
	}

	private int boolToInt(boolean bool) {
		return (bool ? 1 : 0);
	}

	public void loadConfig() {
		this.reportChestLoc = getConfig().getBoolean("reportChestLocation", false);
		this.attemptDoubleChest = getConfig().getBoolean("attemptDoubleChest", false);
		this.generateChest = getConfig().getBoolean("generateChest", false);
		this.useInventoryChest = getConfig().getBoolean("useInventoryChest", true);
	}
	
	public void saveConfigValues() {
		getConfig().set("reportChestLocation", this.reportChestLoc);
		getConfig().set("attemptDoubleChest", this.attemptDoubleChest);
		getConfig().set("generateChest", this.generateChest);
		getConfig().set("useInventoryChest", this.useInventoryChest);
	}
	
	@Override
	protected boolean startup() {
		loadConfig();
		return true;
	}
	
	@Override
	protected void shutdown() {
		saveConfigValues();
	}
	
	@Override
	public int getRequiredMajorVersion() {
		return 1;
	}

	@Override
	public int getRequiredMinorVersion() {
		return 0;
	}
}
