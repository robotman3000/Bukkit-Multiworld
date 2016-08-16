package io.github.robotman3000.bukkit.spigotplus.api.command;

import java.util.List;

import org.bukkit.command.CommandSender;

public abstract class CommandParameter<T> {

	private String name;

	public CommandParameter(String parameterName){
		this.name = (parameterName != null ? parameterName : "");
	}

	public String getName() {
		return name;
	}
	
	public abstract List<String> getTabCompletions(CommandSender sender, String arg);
	
	public abstract T getParameterValue(String str);
}
