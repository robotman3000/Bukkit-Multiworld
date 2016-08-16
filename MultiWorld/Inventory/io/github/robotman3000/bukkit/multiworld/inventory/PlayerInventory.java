package io.github.robotman3000.bukkit.multiworld.inventory;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class PlayerInventory implements ConfigurationSerializable {

	private Object[] data = new Object[InventoryProperties.values().length];

	private Object[] getData() {
		return data;
	}

	public enum InventoryProperties {
		// TODO: Add any remaining player properties
		CAN_FLY {
			@Override
			public void setPropertyFromInventory(Player player,
					PlayerInventory inventory) {
				player.setAllowFlight((boolean) inventory.getData()[ordinal()]);

			}

			@Override
			public void setPropertyFromPlayer(PlayerInventory inventory,
					Player player) {
				inventory.getData()[ordinal()] = player.getAllowFlight();

			}
		},
		BED_SPAWN_POINT {
			@Override
			public void setPropertyFromInventory(Player player,
					PlayerInventory inventory) {
				player.setBedSpawnLocation((Location) inventory.getData()[ordinal()]);
			}

			@Override
			public void setPropertyFromPlayer(PlayerInventory inventory,
					Player player) {
				inventory.getData()[ordinal()] = player.getBedSpawnLocation();
			}
		},
		COMPASS_TARGET {
			@Override
			public void setPropertyFromInventory(Player player,
					PlayerInventory inventory) {
				player.setCompassTarget((Location) inventory.getData()[ordinal()]);
			}

			@Override
			public void setPropertyFromPlayer(PlayerInventory inventory,
					Player player) {
				inventory.getData()[ordinal()] = player.getCompassTarget();
			}
		},
		DISPLAY_NAME {
			@Override
			public void setPropertyFromInventory(Player player,
					PlayerInventory inventory) {
				player.setDisplayName((String) inventory.getData()[ordinal()]);
			}

			@Override
			public void setPropertyFromPlayer(PlayerInventory inventory,
					Player player) {
				inventory.getData()[ordinal()] = player.getDisplayName();
			}
		},
		EXHAUSTION {
			@Override
			public void setPropertyFromInventory(Player player,
					PlayerInventory inventory) {
				player.setExhaustion((float) inventory.getData()[ordinal()]);
			}

			@Override
			public void setPropertyFromPlayer(PlayerInventory inventory,
					Player player) {
				inventory.getData()[ordinal()] = player.getExhaustion();
			}
		},
		XP_POINTS {
			@Override
			public void setPropertyFromInventory(Player player,
					PlayerInventory inventory) {
				player.setExp((float) inventory.getData()[ordinal()]);
			}

			@Override
			public void setPropertyFromPlayer(PlayerInventory inventory,
					Player player) {
				inventory.getData()[ordinal()] = player.getExp();
			}
		},
		FALL_DISTANCE {
			@Override
			public void setPropertyFromInventory(Player player,
					PlayerInventory inventory) {
				player.setFallDistance((float) inventory.getData()[ordinal()]);
			}

			@Override
			public void setPropertyFromPlayer(PlayerInventory inventory,
					Player player) {
				inventory.getData()[ordinal()] = player.getFallDistance();
			}
		},
		FIRE_TICKS {
			@Override
			public void setPropertyFromInventory(Player player,
					PlayerInventory inventory) {
				player.setFireTicks((int) inventory.getData()[ordinal()]);
			}

			@Override
			public void setPropertyFromPlayer(PlayerInventory inventory,
					Player player) {
				inventory.getData()[ordinal()] = player.getFireTicks();
			}
		},
		IS_FLYING {
			@Override
			public void setPropertyFromInventory(Player player,
					PlayerInventory inventory) {
				player.setFlying((boolean) inventory.getData()[ordinal()]);
			}

			@Override
			public void setPropertyFromPlayer(PlayerInventory inventory,
					Player player) {
				inventory.getData()[ordinal()] = player.isFlying();
			}
		},
		FOOD_LEVEL {
			@Override
			public void setPropertyFromInventory(Player player,
					PlayerInventory inventory) {
				player.setFoodLevel((int) inventory.getData()[ordinal()]);
			}

			@Override
			public void setPropertyFromPlayer(PlayerInventory inventory,
					Player player) {
				inventory.getData()[ordinal()] = player.getFoodLevel();
			}
		},
		HEALTH_POINTS {
			@Override
			public void setPropertyFromInventory(Player player,
					PlayerInventory inventory) {
				player.setHealth((double) inventory.getData()[ordinal()]);
			}

			@Override
			public void setPropertyFromPlayer(PlayerInventory inventory,
					Player player) {
				inventory.getData()[ordinal()] = player.getHealth();
			}
		},
		XP_LEVEL {
			@Override
			public void setPropertyFromInventory(Player player,
					PlayerInventory inventory) {
				player.setLevel((int) inventory.getData()[ordinal()]);
			}

			@Override
			public void setPropertyFromPlayer(PlayerInventory inventory,
					Player player) {
				inventory.getData()[ordinal()] = player.getLevel();
			}
		},
		REMAINING_AIR {
			@Override
			public void setPropertyFromInventory(Player player,
					PlayerInventory inventory) {
				player.setRemainingAir((int) inventory.getData()[ordinal()]);
			}

			@Override
			public void setPropertyFromPlayer(PlayerInventory inventory,
					Player player) {
				inventory.getData()[ordinal()] = player.getRemainingAir();
			}
		},
		FOOD_SATURATION {
			@Override
			public void setPropertyFromInventory(Player player,
					PlayerInventory inventory) {
				player.setSaturation((float) inventory.getData()[ordinal()]);
			}

			@Override
			public void setPropertyFromPlayer(PlayerInventory inventory,
					Player player) {
				inventory.getData()[ordinal()] = player.getSaturation();
			}
		},
		VELOCITY {
			@Override
			public void setPropertyFromInventory(Player player,
					PlayerInventory inventory) {
				player.setVelocity((Vector) inventory.getData()[ordinal()]);
			}

			@Override
			public void setPropertyFromPlayer(PlayerInventory inventory,
					Player player) {
				inventory.getData()[ordinal()] = player.getVelocity();
			}
		},
		ARMOR_CONTENTS {
			@Override
			public void setPropertyFromInventory(Player player,
					PlayerInventory inventory) {
				player.getInventory().setArmorContents(
						(ItemStack[]) inventory.getData()[ordinal()]);
			}

			@Override
			public void setPropertyFromPlayer(PlayerInventory inventory,
					Player player) {
				inventory.getData()[ordinal()] = player.getInventory()
						.getArmorContents();
			}
		},
		INVENTORY_CONTENTS {
			@Override
			public void setPropertyFromInventory(Player player,
					PlayerInventory inventory) {
				player.getInventory().setContents(
						(ItemStack[]) inventory.getData()[ordinal()]);
			}

			@Override
			public void setPropertyFromPlayer(PlayerInventory inventory,
					Player player) {
				inventory.getData()[ordinal()] = player.getInventory()
						.getContents();
			}
		},
		ENDER_CHEST {
			@Override
			public void setPropertyFromInventory(Player player,
					PlayerInventory inventory) {
				player.getEnderChest().setContents(
						(ItemStack[]) inventory.getData()[ordinal()]);
			}

			@Override
			public void setPropertyFromPlayer(PlayerInventory inventory,
					Player player) {
				inventory.getData()[ordinal()] = player.getEnderChest()
						.getContents();
			}
		},
		PLAYER_LOCATION {
			@Override
			public void setPropertyFromInventory(Player player,
					PlayerInventory inventory) {
				player.teleport((Location) inventory.getData()[ordinal()]);
			}

			@Override
			public void setPropertyFromPlayer(PlayerInventory inventory,
					Player player) {
				inventory.getData()[ordinal()] = player.getLocation();
			}
		};

		protected void setProperty(Object[] values, Object value) {
			values[ordinal()] = value;
		}

		public abstract void setPropertyFromInventory(Player player,
				PlayerInventory inventory);

		public abstract void setPropertyFromPlayer(PlayerInventory inventory,
				Player player);
	}

	private PlayerInventory() {
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		for (InventoryProperties prop : InventoryProperties.values()) {
			map.put(prop.name(), getData()[prop.ordinal()]);
		}
		return map;
	}

	public static PlayerInventory deserialize(Map<String, Object> map) {
		PlayerInventory inventory = new PlayerInventory();
		for (InventoryProperties prop : InventoryProperties.values()) {
			if (map.containsKey(prop.name())) {
				prop.setProperty(inventory.getData(), map.get(prop.name()));
			}
		}
		return inventory;
	}

	/**
	 * Used to write the players inventory to a PlayerInventory object
	 * @param player
	 * @return
	 */
	public static PlayerInventory getPlayerInventory(Player player) {
		PlayerInventory inventory = new PlayerInventory();
		for (InventoryProperties prop : InventoryProperties.values()) {
			prop.setPropertyFromPlayer(inventory, player);
		}
		return inventory;
	}

	/**
	 * Used to write the contents of a PlayerInventory to a player
	 * @param player
	 * @param inventory
	 */
	public static void setPlayerInventory(Player player,
			PlayerInventory inventory) {
		for (InventoryProperties prop : InventoryProperties.values()) {
			prop.setPropertyFromInventory(player, inventory);
		}
	}

}
