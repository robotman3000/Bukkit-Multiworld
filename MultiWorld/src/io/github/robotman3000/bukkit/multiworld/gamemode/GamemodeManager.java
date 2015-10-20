package io.github.robotman3000.bukkit.multiworld.gamemode;

import io.github.robotman3000.bukkit.multiworld.MultiWorld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class GamemodeManager implements Listener, CommandExecutor {

    public final String[] commands = {};
    private final MultiWorld plugin;
    private final HashMap<String, GameMode> gamemodes = new HashMap<>();

    public GamemodeManager(MultiWorld multiWorld) {
        plugin = multiWorld;
    }

    private void forceGamemode(PlayerEvent evt) {
        Bukkit.getLogger().info("Forcing Gamemode");
        if (!evt.getPlayer().hasPermission("multiworld.gamemodeExempt")) {
            Bukkit.getLogger().info("Player Not Exempt");
            GameMode gamemode = gamemodes.get(evt.getPlayer().getWorld().getName());
            if (gamemode == null) {
                gamemode = Bukkit.getDefaultGameMode();
            }
            evt.getPlayer().setGameMode(gamemode);
        }
    }

    public void loadGamemodeConfig() {
        String path = "gamemode";
        for (GameMode gamemode : GameMode.values()) {
            List<String> list = plugin.getConfig().getStringList(path + "." + gamemode.name());
            if (list != null) {
                for (String str : list) {
                    GameMode game = gamemodes.put(str, gamemode);
                    if (game != null) {
                        Bukkit.getLogger().warning("[MultiWorld] The world " + str
                                                           + " already had the gamemode " + game
                                                           + " assigned. It has been overwriten");
                    }
                }
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // TODO Auto-generated method stub
        return true;
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent evt) {
        forceGamemode(evt);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent evt) {
        forceGamemode(evt);
    }

    public void saveGamemodeConfig() {
        HashMap<GameMode, List<String>> config = new HashMap<GameMode, List<String>>();
        for (GameMode gamemode : GameMode.values()) {
            config.put(gamemode, new ArrayList<String>());
        }

        for (World world : Bukkit.getWorlds()) {
            GameMode gamemode = gamemodes.get(world.getName());
            if (gamemode == null) {
                gamemode = Bukkit.getDefaultGameMode();
                Bukkit.getLogger()
                        .info("[MultiWorld] The world "
                                      + world.getName()
                                      + " was not defined in the config. Defining it with gamemode "
                                      + gamemode.name());
            }
            config.get(gamemode).add(world.getName());

        }
        for (GameMode gamemode : config.keySet()) {
            plugin.getConfig().set("gamemode." + gamemode.name(), config.get(gamemode));
        }

    }
}
