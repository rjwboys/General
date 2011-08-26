
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
import net.craftstars.general.util.LanguageText;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Option;
import net.craftstars.general.util.Toolbox;

public class getposCommand extends CommandBase {
	public getposCommand(General instance) {
		super(instance);
	}

	@Override
	public Map<String, Object> parse(CommandSender sender, Command command, String label, String[] args, boolean isPlayer) {
		HashMap<String,Object> params = new HashMap<String,Object>();
		String cmd = null;
		switch(args.length) {
		case 0:
			if(isPlayer) {
				params.put("player", sender);
				cmd = "full";
			} else return null;
			break;
		case 1:
			Player who = Toolbox.matchPlayer(args[0]);
			if(who != null) {
				params.put("player", who);
				cmd = "full";
			} else if(isPlayer) {
				params.put("player", sender);
				cmd = checkCommand(sender, args[0]);
			} else {
				Messaging.invalidPlayer(sender, args[0]);
				return null;
			}
			break;
		case 2:
			Player player = Toolbox.matchPlayer(args[0]);
			if(player != null) params.put("player", player);
			else {
				Messaging.invalidPlayer(sender, args[0]);
				return null;
			}
			cmd = checkCommand(sender, args[1]);
			break;
		default:
			return null;
		}
		if(cmd == null) return null;
		setCommand(cmd);
		return params;
	}
	
	private String checkCommand(CommandSender sender, String cmd) {
		if(!Toolbox.equalsOne(cmd, "dir", "direction", "brief", "pos", "where", "compass", "full",
			"short", "long", "facing", "rotation", "pointing", "rotation", "rot")) {
			Messaging.send(sender, LanguageText.GETPOS_INVALID.value("option", cmd));
			return null;
		}
		return cmd;
	}

	@Override
	public boolean execute(CommandSender sender, String command, Map<String, Object> args) {
		if(Toolbox.lacksPermission(sender, "general.getpos", "general.basic"))
			return Messaging.lacksPermission(sender, "general.getpos");
		Player whose = (Player) args.get("player");
		if(!whose.equals(sender) && Toolbox.lacksPermission(sender, "general.getpos.other"))
			return Messaging.lacksPermission(sender, "general.getpos.other");
		double degrees = getRotation(whose);
		double compass = getCompass(whose);
		String player = whose.getName();
		General.logger.debug(command);
		if(Toolbox.equalsOne(command, "dir", "direction")) {
			Messaging.send(sender, LanguageText.GETPOS_DIR.value("player", player,
				"direction", this.getDirection(degrees)));
		} else if(Toolbox.equalsOne(command, "brief", "pos", "where", "short")) {
			String msg = LanguageText.GETPOS_POS.value("player", player, "x", whose.getLocation().getX(),
				"y", whose.getLocation().getY(), "z", whose.getLocation().getZ());
			if(Option.SHOW_WORLD.get())
				msg += LanguageText.GETPOS_WORLD.value("world", whose.getWorld().getName());
			Messaging.send(sender, msg);
		} else if(Toolbox.equalsOne(command, "rotation", "rot", "facing")) {
			Messaging.send(sender, LanguageText.GETPOS_ROTATION.value("player", player, 
				"yaw", whose.getLocation().getYaw(), "pitch", whose.getLocation().getPitch()));
		} else if(Toolbox.equalsOne(command, "compass", "pointing")) {
			Messaging.send(sender, LanguageText.GETPOS_COMPASS.value("player", player,
				"direction", this.getDirection(compass)));
		} else if(Toolbox.equalsOne(command, "long", "full")) {
			String msg = LanguageText.GETPOS_POS.value("player", player, "x", whose.getLocation().getX(),
				"y", whose.getLocation().getY(), "z", whose.getLocation().getZ());
			if(Option.SHOW_WORLD.get())
				msg += LanguageText.GETPOS_WORLD.value("world", whose.getWorld().getName());
			msg += "\n" + LanguageText.GETPOS_ROTATION.value("player", player, 
				"yaw", whose.getLocation().getYaw(), "pitch", whose.getLocation().getPitch());
			msg += "\n" + LanguageText.GETPOS_DIR.value("player", player,
				"direction", this.getDirection(degrees));
			msg += LanguageText.GETPOS_ANGLE.value("angle", round(degrees * 10) / 10.0);
			Messaging.send(sender, msg);
		} else return false;
		return false;
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
