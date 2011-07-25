package net.craftstars.general.command.info;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.General;
import net.craftstars.general.command.CommandBase;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Time;
import net.craftstars.general.util.Toolbox;

public class worldinfoCommand extends CommandBase {
	public worldinfoCommand(General instance) {
		super(instance);
	}
	
	private void showInfo(CommandSender toWhom, World ofWhom) {
		// TODO: Use formatting framework for this
		Messaging.send(toWhom, "&f------------------------------------------------");
		Messaging.send(toWhom, "&e World &f[" + ofWhom.getName() + "]&e Info");
		Messaging.send(toWhom, "&f------------------------------------------------");
		Messaging.send(toWhom, "&6 Name: &f" + ofWhom.getName());
		Messaging.send(toWhom, "&6 Environment: &f" + Toolbox.formatItemName(ofWhom.getEnvironment().toString()));
		Messaging.send(toWhom, "&6 PVP: &f" + (ofWhom.getPVP() ? "Enabled" : "Disabled"));
		Messaging.send(toWhom, "&6 Spawn: &f" + Toolbox.formatLocation(ofWhom.getSpawnLocation()));
		Messaging.send(toWhom, "&6 Seed: &f" + ofWhom.getSeed());
		Messaging.send(toWhom, "&6 Time: &f" + Time.formatTime(ofWhom.getTime(), Time.currentFormat));
		Messaging.send(toWhom, "&f------------------------------------------------");
	}

	@Override
	public Map<String, Object> parse(CommandSender sender, Command command, String label, String[] args, boolean isPlayer) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		if(args.length == 0) {
			if(isPlayer) params.put("world", ((Player)sender).getWorld());
			else params.put("world", plugin.getServer().getWorlds().get(0));
		} else if(args.length == 1) {
			World who = Toolbox.matchWorld(args[0]);
			if(who == null) {
				Messaging.invalidWorld(sender, args[0]);
				return null;
			}
			params.put("world", who);
		} else return null;
		return params;
	}

	@Override
	public boolean execute(CommandSender sender, String command, Map<String, Object> args) {
		if(Toolbox.lacksPermission(sender, "general.worldinfo"))
			return Messaging.lacksPermission(sender, "view info on worlds");
		World world = (World) args.get("world");
		showInfo(sender, world);
		return true;
	}
}
