
package net.craftstars.general.command.info;

import static java.lang.Math.acos;
import static java.lang.Math.toDegrees;
import static java.lang.Math.round;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.command.CommandBase;
import net.craftstars.general.General;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

public class getposCommand extends CommandBase {
	public getposCommand(General instance) {
		super(instance);
	}

	@Override
	public Map<String, Object> parse(CommandSender sender, Command command, String label, String[] args, boolean isPlayer) {
		HashMap<String,Object> params = new HashMap<String,Object>();
		if(isPlayer && args.length == 0) params.put("player", sender);
		else if(args.length <= 2) {
			Player who = Toolbox.matchPlayer(args[0]);
			if(who != null) {
				params.put("player", who);
			} else {
				Messaging.invalidPlayer(sender, args[0]);
				return null;
			}
			if(args.length == 2) {
				if(!Toolbox.equalsOne(args[1], "dir", "direction", "brief", "pos", "where", "compass", "full",
					"short", "long", "facing", "rotation", "pointing", "rotation", "rot")) {
					Messaging.send(sender, Messaging.get("getpos.invalid", "{rose}Invalid getpost option."));
					return null;
				}
				setCommand(args[1]);
			} else params.put("option", "full");
		} else return null;
		return params;
	}

	@Override
	public boolean execute(CommandSender sender, String command, Map<String, Object> args) {
		if(Toolbox.lacksPermission(sender, "general.getpos", "general.basic"))
			return Messaging.lacksPermission(sender, "check your location");
		Player whose = (Player) args.get("player");
		if(!whose.equals(sender) && Toolbox.lacksPermission(sender, "general.getpos.other"))
			return Messaging.lacksPermission(sender, "check someone else's location");
		double degrees = getRotation(whose);
		double compass = getCompass(whose);
		if(Toolbox.equalsOne(command, "dir", "direction")) {
			String format = dirMsg();
			Messaging.send(sender, Messaging.format(format, "player", whose.getName(),
				"direction", this.getDirection(degrees)));
		} else if(Toolbox.equalsOne(command, "brief", "pos", "where", "short")) {
			String format = posMsg();
			if(General.plugin.config.getBoolean("playerlist.show-world", false))
				format += worldMsg();
			Messaging.send(sender, Messaging.format(format,
				"player", whose.getName(), "x", whose.getLocation().getX(), "y", whose.getLocation().getY(),
				"z", whose.getLocation().getZ(), "world", whose.getWorld().getName()));
		} else if(Toolbox.equalsOne(command, "rotation", "rot", "facing")) {
			String format = rotMsg();
			Messaging.send(sender, Messaging.format(format, "player", whose.getName(), 
				"yaw", whose.getLocation().getYaw(), "pitch", whose.getLocation().getPitch()));
		} else if(Toolbox.equalsOne(command, "compass", "pointing")) {
			String format = compassMsg();
			Messaging.send(sender, Messaging.format(format, "player", whose.getName(),
				"direction", this.getDirection(compass)));
		} else if(Toolbox.equalsOne(command, "long", "full")) {
			String format = posMsg();
			if(General.plugin.config.getBoolean("playerlist.show-world", false))
				format += worldMsg();
			format += "\n" + rotMsg() + "\n" + dirMsg() + angleMsg();
			Messaging.send(sender, Messaging.format(format, 
				"player", whose.getName(), "x", whose.getLocation().getX(), "y", whose.getLocation().getY(),
				"z", whose.getLocation().getZ(), "world", whose.getWorld().getName(),
				"yaw", whose.getLocation().getYaw(), "pitch", whose.getLocation().getPitch(),
				"direction", this.getDirection(degrees), "angle", round(degrees * 10) / 10.0));
		} else return SHOW_USAGE;
		return false;
	}

	private String angleMsg() {
		return Messaging.get("getpos.angle", "{yellow} ({white}{angle}{yellow})");
	}

	private String compassMsg() {
		return Messaging.get("getpos.compass", "{yellow}Compass: {white}{direction}");
	}

	private String rotMsg() {
		return Messaging.get("getpos.rotation", "{yellow}Rotation: {white}{yaw}{yellow} Pitch: {white}{pitch}");
	}

	private String worldMsg() {
		return Messaging.get("getpos.world", "{yellow} in '{white}{world}{yellow}'");
	}

	private String posMsg() {
		return Messaging.get("getpos.pos", "{yellow}Pos X: {white}{x}{yellow} Y: {white}" +
			"{y}{yellow} Z: {white}{z}{yellow}");
	}

	private String dirMsg() {
		return Messaging.get("getpos.dir", "{yellow}Direction: {white}{direction}");
	}
	
	private double getRotation(Player whose) {
		double degreeRotation = ((whose.getLocation().getYaw() - 90) % 360);
		if(degreeRotation < 0) degreeRotation += 360.0;
		return degreeRotation;
	}
	
	private double getCompass(Player whose) {
		Location player = whose.getLocation(), compass = whose.getCompassTarget();
		Location northOfPlayer = player.add(-5, 0, 0);
		// c^2 = a^2 + b^2 + 2ab*cosC
		// cosC = (c^2 - a^2 - b^2) / 2ab
		double a = player.distance(northOfPlayer);
		double b = player.distance(compass);
		double c = northOfPlayer.distance(compass);
		double angle = acos((c*c - a*a - b*b) / (2*a*b));
		angle = toDegrees(angle);
		if(angle < 0) angle += 360.0;
		return angle;
	}
	
	private String getDirection(double degrees) {
		if(0 <= degrees && degrees < 22.5)
			return "N";
		else if(22.5 <= degrees && degrees < 67.5)
			return "NE";
		else if(67.5 <= degrees && degrees < 112.5)
			return "E";
		else if(112.5 <= degrees && degrees < 157.5)
			return "SE";
		else if(157.5 <= degrees && degrees < 202.5)
			return "S";
		else if(202.5 <= degrees && degrees < 247.5)
			return "SW";
		else if(247.5 <= degrees && degrees < 292.5)
			return "W";
		else if(292.5 <= degrees && degrees < 337.5)
			return "NW";
		else if(337.5 <= degrees && degrees < 360.0)
			return "N";
		else return "ERR";
	}
}
