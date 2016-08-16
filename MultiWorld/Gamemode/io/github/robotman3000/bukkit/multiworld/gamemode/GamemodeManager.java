package io.github.robotman3000.bukkit.multiworld.gamemode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import io.github.robotman3000.bukkit.spigotplus.api.JavaPluginFeature;

public class GamemodeManager extends JavaPluginFeature {

    private final HashMap<String, GameMode> gamemodes = new HashMap<>();

    private void forceGamemode(PlayerEvent evt) {
        if (!evt.getPlayer().hasPermission("spigotplus.gamemode.exempt")) {
            GameMode gamemode = getWorldGamemode(evt.getPlayer().getWorld().getName());
            if (!evt.getPlayer().getGameMode().equals(gamemode)) {
                evt.getPlayer().setGameMode(gamemode);
            }
        }
    }

    private synchronized GameMode getWorldGamemode(String worldName){
        GameMode gamemode = gamemodes.get(worldName);
        if (gamemode == null) {
            gamemode = Bukkit.getDefaultGameMode();
        }
        return gamemode;
    }
    
    protected void loadConfig() {
        String path = "gamemode";
        for (GameMode gamemode : GameMode.values()) {
            List<String> list = getConfig().getStringList(path + "." + gamemode.name());
            if (list != null) {
                for (String str : list) {
                    GameMode game = gamemodes.put(str, gamemode);
                    if (game != null) {
                        logWarn("The world " + str + " already had the gamemode " + game
                                + " assigned. It has been overwriten");
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent evt) {
        forceGamemode(evt);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent evt) {
        forceGamemode(evt);
    }

    @EventHandler
    public void onGamemodeChange(PlayerGameModeChangeEvent event){
    	GameMode newMode = event.getNewGameMode();
    	String worldName = event.getPlayer().getWorld().getName();
    	if(!event.getPlayer().hasPermission("spigotplus.gamemode." + worldName + "." + newMode)){
    		boolean modeFound = false;
    		for(GameMode mode : GameMode.values()){
    			if(event.getPlayer().hasPermission("spigotplus.gamemode." + worldName + "." + mode)){
    				modeFound = true;
    				break;
    			}
    		}

    		event.setCancelled(true);
    		event.getPlayer().sendMessage("You are not permitted to use the " + newMode + " gamemode in this world");
    		
    		if(!modeFound){
    			logWarn("The player " + event.getPlayer().getName() + " is not permitted to use any gamemode in the world \"" + worldName + "\"");
    			event.getPlayer().kickPlayer("There is a configuration error with the gamemodes you are permitted to use. Please contact the server administrator");
    		}
    	}
    }

	public void saveConfigValues() {
        HashMap<GameMode, List<String>> config = new HashMap<GameMode, List<String>>();
        for (GameMode gamemode : GameMode.values()) {
            config.put(gamemode, new ArrayList<String>());
        }

        for (World world : Bukkit.getWorlds()) {
            GameMode gamemode = gamemodes.get(world.getName());
            if (gamemode == null) {
                gamemode = Bukkit.getDefaultGameMode();
                logInfo("The world " + world.getName()
                        + " was not defined in the config. Defining it with gamemode "
                        + gamemode.name());
            }
            config.get(gamemode).add(world.getName());

        }
        for (GameMode gamemode : config.keySet()) {
            getConfig().set("gamemode." + gamemode.name(), config.get(gamemode));
        }
    }

    @Override
    public void shutdown() {
        saveConfigValues();
    }

    @Override
    protected boolean startup() {
    	loadConfig();
    	return true;
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
