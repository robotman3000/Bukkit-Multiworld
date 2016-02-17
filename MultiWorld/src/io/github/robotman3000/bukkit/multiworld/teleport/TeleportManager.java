package io.github.robotman3000.bukkit.multiworld.teleport;

import io.github.robotman3000.bukkit.multiworld.SpigotPlus;
import io.github.robotman3000.bukkit.multiworld.teleport.commands.ListPortalsCommand;
import io.github.robotman3000.bukkit.spigotplus.api.CommandEnumMethods;
import io.github.robotman3000.bukkit.spigotplus.api.JavaPluginFeature;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPortalEvent;

public class TeleportManager extends JavaPluginFeature<SpigotPlus> {

    private enum Commands implements CommandEnumMethods<SpigotPlus> {
        listportals {
            @Override
            public CommandExecutor getExecutor(SpigotPlus plugin) {
                return new ListPortalsCommand();
            }

            @Override
            public TabCompleter getTabCompleter(SpigotPlus plugin) {
                return null;
            }
        };

        @Override
        public abstract CommandExecutor getExecutor(SpigotPlus plugin);

        @Override
        public abstract TabCompleter getTabCompleter(SpigotPlus plugin);

    }

    public TeleportManager(SpigotPlus plugin) {
        super(plugin, "Teleport Manager");
    }

    @Override
    public void initalize() {
        logInfo("Initializing...");
        for (Commands cmd : Commands.values()) { // Register Commands
            logInfo("Registering Command: " + cmd);
            PluginCommand pCmd = getPlugin().getCommand(cmd.name());
            pCmd.setExecutor(cmd.getExecutor(getPlugin()));
            pCmd.setTabCompleter(cmd.getTabCompleter(getPlugin()));
        }

        logInfo("Registering Event Handlers");
        for (Listener evt : getEventHandlers()) {
            getPlugin().getServer().getPluginManager().registerEvents(evt, getPlugin());
        }
        logInfo("Loading Config");
        loadConfig();
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
    protected void saveConfig() {
        // TODO Auto-generated method stub
        super.saveConfig();
    }

    @Override
    public void shutdown() {
        // TODO Auto-generated method stub
        super.shutdown();
    }

}
