
package net.craftstars.general.command.teleport;

import java.util.HashMap;
import java.util.Map;

import net.craftstars.general.General;
import net.craftstars.general.command.CommandBase;
import net.craftstars.general.teleport.Destination;
import net.craftstars.general.teleport.Target;
import net.craftstars.general.text.LanguageText;
import net.craftstars.general.text.Messaging;
import net.craftstars.general.util.EconomyManager;
import net.craftstars.general.util.Toolbox;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class setspawnCommand extends CommandBase {
	public setspawnCommand(General instance) {
		super(instance);
	}

	@Override
	public Map<String, Object> parse(CommandSender sender, Command command, String label, String[] args, boolean isPlayer) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		Destination dest;
		switch(args.length) {
		case 0: // /setspawn
			if(!isPlayer) return null;
			params.put("dest", Destination.locOf((Player) sender));
			params.put("world", ((Player) sender).getWorld());
		break;
		case 1: // /setspawn <destination>
			dest = Destination.get(args[0], isPlayer ? (Player) sender : null);
			if(dest == null) return null;
			setSpawn(sender, dest, isPlayer ? ((Player) sender).getWorld() : null);
		break;
		case 2: // /setspawn <player> <destination>
			if(isPlayer && Toolbox.equalsOne(args[0], "self", "$self", "me", ((Player) sender).getName())) {
				params.put("player", sender);
				params.put("other", false);
			} else {
				params.put("player", Toolbox.matchPlayer(args[0]));
				params.put("other", true);
			}
			Player who = (Player) params.get("player");
			if(who == null) {
				Messaging.invalidPlayer(sender, args[0]);
				return null;
			}
			params.put("dest", Destination.get(args[1], isPlayer ? (Player) sender : null));
			setCommand("sethome");
		break;
		default:
			return null;
		}
		return params;
	}

	@Override
	public boolean execute(CommandSender sender, String command, Map<String, Object> args) {
		if(command.equals("setspawn")) {
			Destination dest = (Destination) args.get("dest");
			World world = (World) args.get("world");
			if(dest.hasPermission(sender, "general.setspawn", Target.fromWorld(world)))
				setSpawn(sender, dest, world);
			return true;
		} else if(command.equals("sethome")) {
			Destination dest = (Destination) args.get("dest");
			Player who = (Player) args.get("player");
			boolean other = (Boolean) args.get("other");
			Target what = Target.fromPlayer(who);
			if(other) what.makeOther();
			if(dest.hasPermission(sender, "general.setspawn", what))
				setHome(sender, dest, who);
			return true;
		}
		return false;
	}
	
	private void setHome(CommandSender sender, Destination dest, Player who) {
		if(sender instanceof Player) {
			Player setter = (Player) sender;
			String targetCost;
			if(setter.equals(who))
				targetCost = "economy.setspawn.self";
			else targetCost = "economy.setspawn.other";
			if(cannotPay(dest, setter, targetCost)) return;
		}
		Location loc = dest.getLoc();
		who.setBedSpawnLocation(loc);
		Messaging.send(sender, LanguageText.SETHOME.value("player", who.getName(), "x", loc.getBlockX(),
			"y", loc.getBlockY(), "z", loc.getBlockZ()));
	}

	private boolean cannotPay(Destination dest, Player setter, String targetCost) {
		String[] costs = dest.getCostClasses(setter, "economy.setspawn");
		costs = Toolbox.arrayCopy(costs, 0, new String[costs.length+1], 1, costs.length);
		costs[0] = targetCost;
		if(!EconomyManager.canPay(setter, 1, costs)) return true;
		return false;
	}
	
	private void setSpawn(CommandSender sender, Destination dest, World from) {
		if(sender instanceof Player) {
			Player setter = (Player) sender;
			if(cannotPay(dest, setter, "economy.setspawn.world")) return;
		}
		Location loc = dest.getLoc();
		World world = loc.getWorld();
		LanguageText feedback;
		if(from == null || !world.equals(from)) {
			feedback = LanguageText.SETSPAWN_WORLD;
		} else {
			feedback = LanguageText.SETSPAWN;
		}
		Player who = dest.getPlayer();
		if(who != null && loc.equals(who.getLocation())) {
			if(who.equals(sender)) {
				feedback = LanguageText.SETSPAWN_HERE;
			} else {
				feedback = LanguageText.SETSPAWN_PLAYER;
			}
		}
		if(!world.setSpawnLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()))
			feedback = LanguageText.SETSPAWN_ERROR;
		String player = who == null ? "null" : who.getDisplayName();
		Messaging.send(sender, feedback.value("world", world.getName(), "player", player,
			"x", loc.getBlockX(), "y", loc.getBlockY(), "z", loc.getBlockZ()));
	}
}
