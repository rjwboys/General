package net.craftstars.general.command.info;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.General;
import net.craftstars.general.command.CommandBase;
import net.craftstars.general.util.LanguageText;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Time;
import net.craftstars.general.util.Toolbox;

public class worldinfoCommand extends CommandBase {
	public worldinfoCommand(General instance) {
		super(instance);
	}
	
	private void showInfo(CommandSender toWhom, World ofWhom) {
		LanguageText divider = LanguageText.INFO_DIVIDER;
		Messaging.send(toWhom, divider);
		Messaging.send(toWhom, LanguageText.INFO_TITLE_WORLD.value("name",ofWhom.getName()));
		Messaging.send(toWhom, divider);
		Messaging.send(toWhom, LanguageText.INFO_NAME.value("name",ofWhom.getName()));
		String env = Toolbox.formatItemName(ofWhom.getEnvironment().toString());
		Messaging.send(toWhom, LanguageText.INFO_ENVIRONMENT.value("env", env));
		LanguageText pvp = ofWhom.getPVP() ? LanguageText.INFO_PVP_ON : LanguageText.INFO_PVP_OFF;
		Messaging.send(toWhom, LanguageText.INFO_PVP.value("pvp", pvp.value()));
		String spawn = Toolbox.formatLocation(ofWhom.getSpawnLocation());
		Messaging.send(toWhom, LanguageText.INFO_SPAWN.value("location",spawn));
		Messaging.send(toWhom, LanguageText.INFO_SEED.value("seed", ofWhom.getSeed()));
		String time = Time.formatTime(ofWhom.getTime());
		Messaging.send(toWhom, LanguageText.INFO_TIME.value("time", time));
		Messaging.send(toWhom, divider);
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
			return Messaging.lacksPermission(sender, "general.worldinfo");
		World world = (World) args.get("world");
		showInfo(sender, world);
		return true;
	}
}
