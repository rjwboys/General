
package net.craftstars.general.command.misc;

import net.craftstars.general.command.CommandBase;
import net.craftstars.general.General;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Time;
import net.craftstars.general.util.Toolbox;

import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class timeCommand extends CommandBase {
	private World world;
	
	public timeCommand(General instance) {
		super(instance);
	}
	
	@Override
	public boolean fromPlayer(Player sender, Command command, String commandLabel, String[] args) {
		switch(args.length) {
		case 0: // /time
			// No arguments, assuming get current time for current world.
			this.world = sender.getWorld();
			showTime(sender);
			return true;
		case 1: // /time <world> OR /time <time> OR /time help
			this.world = null;
			// This mega-if is to ensure that "no such world" messages are not displayed on valid input
			// or on input that is obviously not intended to be a world, while still allowing access
			// to worlds if their name happens to match one of the special time keywords.
			if(!"0123456789+-=".contains(args[0].substring(0, 1)))
				this.world = Toolbox.matchWorld(args[0]);
				if(world == null && !Toolbox.equalsOne(args[0], "day", "night", "nood", "midday", "midnight",
						"dawn", "sunrise", "morning", "dusk", "sunset", "evening"))
					Messaging.invalidWorld(sender, args[0]);
			if(world != null)
				showTime(sender);
			else {
				this.world = sender.getWorld();
				return setTime(sender, args[0]);
			}
			return true;
		case 2: // /time <world> <time>
			if(args[0].equalsIgnoreCase("add")) {
				this.world = sender.getWorld();
				if(args[1].charAt(0) == '-')
					return setTime(sender, args[1]);
				else return setTime(sender, '+' + args[1]);
			} else if(args[0].equalsIgnoreCase("set")) {
				this.world = sender.getWorld();
				return setTime(sender, args[1]);
			}
			this.world = General.plugin.getServer().getWorld(args[0]);
			if(world == null) return true;
			return setTime(sender, args[1]);
		default:
			return SHOW_USAGE;
		}
	}
	
	private boolean setTime(CommandSender sender, String time) {
		if(world.getEnvironment() != Environment.NORMAL) {
			Messaging.send(sender, "&cTime has no meaning here.");
			return true;
		}
		if(Toolbox.lacksPermission(sender, "general.time.set"))
			return Messaging.lacksPermission(sender, "set the time");
		if(Toolbox.checkCooldown(sender, world, "time", "general.time")) return true;
		if(time.equalsIgnoreCase("day")) { // 6am
			if(!Toolbox.canPay(sender, 1, "economy.time.day")) return true;
			this.world.setTime(this.getStartTime());
			Messaging.send(sender, "Time set to day: " + Time.formatTime(0, Time.currentFormat) + "!");
			return true;
		} else if(time.equalsIgnoreCase("night")) { // 7:48pm
			if(!Toolbox.canPay(sender, 1, "economy.time.night")) return true;
			this.world.setTime(this.getStartTime() + 13800);
			Messaging.send(sender, "Time set to night: " + Time.formatTime(13800, Time.currentFormat) + "!");
			return true;
		} else if(Toolbox.equalsOne(time, "dusk", "sunset", "evening")) { // 6pm
			if(!Toolbox.canPay(sender, 1, "economy.time.dusk")) return true;
			this.world.setTime(this.getStartTime() + 12000);
			Messaging.send(sender, "Time set to dusk: " + Time.formatTime(12000, Time.currentFormat) + "!");
			return true;
		} else if(Toolbox.equalsOne(time, "dawn", "sunrise", "morning")) { // 4:12am
			if(!Toolbox.canPay(sender, 1, "economy.time.dawn")) return true;
			this.world.setTime(this.getStartTime() + 22200);
			Messaging.send(sender, "Time set to dawn: " + Time.formatTime(22200, Time.currentFormat) + "!");
			return true;
		} else if(Toolbox.equalsOne(time, "midday", "noon")) { // 12am
			if(!Toolbox.canPay(sender, 1, "economy.time.noon")) return true;
			this.world.setTime(this.getStartTime() + 6000);
			Messaging.send(sender, "Time set to noon: " + Time.formatTime(6000, Time.currentFormat) + "!");
			return true;
		} else if(Toolbox.equalsOne(time, "midnight")) { // 12pm
			if(!Toolbox.canPay(sender, 1, "economy.time.midnight")) return true;
			this.world.setTime(this.getStartTime() + 18000);
			Messaging.send(sender, "Time set to midnight: " + Time.formatTime(18000, Time.currentFormat) + "!");
			return true;
		} else if(time.startsWith("+")) {
			if(!Toolbox.canPay(sender, 1, "economy.time.set")) return true;
			try {
				long now = this.world.getTime();
				long ticks = Time.extractDuration(time.substring(1));
				this.world.setTime(now + ticks);
				Messaging.send(sender, "Time advanced by " + Time.formatDuration(ticks) + "!");
			} catch(NumberFormatException x) {
				Messaging.send(sender, "&rose;Invalid duration format.");
			} catch(Exception ex) {
				ex.printStackTrace();
				return SHOW_USAGE;
			}
		} else if(time.startsWith("-")) {
			if(!Toolbox.canPay(sender, 1, "economy.time.set")) return true;
			try {
				long now = this.world.getTime();
				long ticks = Time.extractDuration(time.substring(1));
				this.world.setTime(now - ticks);
				Messaging.send(sender, "Time set back by " + Time.formatDuration(ticks) + "!");
			} catch(NumberFormatException x) {
				Messaging.send(sender, "&rose;Invalid duration format.");
			} catch(Exception ex) {
				ex.printStackTrace();
				return SHOW_USAGE;
			}
		} else {
			if(!Toolbox.canPay(sender, 1, "economy.time.set")) return true;
			if(time.startsWith("=")) time = time.substring(1);
			try {
				long ticks = Time.extractTime(time);
				if(ticks < 0) ticks += 24000;
				this.world.setTime(ticks);
				Messaging.send(sender, "Time set to " + Time.formatTime(ticks, Time.currentFormat) + "!");
			} catch(NumberFormatException x) {
				Messaging.send(sender, "&rose;Invalid time format.");
			} catch(Exception ex) {
				return SHOW_USAGE;
			}
		}
		return SHOW_USAGE;
	}
	
	private void showTime(CommandSender sender) {
		if(world.getEnvironment() != Environment.NORMAL) {
			Messaging.send(sender, "&cTime has no meaning here.");
			return;
		}
		if(Toolbox.lacksPermission(sender, "general.time", "general.basic"))
			Messaging.lacksPermission(sender, "see the time");
		else {
			int time = (int) this.world.getTime();
			Messaging.send(sender, "Current Time: " + this.getFriendlyTime(time) + " " +
					Time.formatTime(time, Time.currentFormat));
		}
	}
	
	private long getTime() {
		return world.getTime();
	}
	
	private long getRelativeTime() {
		return (this.getTime() % 24000);
	}
	
	private long getStartTime() {
		return (this.getTime() - this.getRelativeTime());
	}
	
	public String getFriendlyTime(int time) {
		if(time >= 12000 && time < 13800) {
			return "Dusk";
		} else if(time >= 13800 && time < 22200) {
			return "Night";
		} else if(time >= 22200 && time < 24000) {
			return "Dawn";
		} else {
			return "Day";
		}
	}
	
	@Override
	public boolean fromConsole(ConsoleCommandSender sender, Command command, String commandLabel, String[] args) {
		if(args.length < 1 || args.length > 2)
			return SHOW_USAGE;
		else {
			if(args[0].equalsIgnoreCase("add")) {
				this.world = General.plugin.getServer().getWorlds().get(0);
				if(args[1].charAt(0) == '-')
					return setTime(sender, args[1]);
				else return setTime(sender, '+' + args[1]);
			} else if(args[0].equalsIgnoreCase("set")) {
				this.world = General.plugin.getServer().getWorlds().get(0);
				return setTime(sender, args[1]);
			}
			this.world = Toolbox.matchWorld(args[0]);
			if(this.world == null) return Messaging.invalidWorld(sender, args[0]);
			if(args.length == 1) {
				showTime(sender);
				return true;
			}
			String time = args[1];
			return setTime(sender, time);
		}
	}
	
	@Override
	public boolean fromUnknown(CommandSender sender, Command command, String commandLabel, String[] args) {
		if(Toolbox.hasPermission(sender, "general.time") || sender.isOp()) {
			if(args.length < 1 || args.length > 2)
				return SHOW_USAGE;
			else {
				if(args[0].equalsIgnoreCase("add")) {
					this.world = General.plugin.getServer().getWorlds().get(0);
					if(args[1].charAt(0) == '-')
						return setTime(sender, args[1]);
					else return setTime(sender, '+' + args[1]);
				} else if(args[0].equalsIgnoreCase("set")) {
					this.world = General.plugin.getServer().getWorlds().get(0);
					return setTime(sender, args[1]);
				}
				this.world = Toolbox.matchWorld(args[0]);
				if(this.world == null) return Messaging.invalidWorld(sender, args[0]);
				if(args.length == 1) {
					showTime(sender);
					return true;
				}
				String time = args[1];
				return setTime(sender, time);
			}
		}
		return true;
	}
}
