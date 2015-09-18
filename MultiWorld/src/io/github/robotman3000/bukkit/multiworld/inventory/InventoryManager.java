package io.github.robotman3000.bukkit.multiworld.inventory;

import io.github.robotman3000.bukkit.multiworld.CommonLogic;
import io.github.robotman3000.bukkit.multiworld.MultiWorld;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class InventoryManager implements CommandExecutor, Listener {
	//TODO: make this private
	
	public final String[] commands = {"increate", "inlist", "inset", "ininfo"};
	private BukkitInventories invs = new BukkitInventories();
	private final MultiWorld plugin;
	
	private Map<String, WorldGroup> worldGroups = new HashMap<>();

	public boolean seperateWorldInventories;
	public boolean teleportOnSwich;
	public boolean seperateGamemodeInventories;
	public boolean forceGamemode;
	
	
	public InventoryManager(MultiWorld multiWorld) {
		this.plugin = multiWorld;
	}

	public void loadInventoryConfig() {	
		// Load the inventories
		Gson gson = new Gson();
		for(File file : plugin.getDataFolder().listFiles()){
			if(file.isDirectory() && file.getName().contentEquals("inventories")){
				for(File confFile : file.listFiles()){
					if(confFile.getName().contains("playerInv-") && confFile.isFile()){
						String invConfStr = CommonLogic.loadJsonAsString(confFile);
						BukkitInventory newInv = gson.fromJson(invConfStr, BukkitInventory.class);
						invs.createInventory(newInv);
					}
				}
			}
		}
		
/*		// Load the world groups and gamemode settings
		List<String> groupList = plugin.getConfig().getStringList("inventory.groups");
		Bukkit.getLogger().warning("List Length: " + groupList.size());
		for(String str : groupList){
			
			Bukkit.getLogger().warning("Inv Group " + str);
			
			List<String> groupConf = plugin.getConfig().getStringList(str);
			GameMode gamemode = Bukkit.getDefaultGameMode();
			String groupName = "default";
			ArrayList<UUID> groupWorlds = new ArrayList<>();
			for(String grStr : groupConf){
				String[] worldStrArray = grStr.split(":");
				if(worldStrArray[0].equalsIgnoreCase("gamemode")){
					gamemode = GameMode.valueOf(worldStrArray[1]);
				} else {
					World world = Bukkit.getWorld(worldStrArray[0]);
					if(world != null){
						groupWorlds.add(world.getUID());
					}
				}
			}
			gameModes.put(groupName, gamemode);
			worldGroups.put(groupName, groupWorlds);
		}*/
		
		Set<String> groupKeys = plugin.getConfig().getConfigurationSection("inventory.groups").getKeys(false);
		for(String groupKey : groupKeys){
			String path = "inventory.groups." + groupKey;
			List<String> worlds = plugin.getConfig().getStringList(path + ".worlds");
			Bukkit.getLogger().warning("Worlds " + path + " " + worlds);
			String gamemodeStr = plugin.getConfig().getString(path + ".gamemode");
			Bukkit.getLogger().warning(gamemodeStr);
			GameMode gamemode = Bukkit.getDefaultGameMode();
			try{
				gamemode = GameMode.valueOf(gamemodeStr);
			} catch (Exception e){}
			
			WorldGroup wGroup = new WorldGroup(groupKey, gamemode, worlds);
			worldGroups.put(groupKey, wGroup);
		}
	}

	public void saveInventoryConfig() {
		// Save the inventory data
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		for(BukkitInventory inv : invs.getInventories()){
			File invDataFolder = new File(plugin.getDataFolder(), "inventories");
			invDataFolder.mkdirs();
			File invConfFile = new File(invDataFolder, "playerInv-" + inv.getInventoryId() + ".json");
			CommonLogic.saveJsonAsFile(invConfFile, gson.toJson(inv));
		}
		
		// Save the world groups and gamemode settings
		
		for(String groupkey : worldGroups.keySet()){
			String path = "inventory.groups." + groupkey;
			WorldGroup group = worldGroups.get(groupkey);
			
			plugin.getConfig().createSection(path);
			plugin.getConfig().set(path + ".gamemode", group.getGamemode().toString());
			plugin.getConfig().set(path + ".worlds", group.getWorlds());
		}
		
/*		ArrayList<String> worldList = new ArrayList<String>();
		HashMap<String, List<String>> groups = new HashMap<>();
		
		for(String groupStr : worldGroups.keySet()){
			boolean gamemodeSet = false;
			List<UUID> groupWorlds = worldGroups.get(groupStr);
			ArrayList<String> groupWorldString = new ArrayList<String>();
			if(groupWorlds != null){
				for(UUID uid : groupWorlds){
					World world = Bukkit.getWorld(uid);
					if(world != null){
						groupWorldString.add(world.getName());
						worldList.add(world.getName());
					}
				}
				if(gamemodeSet == false){
					groupWorldString.add("gamemode:" + gameModes.get(groupStr));
					gamemodeSet = true;
				}
			}
			groups.put(groupStr, groupWorldString);
			Bukkit.getLogger().warning("Added to Groups: " + groupStr);
		}
		
		if(worldList.size() > 0 || groups.size() == 0){
			List<String> theSet = groups.get("default");
			if(theSet == null){
				groups.put("default", new ArrayList<String>() );
			}
			theSet = groups.get("default");
			
			for(World world : Bukkit.getWorlds()){
				if(!worldList.contains(world.getName())){
					theSet.add(world.getName());
				}
			}
			theSet.add("gamemode:" + Bukkit.getDefaultGameMode());
		}
		
		
		plugin.getConfig().set("inventory.groups", new ArrayList<String>(groups.keySet()));
		for(String key : groups.keySet()){
			plugin.getConfig().set("inventory.groups." + key, groups.get(key));
		}*/
	}

	private UUID getInventoryForEvent(PlayerState beforeState, PlayerState afterState){
		UUID theInv = invs.queryAvalInventories(afterState);
		if (theInv == null){
			invs.createInventory(BukkitInventories.generateBlankConfig(afterState));
			theInv = invs.queryAvalInventories(afterState);
		}
		return theInv;
	}
	
	private void swapInventory(PlayerState beforeState, PlayerState afterState, Boolean registerOnly){
		// If registerOnly is null it means not applicable
		
		UUID theInv;
		if(registerOnly == null || registerOnly == Boolean.FALSE){
			invs.unregisterInventory(beforeState, afterState);
			if(registerOnly == Boolean.FALSE){
				return;
			}
		}
		theInv = getInventoryForEvent(beforeState, afterState);	
		if(registerOnly == null || registerOnly == Boolean.TRUE){
			invs.registerInventory(beforeState, afterState, theInv);
		}
		
/*		if(afterState.getPlayer().getGameMode() != afterState.getGamemode()){
			afterState.getPlayer().setGameMode(afterState.getGamemode());
		}*/
	}

	private void forceGamemode(PlayerState evt) {
		if(forceGamemode){
			GameMode gamemode = getGamemodeForWorld(evt.getPlayer().getWorld());
			if(!gamemode.equals(evt.getGamemode())){
				evt.getPlayer().setGameMode(gamemode);
			}
		}
	}
	
	private GameMode getGamemodeForWorld(World world) {
		String worldGroup = getGroupForWorld(world);
		return getGamemodeForGroup(worldGroup);
	}

	private String getGroupForWorld(World world) {
		for(String str : worldGroups.keySet()){
			WorldGroup group = worldGroups.get(str);
			if(group.getWorlds().contains(world.getName())){
				return str;
			}
		}
		return "default";
	}
	
	private GameMode getGamemodeForGroup(String groupName){
		WorldGroup gamemode = worldGroups.get(groupName);
		if(gamemode != null){
			return gamemode.getGamemode();
		}
		return Bukkit.getDefaultGameMode();
	}

	// Command Logic
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		switch (cmd.getName()) {
		case "inlist":
			return listInvCommand(sender, cmd, label, args);
		case "inset":
			return setInvCommand(sender, cmd, label, args);
		case "increate":
			return createInvCommand(sender, cmd, label, args);
		case "ininfo":
			return invInfoCommand(sender, cmd, label, args);
		default:
			sender.sendMessage("Command Error in the InventoryManager Class!!");
		}
		return false;
	
	}
	
	// Event Handlers
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent evt) {
		PlayerState playerState = new PlayerState(evt.getPlayer(), evt.getPlayer().getWorld(), evt.getPlayer().getGameMode());
		swapInventory(playerState, playerState, Boolean.FALSE);
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent evt) {
		PlayerState playerState = new PlayerState(evt.getPlayer(), evt.getPlayer().getWorld(), evt.getPlayer().getGameMode());
		swapInventory(playerState, playerState, Boolean.TRUE);
		forceGamemode(playerState);
	}
	
	@EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent evt) {
		PlayerState beforeState = new PlayerState(evt.getPlayer(), evt.getFrom(), evt.getPlayer().getGameMode());
		PlayerState afterState = new PlayerState(evt.getPlayer(), evt.getPlayer().getWorld(), evt.getPlayer().getGameMode());
		swapInventory(beforeState, afterState, null);
		forceGamemode(afterState);
	}

	@EventHandler
	public void onPlayerGamemodeChanged(PlayerGameModeChangeEvent evt) {
		PlayerState beforeState = new PlayerState(evt.getPlayer(), evt.getPlayer().getWorld(), evt.getPlayer().getGameMode());
		PlayerState afterState = new PlayerState(evt.getPlayer(), evt.getPlayer().getWorld(), evt.getNewGameMode());
		swapInventory(beforeState, afterState, null);
	}
	
	// Command Work Code
	private boolean invInfoCommand(CommandSender sender, Command cmd, String label, String[] args) {
		//Player player = (Player) sender;
		//player.sendMessage(invs.getCurrentInventory(player));
		return false;
	}

	private boolean createInvCommand(CommandSender sender, Command cmd, String label, String[] args) {
		//Player player = (Player) sender;
		//player.sendMessage(invs.);
		return false;
	}

	private boolean setInvCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;
		player.sendMessage("Updating your inventory");
		//String str = inventoryKeeper.getCurrentInventory(player);
		//invs.unregisterInventory(player);
		//invs.registerInventory(player, UUID.fromString(args[0]));
		//inventoryKeeper.setPlayerInventory(player, inventoryKeeper.getInventory(args[0]));
		return true;
	}

	private boolean listInvCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(invs.getInventories().size() == 0){
			sender.sendMessage(ChatColor.GOLD + "There are no Unregistered inventories");
		} else {
			for(BukkitInventory inv : invs.getInventories()){
				sender.sendMessage("Unregistered Inv: " + ChatColor.BLUE + inv.getDisplayName() + " " + ChatColor.GREEN + inv.getPlayerState().getWorld().getName() + " " + ChatColor.YELLOW + inv.getPlayerState().getGamemode());

			}
		}
		
		if(invs.getRegisteredInventories().size() == 0){
			sender.sendMessage(ChatColor.GOLD + "There are no Registered inventories");
		} else {
			for(PlayerState inv : invs.getRegisteredInventories()){
				sender.sendMessage("Registered Inv: " + ChatColor.BLUE + inv.getPlayer().getDisplayName() + " " + ChatColor.GREEN + inv.getWorld().getName() + " " + ChatColor.YELLOW + inv.getGamemode());
			}
		}
		
		if(worldGroups.size() == 0){
			sender.sendMessage(ChatColor.GOLD + " There are no world groups");
		} else {
			for(String str : worldGroups.keySet()){
				WorldGroup group = worldGroups.get(str);
				sender.sendMessage("World Group (" + str + "): " + ChatColor.BLUE + group.getName() + " " + ChatColor.GREEN + group.getWorlds() + " " + ChatColor.YELLOW + group.getGamemode());
			}
		}
		return true;
	}
}
