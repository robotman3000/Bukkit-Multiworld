package io.github.robotman3000.bukkit.multiworld.world.command;

import io.github.robotman3000.bukkit.spigotplus.api.JavaPluginCommand;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

public class WorldConfirmCommand extends JavaPluginCommand implements Listener {
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2,
			String[] arg3) {
		return WorldDeleteCommand.confirmOperation(sender);
	}
}
