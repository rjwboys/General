
package net.craftstars.general.command.teleport;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.command.CommandBase;
import net.craftstars.general.General;
import net.craftstars.general.option.Options;
import net.craftstars.general.teleport.*;
import net.craftstars.general.text.LanguageText;
import net.craftstars.general.text.Messaging;
import net.craftstars.general.util.EconomyManager;
import net.craftstars.general.util.Toolbox;

public class goCommand extends CommandBase {
	private static HashSet<Player> inWarmup = new HashSet<Player>();
	public goCommand(General instance) {
		super(instance);
	}

	@Override
	public Map<String, Object> parse(CommandSender sender, Command command, String label, String[] args, boolean isPlayer) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		Player player = (Player) (sender instanceof Player ? sender : null);
		Target target;
		Destination dest;
		if(args.length == 2) {
			target = Target.get(args[0], player);
			dest = Destination.get(args[1], player);
		} else if(args.length == 1 && isPlayer) {
			target = Target.fromPlayer(player);
			dest = Destination.get(args[0], player);
		} else return null;
		if(dest == null || target == null) return null;
		params.put("target", target);
		params.put("dest", dest);
		return params;
	}

	@Override
	public boolean execute(CommandSender sender, String command, Map<String, Object> args) {
		int warmup = Options.TELEPORT_WARMUP.get();
		if(warmup > 0) {
			if(Toolbox.inCooldown(sender, "general.teleport"))
				return Messaging.inCooldown(sender, "general.teleport", LanguageText.COOLDOWN_TELEPORT);
			Toolbox.cooldown(sender, "general.teleport", "general.teleport.instant", warmup);
		}
		final Target target = (Target) args.get("target");
		final Destination dest = (Destination) args.get("dest");
		if(dest.hasPermission(sender, "general.teleport", target)) {
			if(sender instanceof Player) {
				final Player player = (Player) sender;
				String[] costs = dest.getCostClasses(player, "general.teleport");
				costs = Toolbox.arrayCopy(costs, 0, new String[costs.length+1], 1, costs.length);
				costs[0] = target.getCostClass();
				if(!EconomyManager.canPay(sender, target.count(), costs)) return true;
				Runnable teleport = new Runnable() {
					@Override
					public void run() {
						target.teleport(dest);
						LanguageText format;
						if(target.getType() == TargetType.SELF)
							format = LanguageText.TELEPORT_SELF;
						else format = LanguageText.TELEPORT_OTHER;
						Messaging.send(player, format.value("target", target.getName(), "destination", dest.getName()));
						inWarmup.remove(player);
					}
				};
				if(warmup == 0 || dest.hasInstant(player, "general.teleport", target))
					teleport.run();
				else {
					Messaging.send(sender, LanguageText.TELEPORT_WARMUP.value("time", warmup));
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, teleport, warmup);
					inWarmup.add(player);
				}
			} else {
				target.teleport(dest);
				Messaging.send(sender, LanguageText.TELEPORT_OTHER.value("target", target.getName(),
					"destination", dest.getName()));
			}
		}
		return true;
	}
}
