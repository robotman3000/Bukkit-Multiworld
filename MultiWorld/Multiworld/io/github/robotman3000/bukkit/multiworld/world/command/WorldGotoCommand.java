package io.github.robotman3000.bukkit.multiworld.world.command;

import io.github.robotman3000.bukkit.spigotplus.api.JavaPluginCommand;
import io.github.robotman3000.bukkit.spigotplus.api.command.CommandParameter;
import io.github.robotman3000.bukkit.spigotplus.api.command.PlayerParameter;
import io.github.robotman3000.bukkit.spigotplus.api.command.WorldParameter;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorldGotoCommand extends JavaPluginCommand {
	
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    	if(args.length > 0){
	    	boolean hasPlayerName = args.length > 1;
	    	CommandSender sendr = sender;
	    	World gotoWorld = Bukkit.getWorld(args[0]);
	    	
	    	if(gotoWorld != null){
		    	if(hasPlayerName){
		    		Player play = Bukkit.getPlayer(args[1]);
		    		if(play != null){
		    			sendr = play;
		    		} else {
		    			sender.sendMessage(ChatColor.RED + "No player with that name is currently online!!");
		    			return false;
		    		}
		    	}
		    	
		    	if(sendr instanceof Player){
		    		Player play = (Player) sendr;
		    		if(sender instanceof Player){
		    			Player player = (Player) sender;
		    			if(player.getUniqueId().equals(play.getUniqueId())){
		    				if(!sender.hasPermission("spigotplus.multiworld.command.goto.self")){
		    					sender.sendMessage(ChatColor.RED + "You are not permitted to teleport yourself");
		    					return false;
		    				}
		    			} else {
		    				if(!sender.hasPermission("spigotplus.multiworld.command.goto.others")){
		    					sender.sendMessage(ChatColor.RED + "You are not permitted to teleport other players");
		    					return false;
		    				}
		    			}
		    		}
		    		if(!play.getWorld().getUID().equals(gotoWorld.getUID())){
		    			play.teleport(gotoWorld.getSpawnLocation());
		    			return true;
		    		} else {
		    			sender.sendMessage(ChatColor.RED + "You are already in that world");
		    		}
		    	} else {
		    		sender.sendMessage(ChatColor.RED + "You must be a player to teleport yourself!!");
		    	}
	    	} else {
	    		sender.sendMessage(ChatColor.RED + "No world with the name \"" + args[0] + "\" exists!!");
	    	}
    	}
    	return false;
    }

	@Override
	protected void initializeParameters(
			List<CommandParameter<?>> commandParameters2) {
		commandParameters2.add(new WorldParameter("World Name"));
		commandParameters2.add(new PlayerParameter("Player"));
	}

}
