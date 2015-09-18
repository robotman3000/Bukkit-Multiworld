package io.github.robotman3000.bukkit.multiworld.inventory;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class BukkitInventory {
	private UUID inventoryId = UUID.randomUUID();
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
	private double healthPoints = 20;
	private int xpLevel = 0;
	private int remainingAir = 300;
	private float foodSaturation;
	private Vector velocity = new Vector();
	private transient ItemStack[] armorContents = new ItemStack[0];
	private transient ItemStack[] inventoryContents = new ItemStack[0];
	private transient ItemStack[] enderChest = new ItemStack[0];
	private FixedLocation playerLocation = new FixedLocation(getAWorld().getSpawnLocation());
	private PlayerState playerState;

	/**
	 * This constructor is meant for use by gson
	 * @Deprecated
	 */
	@Deprecated
	public BukkitInventory(){
		
	}

	public BukkitInventory(PlayerState playerState){
		this.setPlayerState(playerState);
		this.configureForplayer(playerState.getPlayer());
	}

	protected void setLocation(Location location) {
		this.playerLocation = new FixedLocation(location);
	}

	public UUID getInventoryId() {
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
		if(conf instanceof BukkitInventory){
			BukkitInventory inv = (BukkitInventory) conf;
			if(this.inventoryId.equals(inv.inventoryId)){
				return true;
			}
		}
		return false;
	}
	
	private World getAWorld(){
		return Bukkit.getServer().getWorlds().get(0);
	}
	
	private void configureForplayer(Player player){
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

	public PlayerState getPlayerState() {
		return playerState;
	}

	private void setPlayerState(PlayerState playerState) {
		this.playerState = playerState;
	}
}
