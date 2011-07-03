
package net.craftstars.general.command.info;


import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.General;
import net.craftstars.general.command.CommandBase;
import net.craftstars.general.teleport.Destination;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

public class whoCommand extends CommandBase {
	
	public whoCommand(General instance) {
		super(instance);
	}
	
	@Override
	public boolean fromPlayer(Player toWhom, Command command, String commandLabel, String[] args) {
		Player who;
		if(args.length < 1 || commandLabel.equalsIgnoreCase("whoami"))
			who = toWhom;
		else if(args.length == 1) {
			who = Toolbox.matchPlayer(args[0]);
			if(!toWhom.equals(who) && Toolbox.lacksPermission(toWhom, "general.who", "general.basic"))
				return Messaging.lacksPermission(toWhom, "view info on users other than yourself");
			if(who == null) return true;
		} else return SHOW_USAGE;
		showInfo(who, toWhom);
		return true;
	}
	
	@Override
	public boolean fromConsole(ConsoleCommandSender toWhom, Command command, String commandLabel,
			String[] args) {
		if(args.length != 1) return SHOW_USAGE;
		Player who = Toolbox.matchPlayer(args[0]);
		if(who == null) return true;
		showInfo(who, toWhom);
		return true;
	}
	
	private String getHealthBar(Player ofWhom) {
		String healthBar = "[";
		double health = ((double) ofWhom.getHealth()) / 2.0;
		String colour = health < 3 ? "&c" : (health < 7 ? "&e" : "&2");
		healthBar += colour;
		healthBar += Toolbox.repeat('|', (int) health);
		healthBar += "&7";
		healthBar += Toolbox.repeat('|', 10 - (int) health);
		healthBar += "&f] ";
		healthBar += colour;
		healthBar += health;
		return healthBar;
	}

	private void showInfo(Player ofWhom, CommandSender toWhom) {
		Messaging.send(toWhom, "&f------------------------------------------------");
		Messaging.send(toWhom, "&e Player &f[" + ofWhom.getName() + "]&e Info");
		Messaging.send(toWhom, "&f------------------------------------------------");
		Messaging.send(toWhom, "&6 Username: &f" + ofWhom.getName());
		Messaging.send(toWhom, "&6 DisplayName: &f" + ofWhom.getDisplayName());
		if(General.plugin.config.getBoolean("playerlist.show-health", true))
			Messaging.send(toWhom, "&6 -&e Health: &f" + getHealthBar(ofWhom));
		if(General.plugin.config.getBoolean("playerlist.show-coords", true)) {
			Location loc = ofWhom.getLocation(), home = Destination.homeOf(ofWhom).getLoc();
			Messaging.send(toWhom, "&6 -&e Location: &f" + Toolbox.formatLocation(loc));
			Messaging.send(toWhom, "&6 -&e Spawn: &f" + Toolbox.formatLocation(home));
		}
		if(General.plugin.config.getBoolean("playerlist.show-world", false))
			Messaging.send(toWhom, "&6 -&e World: &f" + ofWhom.getWorld().getName());
		if(General.plugin.config.getBoolean("playerlist.show-ip", false) && canSeeIp(ofWhom, toWhom)) {
			String ip = ofWhom.getAddress().getAddress().getHostAddress();
			Messaging.send(toWhom, "&6 -&e IP: &f" + ip);
		}
		String status = "Around";
		if(General.plugin.isAway(ofWhom))
			status = "Away (" + General.plugin.whyAway(ofWhom) + ")";
		Messaging.send(toWhom, "&6 -&e Status: &f" + status + ".");
		Messaging.send(toWhom, "&f------------------------------------------------");
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
}
