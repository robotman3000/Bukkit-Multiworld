package io.github.robotman3000.bukkit.spigotplus.api.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

public class EnumParameter<T extends Enum<T>> extends CommandParameter<T> {
	
	private Class<T> classType;
	private List<String> tabCompletes = new ArrayList<String>();
	
	public EnumParameter(String parameterName, Class<T> class1) {
		super(parameterName);
		this.classType = class1;
		T[] values = class1.getEnumConstants();
		for(T value : values){
			tabCompletes.add(value.toString());
		}
	}

	@Override
	public T getParameterValue(String str) {
		try{
			return Enum.valueOf(classType, str);
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<String> getTabCompletions(CommandSender sender, String arg) {
		// TODO: make this complete partial strings
		return tabCompletes;
	}
}
