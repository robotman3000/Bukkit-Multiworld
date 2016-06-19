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
import io.github.robotman3000.bukkit.spigotplus.api.JavaPluginFeature;

public class TeleportManager extends JavaPluginFeature {

    private enum Commands implements JavaPluginCommand {
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

    public TeleportManager() {
        setFeatureName("Teleport Manager");
    }

    @Override
    public boolean initalize() {
        logInfo("Initializing...");
        for (Commands cmd : Commands.values()) { // Register Commands
            logInfo("Registering Command: " + cmd);
            PluginCommand pCmd = getPlugin().getCommand(cmd.name());
            pCmd.setExecutor(cmd.getExecutor());
            pCmd.setTabCompleter(cmd.getTabCompleter());
        }

        logInfo("Registering Event Handlers");
        for (Listener evt : getEventHandlers()) {
            getPlugin().getServer().getPluginManager().registerEvents(evt, getPlugin());
        }
        logInfo("Loading Config");
        loadConfig();
        return true;
    }

    @Override
    protected void loadConfig() {
        // TODO Auto-generated method stub
        super.loadConfig();
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
	public void saveConfig() {
        // TODO Auto-generated method stub
        super.saveConfig();
    }

    @Override
    public void shutdown() {
        // TODO Auto-generated method stub
        super.shutdown();
    }
    
	@Override
	public int getMinimumMajorVersion() {
		return 2;
	}

	@Override
	public int getMinimumMinorVersion() {
		return 0;
	}

}
