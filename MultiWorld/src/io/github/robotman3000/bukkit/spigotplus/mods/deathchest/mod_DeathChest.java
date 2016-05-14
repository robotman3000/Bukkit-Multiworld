package io.github.robotman3000.bukkit.spigotplus.mods.deathchest;

import io.github.robotman3000.bukkit.spigotplus.mods.PluginModBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class mod_DeathChest extends PluginModBase implements Listener {

	public mod_DeathChest(JavaPlugin hostPlugin) {
		super(hostPlugin);
	}

	@Override
	public String getName() {
		return "Death Chest";
	}

	@Override
	public void initialize(ConfigurationSection config) {
		Bukkit.getLogger().info("Starting the death chest mod");
		getHost().getServer().getPluginManager().registerEvents(this, getHost());
	}

	@Override
	public void shutdown() {
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event){
		if(!event.getKeepInventory()){
			Player player = event.getEntity();
			ItemStack[] inventoryContents = player.getInventory().getContents();
		
			boolean chestFound = false;
			for(ItemStack item : inventoryContents){
				if(item != null){
					if(item.getType() == Material.CHEST || item.getType() == Material.TRAPPED_CHEST){
						chestFound = true;
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
				loc.getBlock().setType(Material.CHEST);
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
