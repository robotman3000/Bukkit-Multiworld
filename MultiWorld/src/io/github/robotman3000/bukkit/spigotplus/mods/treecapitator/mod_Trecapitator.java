package io.github.robotman3000.bukkit.spigotplus.mods.treecapitator;

import io.github.robotman3000.bukkit.multiworld.SpigotPlus;
import io.github.robotman3000.bukkit.spigotplus.api.JavaPluginFeature;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class mod_Trecapitator extends JavaPluginFeature<SpigotPlus> implements
		Listener {

	public mod_Trecapitator(SpigotPlus plugin) {
		super(plugin, "Treecapitator Mod");
	}

	@Override
	public boolean initalize() {
		Bukkit.getServer().getPluginManager().registerEvents(this, getPlugin());
		return true;
	}

	@EventHandler
	public void onBlockBroken(BlockBreakEvent event) {
		if(event.getPlayer().isSneaking()){
			ItemStack item1 = event.getPlayer().getInventory().getItemInMainHand();
			ItemStack item2 = event.getPlayer().getInventory().getItemInOffHand();
			if (itemIsValid(item1) || itemIsValid(item2)) {
				Block block = event.getBlock();
				Location loc = block.getLocation();
				checkBlock((item1 != null ? item1 : item2), loc.getWorld(), loc.getBlockX(), loc.getBlockY(),
						loc.getBlockZ());
			}
		}
	}

	private boolean itemIsValid(ItemStack i) {
		if (i != null) {
			switch (i.getType()) {
			case DIAMOND_AXE:
			case GOLD_AXE:
			case IRON_AXE:
			case STONE_AXE:
			case WOOD_AXE:
				return true;
			default:
				break;
			}
		}
		return false;

	}

	private void checkBlock(ItemStack item, World world, int x, int y, int z) {
		Block block = world.getBlockAt(x, y, z);
		Material type = block.getType();
		if (type == Material.LOG || type == Material.LOG_2) {
			if(item != null && (item.getType().getMaxDurability() - item.getDurability()) > 2){
				block.breakNaturally();
				item.setDurability((short) (item.getDurability() + 1));
			}
			for (int xmod = -1; xmod <= 1; xmod++) {
				for (int ymod = -1; ymod <= 1; ymod++) {
					for (int zmod = -1; zmod <= 1; zmod++) {
						checkBlock(item, world, xmod + x, ymod + y, zmod + z);
					}
				}
			}
		}
	}
}
