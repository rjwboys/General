
package net.craftstars.general.command.info;


import static java.lang.Math.rint;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.General;
import net.craftstars.general.command.CommandBase;
import net.craftstars.general.teleport.Destination;
import net.craftstars.general.text.LanguageText;
import net.craftstars.general.text.Messaging;
import net.craftstars.general.util.Option;
import net.craftstars.general.util.Toolbox;

import static net.craftstars.general.command.info.whoCommand.Property.*;

public class whoCommand extends CommandBase {
	enum Property {UNAME, DNAME, HEALTH, LOC, HOME, WORLD, IP, STATUS, TITLE, LEVEL, HUNGER}
	private final Set<Property> defaultMask;
	
	public whoCommand(General instance) {
		super(instance);
		Set<Property> mask = EnumSet.of(TITLE, UNAME, DNAME, STATUS, LEVEL, HUNGER);
		if(Option.SHOW_HEALTH.get()) mask.add(HEALTH);
		if(Option.SHOW_COORDS.get()) mask.addAll(EnumSet.of(LOC, HOME));
		if(Option.SHOW_WORLD.get()) mask.add(WORLD);
		if(Option.SHOW_IP.get()) mask.add(IP);
		defaultMask = mask;
	}
	
	private String getHealthBar(double health) {
		String full = Toolbox.repeat('|', (int) health);
		String empty = Toolbox.repeat('|', 10 - (int) health);
		return LanguageText.INFO_HEALTHBAR.value("value", health, "health", full, "filler", empty);
	}

	private void showInfo(Player ofWhom, CommandSender toWhom, Set<Property> mask) {
		LanguageText divider = LanguageText.INFO_DIVIDER;
		boolean title = mask.contains(TITLE);
		if(title) {
			Messaging.send(toWhom, divider);
			Messaging.send(toWhom, LanguageText.INFO_TITLE_PLAYER.value("name", ofWhom.getName()));
			Messaging.send(toWhom, divider);
		}
		if(mask.contains(UNAME))
			Messaging.send(toWhom, LanguageText.INFO_USERNAME.value("name", ofWhom.getName()));
		if(mask.contains(DNAME))
			Messaging.send(toWhom, LanguageText.INFO_DISPLAYNAME.value("name", ofWhom.getDisplayName()));
		if(mask.contains(HEALTH)) {
			double health = ofWhom.getHealth() / 2.0;
			String bar = getHealthBar(health);
			Messaging.send(toWhom, LanguageText.INFO_HEALTH.value("bar", bar, "value", health));
		}
		if(mask.contains(LEVEL)) {
			float percent = (float)(rint(ofWhom.getExp() * 1000)) / 10.0f;
			Messaging.send(toWhom, LanguageText.INFO_LEVEL.value("lvl", ofWhom.getLevel(), "percent", percent));
			Messaging.send(toWhom, LanguageText.INFO_XP.value("xp", ofWhom.getTotalExperience(), "percent", percent));
		}
		if(mask.contains(HUNGER)) {
			int hunger = ofWhom.getFoodLevel();
			float saturate = ofWhom.getSaturation();
			float exhaust = ofWhom.getExhaustion();
			Messaging.send(toWhom, LanguageText.INFO_FOOD.value("food", hunger, "sat", saturate, "ex", exhaust));
		}
		if(mask.contains(LOC)) {
			Location loc = ofWhom.getLocation();
			Messaging.send(toWhom, LanguageText.INFO_LOCATION.value("location", Toolbox.formatLocation(loc)));
		}
		if(mask.contains("HOME")) {
			Location home = Destination.homeOf(ofWhom).getLoc();
			Messaging.send(toWhom, LanguageText.INFO_SPAWN.value("location", Toolbox.formatLocation(home)));
		}
		if(mask.contains(WORLD))
			Messaging.send(toWhom, LanguageText.INFO_WORLD.value("world", ofWhom.getWorld().getName()));
		if(mask.contains(IP) && canSeeIp(ofWhom, toWhom)) {
			String ip = ofWhom.getAddress().getAddress().getHostAddress();
			Messaging.send(toWhom, LanguageText.INFO_IP.value("ip", ip));
		}
		if(mask.contains(STATUS)) {
			String status;
			if(General.players.isAway(ofWhom))
				status = LanguageText.INFO_STATUS_AWAY.value("away", General.players.whyAway(ofWhom));
			else status = LanguageText.INFO_STATUS_AROUND.value();
			Messaging.send(toWhom, LanguageText.INFO_STATUS.value("status", status));
		}
		if(title) Messaging.send(toWhom, divider);
	}
	
	private void showInfo(ConsoleCommandSender ofWhom, CommandSender toWhom, Set<Property> mask) {
		LanguageText divider = LanguageText.INFO_DIVIDER;
		boolean title = mask.contains(TITLE);
		if(title) {
			Messaging.send(toWhom, divider);
			Messaging.send(toWhom, LanguageText.INFO_TITLE_CONSOLE);
			Messaging.send(toWhom, divider);
		}
		if(mask.contains(UNAME))
			Messaging.send(toWhom, LanguageText.INFO_USERNAME.value("name", ofWhom.getName()));
		if(mask.contains(DNAME))
			Messaging.send(toWhom, LanguageText.INFO_DISPLAYNAME.value("name", Bukkit.getServerName()));
		Set<Property> healthloc = EnumSet.of(HEALTH, LOC, HOME);
		if(mask.containsAll(healthloc))
			Messaging.send(toWhom, LanguageText.INFO_VERSION.value("version", Bukkit.getVersion()));
		if(mask.contains(WORLD))
			Messaging.send(toWhom, LanguageText.INFO_PRIMARY.value("world", Bukkit.getWorlds().get(0).getName()));
		if(mask.contains(IP)) {
			String ip = Bukkit.getIp();
			int port = Bukkit.getPort();
			if(ip.isEmpty()) ip = "<???>";
			Messaging.send(toWhom, LanguageText.INFO_IP.value("ip", ip + ":" + port));
		}
		if(mask.contains(STATUS))
			Messaging.send(toWhom, LanguageText.INFO_STATUS.value("status", LanguageText.INFO_STATUS_SERVER.value()));
		if(title) Messaging.send(toWhom, divider);
	}
	
	private void showInfo(CommandSender ofWhom, CommandSender toWhom, Set<Property> mask) {
		LanguageText divider = LanguageText.INFO_DIVIDER;
		boolean title = mask.contains(TITLE);
		mask.remove(TITLE);
		if(title) {
			Messaging.send(toWhom, divider);
			Messaging.send(toWhom, LanguageText.INFO_TITLE_UNKNOWN);
			Messaging.send(toWhom, divider);
		}
		if(!mask.isEmpty()) Messaging.send(toWhom, LanguageText.INFO_NAME.value("name", ofWhom.getName()));
		if(title) Messaging.send(toWhom, divider);
	}
	
	public boolean canSeeIp(Player ofWhom, CommandSender who) {
		boolean canSeeIp = false;
		if(who instanceof Player) {
			Player p = (Player) who;
			if(p.getName().equals(ofWhom.getName()))
				canSeeIp = true;
			else canSeeIp = p.hasPermission("general.who.ip");
		} else if(who instanceof ConsoleCommandSender) canSeeIp = true;
		return canSeeIp;
	}

	@Override
	public Map<String, Object> parse(CommandSender sender, Command command, String label, String[] args, boolean isPlayer) {
		if(args.length > 1) return null;
		HashMap<String, Object> params = new HashMap<String,Object>();
		if(label.equalsIgnoreCase("whoami") || args.length == 0) {
			Set<Property> mask = EnumSet.noneOf(Property.class);
			if(args.length == 1) {
				if(Toolbox.equalsOne(args[0], "all", "uname", "name", "username")) mask.add(UNAME);
				if(Toolbox.equalsOne(args[0], "all", "dname", "name", "displayname", "nick", "nickname")) mask.add(DNAME);
				if(Toolbox.equalsOne(args[0], "all", "health", "hp", "version")) mask.add(HEALTH);
				if(Toolbox.equalsOne(args[0], "all", "loc", "location", "pos", "position", "coords")) mask.add(LOC);
				if(Toolbox.equalsOne(args[0], "all", "home", "spawn", "bed")) mask.add(HOME);
				if(Toolbox.equalsOne(args[0], "all", "world", "worldname")) mask.add(WORLD);
				if(Toolbox.equalsOne(args[0], "all", "ip", "address")) mask.add(IP);
				if(Toolbox.equalsOne(args[0], "all", "status", "away")) mask.add(STATUS);
				if(Toolbox.equalsOne(args[0], "all", "xp", "exp", "experience", "lvl", "level")) mask.add(LEVEL);
				if(Toolbox.equalsOne(args[0], "all", "hunger", "food")) mask.add(HUNGER);
			} else mask = defaultMask;
			// If they're not allowed to override, mask out disabled values
			if(!Option.ALLOW_OVERRIDE.get()) mask.retainAll(defaultMask);
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
		if(!who.equals(sender) && !sender.hasPermission("general.who"))
			return Messaging.lacksPermission(sender, "general.who");
		@SuppressWarnings("unchecked")
		Set<Property> mask = (Set<Property>) args.get("mask");
		if(who instanceof Player) showInfo((Player) who, sender, mask);
		else if(who instanceof ConsoleCommandSender)
			showInfo((ConsoleCommandSender) who, sender, mask);
		else showInfo(who, sender, mask);
		return true;
	}
}
