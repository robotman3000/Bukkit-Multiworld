package io.github.robotman3000.bukkit.multiworld.world.command;

import io.github.robotman3000.bukkit.multiworld.world.WorldManagerHelper;
import io.github.robotman3000.bukkit.spigotplus.api.JavaPluginCommand;
import io.github.robotman3000.bukkit.spigotplus.api.command.BooleanParameter;
import io.github.robotman3000.bukkit.spigotplus.api.command.CommandParameter;
import io.github.robotman3000.bukkit.spigotplus.api.command.EnumParameter;
import io.github.robotman3000.bukkit.spigotplus.api.command.StringParameter;

import java.io.File;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.World.Environment;
import org.bukkit.ChatColor;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
public class WorldCreateCommand extends JavaPluginCommand {

	@Override
	public boolean onCommand(CommandSender sender, Command arg1,
			String arg2, String[] args) {
	
		Object[] params = {"", 
						   String.valueOf(new Random().nextLong()), 
						   WorldType.NORMAL, 
						   Environment.NORMAL,
						   true};
		
		if(args.length > 0){
			if(args.length > params.length){
				sender.sendMessage(ChatColor.RED + "Too many arguments were provided!");
			} else {
				for(int index = (args.length - 1); index > -1; index--){
					// TODO: If invalid enum values are passed in we must send the player an error message
					Object obj = getParameters().get(index).getParameterValue(args[index]);
					if(obj != null){
						params[index] = obj;
					} else {
						sender.sendMessage(ChatColor.RED + "Invalid parameter supplied at index " + index);
						return false;
					}
				}
			
	            if(!WorldManagerHelper.isWorldFolder(new File(Bukkit.getWorldContainer(), (String) params[0]))){
	            	
	                WorldCreator creator = new WorldCreator((String) params[0]);
	                
	                String seedStr = (String) params[1];
	                long seed = seedStr.hashCode();
	                try {
	                	seed = Long.valueOf(seedStr);
	                } catch (NumberFormatException e){}
	             
	                creator.seed(seed);
	                creator.type((WorldType) params[2]);
	                creator.environment((Environment) params[3]);
	                creator.generateStructures((boolean) params[4]);

	                sender.sendMessage("Generating world");
	                creator.createWorld().save();
	                //Bukkit.getLogger().info("Generator String: " + creator.generatorSettings());
	                sender.sendMessage("Done generating world");
	                return true;
	            } else {
	            	sender.sendMessage(ChatColor.RED + "That world already exists. You must delete the world before creating a new world with the same name!");
	            }
			}
		} else {
			sender.sendMessage(ChatColor.RED + "You must provide at least a world name");
		}
        
        return false;
	}

	@Override
	protected void initializeParameters(List<CommandParameter<?>> params) {
		//<World Name> <Seed> <World Type> <World Environment> <Generate Structures>
		params.add(new StringParameter("World Name"));
		params.add(new StringParameter("Seed"));
		params.add(new EnumParameter<WorldType>("World Type", WorldType.class));
		params.add(new EnumParameter<Environment>("World Environment", Environment.class));
		params.add(new BooleanParameter("Generate Structures"));
	}

}
