
package net.craftstars.general.command.info;


import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.General;
import net.craftstars.general.command.CommandBase;
import net.craftstars.general.teleport.Destination;
import net.craftstars.general.util.LanguageText;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Option;
import net.craftstars.general.util.Toolbox;

public class whoCommand extends CommandBase {
	private static final int UNAME = 1 << 0;
	private static final int DNAME = 1 << 1;
	private static final int HEALTH = 1 << 2;
	private static final int LOC = 1 << 3;
	private static final int HOME = 1 << 4;
	private static final int WORLD = 1 << 5;
	private static final int IP = 1 << 6;
	private static final int STATUS = 1 << 7;
	private static final int TITLE = 1 << 8;
	private final int defaultMask;
	
	public whoCommand(General instance) {
		super(instance);
		int mask = TITLE | UNAME | DNAME | STATUS;
		if(Option.SHOW_HEALTH.get()) mask |= HEALTH;
		if(Option.SHOW_COORDS.get()) mask |= LOC | HOME;
		if(Option.SHOW_WORLD.get()) mask |= WORLD;
		if(Option.SHOW_IP.get()) mask |= IP;
		defaultMask = mask;
	}
	
	private String getHealthBar(double health) {
//		String healthBar = "[";
//		String colour = health < 3 ? "&c" : (health < 7 ? "&e" : "&2");
//		healthBar += colour;
//		healthBar += Toolbox.repeat('|', (int) health);
//		healthBar += "&7";
//		healthBar += Toolbox.repeat('|', 10 - (int) health);
//		healthBar += "&f] ";
//		return healthBar;
		String full = Toolbox.repeat('|', (int) health);
		String empty = Toolbox.repeat('|', 10 - (int) health);
		return LanguageText.INFO_HEALTHBAR.value("value", health, "health", full, "filler", empty);
	}

	private void showInfo(Player ofWhom, CommandSender toWhom, int mask) {
		LanguageText divider = LanguageText.INFO_DIVIDER;
		boolean title = (mask & TITLE) > 0;
		if(title) {
			Messaging.send(toWhom, divider);
			Messaging.send(toWhom, LanguageText.INFO_TITLE_PLAYER.value("name", ofWhom.getName()));
			Messaging.send(toWhom, divider);
		}
		if((mask & UNAME) > 0)
			Messaging.send(toWhom, LanguageText.INFO_USERNAME.value("name", ofWhom.getName()));
		if((mask & DNAME) > 0)
			Messaging.send(toWhom, LanguageText.INFO_DISPLAYNAME.value("name", ofWhom.getDisplayName()));
		if((mask & HEALTH) > 0) {
			double health = ofWhom.getHealth() / 2.0;
			String bar = getHealthBar(health);
			Messaging.send(toWhom, LanguageText.INFO_HEALTH.value("bar", bar, "value", health));
		}
		if((mask & LOC) > 0) {
			Location loc = ofWhom.getLocation();
			Messaging.send(toWhom, LanguageText.INFO_LOCATION.value("location", Toolbox.formatLocation(loc)));
		}
		if((mask & HOME) > 0) {
			Location home = Destination.homeOf(ofWhom).getLoc();
			Messaging.send(toWhom, LanguageText.INFO_SPAWN.value("location", Toolbox.formatLocation(home)));
		}
		if((mask & WORLD) > 0)
			Messaging.send(toWhom, LanguageText.INFO_WORLD.value("world", ofWhom.getWorld().getName()));
		if((mask & IP) > 0 && canSeeIp(ofWhom, toWhom)) {
			String ip = ofWhom.getAddress().getAddress().getHostAddress();
			Messaging.send(toWhom, LanguageText.INFO_IP.value("ip", ip));
		}
		if((mask & STATUS) > 0) {
			String status;
			if(General.plugin.isAway(ofWhom))
				status = LanguageText.INFO_STATUS_AWAY.value("away", General.plugin.whyAway(ofWhom));
			else status = LanguageText.INFO_STATUS_AROUND.value();
			Messaging.send(toWhom, LanguageText.INFO_STATUS.value("status", status));
		}
		if(title) Messaging.send(toWhom, divider);
	}
	
	private void showInfo(@SuppressWarnings("unused") ConsoleCommandSender ofWhom, CommandSender toWhom, int mask) {
		LanguageText divider = LanguageText.INFO_DIVIDER;
		boolean title = (mask & TITLE) > 0;
		Server server = plugin.getServer();
		if(title) {
			Messaging.send(toWhom, divider);
			Messaging.send(toWhom, LanguageText.INFO_TITLE_CONSOLE);
			Messaging.send(toWhom, divider);
		}
		if((mask & UNAME) > 0)
			Messaging.send(toWhom, LanguageText.INFO_USERNAME.value("name", "CONSOLE"));
		if((mask & DNAME) > 0)
			Messaging.send(toWhom, LanguageText.INFO_DISPLAYNAME.value("name", "Server"));
		int healthloc = HEALTH | LOC | HOME;
		if((mask & healthloc) > 0)
			Messaging.send(toWhom, LanguageText.INFO_VERSION.value("version", server.getVersion()));
		if((mask & WORLD) > 0)
			Messaging.send(toWhom, LanguageText.INFO_PRIMARY.value("world", server.getWorlds().get(0).getName()));
		if((mask & IP) > 0) {
			String ip = server.getIp();
			int port = server.getPort();
			if(ip.isEmpty()) ip = "<???>";
			Messaging.send(toWhom, LanguageText.INFO_IP.value("ip", ip + ":" + port));
		}
		if((mask & STATUS) > 0)
			Messaging.send(toWhom, LanguageText.INFO_STATUS.value("status", LanguageText.INFO_STATUS_SERVER.value()));
		if(title) Messaging.send(toWhom, divider);
	}
	
	private void showInfo(CommandSender ofWhom, CommandSender toWhom, int mask) {
		LanguageText divider = LanguageText.INFO_DIVIDER;
		boolean title = (mask & TITLE) > 0;
		if(title) {
			Messaging.send(toWhom, divider);
			Messaging.send(toWhom, LanguageText.INFO_TITLE_UNKNOWN);
			Messaging.send(toWhom, divider);
		}
		if((mask &~ TITLE) > 0) Messaging.send(toWhom, LanguageText.INFO_NAME.value("name", getName(ofWhom)));
		if(title) Messaging.send(toWhom, divider);
	}
	
	public boolean canSeeIp(Player ofWhom, CommandSender who) {
		boolean canSeeIp = false;
		if(who instanceof Player) {
			Player p = (Player) who;
			if(p.getName().equals(ofWhom.getName()))
				canSeeIp = true;
			else canSeeIp = Toolbox.hasPermission(p, "general.who.ip");
		} else if(who instanceof ConsoleCommandSender) canSeeIp = true;
		return canSeeIp;
	}

	@Override
	public Map<String, Object> parse(CommandSender sender, Command command, String label, String[] args, boolean isPlayer) {
		if(args.length > 1) return null;
		HashMap<String, Object> params = new HashMap<String,Object>();
		if(label.equalsIgnoreCase("whoami") || args.length == 0) {
			int mask = 0;
			if(Toolbox.equalsOne(args[0], "all", "uname", "name", "username")) mask |= UNAME;
			if(Toolbox.equalsOne(args[0], "all", "dname", "name", "displayname", "nick", "nickname")) mask |= DNAME;
			if(Toolbox.equalsOne(args[0], "all", "health", "hp", "version")) mask |= HEALTH;
			if(Toolbox.equalsOne(args[0], "all", "loc", "location", "pos", "position", "coords")) mask |= LOC;
			if(Toolbox.equalsOne(args[0], "all", "home", "spawn", "bed")) mask |= HOME;
			if(Toolbox.equalsOne(args[0], "all", "world", "worldname")) mask |= WORLD;
			if(Toolbox.equalsOne(args[0], "all", "ip", "address")) mask |= IP;
			if(Toolbox.equalsOne(args[0], "all", "status", "away")) mask |= STATUS;
			// If they're not allowed to override, mask out disabled values
			if(!Option.ALLOW_OVERRIDE.get()) mask &= defaultMask;
			params.put("mask", mask);
			params.put("who", sender);
		} else {
			Player who = Toolbox.matchPlayer(args[0]);
			if(who == null) {
				Messaging.invalidPlayer(sender, args[0]);
				return null;
			}
			params.put("mask", defaultMask);
			params.put("who", who);
		}
		return params;
	}

	@Override
	public boolean execute(CommandSender sender, String command, Map<String, Object> args) {
		CommandSender who = (CommandSender) args.get("who");
		if(!who.equals(sender) && Toolbox.lacksPermission(sender, "general.who", "general.basic"))
			return Messaging.lacksPermission(sender, "general.who");
		int mask = (Integer) args.get("mask");
		if(who instanceof Player) showInfo((Player) who, sender, mask);
		else if(who instanceof ConsoleCommandSender)
			showInfo((ConsoleCommandSender) who, sender, mask);
		else showInfo(who, sender, mask);
		return true;
	}
}
