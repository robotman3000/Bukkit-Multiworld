package io.github.robotman3000.bukkit.spigotplus.api;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

public interface JavaPluginCommand {
    public CommandExecutor getExecutor();

    public TabCompleter getTabCompleter();

	public String name();
}
