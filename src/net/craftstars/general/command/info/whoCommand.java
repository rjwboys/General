
package net.craftstars.general.command.info;

import java.util.Formatter;

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

public class whoCommand extends CommandBase {private String name;
	private String displayName, bar, location, home, world, ip, status;
	
	public whoCommand(General instance) {
		super(instance);
	}
	
	@Override
	public boolean fromPlayer(Player toWhom, Command command, String commandLabel, String[] args) {
		if(args.length < 1 || commandLabel.equalsIgnoreCase("whoami"))
			getInfo(toWhom);
		else if(args.length == 1) {
			Player who = Toolbox.matchPlayer(args[0]);
			if(!toWhom.equals(who) && Toolbox.lacksPermission(toWhom, "general.who", "general.basic"))
				return Messaging.lacksPermission(toWhom, "view info on users other than yourself");
			if(who == null) return true;
			getInfo(who);
		} else return SHOW_USAGE;
		showInfo(toWhom);
		return true;
	}
	
	@Override
	public boolean fromConsole(ConsoleCommandSender toWhom, Command command, String commandLabel,
			String[] args) {
		if(args.length != 1) return SHOW_USAGE;
		Player who = Toolbox.matchPlayer(args[0]);
		if(who == null) return true;
		getInfo(who);
		showInfo(toWhom);
		return true;
	}
	
	private void getInfo(Player ofWhom) {
		this.name = ofWhom.getName();
		this.displayName = ofWhom.getDisplayName();
		this.bar = getHealthBar(ofWhom);
		this.location = formatLocation(ofWhom.getLocation());
		this.home = formatLocation(Destination.homeOf(ofWhom).getLoc());
		this.world = ofWhom.getWorld().getName();
		this.ip = ofWhom.getAddress().getAddress().getHostAddress();
		if(General.plugin.isAway(ofWhom))
			this.status = "Away (" + General.plugin.whyAway(ofWhom) + ")";
		else this.status = "Around";
	}
	
	private String formatLocation(Location loc) {
		Formatter fmt = new Formatter();
		fmt.format("(%f, %f, %f) facing (%f, %f)", loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
		return fmt.toString();
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

	private void showInfo(CommandSender toWhom) {
		Messaging.send(toWhom, "&f------------------------------------------------");
		Messaging.send(toWhom, "&e Player &f[" + this.name + "]&e Info");
		Messaging.send(toWhom, "&f------------------------------------------------");
		Messaging.send(toWhom, "&6 Username: &f" + this.name);
		Messaging.send(toWhom, "&6 DisplayName: &f" + this.displayName);
		if(General.plugin.config.getBoolean("playerlist.show-health", true))
			Messaging.send(toWhom, "&6 -&e Health: &f" + this.bar);
		if(General.plugin.config.getBoolean("playerlist.show-coords", true)) {
			Messaging.send(toWhom, "&6 -&e Location: &f" + this.location);
			Messaging.send(toWhom, "&6 -&e Spawn: &f" + this.home);
		}
		if(General.plugin.config.getBoolean("playerlist.show-world", false))
			Messaging.send(toWhom, "&6 -&e World: &f" + this.world);
		if(General.plugin.config.getBoolean("playerlist.show-ip", false)) {
			if(canSeeIp(toWhom)) Messaging.send(toWhom, "&6 -&e IP: &f" + this.ip);
		}
		Messaging.send(toWhom, "&6 -&e Status: &f" + this.status + ".");
		Messaging.send(toWhom, "&f------------------------------------------------");
	}
	
	public boolean canSeeIp(CommandSender who) {
		boolean canSeeIp = false;
		if(who instanceof Player) {
			Player p = (Player) who;
			if(p.getName().equals(name))
				canSeeIp = true;
			else canSeeIp = General.plugin.permissions.hasPermission(p, "general.who.ip");
		} else if(who instanceof ConsoleCommandSender) canSeeIp = true;
		return canSeeIp;
	}
}
