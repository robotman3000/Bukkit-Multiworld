package io.github.robotman3000.bukkit.spigotplus.mods;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class PluginModBase {

	private JavaPlugin host;

	public PluginModBase(JavaPlugin hostPlugin){
		this.host = hostPlugin;
	}

	protected final JavaPlugin getHost() {
		return host;
	}

	public abstract String getName();

	public abstract void initialize(ConfigurationSection config);

	public abstract void shutdown();
}
