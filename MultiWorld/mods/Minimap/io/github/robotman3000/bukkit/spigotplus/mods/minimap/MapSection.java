package io.github.robotman3000.bukkit.spigotplus.mods.minimap;

import org.bukkit.Bukkit;
import org.bukkit.map.MapPalette;

@SuppressWarnings("deprecation")
public class MapSection {

	private byte[][] mapData;	

	public MapSection(){
		mapData = new byte[128][128];
		byte min = MapPalette.TRANSPARENT;
		for(int index = 0; index < 128; index++){
			for(int index2 = 0; index2 < 128; index2++){
				mapData[index][index2] = min; 
			}
		}
	}
	
	public synchronized void setPixel(int x, int z, byte color){
		if((x > -1 && x < 128 && z > -1 && z < 128)){
			mapData[x][z] = color;
		} else {
			Bukkit.getLogger().warning("Invalid Map Set Cordinates: " + x + " " + z);
		}
	}
	
	public synchronized byte getPixel(int x, int z){
		if((x > -1 && x < 128 && z > -1 && z < 128)){
			return mapData[x][z];
		}
		Bukkit.getLogger().warning("Invalid Map Get Cordinates: " + x + " " + z);
		return MapPalette.TRANSPARENT;
	}
}
