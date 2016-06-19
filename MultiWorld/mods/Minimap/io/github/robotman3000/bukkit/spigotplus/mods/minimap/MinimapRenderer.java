package io.github.robotman3000.bukkit.spigotplus.mods.minimap;

import java.awt.Color;
import java.util.List;
import java.util.Random;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class MinimapRenderer extends MapRenderer {

    private static Color c(int r, int g, int b) {
        return new Color(r, g, b);
    }
    
	static final Color[] colors = {
	        c(0, 0, 0), c(0, 0, 0), c(0, 0, 0), c(0, 0, 0),
	        c(89, 125, 39), c(109, 153, 48), c(127, 178, 56), c(67, 94, 29),
	        c(174, 164, 115), c(213, 201, 140), c(247, 233, 163), c(130, 123, 86),
	        c(140, 140, 140), c(171, 171, 171), c(199, 199, 199), c(105, 105, 105),
	        c(180, 0, 0), c(220, 0, 0), c(255, 0, 0), c(135, 0, 0),
	        c(112, 112, 180), c(138, 138, 220), c(160, 160, 255), c(84, 84, 135),
	        c(117, 117, 117), c(144, 144, 144), c(167, 167, 167), c(88, 88, 88),
	        c(0, 87, 0), c(0, 106, 0), c(0, 124, 0), c(0, 65, 0),
	        c(180, 180, 180), c(220, 220, 220), c(255, 255, 255), c(135, 135, 135),
	        c(115, 118, 129), c(141, 144, 158), c(164, 168, 184), c(86, 88, 97),
	        c(106, 76, 54), c(130, 94, 66), c(151, 109, 77), c(79, 57, 40),
	        c(79, 79, 79), c(96, 96, 96), c(112, 112, 112), c(59, 59, 59),
	        c(45, 45, 180), c(55, 55, 220), c(64, 64, 255), c(33, 33, 135),
	        c(100, 84, 50), c(123, 102, 62), c(143, 119, 72), c(75, 63, 38),
	        c(180, 177, 172), c(220, 217, 211), c(255, 252, 245), c(135, 133, 129),
	        c(152, 89, 36), c(186, 109, 44), c(216, 127, 51), c(114, 67, 27),
	        c(125, 53, 152), c(153, 65, 186), c(178, 76, 216), c(94, 40, 114),
	        c(72, 108, 152), c(88, 132, 186), c(102, 153, 216), c(54, 81, 114),
	        c(161, 161, 36), c(197, 197, 44), c(229, 229, 51), c(121, 121, 27),
	        c(89, 144, 17), c(109, 176, 21), c(127, 204, 25), c(67, 108, 13),
	        c(170, 89, 116), c(208, 109, 142), c(242, 127, 165), c(128, 67, 87),
	        c(53, 53, 53), c(65, 65, 65), c(76, 76, 76), c(40, 40, 40),
	        c(108, 108, 108), c(132, 132, 132), c(153, 153, 153), c(81, 81, 81),
	        c(53, 89, 108), c(65, 109, 132), c(76, 127, 153), c(40, 67, 81),
	        c(89, 44, 125), c(109, 54, 153), c(127, 63, 178), c(67, 33, 94),
	        c(36, 53, 125), c(44, 65, 153), c(51, 76, 178), c(27, 40, 94),
	        c(72, 53, 36), c(88, 65, 44), c(102, 76, 51), c(54, 40, 27),
	        c(72, 89, 36), c(88, 109, 44), c(102, 127, 51), c(54, 67, 27),
	        c(108, 36, 36), c(132, 44, 44), c(153, 51, 51), c(81, 27, 27),
	        c(17, 17, 17), c(21, 21, 21), c(25, 25, 25), c(13, 13, 13),
	        c(176, 168, 54), c(215, 205, 66), c(250, 238, 77), c(132, 126, 40),
	        c(64, 154, 150), c(79, 188, 183), c(92, 219, 213), c(48, 115, 112),
	        c(52, 90, 180), c(63, 110, 220), c(74, 128, 255), c(39, 67, 135),
	        c(0, 153, 40), c(0, 187, 50), c(0, 217, 58), c(0, 114, 30),
	        c(91, 60, 34), c(111, 74, 42), c(129, 86, 49), c(68, 45, 25),
	        c(79, 1, 0), c(96, 1, 0), c(112, 2, 0), c(59, 1, 0),
	    };
	
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
		
		int lastY = 256;
		for(int zIndex = firstBlockZ, arrayZ = 0; zIndex < (firstBlockZ + blocksPerPixel); zIndex++, arrayZ++){
			for(int xIndex = firstBlockX, arrayX = 0; xIndex < (firstBlockX + blocksPerPixel); xIndex++, arrayX++){
				
				int colorIndex = 0;
				int colorMod = 1;
				int blockY = MAX_WORLD_HEIGHT;
				while(blockY > -1){
					Block block = world.getBlockAt(xIndex, blockY, zIndex);
					if(!block.isEmpty()){
						colorIndex = BlockMaterialColorMapping.getBlockForID(block.getType().getId()).getColorIndex();
						if(lastY > blockY){
							colorMod = 2;
						} else if (lastY < blockY){
							colorMod = 0;
						}
						break;
					}
					//Bukkit.getLogger().info(lastY + " " + blockY);
					lastY = blockY;
					blockY--;
				}
				int index = (colorIndex * 4) + colorMod;
				blocksForPixel[arrayX][arrayZ] = (byte) (index < 128 ? index : -129 + (index - 127));
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
