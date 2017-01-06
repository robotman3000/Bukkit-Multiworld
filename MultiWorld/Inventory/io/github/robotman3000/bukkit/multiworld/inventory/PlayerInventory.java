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

			@Override
			protected Object getDefaultValue() {
				// TODO Auto-generated method stub
				return null;
			}
		},
		BED_SPAWN_POINT {
			@Override
			public void setPropertyFromInventory(Player player,
					PlayerInventory inventory) {
				player.setBedSpawnLocation((Location) getPropertyFromInventory(inventory));
			}

			@Override
			public Object getPropertyFromPlayer(Player player) {
				return player.getBedSpawnLocation();
			}

			@Override
			protected Object getDefaultValue() {
				// TODO Auto-generated method stub
				return null;
			}
		},
		COMPASS_TARGET {
			@Override
			public void setPropertyFromInventory(Player player,
					PlayerInventory inventory) {
				player.setCompassTarget((Location) getPropertyFromInventory(inventory));
			}

			@Override
			public Object getPropertyFromPlayer(Player player) {
				return player.getCompassTarget();
			}

			@Override
			protected Object getDefaultValue() {
				// TODO Auto-generated method stub
				return null;
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

			@Override
			protected Object getDefaultValue() {
				// TODO Auto-generated method stub
				return null;
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

			@Override
			protected Object getDefaultValue() {
				// TODO Auto-generated method stub
				return null;
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

			@Override
			protected Object getDefaultValue() {
				// TODO Auto-generated method stub
				return null;
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

			@Override
			protected Object getDefaultValue() {
				// TODO Auto-generated method stub
				return null;
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

			@Override
			protected Object getDefaultValue() {
				// TODO Auto-generated method stub
				return null;
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

			@Override
			protected Object getDefaultValue() {
				// TODO Auto-generated method stub
				return null;
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

			@Override
			protected Object getDefaultValue() {
				// TODO Auto-generated method stub
				return null;
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

			@Override
			protected Object getDefaultValue() {
				// TODO Auto-generated method stub
				return null;
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

			@Override
			protected Object getDefaultValue() {
				// TODO Auto-generated method stub
				return null;
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

			@Override
			protected Object getDefaultValue() {
				// TODO Auto-generated method stub
				return null;
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

			@Override
			protected Object getDefaultValue() {
				// TODO Auto-generated method stub
				return null;
			}
		},
		VELOCITY {
			@Override
			public void setPropertyFromInventory(Player player,
					PlayerInventory inventory) {
				player.setVelocity((Vector) getPropertyFromInventory(inventory));
			}

			@Override
			public Object getPropertyFromPlayer(Player player) {
				return player.getVelocity();
			}

			@Override
			protected Object getDefaultValue() {
				// TODO Auto-generated method stub
				return null;
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

			@Override
			protected Object getDefaultValue() {
				// TODO Auto-generated method stub
				return null;
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

			@Override
			protected Object getDefaultValue() {
				// TODO Auto-generated method stub
				return null;
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

			@Override
			protected Object getDefaultValue() {
				// TODO Auto-generated method stub
				return null;
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

			@Override
			protected Object getDefaultValue() {
				// TODO Auto-generated method stub
				return null;
			}
		};

		private void setProperty(Object[] values, Object value) {
			values[ordinal()] = value;
		}

		public abstract void setPropertyFromInventory(Player player,
				PlayerInventory inventory);

		public void setPropertyFromPlayer(PlayerInventory inventory,
				Player player) {
			inventory.getData()[ordinal()] = getPropertyFromPlayer(player);
		}

		public abstract Object getPropertyFromPlayer(Player player);

		public Object getPropertyFromInventory(PlayerInventory inventory) {
			return inventory.getData()[ordinal()];
		}

		protected abstract Object getDefaultValue();
		
		public void resetProperty(PlayerInventory inventory){
			setProperty(inventory.getData(), getDefaultValue());
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
	public static void setPlayerInventory(Player player,
			PlayerInventory inventory) {
		for (InventoryProperties prop : InventoryProperties.values()) {
			prop.setPropertyFromInventory(player, inventory);
		}
	}

}
