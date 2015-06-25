package io.github.robotman3000.bukkit.multiworld;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class InventoryConfig {
	private String inventoryId = UUID.randomUUID().toString();
	private String defaultPlayer = "noPlayer";
	private boolean canFly = false;
	private FixedLocation bedSpawnPoint = new FixedLocation(getAWorld().getSpawnLocation());
	private FixedLocation compassTarget = new FixedLocation(getAWorld().getSpawnLocation());
	private String displayName = "Steve";
	private float exhaustion;
	private float xpPoints = 0;
	private float fallDistance = 0;
	private int fireTicks = 0;
	private boolean isFlying = false;
	private int foodLevel = 20;
	private GameMode gamemode = GameMode.SURVIVAL;
	private double healthPoints = 20;
	private int xpLevel = 0;
	private int remainingAir = 300;
	private float foodSaturation;
	private Vector velocity = new Vector();
	private ItemStack[] armorContents = new ItemStack[0];
	private ItemStack[] inventoryContents = new ItemStack[0];
	private ItemStack[] enderChest = new ItemStack[0];
	private FixedLocation playerLocation = new FixedLocation(getAWorld().getSpawnLocation());

	/**
	 * This constructor is meant for use by gson
	 * @Deprecated
	 */
	@Deprecated
	public InventoryConfig(){
		
	}

	public InventoryConfig(Player player){
		setCanFly(player.getAllowFlight());
		setBedSpawnPoint(player.getBedSpawnLocation());
		setCompassTarget(player.getCompassTarget());
		setDisplayName(player.getDisplayName());
		setExhaustion(player.getExhaustion());
		setXpPoints(player.getExp());
		setFallDistance(player.getFallDistance());
		setFireTicks(player.getFireTicks());
		setFlying(player.isFlying());
		setFoodLevel(player.getFoodLevel());
		setGamemode(player.getGameMode());
		setHealthPoints(player.getHealth());
		setXpLevel(player.getLevel());
		setRemainingAir(player.getRemainingAir());
		setFoodSaturation(player.getSaturation());
		setVelocity(player.getVelocity());
		setArmorContents(player.getInventory().getArmorContents());
		setInventoryContents(player.getInventory().getContents());
		setEnderChest(player.getEnderChest().getContents());
		setLocation(player.getLocation());
	}
	
	protected void setLocation(Location location) {
		this.playerLocation = new FixedLocation(location);
	}

	public void makeDefaultForPlayer(Player player) {
		defaultPlayer = player.getUniqueId().toString();
	}

	public String getDefaultPlayer() {
		return defaultPlayer;
	}

	public String getInventoryId() {
		return inventoryId;
	}

	public boolean canFly() {
		return canFly;
	}

	public Location getBedSpawnPoint() {
		return bedSpawnPoint.getLocation();
	}

	public Location getCompassTarget() {
		return compassTarget.getLocation();
	}

	public String getDisplayName() {
		return displayName;
	}

	public float getExhaustion() {
		return exhaustion;
	}

	public float getXpPoints() {
		return xpPoints;
	}

	public float getFallDistance() {
		return fallDistance;
	}

	public int getFireTicks() {
		return fireTicks;
	}

	public boolean isFlying() {
		return isFlying;
	}

	public int getFoodLevel() {
		return foodLevel;
	}

	public GameMode getGamemode() {
		return gamemode;
	}

	public double getHealthPoints() {
		return healthPoints;
	}

	public int getXpLevel() {
		return xpLevel;
	}

	public int getRemainingAir() {
		return remainingAir;
	}

	public float getFoodSaturation() {
		return foodSaturation;
	}

	public Vector getVelocity() {
		return velocity;
	}

	public ItemStack[] getArmorContents() {
		return armorContents;
	}

	public ItemStack[] getInventoryContents() {
		return inventoryContents;
	}

	protected void setInventoryId(String inventoryId) {
		this.inventoryId = inventoryId;
	}

	protected void setDefaultPlayer(String defaultPlayer) {
		this.defaultPlayer = defaultPlayer;
	}

	protected void setCanFly(boolean canFly) {
		this.canFly = canFly;
	}

	protected void setBedSpawnPoint(Location bedSpawnPoint) {
		this.bedSpawnPoint = new FixedLocation(bedSpawnPoint);
	}

	protected void setCompassTarget(Location compassTarget) {
		this.compassTarget = new FixedLocation(compassTarget);
	}

	protected void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	protected void setExhaustion(float exhaustion) {
		this.exhaustion = exhaustion;
	}

	protected void setXpPoints(float xpPoints) {
		this.xpPoints = xpPoints;
	}

	protected void setFallDistance(float fallDistance) {
		this.fallDistance = fallDistance;
	}

	protected void setFireTicks(int fireTicks) {
		this.fireTicks = fireTicks;
	}

	protected void setFlying(boolean isFlying) {
		this.isFlying = isFlying;
	}

	protected void setFoodLevel(int foodLevel) {
		this.foodLevel = foodLevel;
	}

	protected void setGamemode(GameMode gamemode) {
		this.gamemode = gamemode;
	}

	protected void setHealthPoints(double healthPoints) {
		this.healthPoints = healthPoints;
	}

	protected void setXpLevel(int xpLevel) {
		this.xpLevel = xpLevel;
	}

	protected void setRemainingAir(int remainingAir) {
		this.remainingAir = remainingAir;
	}

	protected void setFoodSaturation(float foodSaturation) {
		this.foodSaturation = foodSaturation;
	}

	protected void setVelocity(Vector velocity) {
		this.velocity = velocity;
	}

	protected void setArmorContents(ItemStack[] armorContents) {
		this.armorContents = armorContents;
	}

	protected void setInventoryContents(ItemStack[] inventoryContents) {
		this.inventoryContents = inventoryContents;
	}

	protected void setEnderChest(ItemStack[] enderChest) {
		this.enderChest = enderChest;
	}

	public ItemStack[] getEnderChest() {
		return enderChest;
	}
	
	public Location getLocation(){
		return playerLocation.getLocation();	
	}
	
	@Override
	public boolean equals(Object conf){
		if(conf instanceof InventoryConfig){
			InventoryConfig inv = (InventoryConfig) conf;
			if(this.inventoryId.equalsIgnoreCase(inv.inventoryId)){
				return true;
			}
		}
		return false;
	}
	
	private World getAWorld(){
		return Bukkit.getServer().getWorlds().get(0);
	}

	public void updateContents(Player player) {
		setCanFly(player.getAllowFlight());
		setBedSpawnPoint(player.getBedSpawnLocation());
		setCompassTarget(player.getCompassTarget());
		setDisplayName(player.getDisplayName());
		setExhaustion(player.getExhaustion());
		setXpPoints(player.getExp());
		setFallDistance(player.getFallDistance());
		setFireTicks(player.getFireTicks());
		setFlying(player.isFlying());
		setFoodLevel(player.getFoodLevel());
		setGamemode(player.getGameMode());
		setHealthPoints(player.getHealth());
		setXpLevel(player.getLevel());
		setRemainingAir(player.getRemainingAir());
		setFoodSaturation(player.getSaturation());
		setVelocity(player.getVelocity());
		setArmorContents(player.getInventory().getArmorContents());
		setInventoryContents(player.getInventory().getContents());
		setEnderChest(player.getEnderChest().getContents());
		setLocation(player.getLocation());
	}
}
