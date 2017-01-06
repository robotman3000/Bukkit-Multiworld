package io.github.robotman3000.bukkit.spigotplus.mods.minimap;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.map.MapView;

public class MapState {

	private MapView map;
	private Map<SectionIndex, MapSection> mapData = new HashMap<>();
	private SectionIndex lastIndex = null;
	private SectionIndex cachedIndex = null;
	private MapSection cachedSection = null;
	public int lastY;
	public boolean exploreMode = true;
	
	public int getSectionX() {
		return (cachedIndex != null ? cachedIndex.getX() : 0);
	}

	public int getSectionZ() {
		return (cachedIndex != null ? cachedIndex.getZ() : 0);
	}
	
	public int getLastSectionX() {
		return (lastIndex != null ? lastIndex.getX() : 0);
	}
	
	public int getLastSectionZ() {
		return (lastIndex != null ? lastIndex.getZ() : 0);
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
		MapSection section = cachedSection;
		if(cachedIndex == null || !(cachedIndex.getX() == x && cachedIndex.getZ() == z)){
			SectionIndex key = new SectionIndex(x, z);
			section = mapData.get(key);
			if(section == null){
				section = new MapSection();
				mapData.put(key, section);
			}
		}
		return section;
	}

	public void setSection(int lastSectionX, int lastSectionZ) {
		this.cachedSection = getMapSection(lastSectionX, lastSectionZ);
		this.lastIndex = this.cachedIndex;
		this.cachedIndex = new SectionIndex(lastSectionX, lastSectionZ);
	}

}
