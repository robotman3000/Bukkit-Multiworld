package io.github.robotman3000.bukkit.spigotplus.mods.minimap;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class MinimapRenderer extends MapRenderer {

	private List<MapState> maps;
	private boolean exploreMode = true;
	private final int RENDER_RADIUS_IN_BLOCKS = 128;
	private final int EDGE_SIZE_IN_BLOCKS_AT_SINGLE_SCALE = 128;
	private final int EDGE_SIZE_IN_PIXELS_OF_CANVAS = 128;
	int MAX_WORLD_HEIGHT = 256;
	
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
			
			int edgeSizeInBlocksAtDesiredScale = EDGE_SIZE_IN_BLOCKS_AT_SINGLE_SCALE * mapScale;                  /* Blocks@Scale */
			int blocksPerPixelAtDesiredScale = edgeSizeInBlocksAtDesiredScale / EDGE_SIZE_IN_PIXELS_OF_CANVAS;    /* Blocks@Scale/Pixel */
			
			int xStartOfCurrentMapSectionInBlocks = mapSectionX * edgeSizeInBlocksAtDesiredScale;
			int xEndOfCurrentMapSectionInBlocks = xStartOfCurrentMapSectionInBlocks + edgeSizeInBlocksAtDesiredScale;
			int zStartOfCurrentMapSectionInBlocks = mapSectionZ * edgeSizeInBlocksAtDesiredScale;
			int zEndOfCurrentMapSectionInBlocks = zStartOfCurrentMapSectionInBlocks + edgeSizeInBlocksAtDesiredScale;

			for(int canvasX = 0; canvasX < EDGE_SIZE_IN_PIXELS_OF_CANVAS; canvasX++){
				for(int canvasZ = 0; canvasZ < EDGE_SIZE_IN_PIXELS_OF_CANVAS; canvasZ++){
					boolean renderPixel = !exploreMode;
					
					if(exploreMode){
						int scaledRenderRadiusInPixels = RENDER_RADIUS_IN_BLOCKS / mapScale;
						
						//player.sendMessage("PixelX: " + playerPixelX + ", PixelZ: " + playerPixelZ + ", Radius: " + scaledRenderRadiusInPixels + ", B/P: " + blocksPerPixelAtDesiredScale);
						//Bukkit.getLogger().info("WorldX: " + playerX + ", WorldZ: " + playerZ + " BlockX: " + blockX + ", BlockZ: " + blockZ + ", PixelX: " + playerPixelX + ", PixelZ: " + playerPixelZ + ", Radius: " + scaledRenderRadiusInPixels + ", B/P: " + blocksPerPixelAtDesiredScale + ", Edge: " + edgeSizeInBlocksAtDesiredScale);
						
						int xVal = (int) (canvasX + (-1 * Math.floor(Math.abs(playerX - xStartOfCurrentMapSectionInBlocks) / blocksPerPixelAtDesiredScale)));
						int zVal = (int) (canvasZ + (-1 * Math.floor(Math.abs(playerZ - zStartOfCurrentMapSectionInBlocks) / blocksPerPixelAtDesiredScale)));
						double hypo = Math.sqrt(Math.pow(xVal, 2) + Math.pow(zVal, 2));
						if(hypo < scaledRenderRadiusInPixels){
							renderPixel = true;
						}
					}
						
					if (renderPixel) {
						byte pixelColor = calculatePixel(xStartOfCurrentMapSectionInBlocks, xEndOfCurrentMapSectionInBlocks, zStartOfCurrentMapSectionInBlocks, zEndOfCurrentMapSectionInBlocks, canvasX, canvasZ, blocksPerPixelAtDesiredScale, player.getWorld(), mapScale);
						section.setPixel(canvasX, canvasZ, pixelColor);
					}
				}			
			}
			
			// Determine the section that should be displayed on the map
			if (mapData.getLastSectionX() != mapSectionX || mapData.getLastSectionZ() != mapSectionZ) {
				mapData.setSectionX(mapSectionX);
				mapData.setSectionZ(mapSectionZ);

				map.setCenterX(xEndOfCurrentMapSectionInBlocks - (edgeSizeInBlocksAtDesiredScale / 2));
				map.setCenterZ(zEndOfCurrentMapSectionInBlocks - (edgeSizeInBlocksAtDesiredScale / 2));
				
				player.sendMessage("Map Center: " + map.getCenterX() + " " + map.getCenterZ() + ", Sec X: "
						+ mapSectionX + ", Sec Z: " + mapSectionZ + ", X: " + xStartOfCurrentMapSectionInBlocks + " - " + xEndOfCurrentMapSectionInBlocks + ", Z: " + zStartOfCurrentMapSectionInBlocks
						+ " - " + zEndOfCurrentMapSectionInBlocks);
			}
			
			// Now we update the map display
			renderMap(mapData, mapSectionX, mapSectionZ, canvas);
		}
	}

	private byte calculatePixel(int xStart, int xEnd, int zStart, int zEnd, int x, int z, int blocksPerPixel, World world, int mapScale) {
		int firstBlockX = xStart + (x * blocksPerPixel);
		int firstBlockZ = zStart + (z * blocksPerPixel);
		
		byte[][] blocksForPixel = new byte[blocksPerPixel][blocksPerPixel];
		
		for(int xIndex = firstBlockX, arrayX = 0; xIndex < (firstBlockX + blocksPerPixel); xIndex++, arrayX++){
			for(int zIndex = firstBlockZ, arrayZ = 0; zIndex < (firstBlockZ + blocksPerPixel); zIndex++, arrayZ++){
				
				byte baseColor = 0;
				int blockY = MAX_WORLD_HEIGHT;
				while(blockY > -1){
					Block block = world.getBlockAt(xIndex, blockY, zIndex);
					if(!block.isEmpty()){
						baseColor = BlockMaterialColorMapping.getBlockForID(block.getType().getId()).getColorID();
						break;
					}
					blockY--;
				}
				blocksForPixel[arrayX][arrayZ] = baseColor;
			}	
		}
		
		// We would "average" the colors returned and use that if the implementation were complete
		// byte color = averageBlockColors(blocksForPixel);
		//return getRandomColor();
		return blocksForPixel[0][0];
	}
	
	@SuppressWarnings("deprecation")
	private byte getRandomColor() {
		return MapPalette.matchColor(MapPalette.getColor((byte) rand.nextInt(128)));
	}
	
	private void renderMap(MapState state, int sectionX, int sectionZ, MapCanvas canvas) {
		MapSection section = state.getMapSection(sectionX, sectionZ);
		for (int x = 0; x < EDGE_SIZE_IN_PIXELS_OF_CANVAS; x++) {
			for (int z = 0; z < EDGE_SIZE_IN_PIXELS_OF_CANVAS; z++) {
				canvas.setPixel(x, z, section.getPixel(x, z));
			}
		}
	}
}
