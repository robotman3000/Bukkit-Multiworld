package io.github.robotman3000.bukkit.spigotplus.api;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

public interface CommandEnumMethods<T extends JavaPlugin> {
    public CommandExecutor getExecutor(T plugin);

    public TabCompleter getTabCompleter(T plugin);
}
