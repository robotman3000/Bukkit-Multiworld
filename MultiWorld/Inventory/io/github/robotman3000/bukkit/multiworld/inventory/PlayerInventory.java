package io.github.robotman3000.bukkit.multiworld.inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
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
				player.setAllowFlight((boolean) getPropertyFromInventory(inventory));

			}

			@Override
			public Object getPropertyFromPlayer(Player player) {
				return player.getAllowFlight();
			}
		},
		BED_SPAWN_POINT {
			@Override
			public void setPropertyFromInventory(Player player,
					PlayerInventory inventory) {
				if(getPropertyFromInventory(inventory) != null) {
					player.setBedSpawnLocation((Location) getPropertyFromInventory(inventory));
				}
			}

			@Override
			public Object getPropertyFromPlayer(Player player) {
				return player.getBedSpawnLocation();
			}
		},
		COMPASS_TARGET {
			@Override
			public void setPropertyFromInventory(Player player,
					PlayerInventory inventory) {
				if(getPropertyFromInventory(inventory) != null) {
					player.setCompassTarget((Location) getPropertyFromInventory(inventory));
				}
			}

			@Override
			public Object getPropertyFromPlayer(Player player) {
				return player.getCompassTarget();
			}
		},
		DISPLAY_NAME {
			@Override
			public void setPropertyFromInventory(Player player,
					PlayerInventory inventory) {
				player.setDisplayName((String) getPropertyFromInventory(inventory));
			}

			@Override
			public Object getPropertyFromPlayer(Player player) {
				return player.getDisplayName();
			}
		},
		EXHAUSTION {
			@Override
			public void setPropertyFromInventory(Player player,
					PlayerInventory inventory) {
				player.setExhaustion(getAsFloat(getPropertyFromInventory(inventory)));
			}

			@Override
			public Object getPropertyFromPlayer(Player player) {
				return player.getExhaustion();
			}
		},
		XP_POINTS {
			@Override
			public void setPropertyFromInventory(Player player,
					PlayerInventory inventory) {
				player.setExp(getAsFloat(getPropertyFromInventory(inventory)));
			}

			@Override
			public Object getPropertyFromPlayer(Player player) {
				return player.getExp();
			}
		},
		FALL_DISTANCE {
			@Override
			public void setPropertyFromInventory(Player player,
					PlayerInventory inventory) {
				player.setFallDistance(getAsFloat(getPropertyFromInventory(inventory)));
			}

			@Override
			public Object getPropertyFromPlayer(Player player) {
				return player.getFallDistance();
			}
		},
		FIRE_TICKS {
			@Override
			public void setPropertyFromInventory(Player player,
					PlayerInventory inventory) {
				player.setFireTicks((int) getPropertyFromInventory(inventory));
			}

			@Override
			public Object getPropertyFromPlayer(Player player) {
				return player.getFireTicks();
			}
		},
		IS_FLYING {
			@Override
			public void setPropertyFromInventory(Player player,
					PlayerInventory inventory) {
				player.setFlying((boolean) getPropertyFromInventory(inventory));
			}

			@Override
			public Object getPropertyFromPlayer(Player player) {
				return player.isFlying();
			}
		},
		FOOD_LEVEL {
			@Override
			public void setPropertyFromInventory(Player player,
					PlayerInventory inventory) {
				player.setFoodLevel((int) getPropertyFromInventory(inventory));
			}

			@Override
			public Object getPropertyFromPlayer(Player player) {
				return player.getFoodLevel();
			}
		},
		HEALTH_POINTS {
			@Override
			public void setPropertyFromInventory(Player player,
					PlayerInventory inventory) {
				player.setHealth((double) getPropertyFromInventory(inventory));
			}

			@Override
			public Object getPropertyFromPlayer(Player player) {
				return player.getHealth();
			}
		},
		XP_LEVEL {
			@Override
			public void setPropertyFromInventory(Player player,
					PlayerInventory inventory) {
				player.setLevel((int) getPropertyFromInventory(inventory));
			}

			@Override
			public Object getPropertyFromPlayer(Player player) {
				return player.getLevel();
			}
		},
		REMAINING_AIR {
			@Override
			public void setPropertyFromInventory(Player player,
					PlayerInventory inventory) {
				player.setRemainingAir((int) getPropertyFromInventory(inventory));
			}

			@Override
			public Object getPropertyFromPlayer(Player player) {
				return player.getRemainingAir();
			}
		},
		FOOD_SATURATION {
			@Override
			public void setPropertyFromInventory(Player player,
					PlayerInventory inventory) {
				player.setSaturation(getAsFloat(getPropertyFromInventory(inventory)));
			}

			@Override
			public Object getPropertyFromPlayer(Player player) {
				return player.getSaturation();
			}
		},
		VELOCITY {
			@Override
			public void setPropertyFromInventory(Player player,
					PlayerInventory inventory) {
				if(getPropertyFromInventory(inventory) != null) {
					player.setVelocity((Vector) getPropertyFromInventory(inventory));
				}
			}

			@Override
			public Object getPropertyFromPlayer(Player player) {
				return player.getVelocity();
			}
		},
		ARMOR_CONTENTS {
			@Override
			public void setPropertyFromInventory(Player player,
					PlayerInventory inventory) {
				player.getInventory().setArmorContents(
						listToArray(getPropertyFromInventory(inventory)));
			}

			@Override
			public Object getPropertyFromPlayer(Player player) {
				return player.getInventory().getArmorContents();
			}
		},
		INVENTORY_CONTENTS {
			@Override
			public void setPropertyFromInventory(Player player,
					PlayerInventory inventory) {
				player.getInventory().setContents(
						listToArray(getPropertyFromInventory(inventory)));
			}

			@Override
			public Object getPropertyFromPlayer(Player player) {
				return player.getInventory().getContents();
			}
		},
		ENDER_CHEST {
			@Override
			public void setPropertyFromInventory(Player player,
					PlayerInventory inventory) {
				player.getEnderChest().setContents(
						listToArray(getPropertyFromInventory(inventory)));
			}

			@Override
			public Object getPropertyFromPlayer(Player player) {
				return player.getEnderChest().getContents();
			}
		},
		PLAYER_LOCATION {
			@Override
			public void setPropertyFromInventory(Player player,
					PlayerInventory inventory) {
				// player.teleport((Location)
				// getPropertyFromInventory(inventory));
			}

			@Override
			public Object getPropertyFromPlayer(Player player) {
				return player.getLocation();
			}
		};

		private void setProperty(Object[] values, Object value) {
			if (!(values == null || value == null)) {
				values[ordinal()] = value;
			} else {
				Bukkit.getLogger().info("PlayerInventory.class:setProperty(): Null value found");
			}
		}

		public abstract void setPropertyFromInventory(Player player, PlayerInventory inventory);

		public void setPropertyFromPlayer(PlayerInventory inventory, Player player) {
			inventory.getData()[ordinal()] = getPropertyFromPlayer(player);
		}

		public abstract Object getPropertyFromPlayer(Player player);

		public Object getPropertyFromInventory(PlayerInventory inventory) {
			return inventory.getData()[ordinal()];
		}

		protected Object getDefaultValue() {
			Bukkit.getLogger().warning("PlayerInventory.class:getDefaultValue(): No default value has been provided for player attr " + InventoryProperties.values()[ordinal()].name());
			Thread.dumpStack();
			return null;
		}
		
		public void resetProperty(PlayerInventory inventory){
			Object obj = getDefaultValue();
			if(obj != null) {
				setProperty(inventory.getData(), obj);
			}
		}

		private static float getAsFloat(Object object) {
			return Float.valueOf(String.valueOf(object));
		}

		private static ItemStack[] listToArray(Object object) {
			if (object instanceof List) {
				//Bukkit.getServer().getLogger().info("List");
				List<?> obj = (List<?>) object;
				return obj.toArray(new ItemStack[0]);
			} else if (object instanceof ItemStack[]) {
				//Bukkit.getServer().getLogger().info("ItemStack[]");
				ItemStack[] obj = (ItemStack[]) object;
				return obj;
			}
			//Bukkit.getServer().getLogger().info("None");
			return null;
		}

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
	 * 
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
	 * 
	 * @param player
	 * @param inventory
	 */
	public static void setPlayerInventory(Player player, PlayerInventory inventory) {
		for (InventoryProperties prop : InventoryProperties.values()) {
			if(prop.getPropertyFromInventory(inventory) == null){
				prop.resetProperty(inventory);
			}
			prop.setPropertyFromInventory(player, inventory);
		}
	}

}
