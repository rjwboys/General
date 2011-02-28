package com.nijikokun.bukkit.General;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.nijiko.General.ConfigurationHandler;
import com.nijiko.General.DefaultConfiguration;
import org.bukkit.plugin.Plugin;

/**
 * General 2.x
 * Copyright (C) 2011  Nijikokun <nijikokun@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
public class General extends JavaPlugin {
    /*
     * Loggery Foggery
     */
    public static final Logger log = Logger.getLogger("Minecraft");

    /*
     * Central Data pertaining directly to the plugin name & versioning.
     */
    public static String name = "General";
    public static String codename = "Hindenburg";
    public static String version = "2.2";

    /**
     * Listener for the plugin system.
     */
    public iListen lstn = new iListen(this);

    /**
     * Things the controller needs to watch permissions for
     */
    private final String[] watching = { "manage-plugins", "teleport", "spawn", "set-spawn", "set-time", "give-items", "see-player-info" };

    /*
     * Internal Properties controllers
     */
    public static iProperty itemsp;
    private final DefaultConfiguration config;
    public static File Motd;
    public static Permissions permissions = null;

    /*
     * Variables
     */
    public static String directory = "General" + File.separator, spawn = "";
    public static HashMap<String, String> items;
    public static boolean health = true, coords = true, commands = true;

    public General() {

		// Start Registration
		//folder.mkdirs();
	
		// Attempt
		if(!(new File(getDataFolder(), "config.yml").exists())) {
			DefaultConfiguration("config.yml");
		}
	
		// Gogo
		this.config = new ConfigurationHandler(getConfiguration());
		//getConfiguration().load();
		this.config.load();
	
		// Register
		//registerEvents();
	
		log.info(Messaging.bracketize(name) + " version " + Messaging.bracketize(version) + " ("+codename+") loaded");
    }

    public void onDisable() {
		log.info(Messaging.bracketize(name) + " version " + Messaging.bracketize(version) + " ("+codename+") disabled");
    }

    public void onEnable() {
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, lstn, Priority.Normal, this);
    	registerCommands();
    	
		Motd = new File(getDataFolder() + File.separator + "general.motd");
		itemsp = new iProperty("items.db");
	
		try { Motd.createNewFile(); } catch (IOException ex) { }
	
		// Setup
		setupCommands();
		setupPermissions();
		setupItems();
    }

    public void setupCommands() {
		try {
			BufferedReader in = new BufferedReader(new FileReader(getDataFolder() + File.separator + "general.help"));
			String str;
	
			while ((str = in.readLine()) != null) {
				lstn.Commands.add(str);
			}
	
			in.close();
		} catch (IOException e) { }
    }

    public void registerCommands() {
		if(commands) {
			lstn.register_custom_command("&f/online|playerlist|who &6-&e Shows player list.");
			lstn.register_custom_command("&f/online|playerlist|who [player] &6-&e Shows player info.");
			lstn.register_custom_command("&f/spawn &6-&e Return to spawn");
			lstn.register_custom_command("&f/setspawn &6-&e Change spawn to where you are.");
			lstn.register_custom_command("&f/time help &6-&e for more information.");
			lstn.register_custom_command("&f/me &6-&e Emote your messages");
			lstn.register_custom_command("&f/afk (message) &6-&e Go away or come back");
			lstn.register_custom_command("&f/i|give [item|player] (item|amount) (amount) &6-&e Give items.");
			lstn.register_custom_command("&f/message|tell|m [player] [message] &6-&e Private msg");
			lstn.register_custom_command("&f/compass|getpos &6-&e information about position");
			lstn.register_custom_command("&f/rlidb|reloaditems &6-&e Reload the items.db");
			lstn.register_custom_command("&f/help or /? &6-&e Returns this documentation");
		}
    }

    public void setupPermissions() {
		Plugin test = this.getServer().getPluginManager().getPlugin("Permissions");
	
		if(this.permissions == null) {
			if(test != null) {
				this.permissions = (Permissions)test;
			} else {
				log.info(Messaging.bracketize(name) + " Permission system not enabled. Disabling plugin.");
				this.getServer().getPluginManager().disablePlugin(this);
			}
		}
    }

    /**
     * Setup Items
     */
    public void setupItems() {
		Map mappedItems = null;
		items = new HashMap<String, String>();
	
		try {
			mappedItems = itemsp.returnMap();
		} catch (Exception ex) {
			log.warning(Messaging.bracketize(name + " Flatfile") + " could not open items.db!");
		}
	
		if(mappedItems != null) {
			for (Object item : mappedItems.keySet()) {
				String left = (String)item;
				String right = (String) mappedItems.get(item);
				String id = left.trim();
				String itemName;
				//log.info("Found " + left + "=" + right + " in items.db");
				if(id.matches("[0-9]+") || id.matches("[0-9]+,[0-9]+")) {
					//log.info("matches");
					if(right.contains(",")) {
						String[] synonyms = right.split(",");
						itemName = synonyms[0].replaceAll("\\s","");
						items.put(id, itemName);
						//log.info("Added " + id + "=" + itemName);
						for(int i = 1; i < synonyms.length; i++) {
							itemName = synonyms[i].replaceAll("\\s","");
							items.put(itemName, id);
							//log.info("Added " + itemName + "=" + id);
						}
					} else {
						itemName = right.replaceAll("\\s","");
						items.put(id, itemName);
						//log.info("Added " + id + "=" + itemName);
					}
				} else {
					itemName = left.replaceAll("\\s","");
					id = right.trim();
					items.put(itemName, id);
					//log.info("Added " + itemName + "=" + id);
				}
			}
		}
    }

    private void DefaultConfiguration(String name) {
		try {
			(new File(getDataFolder(), name)).createNewFile();
		} catch (IOException ex) { }
    }
    
    /**
     * Commands sent from in game to us.
     *
     * @param player The player who sent the command.
     * @param split The input line split by spaces.
     * @return <code>boolean</code> - True denotes that the command existed, false the command doesn't.
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
    	//(PlayerChatEvent event)
        Player player = sender instanceof Player ? (Player) sender : null;
		World world = player.getWorld();
		//server = ((CraftWorld)world).getHandle();
		Messaging.save(player);
        String base = command.getName().toLowerCase();
		
		if(/*(!event.isCancelled()) && */Misc.isEither(base, "help", "?")) {
			int page = 0;
	
			if (args.length >= 2) {
				try {
					page = Integer.parseInt(args[1]);
				} catch (NumberFormatException ex) {
					Messaging.send("&c;Not a valid page number.");// event.setCancelled(true);
					return true;
				}
			}
	
			lstn.print_commands(page); //event.setCancelled(true);
			return true;
		}
		
		// Disabled for now because I have no idea what m or o are. --celticminstrel
//		if((!event.isCancelled()) && Misc.is(base, "/setspawn")) {
//			if (!General.Permissions.Security.permission(player, "general.spawn.set")) {
//				return;
//			}
//			
//			server.m = (int)Math.ceil(player.getLocation().getX());
//			server.o = (int)Math.ceil(player.getLocation().getZ());
//			
//			Messaging.send("&eSpawn position changed to where you are standing.");
//		}
	
		if(Misc.isEither(base, "rlidb","reloaditems")) {
			if (!General.permissions.Security.permission(player, "general.reloaditems")) {
				return true;
			}
	
		   setupItems();
		   Messaging.send("&e;Items.db reloaded.");
		}
	
		// Disabled for now because I have no idea what m or o are. --celticminstrel
//		if(Misc.is(base, "/spawn")) {
//			if (!General.Permissions.Security.permission(player, "general.spawn")) {
//				return;
//			}
//	
//			player.teleportTo(spawn(player));
//		}
	
		if(/*(!event.isCancelled()) && */Misc.is(base, "motd")) {
			String[] motd = lstn.readMotd();
	
			if(motd == null || motd.length < 1) {
				return true;
			}
	
			String location = (int)player.getLocation().getX() +"x, " + (int)player.getLocation().getY() +"y, " + (int)player.getLocation().getZ() +"z";
			String ip = player.getAddress().getAddress().getHostAddress();
			String balance = "";
			Plugin test = getServer().getPluginManager().getPlugin("iConomy");
	
			//if(test != null) {
			//iConomy iConomy = (iConomy)test;
			//balance = iConomy.db.get_balance(player.getName()) + " " + iConomy.currency;
			//}
	
			for(String line : motd) {
				Messaging.send(
					player,
					Messaging.argument(
					line,
					new String[]{
						"+dname,+d", "+name,+n", "+location,+l", "+health,+h", "+ip", "+balance", "+online"
					},
					new String[]{
						player.getDisplayName(),
						player.getName(),
						location,
						Misc.string(player.getHealth()),
						ip,
						balance,
						Misc.string(getServer().getOnlinePlayers().length)
					}
					)
				);
			}
		}
	
		if (Misc.isEither(base, "tp", "teleport")) {
			if (!General.permissions.Security.permission(player, "general.teleport")) {
				return true;
			}
	
			if (args.length == 2) {
				String to = args[1];
		
				if (to.equalsIgnoreCase("*")) {
					Messaging.send("&c;Incorrect usage of wildchar *");
				} else if (to.contains(",")) {
					Messaging.send("&c;Incorrect usage of multiple players.");
				} else {
					if (!lstn.teleport(player.getName(), to)) {
						Messaging.send("&c;Cannot find destination player: &f" + to);
					}
				}
			} else if (args.length == 3) {
				String who = args[1];
				String to = args[2];
		
				if (to.equalsIgnoreCase("*")) {
					Messaging.send("&c;Incorrect usage of wildchar *");
				} else if (to.contains(",")) {
					Messaging.send("&c;Incorrect usage of multiple players.");
				} else {
					if (!lstn.teleport(who, to)) {
						Messaging.send("&c;Could not teleport " + who + " to " + to + ".");
					}
				}
			} else {
				Messaging.send("&c;------ &f/tp help&c ------");
				Messaging.send("&c;/tp [player] &f-&c Teleport to a player");
				Messaging.send("&c;/tp [player] [to] &f-&c Teleport player to another player");
				Messaging.send("&c;/tp [player,...] [to] &f-&c Teleport players to another player");
				Messaging.send("&c;/tp * [to] &f-&c Teleport everyone to another player");
			}
		}
	
		if (Misc.isEither(base, "s", "tphere")) {
			if (!General.permissions.Security.permission(player, "general.teleport.here")) {
				return true;
			}
	
			if (args.length < 2) {
				Messaging.send("&c;Correct usage is:&f /s [player] &cor&f /tphere [player]");
				return true;
			}
	
			Player who = Misc.playerMatch(args[1]);
	
			if (who != null) {
				if (who.getName().equalsIgnoreCase(player.getName())) {
					Messaging.send("&c;Wow look at that! You teleported yourself to yourself!");
					return true;
				}
		
				log.info(player.getName() + " teleported " + who.getName() + " to their self.");
				who.teleportTo(player.getLocation());
			} else {
				Messaging.send("&c;Can't find user " + args[1] + ".");
			}
		}
	
		if(/*(!event.isCancelled()) && */Misc.is(base, "/getpos")) {
			Messaging.send("Pos X: " + player.getLocation().getX() + " Y: " + player.getLocation().getY() + " Z: " + player.getLocation().getZ());
			Messaging.send("Rotation: " + player.getLocation().getYaw() + " Pitch: " + player.getLocation().getPitch());
	
			double degreeRotation = ((player.getLocation().getYaw() - 90) % 360);
	
			if (degreeRotation < 0) {
				degreeRotation += 360.0;
			}
	
			Messaging.send("Compass: " + lstn.getDirection(degreeRotation) + " (" + (Math.round(degreeRotation * 10) / 10.0) + ")");
		}
		
		if(Misc.is(base, "/compass")) {
			double degreeRotation = ((player.getLocation().getYaw() - 90) % 360);
	
			if (degreeRotation < 0) {
				degreeRotation += 360.0;
			}
	
			Messaging.send("&cCompass: " + lstn.getDirection(degreeRotation));
		}
	
		if(Misc.isEither(base, "afk", "away")) {
			if ((lstn.AFK != null || !lstn.AFK.isEmpty()) && lstn.isAFK(player)) {
				Messaging.send("&7You have been marked as back.");
				lstn.unAFK(player);
			} else {
			Messaging.send("&7;You are now currently marked as away.");
				String reason = "AFK";
		
				if(args.length >= 2) {
					reason = Misc.combineSplit(1, args, " ");
				}
		
				lstn.AFK(player, reason);
			}
		}
	
		if(Misc.isEither(base, "msg", "tell")) {
			if (args.length < 3) {
				Messaging.send("&c;Correct usage is: /msg [player] [message]");
				//event.setCancelled(true);
				return true;
			}
	
			Player who = Misc.playerMatch(args[1]);
	
			if (who != null) {
				if (who.getName().equals(player.getName())) {
					Messaging.send("&cYou can't message yourself!");
					//event.setCancelled(true);
					return true;
				}
		
				Messaging.send("(MSG) <" + player.getName() + "> " + Misc.combineSplit(2, args, " "));
				Messaging.send(who, "(MSG) <" + player.getName() + "> " + Misc.combineSplit(2, args, " "));
		
				if (lstn.isAFK(who)) {
					Messaging.send("&7;This player is currently away.");
					Messaging.send("&7;Reason: " + lstn.AFK.get(player));
				}
			} else {
				Messaging.send("&c;Couldn't find player " + args[1]);
			}
		}
	
		if (Misc.isEither(base, "i", "give") || Misc.is(base, "item")) {
			if (!General.permissions.Security.permission(player, "general.items")) {
				return true;
			}
	
			if (args.length < 2) {
				Messaging.send("&c;Correct usage is: /i [item(:type)|player] [item(:type)|amount] (amount)");
				return true;
			}
	
			int itemId = 0;
			int[] tmp;
			int amount = 1;
			int dataType = -1;
			Player who = null;
	
			try {
				if (args[1].contains(":")) {
					String[] data = args[1].split(":");
		
					try {
						dataType = Integer.valueOf(data[1]);
					} catch (NumberFormatException e) {
						dataType = -1;
					}
		
					tmp = Items.validate(data[0]);
					itemId = tmp[0];
				} else {
					tmp = Items.validate(args[1]);
					itemId = tmp[0];
					dataType = tmp[1];
				}
		
				if (itemId == -1) {
					who = Misc.playerMatch(args[1]);
				}
			} catch (NumberFormatException e) {
				who = Misc.playerMatch(args[1]);
			}
	
			if ((itemId == 0 || itemId == -1) && who != null) {
				String i = args[2];
		
				if (i.contains(":")) {
					String[] data = i.split(":");
		
					try {
						dataType = Integer.valueOf(data[1]);
					} catch (NumberFormatException e) {
						dataType = -1;
					}
		
					i = data[0];
				}
		
				tmp = Items.validate(i);
				itemId = tmp[0];
		
				if (dataType == -1) {
					dataType = Items.validateGrabType(i);
				}
			}
	
			if (itemId == -1 || itemId == 0) {
				Messaging.send("&c;Invalid item.");
				return true;
			}
	
			if (dataType != -1) {
				if (!Items.validateType(itemId, dataType)) {
					Messaging.send("&f;" + dataType + "&c; is not a valid data type for &f;" + Items.name(itemId, -1) + "&c;.");
					return true;
				}
			}
	
			if (args.length >= 3 && who == null) {
				try {
					amount = Integer.valueOf(args[2]);
				} catch (NumberFormatException e) {
					amount = 1;
				}
			} else if (args.length >= 4) {
				if (who != null) {
					try {
						amount = Integer.valueOf(args[3]);
					} catch (NumberFormatException e) {
						amount = 1;
					}
				} else {
					who = Misc.playerMatch(args[3]);
				}
			}
	
			if (amount == 0) { // give one stack
				if (itemId == 332 || itemId == 344) {
					amount = 16; // eggs and snowballs
				} else if (Items.isStackable(itemId)) {
					amount = 64;
				} else {
					amount = 1;
				}
			}
	
			if (who == null) {
				who = player;
			}
	
			int slot = who.getInventory().firstEmpty();
	
			if (dataType != -1) {
				if (slot < 0) {
					who.getWorld().dropItem(who.getLocation(), new ItemStack(itemId, amount, ((byte) dataType)));
				} else {
					who.getInventory().addItem(new ItemStack(itemId, amount, ((byte) dataType)));
				}
			} else {
				if (slot < 0) {
					who.getWorld().dropItem(who.getLocation(), new ItemStack(itemId, amount));
				} else {
					who.getInventory().addItem(new ItemStack(itemId, amount));
				}
			}
	
			if (who.getName().equals(player.getName())) {
				Messaging.send(who, "&2;Enjoy! Giving &f;" + amount + "&2; of &f;" + Items.name(itemId, dataType) + "&2;.");
			} else {
				Messaging.send(who, "&2;Enjoy the gift! &f;" + amount + "&2; of &f;" + Items.name(itemId, dataType) + "&2;;. c:!");
			}
	
			//event.setCancelled(true);
			return true;
		}
	
		if(Misc.is(base, "time")) {
			if (!General.permissions.Security.permission(player, "general.time")) {
				return true;
			}
			//World world = sender instanceof Player ? ((Player) sender).getWorld() : plugin.getServer().getWorlds().get(0);
	
			long time = lstn.getTime(world);
			long timeRelative = lstn.getRelativeTime(world);
			long timeStart = lstn.getStartTime(world);
	
			if(args.length < 2) {
					int hours = (int)((time / 1000+8) % 24);
					int minutes = (((int)(time % 1000)) / 1000) * 60;
					Messaging.send("&c;Time: "+hours+":"+minutes);
			} else if (args.length == 2) {
				String cmd = args[1];
				if (Misc.is(cmd, "help")) {
					Messaging.send("&c;-------- /time help --------");
					Messaging.send("&c;/time &f;-&c; Shows relative time");
					Messaging.send("&c;/time day &f;-&c; Turns time to day");
					Messaging.send("&c;/time night &f;-&c; Turns time to night");
					Messaging.send("&c;/time raw &f;-&c; Shows raw time");
					Messaging.send("&c;/time =13000 &f;-&c; Sets raw time");
					Messaging.send("&c;/time +500 &f;-&c; Adds to raw time");
					Messaging.send("&c;/time -500 &f;-&c; Subtracts from raw time");
					Messaging.send("&c;/time 12 &f;-&c; Set relative time");
				} else if (Misc.is(cmd, "day")) {
					lstn.setTime(world, timeStart);
				} else if (Misc.is(cmd, "night")) {
					lstn.setTime(world, timeStart+13000);
				} else if (Misc.is(cmd, "raw")) {
					Messaging.send("&c;Raw:  " + time);
				} else if (cmd.startsWith("=")) {
					try {
						lstn.setTime(world, Long.parseLong(cmd.substring(1)));
					} catch(NumberFormatException ex) { }
				} else if (cmd.startsWith("+")) {
					try {
						lstn.setTime(world, time+Long.parseLong(cmd.substring(1)));
					} catch(NumberFormatException ex) { }
				} else if (cmd.startsWith("-")) {
					try {
						lstn.setTime(world, time-Long.parseLong(cmd.substring(1)));
					} catch(NumberFormatException ex) { }
				} else {
					try {
						timeRelative = (Integer.parseInt(cmd)*1000-8000+24000)%24000;
						lstn.setTime(world, timeStart + timeRelative);
					} catch(NumberFormatException ex) { }
				}
			} else {
				Messaging.send("&c;Correct usage is: /time [day|night|raw|([=|+|-]time)] (rawtime)");
				Messaging.send("&c;/time &f;-&c; Shows relative time");
				Messaging.send("&c;/time day &f;-&c; Turns time to day");
				Messaging.send("&c;/time night &f;-&c; Turns time to night");
				Messaging.send("&c;/time raw &f;-&c; Shows raw time");
				Messaging.send("&c;/time =13000 &f;-&c; Sets raw time");
				Messaging.send("&c;/time +500 &f;-&c; Adds to raw time");
				Messaging.send("&c;/time -500 &f;-&c; Subtracts from raw time");
				Messaging.send("&c;/time 12 &f;-&c; Set relative time");
			}
	
			return true;
		}
	
		if(Misc.isEither(base, "/layerlist", "online") || Misc.is(base, "who")) {
			if(args.length == 2) {
				if (!General.permissions.Security.permission(player, "general.player-info")) {
					return true;
				}
		
				Player lookup = Misc.playerMatch(args[1]);
				String name = lookup.getName();
				String displayName = lookup.getDisplayName();
				String bar = "";
				String location = "";
		
				if(General.health) {
					int health = lookup.getHealth();
					int length = 10;
					int bars = Math.round(health/2);
					int remainder = length-bars;
					String hb_color = ((bars >= 7) ? "&2;" : ((bars < 7 && bars >= 3) ? "&e;" : ((bars < 3) ? "&c;" : "&2;")));
					bar = " &f;["+ hb_color + Misc.repeat('|', bars) + "&7;" + Misc.repeat('|', remainder) + "&f;]";
				}
		
				if(General.coords) {
					int x = (int)lookup.getLocation().getX();
					int y = (int)lookup.getLocation().getY();
					int z = (int)lookup.getLocation().getZ();
					location = x+"x, "+y+"y, "+z+"z";
				}
		
				Messaging.send("&f;------------------------------------------------");
				Messaging.send("&e; Player &f;["+name+"/"+displayName+"]&e; Info:");
				Messaging.send("&f;------------------------------------------------");
				Messaging.send("&6; Username: &f;" + name + ((General.health) ? bar : ""));
		
				if(General.coords) {
					Messaging.send("&6; -&e; Location: &f;" + location);
				}
		
				Messaging.send("&6; -&e; Status: &f;" + ((lstn.isAFK(lookup)) ? "AFK ("+lstn.AFK.get(lookup)+")" : "Around."));
		
				Messaging.send("&f;------------------------------------------------");
			} else {
				ArrayList<Player> olist = new ArrayList<Player>();
				Player[] players = new Player[]{};
		
				for(Player p : getServer().getOnlinePlayers()) {
					if(p == null || !p.isOnline()) {
						continue;
					} else {
						olist.add(p);
					}
				}
		
				// Cast it to something empty to prevent nulls / empties
				players = olist.toArray(players);
		
				if(players.length <= 1 || olist.isEmpty()) {
					Messaging.send("&e;Player list (1):");
					Messaging.send("&f; - Just you.");
					Messaging.send(" ");
				} else {
					int online = players.length;
					ArrayList<String> list = new ArrayList<String>();
					String currently = "";
					int on = 0, perLine = 5, i = 1;
		
					for(Player current : players) {
						if(current == null) {
							++on;
							continue;
						}
						if(i == perLine) {
							list.add(currently);
							currently = "";
							i = 0;
						}
						++on;
						++i;
						currently += (on >= online) ? current.getName() : current.getName() + ", ";
					}
		
					// Guess list was smaller than 5.
					list.add(currently);
		
					Messaging.send("&e;Players list ("+on+"):");
		
					for(String line : list) {
						Messaging.send(line);
					}
		
					Messaging.send(" ");
				}
			}
		}
		return true;
    }
}
