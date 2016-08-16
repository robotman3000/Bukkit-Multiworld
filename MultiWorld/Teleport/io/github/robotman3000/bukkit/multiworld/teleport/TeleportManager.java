package io.github.robotman3000.bukkit.multiworld.teleport;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.robotman3000.bukkit.multiworld.teleport.commands.ListPortalsCommand;
import io.github.robotman3000.bukkit.spigotplus.api.JavaPluginCommand;
import io.github.robotman3000.bukkit.spigotplus.api.JavaPluginCommandList;
import io.github.robotman3000.bukkit.spigotplus.api.JavaPluginFeature;

public class TeleportManager extends JavaPluginFeature {

    private enum Commands implements JavaPluginCommandList {
        listportals {
            @Override
            public CommandExecutor getExecutor() {
                return new ListPortalsCommand();
            }

            @Override
            public TabCompleter getTabCompleter() {
                return null;
            }
        };

        @Override
        public abstract CommandExecutor getExecutor();

        @Override
        public abstract TabCompleter getTabCompleter();

    }
    
    @EventHandler
    public void onEntityPortalEvent(EntityPortalEvent evt) {

    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent evt) {

    }

    @EventHandler
    public void onPlayerPortalEvent(PlayerPortalEvent evt) {

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
