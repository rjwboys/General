
package net.craftstars.general.command.misc;

import java.util.HashMap;
import java.util.Map;

import net.craftstars.general.command.CommandBase;
import net.craftstars.general.General;
import net.craftstars.general.text.LanguageText;
import net.craftstars.general.text.Messaging;
import net.craftstars.general.util.Option;
import net.craftstars.general.util.Time;
import net.craftstars.general.util.Toolbox;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class timeCommand extends CommandBase {
	public timeCommand(General instance) {
		super(instance);
	}
	
	@Override
	public Map<String, Object> parse(CommandSender sender, Command command, String label, String[] args, boolean isPlayer) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		setCommand("timeset"); // assume setting unless otherwise determined
		World world = null;
		switch(args.length) {
		case 0: // /time
			 // No arguments, assuming get current time for current/default world.
			world = getWorld(sender, isPlayer);
			setCommand("showtime");
//			showTime(sender, world);	
		break;
		case 1: // /time <world> OR /time <time>
			// This mega-if is to ensure that "no such world" messages are not displayed on valid input
			// or on input that is obviously not intended to be a world, while still allowing access
			// to worlds if their name happens to match one of the special time keywords.
			if(!"0123456789+-=".contains(args[0].substring(0, 1)))
				world = Toolbox.matchWorld(args[0]);
				if(world == null && !Toolbox.equalsOne(args[0], "day", "night", "nood", "midday", "midnight",
						"dawn", "sunrise", "morning", "dusk", "sunset", "evening"))
					Messaging.invalidWorld(sender, args[0]);
			if(world != null) setCommand("showtime");
			else {
				world = getWorld(sender, isPlayer);
				params.put("time", args[0]);
			}
		break;
		case 2: // /time <world> <time> OR /time add <time> OR /time set <time>
			if(args[0].equalsIgnoreCase("add")) {
				world = getWorld(sender, isPlayer);
				if(args[1].charAt(0) == '-') params.put("time", args[1]);
				else params.put("time", "+" + args[0]);
			} else if(args[0].equalsIgnoreCase("set")) {
				world = getWorld(sender, isPlayer);
				params.put("time", args[1]);;
			} else {
				world = Toolbox.matchWorld(args[0]);
				params.put("time", args[1]);
			}
		break;
		default:
			return null;
		}
		if(world == null) return null;
		params.put("world", world);
		return params;
	}

	private World getWorld(CommandSender sender, boolean isPlayer) {
		if(isPlayer) return ((Player)sender).getWorld();
		else return Bukkit.getWorlds().get(0);
	}

	@Override
	public boolean execute(CommandSender sender, String command, Map<String, Object> args) {
		World world = (World) args.get("world");
		if(command.equals("timeset")) {
			String time = args.get("time").toString();
			setTime(sender, time, world);
		} else if(command.equals("showtime"))
			showTime(sender, world);
		else return false;
		return true;
	}
	
	private boolean setTime(CommandSender sender, String timeStr, World world) {
		if(world.getEnvironment() != Environment.NORMAL) {
			Messaging.send(sender, LanguageText.TIME_NONE);
			return true;
		}
		String permission = "general.time.set";
		if(!sender.hasPermission(permission))
			return Messaging.lacksPermission(sender, permission);
		String cooldownPerm = permission + "." + world.getName();
		if(Toolbox.inCooldown(sender, cooldownPerm))
			return Messaging.inCooldown(sender, cooldownPerm, LanguageText.COOLDOWN_TIME, "world", world.getName());
		Toolbox.cooldown(sender, cooldownPerm, permission + ".instant", Option.COOLDOWN("time").get());
		LanguageText timeName;
		int timeTicks;
		if(timeStr.equalsIgnoreCase("day")) { // 6am
			if(!Toolbox.canPay(sender, 1, "economy.time.day")) return true;
			timeName = LanguageText.TIME_DAY;
			timeTicks = 0;
		} else if(timeStr.equalsIgnoreCase("night")) { // 7:48pm
			if(!Toolbox.canPay(sender, 1, "economy.time.night")) return true;
			timeName = LanguageText.TIME_NIGHT;
			timeTicks = 13800;
		} else if(Toolbox.equalsOne(timeStr, "dusk", "sunset", "evening")) { // 6pm
			if(!Toolbox.canPay(sender, 1, "economy.time.dusk")) return true;
			timeName = LanguageText.TIME_DUSK;
			timeTicks = 12000;
		} else if(Toolbox.equalsOne(timeStr, "dawn", "sunrise", "morning")) { // 4:12am
			if(!Toolbox.canPay(sender, 1, "economy.time.dawn")) return true;
			timeName = LanguageText.TIME_DAWN;
			timeTicks = 22200;
		} else if(Toolbox.equalsOne(timeStr, "midday", "noon")) { // 12am
			if(!Toolbox.canPay(sender, 1, "economy.time.noon")) return true;
			timeName = LanguageText.TIME_NOON;
			timeTicks = 6000;
		} else if(Toolbox.equalsOne(timeStr, "midnight")) { // 12pm
			if(!Toolbox.canPay(sender, 1, "economy.time.midnight")) return true;
			timeName = LanguageText.TIME_WITCH;
			timeTicks = 18000;
		} else if(timeStr.startsWith("+")) {
			if(!Toolbox.canPay(sender, 1, "economy.time.set")) return true;
			try {
				long now = world.getTime();
				long ticks = Time.extractDuration(timeStr.substring(1));
				world.setTime(now + ticks);
				Messaging.send(sender, LanguageText.TIME_ADVANCE.value("time", Time.formatDuration(ticks)));
			} catch(NumberFormatException x) {
				Messaging.send(sender, LanguageText.TIME_BAD_DURATION);
			} catch(Exception ex) {
				ex.printStackTrace();
				return false;
			}
			return true;
		} else if(timeStr.startsWith("-")) {
			if(!Toolbox.canPay(sender, 1, "economy.time.set")) return true;
			try {
				long now = world.getTime();
				long ticks = Time.extractDuration(timeStr.substring(1));
				world.setTime(now - ticks);
				Messaging.send(sender, LanguageText.TIME_REWIND.value("time", Time.formatDuration(ticks)));
			} catch(NumberFormatException x) {
				Messaging.send(sender, LanguageText.TIME_BAD_DURATION);
			} catch(Exception ex) {
				ex.printStackTrace();
				return false;
			}
			return true;
		} else {
			if(!Toolbox.canPay(sender, 1, "economy.time.set")) return true;
			if(timeStr.startsWith("=")) timeStr = timeStr.substring(1);
			try {
				long ticks = Time.extractTime(timeStr);
				if(ticks < 0) ticks += 24000;
				world.setTime(ticks);
				Messaging.send(sender, LanguageText.TIME_SET.value("time", Time.formatTime(ticks)));
			} catch(NumberFormatException x) {
				Messaging.send(sender, LanguageText.TIME_BAD_TIME);
			} catch(Exception ex) {
				return false;
			}
			return true;
		}
		String time = Time.formatTime(timeTicks);
		world.setTime(this.getStartTime(world) + timeTicks);
		Messaging.send(sender, LanguageText.TIME_SET_NAME.value("name", timeName.value(), "time", time));
		return true;
	}
	
	private void showTime(CommandSender sender, World world) {
		if(world.getEnvironment() != Environment.NORMAL) {
			Messaging.send(sender, LanguageText.TIME_NONE);
			return;
		}
		if(!sender.hasPermission("general.time.view"))
			Messaging.lacksPermission(sender, "general.time.view");
		else {
			int time = (int) world.getTime();
			String timeName = this.getFriendlyTime(time);
			String timeFmt = Time.formatTime(time);
			Messaging.send(sender, LanguageText.TIME_CURRENT.value("name", timeName, "time", timeFmt));
		}
	}
	
	private long getTime(World world) {
		return world.getTime();
	}
	
	private long getRelativeTime(World world) {
		return (this.getTime(world) % 24000);
	}
	
	private long getStartTime(World world) {
		return (this.getTime(world) - this.getRelativeTime(world));
	}
	
	public String getFriendlyTime(int time) {
		if(time >= 12000 && time < 13800) {
			return LanguageText.TIME_DUSK.value();
		} else if(time >= 13800 && time < 22200) {
			return LanguageText.TIME_NIGHT.value();
		} else if(time >= 22200 && time < 24000) {
			return LanguageText.TIME_DAWN.value();
		} else {
			return LanguageText.TIME_DAY.value();
		}
	}
}
