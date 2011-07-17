
package net.craftstars.general.command.misc;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.General;
import net.craftstars.general.command.CommandBase;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Time;
import net.craftstars.general.util.Toolbox;

public class weatherCommand extends CommandBase {
	static Random lightning = new Random();
	
	public weatherCommand(General instance) {
		super(instance);
	}
	
	@Override
	public boolean fromConsole(ConsoleCommandSender sender, Command command, String commandLabel,
			String[] args) {
		World world;
		switch(args.length) {
		case 1:
			world = Toolbox.matchWorld(args[0]);
			if(world == null) return Messaging.invalidWorld(sender, args[0]);
			showWeatherInfo(sender, world);
			return true;
		case 2:
			if(isLightning(args[0])) {
				Player player = Toolbox.matchPlayer(args[1]);
				Location loc;
				if(player == null) {
					world = Toolbox.matchWorld(args[1]);
					if(world == null) return Messaging.invalidPlayer(sender, args[1]);
					loc = world.getSpawnLocation();
				} else {
					world = player.getWorld();
					loc = player.getLocation();
				}
				doLightning(sender, world, loc);
			} else {
				world = Toolbox.matchWorld(args[0]);
				if(world == null) return Messaging.invalidWorld(sender, args[0]);
				doWeather(sender, args[1], world, world.getSpawnLocation());
			}
			return true;
		case 3:
			if(isThunder(args[1])) {
				long duration;
				// /weather thunder start -- starts thunder for a random duration
				if(isStart(args[2])) duration = -1;
				// /weather thunder stop -- stops thunder
				else if(isStop(args[2])) duration = 0;
				// /weather thunder <duration> -- starts thunder for a specified duration
				else try {
					duration = Time.extractDuration(args[2]);
					if(duration < 0)
						throw new NumberFormatException("Only positive durations accepted for weather.");
					else if(duration > Integer.MAX_VALUE)
						throw new NumberFormatException("Duration too large for thunder.");
				} catch(NumberFormatException e) {
					Messaging.send(sender, "&cInvalid duration: " + e.getMessage());
					return true;
				}
				world = Toolbox.matchWorld(args[0]);
				if(world == null) {
					Player player = Toolbox.matchPlayer(args[0]);
					if(player == null) return Messaging.invalidWorld(sender, args[0]);
					world = player.getWorld();
				}
				doThunder(sender, world, (int) duration);
				return true;
			} // fallthrough intentional
		default:
			return SHOW_USAGE;
		}
	}
	
	@Override
	public boolean fromPlayer(Player sender, Command command, String commandLabel, String[] args) {
		if(Toolbox.lacksPermission(sender, "general.weather"))
			return Messaging.lacksPermission(sender, "control the weather");
		switch(args.length) {
		case 0: // /weather -- toggles the weather
			if(sender.getWorld().hasStorm())
				doWeather(sender, sender.getWorld(), 0);
			else doWeather(sender, sender.getWorld(), -1);
			return true;
		case 1:
			doWeather(sender, args[0], sender.getWorld(), sender.getLocation());
			return true;
		case 2:
			if(isThunder(args[0])) {
				long duration;
				// /weather thunder start -- starts thunder for a random duration
				if(isStart(args[1])) duration = -1;
				// /weather thunder stop -- stops thunder
				else if(isStop(args[1])) duration = 0;
				// /weather thunder <duration> -- starts thunder for a specified duration
				else try {
					duration = Time.extractDuration(args[1]);
					if(duration < 0)
						throw new NumberFormatException("Only positive durations accepted for weather.");
					else if(duration > Integer.MAX_VALUE)
						throw new NumberFormatException("Duration too large for thunder.");
				} catch(NumberFormatException e) {
					Messaging.send(sender, "&cInvalid duration: " + e.getMessage());
					return true;
				}
				doThunder(sender, sender.getWorld(), (int) duration);
			} else if(isLightning(args[0])) {
				Player player = Toolbox.matchPlayer(args[1]);
				World world;
				Location loc;
				if(player == null) {
					world = Toolbox.matchWorld(args[1]);
					if(world == null) return Messaging.invalidPlayer(sender, args[1]);
					loc = world.getSpawnLocation();
				} else {
					world = player.getWorld();
					loc = player.getLocation();
				}
				doLightning(sender, world, loc);
			} else {
				World world = Toolbox.matchWorld(args[0]);
				if(world == null) return Messaging.invalidWorld(sender, args[0]);
				doWeather(sender, args[1], world, world.getSpawnLocation());
			}
			return true;
		case 3:
			if(isThunder(args[1])) {
				long duration;
				// /weather thunder start -- starts thunder for a random duration
				if(isStart(args[2])) duration = -1;
				// /weather thunder stop -- stops thunder
				else if(isStop(args[2])) duration = 0;
				// /weather thunder <duration> -- starts thunder for a specified duration
				else try {
					duration = Time.extractDuration(args[2]);
					if(duration < 0)
						throw new NumberFormatException("Only positive durations accepted for weather.");
					else if(duration > Integer.MAX_VALUE)
						throw new NumberFormatException("Duration too large for thunder.");
				} catch(NumberFormatException e) {
					Messaging.send(sender, "&cInvalid duration: " + e.getMessage());
					return true;
				}
				World world = Toolbox.matchWorld(args[0]);
				if(world == null) {
					Player player = Toolbox.matchPlayer(args[0]);
					if(player == null) return Messaging.invalidWorld(sender, args[0]);
					world = player.getWorld();
				}
				doThunder(sender, world, (int) duration);
				return true;
			} // fallthrough intentional
		default:
			return SHOW_USAGE;
		}
	}
	
	private void showWeatherInfo(CommandSender sender, World where) {
		if(where.hasStorm())
			sender.sendMessage("&blue;World " + where.getName() + " has a storm active for " +
				Time.extractDuration(Integer.toString(where.getWeatherDuration())) + ".");
		else
			sender.sendMessage("&blue;World " + where.getName() + " does not have a storm active.");
		if(where.isThundering())
			sender.sendMessage("&yellow;World " + where.getName() + " is thundering for " +
				Time.extractDuration(Integer.toString(where.getThunderDuration())) + ".");
		else
			sender.sendMessage("&yellow;World " + where.getName() + " is not thundering.");
	}
	
	private boolean isLightning(String cmd) {
		return Toolbox.equalsOne(cmd, "lightning", "strike", "zap");
	}
	
	private boolean isThunder(String cmd) {
		return Toolbox.equalsOne(cmd, "thunder", "boom");
	}
	
	private boolean isStart(String cmd) {
		return Toolbox.equalsOne(cmd, "on", "start");
	}
	
	private boolean isStop(String cmd) {
		return Toolbox.equalsOne(cmd, "off", "stop");
	}
	
	private void doWeather(CommandSender sender, String key, World in, Location where) {
		// /weather [<world>] thunder -- toggles the thunder
		if(isThunder(key))
			if(in.isThundering())
				doThunder(sender, in, 0);
			else doThunder(sender, in, -1);
		// /weather [<world>] lightning -- a lightning strike near the sender
		else if(isLightning(key))
			doLightning(sender, in, where);
		// /weather [<world>] start -- starts a storm for a random duration
		else if(isStart(key))
			doWeather(sender, in, -1);
		// /weather [<world>] stop -- stops a storm
		else if(isStop(key))
			doWeather(sender, in, 0);
		else {
			// /weather [<world>] <duration> -- starts a storm for a specified duration
			try {
				long duration = Time.extractDuration(key);
				if(duration < 0) throw new NumberFormatException("Only positive durations accepted for weather.");
				doWeather(sender, in, duration);
			} catch(NumberFormatException e) {
				// /weather [<world>] <world> -- weather report on a specified world
				World world = Toolbox.matchWorld(key);
				if(world != null) showWeatherInfo(sender, in);
				// /weather [<world>] <player> -- a lightning strike near a player
				else {
					Player player = Toolbox.matchPlayer(key);
					if(player != null) doLightning(sender, player.getWorld(), player.getLocation());
					else Messaging.invalidWorld(sender, key);
				}
			}
		}
	}
	
	private void doThunder(CommandSender sender, World world, int duration) {
		if(Toolbox.lacksPermission(sender, "general.weather.thunder"))
			Messaging.lacksPermission(sender, "toggle thunder");
		if(!Toolbox.canPay(sender, 1, "economy.weather.thunder")) return;
		boolean state = duration != 0;
		boolean hasThunder = world.isThundering();
		world.setThundering(state);
		if(state && duration != -1) world.setThunderDuration(duration);
		if(duration == 0)
			Messaging.send(sender, "&yellow;Thunder stopped!");
		else if(duration == -1)
			Messaging.send(sender, "&yellow;Thunder started!");
		else if(hasThunder)
			Messaging.send(sender, "&yellow;Thunder will stop in " + duration + " ticks!");
		else Messaging.send(sender, "&yellow;Thunder started for " + duration + " + ticks!");
	}
	
	private void doWeather(CommandSender sender, World world, long duration) {
		if(!Toolbox.canPay(sender, 1, "economy.weather.storm")) return;
		boolean state = duration != 0;
		boolean hasStorm = world.hasStorm();
		world.setStorm(state);
		if(state && duration != -1) world.setWeatherDuration((int) duration);
		if(duration == 0)
			Messaging.send(sender, "&blue;Weather storm stopped!");
		else if(duration == -1)
			Messaging.send(sender, "&blue;Weather storm started!");
		else if(hasStorm)
			Messaging.send(sender, "&blue;Weather storm will stop in " + duration + " ticks!");
		else Messaging.send(sender, "&blue;Weather storm started for " + duration + " ticks!");
	}
	
	private void doLightning(CommandSender sender, World world, Location centre) {
		if(Toolbox.lacksPermission(sender, "general.weather.zap"))
			Messaging.lacksPermission(sender, "summon lightning");
		if(!Toolbox.canPay(sender, 1, "economy.weather.zap")) return;
		else {
			int x, y, z;
			x = centre.getBlockX();
			y = 127;
			z = centre.getBlockZ();
			Block block = world.getBlockAt(x, y, z);
			while(block.getType() == Material.AIR)
				block = block.getRelative(BlockFace.DOWN);
			int range = General.plugin.config.getInt("lightning-range", 20);
			x += lightning.nextInt(range * 2) - range;
			y = block.getLocation().getBlockY();
			z += lightning.nextInt(range * 2) - range;
			world.strikeLightning(new Location(world, x, y, z));
			Messaging.send(sender, "&yellow;Lightning strike!");
		}
	}
}
