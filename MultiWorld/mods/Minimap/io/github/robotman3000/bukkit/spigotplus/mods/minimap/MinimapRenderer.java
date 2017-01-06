package io.github.robotman3000.bukkit.spigotplus.mods.minimap;

import java.util.Arrays;

import javax.swing.text.StyledEditorKit.BoldAction;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapView;

public class MinimapRenderer {
	private static final int RENDER_RADIUS_IN_BLOCKS = 128;
	private static final int EDGE_SIZE_IN_BLOCKS_AT_SINGLE_SCALE = 128;
	private static final int EDGE_SIZE_IN_PIXELS_OF_CANVAS = 128;
	private static final int MAX_WORLD_HEIGHT = 256;
	
	@SuppressWarnings("deprecation")
	public static void render(MapState state, MapView map, MapCanvas canvas, Player player) {
		System.out.println("Start render " + map.getId());
		long time = 0;
		long time2 = System.currentTimeMillis();
		long timeStart = time2;
		state.lastY = 256;

		// Find the map data
		
		// If the map data was found then we continue
		
		time = System.currentTimeMillis() - time2;
		System.out.println("Checkpoint 1: " + map.getId() + "; Time: " + time + "ms");
		time2 = time + time2;
		if(state != null){
			int playerX = player.getLocation().getBlockX();
			int playerZ = player.getLocation().getBlockZ();
			
			int scaleBits = 7 + map.getScale().ordinal();
			int mapScale = 1 << map.getScale().ordinal();
			
			int mapSectionX = playerX >> scaleBits;
			int mapSectionZ = playerZ >> scaleBits;
			MapSection section = state.getMapSection(mapSectionX, mapSectionZ);
			
			int edgeSizeInBlocksAtDesiredScale = EDGE_SIZE_IN_BLOCKS_AT_SINGLE_SCALE * mapScale;                  /* Blocks@Scale */
			int blocksPerPixelAtDesiredScale = edgeSizeInBlocksAtDesiredScale / EDGE_SIZE_IN_PIXELS_OF_CANVAS;    /* Blocks@Scale/Pixel */
			
			int xStartOfCurrentMapSectionInBlocks = mapSectionX * edgeSizeInBlocksAtDesiredScale;
			int xEndOfCurrentMapSectionInBlocks = xStartOfCurrentMapSectionInBlocks + edgeSizeInBlocksAtDesiredScale;
			int zStartOfCurrentMapSectionInBlocks = mapSectionZ * edgeSizeInBlocksAtDesiredScale;
			int zEndOfCurrentMapSectionInBlocks = zStartOfCurrentMapSectionInBlocks + edgeSizeInBlocksAtDesiredScale;

			time = System.currentTimeMillis() - time2;
			System.out.println("Checkpoint 2: " + map.getId() + "; Time: " + time + "ms");
			time2 = time + time2;
			
			for(int canvasX = 0; canvasX < EDGE_SIZE_IN_PIXELS_OF_CANVAS; canvasX++){
				for(int canvasZ = 0; canvasZ < EDGE_SIZE_IN_PIXELS_OF_CANVAS; canvasZ++){
					boolean renderPixel = !state.exploreMode;
					
					if(state.exploreMode){
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
						byte pixelColor = calculatePixel(state, xStartOfCurrentMapSectionInBlocks, xEndOfCurrentMapSectionInBlocks, zStartOfCurrentMapSectionInBlocks, zEndOfCurrentMapSectionInBlocks, canvasX, canvasZ, blocksPerPixelAtDesiredScale, player.getWorld(), mapScale);
						section.setPixel(canvasX, canvasZ, pixelColor);
					}
				}			
			}
			
			time = System.currentTimeMillis() - time2;
			System.out.println("Checkpoint 3: " + map.getId() + "; Time: " + time + "ms");
			time2 = time + time2;	
			// Determine the section that should be displayed on the map
			if (state.getLastSectionX() != mapSectionX || state.getLastSectionZ() != mapSectionZ) {
				state.setSection(mapSectionX, mapSectionZ);

				map.setCenterX(xEndOfCurrentMapSectionInBlocks - (edgeSizeInBlocksAtDesiredScale / 2));
				map.setCenterZ(zEndOfCurrentMapSectionInBlocks - (edgeSizeInBlocksAtDesiredScale / 2));
				
				player.sendMessage("Map Center: " + map.getCenterX() + " " + map.getCenterZ() + ", Sec X: "
						+ mapSectionX + ", Sec Z: " + mapSectionZ + ", X: " + xStartOfCurrentMapSectionInBlocks + " - " + xEndOfCurrentMapSectionInBlocks + ", Z: " + zStartOfCurrentMapSectionInBlocks
						+ " - " + zEndOfCurrentMapSectionInBlocks);
			}
			
			time = System.currentTimeMillis() - time2;
			System.out.println("Checkpoint 4: " + map.getId() + "; Time: " + time + "ms");
			time2 = time + time2;
			
			// Now we update the map display
			renderMap(state, mapSectionX, mapSectionZ, canvas);
			
			time = System.currentTimeMillis() - time2;
			System.out.println("Checkpoint 5: " + map.getId() + "; Time: " + time + "ms");
			time2 = time + time2;
		}
		System.out.println("Render took " + (System.currentTimeMillis() - timeStart) + "ms for map " + map.getId());
	}

	@SuppressWarnings("deprecation")
	private static byte calculatePixel(MapState state, int xStart, int xEnd, int zStart, int zEnd, int x, int z, int blocksPerPixel, World world, int mapScale) {
		int firstBlockX = xStart + (x * blocksPerPixel);
		int firstBlockZ = zStart + (z * blocksPerPixel);
		
		byte[] blocksForPixel = new byte[blocksPerPixel*blocksPerPixel];
		
		int index2 = 0;
		for(int xIndex = firstBlockX; xIndex < (firstBlockX + blocksPerPixel); xIndex++){
		for(int zIndex = firstBlockZ; zIndex < (firstBlockZ + blocksPerPixel); zIndex++){
			
				
				int colorIndex = 1;
				int colorMod = 1;
				int blockY = MAX_WORLD_HEIGHT;
				while(blockY > -1){
					/*Block block = world.getBlockAt(xIndex, blockY, zIndex);
					if(!block.isEmpty()){
						colorIndex = BlockMaterialColorMapping.getBlockForID(block.getType().getId()).getColorIndex();
						//Bukkit.getLogger().info("IND: " + colorIndex);
						
						if(state.lastY > blockY){
							colorMod = 0;
						} else if (state.lastY < blockY){
							colorMod = 2;
						}
						
						state.lastY = blockY;
						break;
					}*/
					//Bukkit.getLogger().info(lastY + " " + blockY);
					
					blockY--;
				}
				int index = (colorIndex * 4) + colorMod;
				//blocksForPixel[arrayX][arrayZ] = (byte) (index < 128 ? index : -129 + (index - 127));
				
				blocksForPixel[index2++] = (byte) index;
			}	
		}
		
		//Bukkit.getLogger().info(Arrays.toString(blocksForPixel));
		if(blocksForPixel.length > 1){
		    Arrays.sort(blocksForPixel);

		    int previous = blocksForPixel[0];
		    byte popular = blocksForPixel[0];
		    int count = 1;
		    int maxCount = 1;

		    for (int i = 1; i < blocksForPixel.length; i++) {
		        if (blocksForPixel[i] == previous)
		            count++;
		        else {
		            if (count > maxCount) {
		                popular = blocksForPixel[i-1];
		                maxCount = count;
		            }
		            previous = blocksForPixel[i];
		            count = 1;
		        }
		    }
		    return count > maxCount ? blocksForPixel[blocksForPixel.length-1] : popular;
		}
		return blocksForPixel[0];
	}
	
	private static void renderMap(MapState state, int sectionX, int sectionZ, MapCanvas canvas) {
		MapSection section = state.getMapSection(sectionX, sectionZ);
		for (int x = 0; x < EDGE_SIZE_IN_PIXELS_OF_CANVAS; x++) {
			for (int z = 0; z < EDGE_SIZE_IN_PIXELS_OF_CANVAS; z++) {
				canvas.setPixel(x, z, section.getPixel(x, z));
			}
		}
	}
}
