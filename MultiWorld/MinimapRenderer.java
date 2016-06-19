package io.github.robotman3000.bukkit.spigotplus.mods.minimap;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class MinimapRenderer extends MapRenderer {

	private List<MapState> maps;
	private Random rand = new Random();
	private final int radius = 63;
	
	public MinimapRenderer(List<MapState> list) {
		this.maps = list;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void render(MapView map, MapCanvas canvas, Player player) {
		for (MapState state : maps) {
			if (state.getMapID() == map.getId()) {
				int scaleBits = 7 + map.getScale().ordinal();
				int mapScale = 1 << map.getScale().ordinal();
				int edgeSize = (127 * mapScale);
				int blocksPerPixel = edgeSize / 128;
				
				int blocksX = 1, blocksZ = 1;
				int playerX = player.getLocation().getBlockX();
				int playerZ = player.getLocation().getBlockZ();
				int maxX = (int) (Math.ceil((radius - blocksX / 2) / blocksX) * 2 + 1);
				int maxZ = (int) (Math.ceil((radius - blocksZ / 2) / blocksZ) * 2 + 1);
				for (int z = -maxZ / 2; z <= maxZ / 2; z++) {
					for (int x = -maxX / 2; x <= maxX / 2; x++) {
						double distance = Math.sqrt(((z * blocksZ) ^ 2) + ((x * blocksX) ^ 2));
						if(distance < radius){
							double xCord = (playerX + x);
							double zCord = (playerZ + z);
							int secLocX = (int)xCord >> scaleBits;
							int secLocZ = (int)zCord >> scaleBits;
				
							MapSection section = state.getMapSection(secLocX, secLocZ);
							byte color = getRandomColor();
							
							//double varX = (((xCord / (secLocX != 0 ? secLocX : 1)) / edgeSize) * 128) / edgeSize;
							//double varZ = (((zCord / (secLocZ != 0 ? secLocZ : 1)) / edgeSize) * 128) / edgeSize;
							
							
							double varX = Math.floor(Math.abs(xCord) / (Math.abs(secLocX) + 1));
							int pixelX =  (int) Math.floor(varX / blocksPerPixel);
							
							double varZ = Math.floor(Math.abs(zCord) / (Math.abs(secLocX) + 1));
							int pixelZ =  (int) Math.floor(varZ / blocksPerPixel);
							
							// ? / 128 = xzCord / edgeSize
							//Math: pixel / 128 = step1 / edgeLength  
							
							section.setPixel(pixelX, pixelZ, color);
						}
					}
				}
				

				// Next we determine if we have left the bounds of the current
				// map section
				// and if so we fix it
				int sectionX = (int) Math.floor(player.getLocation().getBlockX() >> scaleBits);
				int sectionZ = (int) Math.floor(player.getLocation().getBlockZ() >> scaleBits);
				if (state.getLastSectionX() != sectionX || state.getLastSectionZ() != sectionZ) {
					int xStart = sectionX * edgeSize;
					int xEnd = xStart + edgeSize;
					map.setCenterX(xEnd - (64 * mapScale));
					state.setSectionX(sectionX);
					
					int zStart = sectionZ * edgeSize;
					int zEnd = zStart + edgeSize;
					map.setCenterZ(zEnd - (64 * mapScale));
					state.setSectionZ(sectionZ);
					
					player.sendMessage("Map Center: " + map.getCenterX() + " " + map.getCenterZ() + ", Sec X: "
							+ sectionX + ", Sec Z: " + sectionZ + ", X: " + xStart + " - " + xEnd + ", Z: " + zStart
							+ " - " + zEnd);
				}
				renderMap(state, sectionX, sectionZ, canvas);
				break;
			}
		}
	}

	private byte getRandomColor() {
		return MapPalette.matchColor(MapPalette.getColor((byte) rand.nextInt(128)));
	}
	

	@SuppressWarnings("deprecation")
	private void renderMap(MapState state, int sectionX, int sectionZ, MapCanvas canvas) {
		MapSection section = state.getMapSection(sectionX, sectionZ);
		for (int x = 0; x < 128; x++) {
			for (int z = 0; z < 128; z++) {
				byte pixel = section.getPixel(x, z);
				if(pixel != Byte.MAX_VALUE && pixel != MapPalette.TRANSPARENT){
					canvas.setPixel(x, z, pixel);
				}
			}
		}
	}
	

	private byte averageColor(byte[] colors) {
		Arrays.sort(colors);

		byte previous = colors[0];
		byte popular = colors[0];
		byte count = 1;
		byte maxCount = 1;

		for (int i = 1; i < colors.length; i++) {
			if (colors[i] == previous)
				count++;
			else {
				if (count > maxCount) {
					popular = colors[i - 1];
					maxCount = count;
				}
				previous = colors[i];
				count = 1;
			}
		}

		return count > maxCount ? colors[colors.length - 1] : popular;
	}
	

	private boolean[][] makeCircle(boolean generateWithCenter, int radius) {
		int blocksX = 1, blocksZ = 1, maxX, maxZ;
		if (!generateWithCenter) {
			maxX = (int) (Math.ceil((radius - blocksX / 2) / blocksX) * 2 + 1);
			maxZ = (int) (Math.ceil((radius - blocksZ / 2) / blocksZ) * 2 + 1);
		} else {
			maxX = (int) (Math.ceil(radius / blocksX) * 2);
			maxZ = (int) (Math.ceil(radius / blocksZ) * 2);
		}

		for (int z = -maxZ / 2; z <= maxZ / 2; z++) {
			for (int x = -maxX / 2; x <= maxX / 2; x++) {
				double distance = Math.sqrt(((z * blocksZ) ^ 2) + ((x * blocksX) ^ 2));
				if(distance < radius){
					// Save the cordinate
				}
			}
		}
		return null;
	}
}

















package io.github.robotman3000.bukkit.spigotplus.mods.minimap;

import java.util.List;
import java.util.Random;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class MinimapRenderer extends MapRenderer {

	private List<MapState> maps;
	private boolean exploreMode = true;
	private final int RADIUS = 128;
	private Random rand = new Random();
	
	public MinimapRenderer(List<MapState> list, boolean explore) {
		this.maps = list;
		this.exploreMode = explore;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void render(MapView map, MapCanvas canvas, Player player) {
		// Find the map data
		MapState mapData = null;
		for(MapState state : maps){
			if(state.getMapID() == map.getId()){
				mapData = state;
				break;
			}
		}
		
		// If the map data was found then we continue
		if(mapData != null){
			int playerX = player.getLocation().getBlockX();
			int playerZ = player.getLocation().getBlockZ();
			
			int scaleBits = 7 + map.getScale().ordinal();
			int mapScale = 1 << map.getScale().ordinal();
			
			int mapSectionX = playerX >> scaleBits;
			int mapSectionZ = playerZ >> scaleBits;
			MapSection section = mapData.getMapSection(mapSectionX, mapSectionZ);
			
			for(int x = 0; x < 128; x++){
				for(int z = 0; z < 128; z++){
					boolean renderPixel = !exploreMode;
					
					if(exploreMode){
						int radius = RADIUS / mapScale;
						double hypo = Math.sqrt(Math.pow(((z-64) / mapScale), 2) + Math.pow(((x-64) / mapScale), 2));
						if(hypo < radius){
							renderPixel = true;
						}
					}
					
					if(renderPixel){
						section.setPixel(x, z, getRandomColor());
					}
				}			
			}
			
			// Determine the section that should be displayed on the map
			int sectionX = (int) Math.floor(player.getLocation().getBlockX() >> scaleBits);
			int sectionZ = (int) Math.floor(player.getLocation().getBlockZ() >> scaleBits);
			int edgeSize = 128 * mapScale;
			if (mapData.getLastSectionX() != sectionX || mapData.getLastSectionZ() != sectionZ) {
				int xStart = sectionX * edgeSize;
				int xEnd = xStart + edgeSize;
				map.setCenterX(xEnd - (64 * mapScale));
				mapData.setSectionX(sectionX);
				
				int zStart = sectionZ * edgeSize;
				int zEnd = zStart + edgeSize;
				map.setCenterZ(zEnd - (64 * mapScale));
				mapData.setSectionZ(sectionZ);
				
				player.sendMessage("Map Center: " + map.getCenterX() + " " + map.getCenterZ() + ", Sec X: "
						+ sectionX + ", Sec Z: " + sectionZ + ", X: " + xStart + " - " + xEnd + ", Z: " + zStart
						+ " - " + zEnd);
			}
			
			// Now we update the map display
			renderMap(mapData, sectionX, sectionZ, canvas);
		}
	}
	
	@SuppressWarnings("deprecation")
	private byte getRandomColor() {
		return MapPalette.matchColor(MapPalette.getColor((byte) rand.nextInt(128)));
	}
	
	private void renderMap(MapState state, int sectionX, int sectionZ, MapCanvas canvas) {
		MapSection section = state.getMapSection(sectionX, sectionZ);
		for (int x = 0; x < 128; x++) {
			for (int z = 0; z < 128; z++) {
				canvas.setPixel(x, z, section.getPixel(x, z));
			}
		}
	}
}
