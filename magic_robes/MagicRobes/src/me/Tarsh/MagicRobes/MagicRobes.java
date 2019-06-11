package me.Tarsh.MagicRobes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;

import org.bukkit.event.player.PlayerJoinEvent;

public class MagicRobes extends JavaPlugin {
	
	public static Server server;
	private static final Logger log = Logger.getLogger("Minecraft");
	public static int cost = 1000;
	public static int reward = 500;
	public static int penalty = 500;
	
	public static MagicRobes magicRobes;
	public static ArrayList<Player> players = new ArrayList<Player>();
	
	@Override
	public void onEnable() {
		server = this.getServer();
		magicRobes = this;
		for(Player p : this.getServer().getOnlinePlayers()) {
			players.add(p);
		}
		getServer().getPluginManager().registerEvents(new RobeListener(), this);
		BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
            	for (Player player: players) {
					if (player.getInventory().getHelmet() != null && player.getInventory().getChestplate() != null
							&& player.getInventory().getLeggings() != null && player.getInventory().getBoots() != null) {
						try {
							String helmLore = player.getInventory().getHelmet().getItemMeta().getLore().get(0);
							String chestLore = player.getInventory().getChestplate().getItemMeta().getLore().get(0);
							String leggingsLore = player.getInventory().getLeggings().getItemMeta().getLore().get(0);
							String bootsLore = player.getInventory().getBoots().getItemMeta().getLore().get(0);
							
							player.getInventory().getHelmet().getItemMeta().setUnbreakable(true);
							player.getInventory().getChestplate().getItemMeta().setUnbreakable(true);
							player.getInventory().getLeggings().getItemMeta().setUnbreakable(true);
							player.getInventory().getBoots().getItemMeta().setUnbreakable(true);
							
							
							// If part of the same set
							if (helmLore.equals(chestLore) && helmLore.equals(leggingsLore) && helmLore.equals(bootsLore)) {
								
								Resident resident = TownyUniverse.getDataSource().getResident(player.getName());
								ArrayList<String> players = new ArrayList<>();
								
								if (resident.hasTown()) {
									Town town = resident.getTown();
									for (int i = 0; i < town.getResidents().size(); i++) {
										players.add(town.getResidents().get(i).getName());
									}
									//player.sendMessage(players.toString());
								}
								
				
								// Check if wearing flame set
								if (helmLore.toLowerCase().contains("helios")) {
									boolean staff;
									try {
										staff = player.getInventory().getItemInOffHand().getItemMeta().getLore().get(0).toLowerCase().contains("helios");
									}
									catch (Exception e) {
										staff = false;
									}
									Location loc = player.getLocation().clone().subtract(0, 1, 0);
									Block b = loc.getBlock();
									
									player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20, 1));
									if (b.getType() == Material.SAND) {
										player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20, 2));
									}
									if (player.getFireTicks() > 0) {
										player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20, 2));
									}
									if (staff && day() && !player.getWorld().hasStorm())
										player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20, 3));
								}
								// Check if wearing frost set
								if (helmLore.toLowerCase().contains("cryo")) {
									boolean staff;
									try {
										staff = player.getInventory().getItemInOffHand().getItemMeta().getLore().get(0).toLowerCase().contains("cryo");
									}
									catch (Exception e) {
										staff = false;
									}
									int radius = 3;
									if (staff)
										radius = 7;
									Location loc = player.getLocation().clone().subtract(0, 1, 0);
									Block b = loc.getBlock();
									Block under = player.getLocation().clone().subtract(0, 2, 0).getBlock();
									
									
									player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20, 1));
									for (Player other : Bukkit.getOnlinePlayers()) {
										if (other.getWorld() == player.getWorld() && 	other.getLocation().distance(player.getLocation()) <= radius) {
											if (player == other) {
												if (b.getType() == Material.ICE || under.getType() == Material.ICE) {
													player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20, 0));
												}
												else if (staff){
													player.addPotionEffect(new PotionEffect(
															PotionEffectType.SPEED, 80, 0));
												}
												else {
													player.addPotionEffect(new PotionEffect(
															PotionEffectType.SLOW, 80, 0));
												}
											}
											else {
												if (!players.contains(other.getName()))
													other.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 80, 2));
											}
										}
									}
								}
								
								if (helmLore.toLowerCase().contains("aether")) {
									boolean staff;
									try {
										staff = player.getInventory().getItemInOffHand().getItemMeta().getLore().get(0).toLowerCase().contains("aether");
									}
									catch (Exception e) {
										staff = false;
									}			
									player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20, 1));
									if (day()) {
										player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20, 1));
										player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 20, 1));
									}
								}
								
								if (helmLore.toLowerCase().contains("necro")) {
									boolean staff;
									try {
										staff = player.getInventory().getItemInOffHand().getItemMeta().getLore().get(0).toLowerCase().contains("necro");
									}
									catch (Exception e) {
										staff = false;
									}
									int cap = 2;
									if (!day()) {
										cap = 5;
										player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20, 0));
									}
									int increment = -1;	
									for (Player other : Bukkit.getOnlinePlayers()) {
										if (other.getWorld() == player.getWorld() && other.getLocation().distance(player.getLocation()) <= 3) {
											increment++;
										}
										if (other.getWorld() == player.getWorld() && !day() && other.getLocation().distance(player.getLocation()) <= 10 && player != other && !players.contains(other.getName())) {
											other.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 40, 1));
											if (staff) {
												other.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 3));
											}
										}
									}
									if (increment <= 4)
										player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20, increment));
									else
										player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20, 4));
									if (increment <= cap)
										player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20, increment));
									else
										player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20, cap));
									
								}
								
								if (helmLore.toLowerCase().contains("wither")) {
									player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20, 4));
									for (Player other : Bukkit.getOnlinePlayers()) {
										if (other.getWorld() == player.getWorld() && other.getLocation().distance(player.getLocation()) <= 5) {
											if (player != other && !players.contains(other.getName())) {
												other.addPotionEffect(new PotionEffect(
														PotionEffectType.WITHER, 60, 1));
											}
										}
									}
									
								}
								
								if (player.getInventory().getItemInOffHand().getItemMeta().getLore().get(0).toLowerCase().contains("orb of healing")){
									double rand = Math.random();
									for (Player other : Bukkit.getOnlinePlayers()) {
										if (other.getWorld() == player.getWorld() && other.getLocation().distance(player.getLocation()) <= 10) {
											if (player != other && players.contains(other.getName())) {
												if (rand <= 0.005) {
													other.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_GREEN + "Hyveria" + ChatColor.DARK_GRAY + "] " +
																		ChatColor.GOLD + player.getName() + ChatColor.GREEN + " has saved you with Orb of Healing");
													other.setHealth(20);
												}
												other.addPotionEffect(new PotionEffect(
														PotionEffectType.REGENERATION, 60, 1));
											}
										}
									}
								}
								
								else if (player.getInventory().getItemInOffHand().getItemMeta().getLore().get(0).toLowerCase().contains("orb of speed")) {
									//player.sendMessage("HOT DOG");
									for (Player other : MagicRobes.players) {
										if (other.getWorld() == player.getWorld() && other.getLocation().distance(player.getLocation()) <= 20) {
											if (player != other && players.contains(other.getName())) {
												other.addPotionEffect(new PotionEffect(
														PotionEffectType.SPEED, 60, 1));
											}
										}
									}
									player.removePotionEffect(PotionEffectType.SPEED);
									player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20, 2));
								}
								
								else if (player.getInventory().getItemInOffHand().getItemMeta().getLore().get(0).toLowerCase().contains("orb of lightning")) {
									double rand = Math.random();
									if (rand <= 0.05) {
										for (Player other : Bukkit.getOnlinePlayers()) {
											if (other.getWorld() == player.getWorld() && other.getLocation().distance(player.getLocation()) <= 7) {
												if (player != other && !players.contains(other.getName())) {
													World world = player.getWorld();
													world.strikeLightning(other.getLocation());
												}
											}
										}
									}
								}
							}
						}
						
						catch (Exception e) {
							
						}
						
					}
					
				}
            }
        }, 0L, 20L);
	}

	@Override
	public void onDisable() {
		log.severe("MagicRobes Shutting Down!");

	}
	
	public boolean day() {
	    Server server = MagicRobes.server;
	    long time = server.getWorld("world").getTime();

	    return time < 12300 || time > 23850;
	}

	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		if (!(sender instanceof Player)) {
			log.info(ChatColor.RED + "You cannot start " + ChatColor.AQUA + "MagicRobes " + ChatColor.RED
					+ "through console!");
			return true;
		}

		Player player = (Player) sender;
		return true;
	}
	

}
