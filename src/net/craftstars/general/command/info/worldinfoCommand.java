package net.craftstars.general.command.info;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.General;
import net.craftstars.general.command.CommandBase;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Time;
import net.craftstars.general.util.Toolbox;

public class worldinfoCommand extends CommandBase {
	String name, environ, pvp, location, seed, time;
	
	public worldinfoCommand(General instance) {
		super(instance);
	}
	
	@Override
	public boolean fromPlayer(Player toWhom, Command command, String commandLabel, String[] args) {
		if(Toolbox.lacksPermission(toWhom, "general.worldinfo"))
			return Messaging.lacksPermission(toWhom, "view info on worlds");
		if(args.length == 1) {
			World who = Toolbox.matchWorld(args[0]);
			if(who == null) return true;
			getInfo(who);
		} else return SHOW_USAGE;
		showInfo(toWhom);
		return true;
	}
	
	@Override
	public boolean fromConsole(ConsoleCommandSender toWhom, Command command, String commandLabel, String[] args) {
		if(args.length != 1) return SHOW_USAGE;
		World who = Toolbox.matchWorld(args[0]);
		if(who == null) return true;
		getInfo(who);
		showInfo(toWhom);
		return true;
	}
	
	@Override
	public boolean fromUnknown(CommandSender toWhom, Command command, String commandLabel, String[] args) {
		if(Toolbox.hasPermission(toWhom, "general.worldinfo") || toWhom.isOp()) {
			if(args.length != 1) return SHOW_USAGE;
			World who = Toolbox.matchWorld(args[0]);
			if(who == null) return true;
			getInfo(who);
			showInfo(toWhom);
		}
		return true;
	}
	
	private void getInfo(World ofWhom) {
		this.name = ofWhom.getName();
		this.environ = Toolbox.formatItemName(ofWhom.getEnvironment().toString());
		this.pvp = ofWhom.getPVP() ? "Enabled" : "Disabled";
		this.location = Toolbox.formatLocation(ofWhom.getSpawnLocation());
		this.seed = Long.toString(ofWhom.getSeed());
		this.time = Time.formatTime(ofWhom.getTime(), Time.currentFormat);
	}
	
	private void showInfo(CommandSender toWhom) {
		Messaging.send(toWhom, "&f------------------------------------------------");
		Messaging.send(toWhom, "&e World &f[" + this.name + "]&e Info");
		Messaging.send(toWhom, "&f------------------------------------------------");
		Messaging.send(toWhom, "&6 Name: &f" + this.name);
		Messaging.send(toWhom, "&6 Environment: &f" + this.environ);
		Messaging.send(toWhom, "&6 PVP: &f" + this.pvp);
		Messaging.send(toWhom, "&6 Spawn: &f" + this.location);
		Messaging.send(toWhom, "&6 Seed: &f" + this.seed);
		Messaging.send(toWhom, "&6 Time: &f" + this.time);
		Messaging.send(toWhom, "&f------------------------------------------------");
	}
}
