package io.github.robotman3000.bukkit.multiworld.inventory;

import java.util.UUID;

public class InventoryKey {

	private final UUID playerKey;
	private final UUID worldGroupKey;

	public InventoryKey(UUID playerKey, UUID worldKey) {
		this.playerKey = playerKey;
		this.worldGroupKey = worldKey;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof InventoryKey) {
			InventoryKey key = (InventoryKey) obj;
			if (playerKey.equals(key.playerKey)) {
				if (worldGroupKey.equals(key.worldGroupKey)) {
					return true;
				}
			}
		}
		return false;
	}

	public UUID getPlayerKey() {
		return playerKey;
	}

	public UUID getWorldGroupKey() {
		return worldGroupKey;
	}

	@Override
	public int hashCode() {
		int result = 3; // This just needs to start as non-zero

		int c2 = playerKey.hashCode();
		result = 37 * result + c2;

		int c3 = worldGroupKey.hashCode();
		result = 37 * result + c3;

		return result;
	}

	@Override
	public String toString() {
		return "InventoryKey {" + hashCode() + "} {" + System.identityHashCode(this) + "} [playerKey" + "=" + playerKey
				+ ", worldGroupKey=" + worldGroupKey + "]";
	}
}
