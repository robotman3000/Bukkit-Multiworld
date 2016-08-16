package io.github.robotman3000.bukkit.spigotplus.api.command;

import java.util.List;

import org.bukkit.command.CommandSender;

public class ImpliedParameter<T extends CommandParameter<V>, V> extends CommandParameter<V> {

	private T baseParam;

	public ImpliedParameter(T baseParameter) {
		super(baseParameter.getName());
		this.baseParam = baseParameter;
	}

	@Override
	public List<String> getTabCompletions(CommandSender sender, String arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public V getParameterValue(String str) {
		return baseParam.getParameterValue(str);
	}

}
