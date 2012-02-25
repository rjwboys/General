
package net.craftstars.general.command.misc;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.General;
import net.craftstars.general.command.CommandBase;
import net.craftstars.general.option.Options;
import net.craftstars.general.text.LanguageText;
import net.craftstars.general.text.Messaging;
import net.craftstars.general.util.EconomyManager;
import net.craftstars.general.util.Time;
import net.craftstars.general.util.Toolbox;

public class weatherCommand extends CommandBase {
	private static Random lightning = new Random();
	
	public weatherCommand(General instance) {
		super(instance);
	}
	
	@Override
	public Map<String, Object> parse(CommandSender sender, Command command, String label, String[] args, boolean isPlayer) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		switch(args.length) {
		case 0: // /weather -- toggles the weather
			if(!isPlayer) return null;
			setCommand("togglestorm");
			params.put("world", ((Player)sender).getWorld());
		break;
		case 1: // /weather on|off|zap|thunder|<duration>
			if(isPlayer && parseSimpleWeather(sender, args[0], ((Player)sender).getLocation(), params));
			else {
				// /weather <world> -- weather report on a specified world
				World world = Toolbox.matchWorld(args[0]);
				// /weather <player> -- a lightning strike near a player
				if(world == null) {
					Player player = Toolbox.matchPlayer(args[0]);
					if(player != null) {
						setCommand("lightning");
						params.put("location", player.getLocation());
					} else Messaging.invalidWorld(sender, args[0]);
				} else {
					setCommand("weatherreport");
					params.put("world", world);
				}
			}
		break;
		case 2: // /weather thunder on|off|<duration>
			if(isPlayer && isThunder(args[0]) && parseThunder(sender, args[1], ((Player)sender).getWorld(), params));
			else if(isLightning(args[0])) {
				// /weather zap <player>
				Player player = Toolbox.matchPlayer(args[1]);
				World world;
				Location loc;
				if(player == null) {
					// /weather zap <world>
					world = Toolbox.matchWorld(args[1]);
					if(world == null) {
						Messaging.invalidPlayer(sender, args[1]);
						return null;
					}
					loc = world.getSpawnLocation();
				} else {
					world = player.getWorld();
					loc = player.getLocation();
				}
				setCommand("lightning");
				params.put("location", loc);
			} else { // weather <world> on|off|zap|thunder|<duration>
				World world = Toolbox.matchWorld(args[0]);
				if(world == null) {
					Messaging.invalidWorld(sender, args[0]);
					return null;
				}
				if(!parseSimpleWeather(sender, args[1], world.getSpawnLocation(), params)) return null;
			}
		break;
		case 3: // /weather <world> thunder on|off|<duration>
			if(isThunder(args[1])) {
				World world = Toolbox.matchWorld(args[0]);
				if(world == null) {
					Player player = Toolbox.matchPlayer(args[0]);
					if(player == null) {
						Messaging.invalidWorld(sender, args[0]);
						return null;
					}
					world = player.getWorld();
				}
				if(!parseThunder(sender, args[2], world, params)) return null;
				break;
			} // fallthrough intentional
		default:
			return null;
		}
		return params;
	}
	
	private boolean parseSimpleWeather(CommandSender sender, String key, Location where, Map<String, Object> params) {
		World in = where.getWorld();
		params.put("world", in);
		// /weather [<world>] thunder -- toggles the thunder
		if(isThunder(key)) {
			setCommand("togglethunder");
		// /weather [<world>] lightning -- a lightning strike near the sender/spawn
		} else if(isLightning(key)) {
			setCommand("lightning");
			params.remove("world");
			params.put("location", where);
		// /weather [<world>] start -- starts a storm for a random duration
		} else if(isStart(key)) {
			setCommand("storm");
			params.put("duration", -1L);
		// /weather [<world>] stop -- stops a storm
		} else if(isStop(key)) {
			setCommand("storm");
			params.put("duration", 0L);
		// /weather [<world>] <duration> -- starts a storm for a specified duration
		} else try {
			long duration = Time.extractDuration(key);
			if(duration < 0) {
				Messaging.send(sender, LanguageText.WEATHER_NEGATIVE);
				return false;
			}
			setCommand("storm");
			params.put("duration", duration);
		} catch(NumberFormatException e) {
			params.remove("world");
			return false;
		}
		return true;
	}
	
	private boolean parseThunder(CommandSender sender, String key, World in, Map<String, Object> params) {
		long duration;
		// /weather [<world>] thunder start -- starts thunder for a random duration
		if(isStart(key)) duration = -1;
		// /weather [<world>] thunder stop -- stops thunder
		else if(isStop(key)) duration = 0;
		// /weather [<world>] thunder <duration> -- starts thunder for a specified duration
		else try {
			duration = Time.extractDuration(key);
			if(duration < 0) {
				Messaging.send(sender, LanguageText.WEATHER_NEGATIVE);
				return false;
			} else if(duration > Integer.MAX_VALUE) {
				Messaging.send(sender, LanguageText.WEATHER_BAD_THUNDER);
				return false;
			}
		} catch(NumberFormatException e) {
			Messaging.send(sender, LanguageText.TIME_BAD_DURATION);
			return false;
		}
		setCommand("thunder");
		params.put("duration", duration);
		params.put("world", in);
		return true;
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

	@Override
	public boolean execute(CommandSender sender, String command, Map<String, Object> args) {
		if(command.equals("weatherreport")) {
			World world = (World) args.get("world");
			showWeatherInfo(sender, world);
		} else if(command.equals("togglestorm")) {
			World world = (World) args.get("world");
			if(world.hasStorm()) doWeather(sender, world, 0);
			else doWeather(sender, world, -1);
		} else if(command.equals("togglethunder")) {
			World world = (World) args.get("world");
			if(world.isThundering()) doThunder(sender, world, 0);
			else doThunder(sender, world, -1);
		} else if(command.equals("lightning")) {
			Location loc = (Location) args.get("location");
			doLightning(sender, loc);
		} else if(command.equals("storm")) {
			World world = (World) args.get("world");
			long duration = (Long) args.get("duration");
			doWeather(sender, world, duration);
		} else if(command.equals("thunder")) {
			World world = (World) args.get("world");
			int duration = ((Long) args.get("duration")).intValue();
			doThunder(sender, world, duration);
		} else return false;
		return true;
	}
	
	private void showWeatherInfo(CommandSender sender, World where) {
		if(!sender.hasPermission("general.weather.view")) {
			Messaging.lacksPermission(sender, "general.weather.view");
			return;
		}
		if(where.getEnvironment() == Environment.NETHER) {
			Messaging.send(sender, LanguageText.WEATHER_NETHER);
			return;
		}
		// TODO: Probably no weather in The End!
		String storm = Time.formatDuration(where.getWeatherDuration());
		if(where.hasStorm()) Messaging.send(sender,
			LanguageText.WEATHER_ACTIVE.value("world", where.getName(),	"duration", storm));
		else Messaging.send(sender, LanguageText.WEATHER_INACTIVE.value("world", where.getName()));
		if(where.getEnvironment() == Environment.THE_END) return; // no thunder in sky
		String thunder = Time.formatDuration(where.getThunderDuration());
		if(where.isThundering()) Messaging.send(sender,
			LanguageText.THUNDER_ACTIVE.value("world", where.getName(), "duration", thunder));
		else Messaging.send(sender, LanguageText.THUNDER_INACTIVE.value("world", where.getName()));
	}
	
	private void doThunder(CommandSender sender, World world, int duration) {
		if(world.getEnvironment() != Environment.NORMAL) {
			Messaging.send(sender, LanguageText.THUNDER_NETHER);
			return;
		}
		String permission = "general.weather.thunder";
		if(!sender.hasPermission(permission)) {
			Messaging.lacksPermission(sender, permission);
			return;
		}
		String cooldownPerm = permission + "." + world.getName();
		if(Toolbox.inCooldown(sender, cooldownPerm)) {
			Messaging.inCooldown(sender, cooldownPerm, LanguageText.COOLDOWN_THUNDER, "world", world.getName());
			return;
		}
		Toolbox.cooldown(sender, cooldownPerm, permission + ".instant", Options.COOLDOWN("thunder").get());
		if(!EconomyManager.canPay(sender, 1, "economy.weather.thunder")) return;
		boolean state = duration != 0;
		boolean hasThunder = world.isThundering();
		world.setThundering(state);
		if(state && duration != -1) world.setThunderDuration(duration);
		if(duration == 0)
			Messaging.send(sender, LanguageText.THUNDER_STOP);
		else if(duration == -1)
			Messaging.send(sender, LanguageText.THUNDER_START);
		else if(hasThunder)
			Messaging.send(sender, LanguageText.THUNDER_CHANGE.value("time", duration));
		else Messaging.send(sender, LanguageText.THUNDER_SET.value("time", duration));
	}
	
	private void doWeather(CommandSender sender, World world, long duration) {
		if(world.getEnvironment() == Environment.NETHER) {
			Messaging.send(sender, LanguageText.WEATHER_NETHER);
			return;
		}
		String permission = "general.weather.set";
		if(!sender.hasPermission(permission))
			Messaging.lacksPermission(sender, permission);
		String cooldownPerm = permission + "." + world.getName();
		if(Toolbox.inCooldown(sender, cooldownPerm)) {
			Messaging.inCooldown(sender, cooldownPerm, LanguageText.COOLDOWN_WEATHER, "world", world.getName());
			return;
		}
		Toolbox.cooldown(sender, cooldownPerm, permission + ".instant", Options.COOLDOWN("storm").get());
		if(!EconomyManager.canPay(sender, 1, "economy.weather.storm")) return;
		boolean state = duration != 0;
		boolean hasStorm = world.hasStorm();
		world.setStorm(state);
		if(state && duration != -1) world.setWeatherDuration((int) duration);
		if(duration == 0)
			Messaging.send(sender, LanguageText.WEATHER_STOP);
		else if(duration == -1)
			Messaging.send(sender, LanguageText.WEATHER_START);
		else if(hasStorm)
			Messaging.send(sender, LanguageText.WEATHER_CHANGE.value("time", duration));
		else Messaging.send(sender, LanguageText.WEATHER_SET.value("time", duration));
	}
	
	private void doLightning(CommandSender sender, Location centre) {
		World world = centre.getWorld();
		String permission = "general.weather.zap";
		if(!sender.hasPermission(permission))
			Messaging.lacksPermission(sender, permission);
		String cooldownPerm = permission + "." + world.getName();
		if(Toolbox.inCooldown(sender, cooldownPerm)) {
			Messaging.inCooldown(sender, cooldownPerm, LanguageText.COOLDOWN_LIGHTNING, "world", world.getName());
			return;
		}
		Toolbox.cooldown(sender, cooldownPerm, permission + ".instant", Options.COOLDOWN("lightning").get());
		if(!EconomyManager.canPay(sender, 1, "economy.weather.zap")) return;
		else {
			int x, y, z;
			x = centre.getBlockX();
			y = 127;
			z = centre.getBlockZ();
			Block block = world.getBlockAt(x, y, z);
			while(block.getType() == Material.AIR)
				block = block.getRelative(BlockFace.DOWN);
			int range = Options.LIGHTNING_RANGE.get();
			x += lightning.nextInt(range * 2) - range;
			y = block.getLocation().getBlockY();
			z += lightning.nextInt(range * 2) - range;
			world.strikeLightning(new Location(world, x, y, z));
			Messaging.send(sender, LanguageText.WEATHER_LIGHTNING);
		}
	}
}
