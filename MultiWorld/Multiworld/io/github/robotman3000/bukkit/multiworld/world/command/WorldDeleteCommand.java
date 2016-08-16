package io.github.robotman3000.bukkit.multiworld.world.command;

import io.github.robotman3000.bukkit.multiworld.world.DeleteWorldInfoPacket;
import io.github.robotman3000.bukkit.multiworld.world.WorldManagerHelper;
import io.github.robotman3000.bukkit.spigotplus.api.JavaPluginCommand;
import io.github.robotman3000.bukkit.spigotplus.api.command.CommandParameter;
import io.github.robotman3000.bukkit.spigotplus.api.command.WorldParameter;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

public class WorldDeleteCommand extends JavaPluginCommand implements Listener {

	private static Map<String, DeleteWorldInfoPacket> pendingWorldDeletes = new HashMap<>();
	
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 0) {
        	World world = Bukkit.getWorld(args[0]);
        	if(world == null){
        		File worldFile = new File(Bukkit.getWorldContainer(), args[0]);
        		if(WorldManagerHelper.isWorldFolder(worldFile)){
        			synchronized (this){
        				pendingWorldDeletes.put(sender.getName(), new DeleteWorldInfoPacket(worldFile, System.currentTimeMillis()));
        			}
        			sender.sendMessage(ChatColor.RED + "You must confirm the operation with /worldconfirm");
        			return true;
        		} else {
        			sender.sendMessage(ChatColor.RED + "No world with that name exists");
        		}
        	} else {
        		sender.sendMessage(ChatColor.RED + "You must unload the world before you can delete it");
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
    
    public static synchronized boolean confirmOperation(CommandSender sender){
    	DeleteWorldInfoPacket worldData = pendingWorldDeletes.remove(sender.getName());
    	if(worldData != null){
    		long timeout = WorldManagerHelper.getConfirmTimeout();
    		if((System.currentTimeMillis() - timeout) < worldData.getTime()){
	    		// We will check one last time that the world is unloaded
	    		World world = Bukkit.getWorld(worldData.getWorldFile().getName());
	    		if(world == null){
	    			boolean result = WorldManagerHelper.deleteWorldFolder(worldData.getWorldFile());
	    			if(result){
	    				sender.sendMessage(ChatColor.RED + "The world has been deleted successfully");
	    				return true;
	    			} else {
	    				sender.sendMessage(ChatColor.RED + "An error occured while deleting the world");
	    			}
	    		} else {
	    			sender.sendMessage(ChatColor.RED + "Please verify that the world is unloaded and try again");
	    		}
    		} else {
    			sender.sendMessage(ChatColor.RED + "The confirmation timeout has been reached. Please try again");
    		}
    	} else {
    		sender.sendMessage(ChatColor.RED + "No pending operation could be found");
    	}
    	return false;
    }
}
