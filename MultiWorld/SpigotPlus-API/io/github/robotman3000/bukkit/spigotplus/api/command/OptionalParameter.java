package io.github.robotman3000.bukkit.spigotplus.api.command;

import java.util.List;

import org.bukkit.command.CommandSender;

public class OptionalParameter<T extends CommandParameter<V>, V> extends CommandParameter<V> {

	private final T baseParam;

	public OptionalParameter(T baseParameter) {
		super(baseParameter.getName());
		this.baseParam = baseParameter;
	}

	@Override
	public List<String> getTabCompletions(CommandSender sender, String arg) {
		return baseParam.getTabCompletions(sender, arg);
	}

	@Override
	public V getParameterValue(String str) {
		return baseParam.getParameterValue(str);
	}

	
}
