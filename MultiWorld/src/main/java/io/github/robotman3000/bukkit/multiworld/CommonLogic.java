package io.github.robotman3000.bukkit.multiworld;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.bukkit.Bukkit;

public class CommonLogic {

	public static void dirDelete(File file) throws IOException {
		if (file.isDirectory()) {
			if (file.list().length == 0)
				file.delete();
			else {
				String files[] = file.list();

				for (String temp : files)
					dirDelete(new File(file, temp));

				if (file.list().length == 0)
					file.delete();
			}
		} else
			file.delete();
	}
	
	public static String loadJsonAsString(File theJsonFile) {
		StringBuffer str = new StringBuffer();
		try {
			theJsonFile.createNewFile();
			BufferedReader read = new BufferedReader(new FileReader(theJsonFile));
			String v = "";
			while((v = read.readLine()) != null){
				str.append(v);
			}
			read.close();
		} catch (IOException e) {
			Bukkit.getLogger().warning("Error reading config file");
			e.printStackTrace();
		}

		String config = str.toString();
		return config;
	}

	public static void saveJsonAsFile(File theJsonFile, String fileContents){
		try {
			theJsonFile.createNewFile(); // This only makes a new file if one doesnt already exist
			BufferedWriter write = new BufferedWriter(new FileWriter(theJsonFile));
			write.write(fileContents);
			write.flush();
			write.close();
		} catch (IOException e) {
			Bukkit.getLogger().warning("Error saving config file");
			e.printStackTrace();
		}
	}
}
