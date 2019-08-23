package io.github.robotman3000.bukkit.spigotplus.mods.treecapitator;

import io.github.robotman3000.bukkit.spigotplus.api.JavaPluginFeature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

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
	private List<Material> pickItems = new ArrayList<>();
	private boolean veinMiningEnabled;
	private boolean chargeDurability;
	private boolean chargeDurabilityCreative;

	@EventHandler
	public void onBlockBroken(BlockBreakEvent event) {
		// This gets called when a block is broken
		//logInfo("=================================================");
		//logInfo("1");
		if (event.getPlayer().hasPermission("spigotplus.treecapitator." + event.getPlayer().getGameMode())) {
			//logInfo("2");
			if (event.getPlayer().isSneaking() == shouldBeSneaking) {
				//logInfo("3");
				ItemStack item1 = event.getPlayer().getInventory().getItemInMainHand();
				ItemStack item2 = event.getPlayer().getInventory().getItemInOffHand();
				boolean isPick = false;
				if ((itemIsValid(item1, false, 0) || itemIsValid(item2, false, 1))
						|| (isPick = (itemIsValid(item1, true, 2) || itemIsValid(item2, true, 3)))) {
					//logInfo("4");
					Block block = event.getBlock();
					Location loc = block.getLocation();
					ItemStack axeItem = (item1 != null ? item1 : item2);
					checkBlock(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), axeItem, isPick, 0);

					if (logCount >= maxBlocks) {
						event.getPlayer().sendMessage(ChatColor.RED
								+ "You have exceded the maximum number of logs that can be broken at once.");
					}

					if (chargeDurability) {
						//logInfo("5");
						if ((event.getPlayer().getGameMode() != GameMode.CREATIVE) || chargeDurabilityCreative) {
							//logInfo("6");
							axeItem.setDurability((short) (axeItem.getDurability() + logCount));
						}
					}
					logCount = 0;
				}
			}
		}
	}

	private boolean itemIsValid(ItemStack i, boolean checkPickaxe, int dbgstr) {
		//logInfo("3.1." + dbgstr);
		if (i != null) {
			//logInfo("3.2." + dbgstr);
			List<Material> list = (checkPickaxe ? pickItems : axeItems);
			for (Material type : list) {
				//logInfo("3.3." + dbgstr + " : " + type.ordinal() + " ?= " + i.getType().ordinal());
				if (type.ordinal() == i.getType().ordinal()) {
					//logInfo("3.4." + dbgstr);
					return true;
				}
			}
		}
		return false;

	}

	// TODO: Use these to make the list of ores and logs configurable
	private boolean isLog(Material type) {
		switch (type) {
		case ACACIA_LOG:
		case BIRCH_LOG:
		case DARK_OAK_LOG:
		case JUNGLE_LOG:
		case OAK_LOG:
		case SPRUCE_LOG:
		case STRIPPED_ACACIA_LOG:
		case STRIPPED_BIRCH_LOG:
		case STRIPPED_DARK_OAK_LOG:
		case STRIPPED_JUNGLE_LOG:
		case STRIPPED_OAK_LOG:
		case STRIPPED_SPRUCE_LOG:
			return true;
		default:
			break;
		}
		return false;
	}

	private boolean isOre(Material type) {
		switch (type) {
		case COAL_ORE:
		case DIAMOND_ORE:
		case EMERALD_ORE:
		case GOLD_ORE:
		case IRON_ORE:
		case LAPIS_ORE:
		case NETHER_QUARTZ_ORE:
		case REDSTONE_ORE:			
			return true;
		default:
			break;
		}
		return false;
	}

	private void checkBlock(World world, int x, int y, int z, ItemStack axe, boolean isVein, int iteration) {
		//logInfo("4.1." + iteration);
		Block block = world.getBlockAt(x, y, z);
		Material type = block.getType();
		if ((isVein ? isOre(type) : isLog(type)) && (logCount < this.maxBlocks)) {
			//logInfo("4.2." + iteration);
			if (!chargeDurability || (((axe.getType().getMaxDurability() - axe.getDurability())
					- logCount) > minRemainingDurability)) {
				//logInfo("4.3." + iteration);
				block.breakNaturally(axe);
				logCount++;
				for (int xmod = -1; xmod <= 1; xmod++) {
					for (int ymod = -1; ymod <= 1; ymod++) {
						for (int zmod = -1; zmod <= 1; zmod++) {
							checkBlock(world, xmod + x, ymod + y, zmod + z, axe, isVein, iteration + 1);
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
		this.veinMiningEnabled = getConfig().getBoolean(TreeConfigKeys.veinMiningEnabled.name(), false);
		List<String> items = getConfig().getStringList(TreeConfigKeys.axeItems.name());
		if (items == null) {
			items = Arrays.asList(Material.DIAMOND_AXE.name(), Material.GOLDEN_AXE.name(), Material.IRON_AXE.name(),
					Material.STONE_AXE.name(), Material.WOODEN_AXE.name());
			getConfig().set(TreeConfigKeys.axeItems.name(), items);
		}
		List<String> pickItems = getConfig().getStringList(TreeConfigKeys.pickItems.name());
		if (items == null) {
			items = Arrays.asList(Material.DIAMOND_PICKAXE.name(), Material.GOLDEN_PICKAXE.name(),
					Material.IRON_PICKAXE.name(), Material.STONE_PICKAXE.name(), Material.WOODEN_PICKAXE.name());
			getConfig().set(TreeConfigKeys.pickItems.name(), items);
		}

		for (String str : items) {
			Material material = Material.matchMaterial(str);
			if (material != null) {
				axeItems.add(material);
			} else {
				logInfo("No material match was found for \"" + str + "\" in the list of Axe Items");
			}
		}

		for (String str : pickItems) {
			Material material = Material.matchMaterial(str);
			if (material != null) {
				this.pickItems.add(material);
			} else {
				logInfo("No material match was found for \"" + str + "\" in the list of Pickaxe Items");
			}
		}

	}

	public void saveConfigValues() {
		getConfig().set(TreeConfigKeys.maxBlocks.name(), this.maxBlocks);
		getConfig().set(TreeConfigKeys.shouldBeSneaking.name(), this.shouldBeSneaking);
		getConfig().set(TreeConfigKeys.minRemainingDurability.name(), this.minRemainingDurability);
		getConfig().set(TreeConfigKeys.chargeDurability.name(), this.chargeDurability);
		getConfig().set(TreeConfigKeys.chargeDurabilityCreative.name(), this.chargeDurabilityCreative);
		getConfig().set(TreeConfigKeys.veinMiningEnabled.name(), this.veinMiningEnabled);
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
