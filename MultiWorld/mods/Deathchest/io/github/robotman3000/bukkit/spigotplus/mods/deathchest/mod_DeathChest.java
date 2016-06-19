package io.github.robotman3000.bukkit.spigotplus.mods.deathchest;

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
import org.bukkit.plugin.java.JavaPlugin;

public class mod_DeathChest extends JavaPluginFeature<JavaPlugin> implements Listener {

	private boolean attemptDoubleChest;
	private boolean saveToEnderChest;
	private boolean enderChestRequired;
	private boolean generateChest;
	private boolean useInventoryChest;
	private List<Integer> enderItems;
	private final int SINGLE_CHEST_SIZE = 36;

	public mod_DeathChest(JavaPlugin plugin) {
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
	
	@SuppressWarnings("unused")
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event){
		// TODO: Allow placing a double chest
		// TODO: Allow enabling and disabling the chest location message
		// TODO: Allow configuring the chest location message
		// TODO: Add support for permissions to determine what players and what gamemodes use the death chest
		
		if(!event.getKeepInventory()){
			Player player = event.getEntity();
			ItemStack[] inventoryContents = player.getInventory().getContents();
			boolean chestFound = this.generateChest;
			boolean enderChestFound = false;
			boolean isTrapped = false;
			boolean needsDoubleChest = this.attemptDoubleChest && (inventoryContents.length > SINGLE_CHEST_SIZE);
			int missingChestCount = 0;
			if(this.useInventoryChest){
				for(ItemStack item : inventoryContents){
					if(item != null && (!chestFound || !enderChestFound)){
						if(!chestFound && (item.getType() == Material.CHEST || item.getType() == Material.TRAPPED_CHEST)){
							chestFound = true;
							
							isTrapped = (item.getType() == Material.TRAPPED_CHEST);
							
							int reqChestCount = (boolToInt(needsDoubleChest) + 1);
							if(item.getAmount() > reqChestCount){
								item.setAmount(item.getAmount() - reqChestCount);
							} else {
								missingChestCount = reqChestCount - item.getAmount();
								player.getInventory().remove(item);
							}
						}
						
						if(!enderChestFound && enderChestRequired && item.getType() == Material.ENDER_CHEST){
							enderChestFound = true;
						}
					}
				}
			}
			if(chestFound){
				Location loc = player.getLocation();
				player.sendMessage("Your things have been put into a chest located at: " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());
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
						//if(itemIsAllowed(item)){
							//if(enderChestFound && !itemSavesToEnderChest()){
								list.add(item);
							//}
						//}
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
	
	private int boolToInt(boolean bool) {
		return (bool ? 1 : 0);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void loadConfig() {
		this.attemptDoubleChest = getFeatureConfig().getBoolean("attemptDoubleChest", false);
		this.saveToEnderChest = getFeatureConfig().getBoolean("ender.saveToEnderChest", false);
		this.enderChestRequired = getFeatureConfig().getBoolean("ender.enderChestRequired", false);
		this.enderItems = (List<Integer>) getFeatureConfig().getList("ender.items", new ArrayList<Integer>());
		this.generateChest = getFeatureConfig().getBoolean("generateChest", false);
		this.useInventoryChest = getFeatureConfig().getBoolean("useInventoryChest", true);
	}
	
	protected void saveConfig() {
		getFeatureConfig().set("attemptDoubleChest", this.attemptDoubleChest);
		getFeatureConfig().set("ender.saveToEnderChest", this.saveToEnderChest);
		getFeatureConfig().set("ender.enderChestRequired", this.enderChestRequired);
		getFeatureConfig().set("ender.items", this.enderItems);
		getFeatureConfig().set("generateChest", this.generateChest);
		getFeatureConfig().set("useInventoryChest", this.useInventoryChest);
	}
}
