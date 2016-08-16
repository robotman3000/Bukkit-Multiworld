package io.github.robotman3000.bukkit.multiworld.world.command;

import io.github.robotman3000.bukkit.spigotplus.api.JavaPluginCommand;
import io.github.robotman3000.bukkit.spigotplus.api.command.CommandParameter;
import io.github.robotman3000.bukkit.spigotplus.api.command.WorldParameter;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class WorldUnloadCommand extends JavaPluginCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		//TODO: Test if world is already unloaded
        if (args.length > 0) {
            World world = Bukkit.getWorld(args[0]);
            if (world != null) {
            	if(sender.hasPermission("spigotplus.multiworld.command.unload." + world.getName())){
	                if (world.getPlayers().size() == 0) {
	                	return Bukkit.unloadWorld(world, true);
	                } else {
		                sender.sendMessage(ChatColor.RED + "You can't unload a world with players in it");
	                }
            	} else {
            		sender.sendMessage(ChatColor.RED + "You are not permitted to unload that world");
            	}
            }
        } else {
        	sender.sendMessage(ChatColor.RED + "You must provide a world name");
        }
        return false;
    }
    
    @Override
    protected void initializeParameters(
    		List<CommandParameter<?>> commandParameters2) {
    	commandParameters2.add(new WorldParameter("World"));
    }

}
