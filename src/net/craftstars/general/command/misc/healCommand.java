
package net.craftstars.general.command.misc;

import java.util.HashMap;
import java.util.Map;

import net.craftstars.general.command.CommandBase;
import net.craftstars.general.General;
import net.craftstars.general.text.LanguageText;
import net.craftstars.general.text.Messaging;
import net.craftstars.general.util.Option;
import net.craftstars.general.util.Toolbox;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class healCommand extends CommandBase {
	public healCommand(General instance) {
		super(instance);
	}

	@Override
	public Map<String, Object> parse(CommandSender sender, Command command, String label, String[] args, boolean isPlayer) {
		HashMap<String, Object> params = new HashMap<String,Object>();
		if(label.equalsIgnoreCase("hurt")) setCommand("hurt");
		Player who = null;
		double health = 10.0;
		switch(args.length) {
		case 0:
			if(!isPlayer) return null;
			who = (Player) sender;
		break;
		case 1:
			who = Toolbox.matchPlayer(args[0]);
			if(who == null && isPlayer) try {
				health = Double.valueOf(args[0]);
				who = (Player) sender;
			} catch(NumberFormatException e) {
				Messaging.invalidNumber(sender, args[0]);
				return null;
			}
		break;
		case 2:
			try {
				health = Double.valueOf(args[1]);
				who = Toolbox.matchPlayer(args[0]);
			} catch(NumberFormatException x) {
				Messaging.invalidNumber(sender, args[1]);
				return null;
			}
		break;
		default:
			return null;
		}
		if(who == null) {
			Messaging.invalidPlayer(sender, args[0]);
			return null;
		}
		params.put("player", who);
		params.put("health", health);
		return params;
	}

	@Override
	public boolean execute(CommandSender sender, String command, Map<String, Object> args) {
		Player who = (Player) args.get("player");
		double health = (Double) args.get("health");
		if(command.equals("hurt")) {
			if(!sender.hasPermission("general.hurt"))
				return Messaging.lacksPermission(sender, "general.hurt");
			health = -health;
		} else if(!sender.hasPermission("general.heal"))
			return Messaging.lacksPermission(sender, "general.heal");
		if(!Toolbox.canPay(sender, 1, "economy." + command)) return true;
		health = doHeal(who, health);
		if(!sender.equals(who)) Messaging.send(sender, LanguageText.HEAL_THEM.value("name", who.getName(),
			"health", health, "hurt", LanguageText.HEAL_HURT.value(), "healed", LanguageText.HEAL_HEALED.value()));
		return true;
	}
	
	private double doHeal(Player who, double amount) {
		int hp = who.getHealth();
		amount *= 2;
		hp += amount;
		if(hp > 20)
			hp = 20;
		else if(hp < 0) hp = 0;
		amount = hp - who.getHealth();
		amount /= 2.0;
		who.setHealth(hp);
		if(Option.HEAL_HUNGER.get()) {
			// TODO: What's the range of these?
			who.setFoodLevel(100);
			who.setSaturation(100);
		}
		Messaging.send(who, LanguageText.HEAL_YOU.value("name", who.getName(), "health", amount,
			"hurt", LanguageText.HEAL_HURT.value(), "healed", LanguageText.HEAL_HEALED.value()));
		return amount;
	}
}
