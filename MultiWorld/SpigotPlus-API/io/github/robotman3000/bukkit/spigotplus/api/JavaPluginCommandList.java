package io.github.robotman3000.bukkit.spigotplus.api;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

public interface JavaPluginCommandList {

	public CommandExecutor getExecutor();

	public TabCompleter getTabCompleter();
	
	public String name();
}
