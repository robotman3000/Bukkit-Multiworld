package io.github.robotman3000.bukkit.spigotplus.sound;

import io.github.robotman3000.bukkit.multiworld.SpigotPlus;
import io.github.robotman3000.bukkit.spigotplus.api.JavaPluginFeature;

public class AtmosphericSoundsManager extends JavaPluginFeature<SpigotPlus> {

	public AtmosphericSoundsManager(SpigotPlus plugin) {
		super(plugin, "Atmospheric Sound System");
	}

	@Override
	public boolean initalize() {
		
		return true;
	}
}
