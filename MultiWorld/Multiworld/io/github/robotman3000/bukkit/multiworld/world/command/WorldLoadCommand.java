package io.github.robotman3000.bukkit.multiworld.world.command;

import io.github.robotman3000.bukkit.multiworld.world.WorldManager;
import io.github.robotman3000.bukkit.multiworld.world.WorldManagerHelper;
import io.github.robotman3000.bukkit.spigotplus.api.JavaPluginCommand;
import io.github.robotman3000.bukkit.spigotplus.api.command.CommandParameter;
import io.github.robotman3000.bukkit.spigotplus.api.command.WorldParameter;

import java.io.File;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class WorldLoadCommand extends JavaPluginCommand {

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		
		if(args.length > 0){
			World world = Bukkit.getWorld(args[0]);
			if(world == null){
				File worldFile = new File(Bukkit.getWorldContainer(), args[0]);
				if(WorldManagerHelper.isWorldFolder(worldFile)){
					if(sender.hasPermission("spigotplus.multiworld.command.load." + args[0])){
						sender.sendMessage("Loading the world \"" + args[0] + "\"");
						WorldManagerHelper.loadWorld(WorldManager.self, worldFile);
						sender.sendMessage("Done loading the world \"" + args[0] + "\"");
						return true;
					} else {
						sender.sendMessage(ChatColor.RED + "You are not permitted to load that world");
					}
				} else {
					sender.sendMessage(ChatColor.RED + "That world doesn't exist. You must create it first");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "That world is already loaded");
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
