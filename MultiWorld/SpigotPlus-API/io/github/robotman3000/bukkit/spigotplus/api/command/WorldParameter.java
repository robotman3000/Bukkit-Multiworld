package io.github.robotman3000.bukkit.spigotplus.api.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

public class WorldParameter extends CommandParameter<World> {

	public WorldParameter(String parameterName) {
		super(parameterName);
	}

	@Override
	public List<String> getTabCompletions(CommandSender sender, String arg) {
		List<String> list = new ArrayList<String>();
    	for (World world : Bukkit.getWorlds()){
    		if(world.getName().startsWith(arg)){
    			list.add(world.getName());
    		}
    	}
    	return list;
	}

	@Override
	public World getParameterValue(String str) {
		return Bukkit.getWorld(str);
	}

}
