package io.github.robotman3000.bukkit.spigotplus.api.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerParameter extends CommandParameter<Player> {

	public PlayerParameter(String parameterName) {
		super(parameterName);
	}

	@Override
	public List<String> getTabCompletions(CommandSender sender, String arg) {
		List<String> list = new ArrayList<String>();
    	for (Player player : Bukkit.getOnlinePlayers()){
    		if(player.getName().startsWith(arg)){
    			list.add(player.getName());
    		}
    	}
    	return list;
	}

	@Override
	public Player getParameterValue(String str) {
		return Bukkit.getPlayer(str);
	}

}
