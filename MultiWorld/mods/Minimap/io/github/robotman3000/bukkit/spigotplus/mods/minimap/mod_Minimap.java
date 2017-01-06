package io.github.robotman3000.bukkit.spigotplus.mods.minimap;

import io.github.robotman3000.bukkit.spigotplus.api.JavaPluginFeature;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

//TODO: Ensure that every map has our renderer
public class mod_Minimap extends JavaPluginFeature implements Listener {

	private Map<Short, MapState> knownMaps = new HashMap<>();
	
	@EventHandler
	public void onPlayerHotbarChange(PlayerItemHeldEvent event){
		Player player = event.getPlayer();
		ItemStack item = player.getInventory().getItem(event.getNewSlot());
		
		if(item != null && item.getType() == Material.MAP){
			MapState state = null;
			boolean instanceMissing = true;
			if((state = knownMaps.get(item.getDurability())) != null){
				MapView map = state.getMapView();
				
				for(MapRenderer render : map.getRenderers()){
					if(render instanceof MapWraper){
						instanceMissing = false;
					}
				}
			} else {
				MapView map = Bukkit.getMap(item.getDurability());
				if(map != null){
					createNewMap(map);
				} else {
					logWarn("===========================================================================================");
					logWarn("I'm confused. How can an item with the material of a map not be associated with a map view?");
					logWarn("API methods used:");
					logWarn("item.getType() == Material.MAP");
					logWarn("MapView map = Bukkit.getMap(item.getDurability())");
					logWarn("===========================================================================================");
				}
			}
			
			if(instanceMissing){
				logInfo("Map " + (state != null ? state.getMapID() : item.getDurability()) + " did not have a Minimap Renderer");
				state.getMapView().getRenderers().add(new MapWraper(state));
			}
			
			player.sendMap(state.getMapView());
		}
	}
	
	@EventHandler
	public void onMapInitialize(MapInitializeEvent event){
		createNewMap(event.getMap());
	}
	
	private void createNewMap(MapView map) {
		// Every new map will get our renderer
		//map.addRenderer(renderer);
		
		MapState mapState = new MapState(map);
		map.addRenderer(new MapWraper(mapState));
		knownMaps.put(map.getId(), mapState);
	}

	@Override
	protected void shutdown() {
	}
	
	@Override
	public int getRequiredMajorVersion() {
		return 1;
	}

	@Override
	public int getRequiredMinorVersion() {
		return 0;
	}
}