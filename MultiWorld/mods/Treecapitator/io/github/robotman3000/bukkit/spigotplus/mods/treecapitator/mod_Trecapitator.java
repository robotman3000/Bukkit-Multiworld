package io.github.robotman3000.bukkit.spigotplus.mods.treecapitator;

import io.github.robotman3000.bukkit.spigotplus.api.JavaPluginFeature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class mod_Trecapitator extends JavaPluginFeature implements Listener {

	int logCount = 0;
	int maxBlocks;
	private boolean shouldBeSneaking;
	private int minRemainingDurability;
	private List<Material> axeItems = new ArrayList<>();
	private boolean chargeDurability;
	private boolean chargeDurabilityCreative;

	@EventHandler
	public void onBlockBroken(BlockBreakEvent event) {
		// This gets called when a block is broken
		if(event.getPlayer().hasPermission("spigotplus.treecapitator." + event.getPlayer().getGameMode())){
			if (event.getPlayer().isSneaking() == shouldBeSneaking){
				ItemStack item1 = event.getPlayer().getInventory().getItemInMainHand();
				ItemStack item2 = event.getPlayer().getInventory().getItemInOffHand();
				if (itemIsValid(item1) || itemIsValid(item2)) {
					Block block = event.getBlock();
					Location loc = block.getLocation();
					ItemStack axeItem = (item1 != null ? item1 : item2);
					checkBlock(loc.getWorld(), loc.getBlockX(), loc.getBlockY(),
							loc.getBlockZ(), axeItem);
					
					if(logCount >= maxBlocks){
						event.getPlayer().sendMessage(ChatColor.RED + "You have exceded the maximum number of logs that can be broken at once.");
					}
					
					if(chargeDurability){
						if((event.getPlayer().getGameMode() != GameMode.CREATIVE) || chargeDurabilityCreative){
							axeItem.setDurability((short) (axeItem.getDurability() + logCount));
						}
					}
					//event.getPlayer().sendMessage("Logs Broken: " + logCount);
					logCount = 0;
				}
			}
		}
	}
	
	private boolean itemIsValid(ItemStack i) {
		if (i != null) {
			for(Material type : axeItems){
				if(type.equals(i.getType())){
					return true;
				}
			}
		}
		return false;

	}

	private void checkBlock(World world, int x, int y, int z, ItemStack axe) {
		Block block = world.getBlockAt(x, y, z);
		Material type = block.getType();
		if ((type == Material.LOG || type == Material.LOG_2) && (logCount < this.maxBlocks)) {
			
			if(!chargeDurability || (((axe.getType().getMaxDurability() - axe.getDurability()) - logCount) > minRemainingDurability)){
				block.breakNaturally();
				logCount++;
				for (int xmod = -1; xmod <= 1; xmod++) {
					for (int ymod = -1; ymod <= 1; ymod++) {
						for (int zmod = -1; zmod <= 1; zmod++) {
							checkBlock(world, xmod + x, ymod + y, zmod + z, axe);
						}
					}
				}
			}
		}
	}
	
	protected void loadConfig() {
		this.maxBlocks = getConfig().getInt(TreeConfigKeys.maxBlocks.name(), 96);
		this.shouldBeSneaking = getConfig().getBoolean(TreeConfigKeys.shouldBeSneaking.name(), true);
		this.minRemainingDurability = getConfig().getInt(TreeConfigKeys.minRemainingDurability.name(), 2);
		this.chargeDurability = getConfig().getBoolean(TreeConfigKeys.chargeDurability.name(), true);
		this.chargeDurabilityCreative = getConfig().getBoolean(TreeConfigKeys.chargeDurabilityCreative.name(), false);
		List<String> items = getConfig().getStringList(TreeConfigKeys.axeItems.name());
		if(items == null){
			items = Arrays.asList(Material.DIAMOND_AXE.name(), Material.GOLD_AXE.name(), Material.IRON_AXE.name(), Material.STONE_AXE.name(), Material.WOOD_AXE.name());
			getConfig().set(TreeConfigKeys.axeItems.name(), items);
		}
		
		for(String str : items){
			Material material = Material.matchMaterial(str);
			if(material != null){
				axeItems.add(material);
			} else {
				logInfo("No material match was found for \"" + str + "\"");
			}
		}
		
	}
	
	public void saveConfigValues() {
		getConfig().set(TreeConfigKeys.maxBlocks.name(), this.maxBlocks);
		getConfig().set(TreeConfigKeys.shouldBeSneaking.name(), this.shouldBeSneaking);
		getConfig().set(TreeConfigKeys.minRemainingDurability.name(), this.minRemainingDurability);
		getConfig().set(TreeConfigKeys.chargeDurability.name(), this.chargeDurability);
		getConfig().set(TreeConfigKeys.chargeDurabilityCreative.name(), this.chargeDurabilityCreative);
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
