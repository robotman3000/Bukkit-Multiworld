package io.github.robotman3000.bukkit.spigotplus.api;

import io.github.robotman3000.bukkit.spigotplus.api.command.CommandParameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public abstract class JavaPluginCommand implements CommandExecutor, TabCompleter {
	
	private List<CommandParameter<?>> commandParameters = new ArrayList<>();
	
	public JavaPluginCommand() {
		initializeParameters(commandParameters);
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command,
			String alias, String[] args) {
		if(args.length > 0 && args.length <= commandParameters.size()){
			CommandParameter<?> param = commandParameters.get(args.length - 1);
			return param.getTabCompletions(sender, args[args.length - 1]);
		}
		return Collections.emptyList();
	}
		
	protected void initializeParameters(List<CommandParameter<?>> commandParameters2){}
	
	protected final List<CommandParameter<?>> getParameters(){
		return Collections.unmodifiableList(commandParameters);
	}
	
}
