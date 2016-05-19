package io.github.robotman3000.bukkit.spigotplus.mods.deathchest;

import io.github.robotman3000.bukkit.multiworld.SpigotPlus;
import io.github.robotman3000.bukkit.spigotplus.api.JavaPluginFeature;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class mod_DeathChest extends JavaPluginFeature<SpigotPlus> implements Listener {

	public mod_DeathChest(SpigotPlus plugin) {
		super(plugin, "Deathchest Mod");
	}

	@Override
	public String getFeatureName() {
		return "Death Chest";
	}

	@Override
	public boolean initalize() {
		Bukkit.getLogger().info("Starting the death chest mod");
		getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
		return true;
	}

	@Override
	public void shutdown() {
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event){
		// TODO: Allow placing a double chest
		// TODO: Allow enabling and disabling the chest location message
		// TODO: Allow configuring the chest location message
		// TODO: Add support for permissions to determine what players and what gamemodes use the death chest
		
		if(!event.getKeepInventory()){
			Player player = event.getEntity();
			ItemStack[] inventoryContents = player.getInventory().getContents();
		
			boolean chestFound = false;
			boolean isTrapped = false;
			for(ItemStack item : inventoryContents){
				if(item != null){
					if(item.getType() == Material.CHEST || item.getType() == Material.TRAPPED_CHEST){
						chestFound = true;
						
						isTrapped = (item.getType() == Material.TRAPPED_CHEST);
						
						if(item.getAmount() > 1){
							item.setAmount(item.getAmount() - 1);
						} else {
							player.getInventory().remove(item);
						}
						break;
					}
				}
			}
			
			if(chestFound){
				Location loc = player.getLocation();
				player.sendMessage("Your things have been put into a chest located at: " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());
				loc.getBlock().setType(isTrapped ? Material.TRAPPED_CHEST : Material.CHEST);
				Block blk = loc.getBlock();
				Chest chest = (Chest) blk.getState();
				
				List<ItemStack> list = new ArrayList<>();
				for(ItemStack item : player.getInventory().getContents()){
					if(item != null){
						list.add(item);
					}
				}
				
				Map<Integer, ItemStack> leftOvers = chest.getBlockInventory().addItem(list.toArray(new ItemStack[0]));
				event.getDrops().clear();
				event.getDrops().addAll(leftOvers.values());
			}
		}
	}
}
