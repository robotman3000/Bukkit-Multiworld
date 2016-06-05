package io.github.robotman3000.bukkit.spigotplus.mods.minimap;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.map.MapView;

public class MapState {

	private MapView map;
	private Map<SectionIndex, MapSection> mapData = new HashMap<>();
	private int lastSectionX = 0;
	private int lastSectionZ = 0;
	private int sectionX = 0;
	private int sectionZ = 0;
	
	public int getSectionX() {
		return sectionX;
	}

	public int getSectionZ() {
		return sectionZ;
	}

	public MapState(MapView map) {
		this.map = map;
	}
	
	@SuppressWarnings("deprecation")
	public short getMapID() {
		return map.getId();
	}

	public MapView getMapView() {
		return map;
	}
	
	public MapSection getMapSection(int x, int z){
		SectionIndex key = new SectionIndex(x, z);
		MapSection section = mapData.get(key);
		if(section == null){
			section = new MapSection();
			mapData.put(key, section);
		}
		return section;
	}

	public int getLastSectionX() {
		return lastSectionX;
	}

	public void setSectionX(int lastSectionX) {
		this.lastSectionX = this.sectionX;
		this.sectionX = lastSectionX;
	}

	public int getLastSectionZ() {
		return lastSectionZ;
	}

	public void setSectionZ(int lastSectionZ) {
		this.lastSectionZ = this.sectionZ;
		this.sectionZ = lastSectionZ;
	}

}
