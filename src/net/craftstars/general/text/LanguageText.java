package net.craftstars.general.text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConstructor;
import org.bukkit.configuration.file.YamlRepresenter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.DumperOptions.ScalarStyle;
import org.yaml.snakeyaml.Yaml;

public enum LanguageText {
	// Server log messages
	// Messages related to /away
	AWAY_BACK("away.back", "{silver}You have been marked as back."),
	AWAY_HERE("away.here", "{silver}You are not away."),
	AWAY_CHANGE("away.change", "{silver}Away reason changed."),
	AWAY_SET("away.set", "{silver}You are now marked as away."),
	AWAY_CONSOLE("away.console", "{rose}It's not possible for the console to be marked as away."),
	AWAY_UNKNOWN("away.unknown", "{rose}I don't know what you are, so I can't mark you as away."),
	AWAY_IS("away.is", "{silver}{name} is currently away."),
	AWAY_REASON("away.reason", "{silver}Reason: {reason}"),
	AWAY_BRIEF("away.brief", "{rose}{name} is away: {reason}"),
	// Messages related to /tell
	WHISPER_TO("whisper.to", "{gray}(whisper)   to <{name}> {message}"),
	WHISPER_FROM_PLAYER("whisper.from-player", "(whisper) from <{name}> {message}"),
	WHISPER_FROM_UNKNOWN("whisper.from-unknown", "(whisper) from [{name}] {message}"),
	WHISPER_SELF("whisper.self","{rose}You can't message yourself!"),
	NO_REPLY("no-reply", "{rose}No-one has messaged you yet."),
	// Messages related to /getpos
	GETPOS_INVALID("getpos.invalid", "{rose}Invalid getpos option."),
	GETPOS_ANGLE("getpos.angle", "{yellow} ({white}{angle}{yellow} deg)"),
	GETPOS_COMPASS("getpos.compass", "{yellow}Compass: {white}{direction}"),
	GETPOS_ROTATION("getpos.rotation", "{yellow}Rotation: {white}{yaw}{yellow} Pitch: {white}{pitch}"),
	GETPOS_WORLD("getpos.world", "{yellow} in '{white}{world}{yellow}'"),
	GETPOS_POS("getpos.pos", "{yellow}Pos X: {white}{x}{yellow} Y: {white}{y}{yellow} Z: {white}{z}{yellow}"),
	GETPOS_DIR("getpos.dir", "{yellow}Direction: {white}{direction}"),
	// Messages relating to /online
	ONLINE_ALL("online.all","{yellow}Online Players ({count}):"),
	ONLINE_WORLD("online.world","{yellow}Online Players in world '{world}' ({count}):"),
	// Messages relating to /who and /worldinfo
	INFO_DIVIDER("info.divider","{white}------------------------------------------------"),
	INFO_TITLE_PLAYER("info.title.player","{yellow} Player {white}[{name}]{yellow} Info"),
	INFO_TITLE_CONSOLE("info.title.console","{yellow} The Minecraft Console Info"),
	INFO_TITLE_WORLD("info.title.world","{yellow} World {white}[{name}]{yellow} Info"),
	INFO_TITLE_UNKNOWN("info.title.unknown","{yellow} Unknown Command Sender"),
	INFO_USERNAME("info.username","{gold} Username: {white}{name}"),
	INFO_DISPLAYNAME("info.display","{gold} Display Name: {white}{name}"),
	INFO_HEALTH("info.health","{gold} -{yellow} Health: " +
		"{white}{bar}{value,choice,0#{rose}|3#{yellow}|7#{green}}{value}"),
	INFO_HEALTHBAR("info.healthbar","[{value,choice,0#{rose}|3#{yellow}|7#{green}}" +
		"{health}{silver}{filler}{white}]  "),
	INFO_LOCATION("info.location","{gold} -{yellow} Location: {white}{location}"),
	INFO_SPAWN("info.spawn","{gold} -{yellow} Spawn: {white}{location}"),
	INFO_WORLD("info.world","{gold} -{yellow} World: {white}{world}"),
	INFO_IP("info.ip","{gold} -{yellow} IP: {white}{ip}"),
	INFO_STATUS("info.status.full","{gold} -{yellow} Status: {white}{status}."),
	INFO_STATUS_AWAY("info.status.away","Away ({away})"),
	INFO_STATUS_AROUND("info.status.around","Around"),
	INFO_STATUS_SERVER("info.status.server","Up and running."),
	INFO_VERSION("info.version","{gold} -{yellow} Version: {white}{version}"),
	INFO_PRIMARY("info.primary","{gold} -{yellow} Primary World: {white}{world}"),
	INFO_NAME("info.name","{gold} Name: {white}{name}"),
	INFO_ENVIRONMENT("info.env","{gold} -{yellow} Environment: {white}{env}"),
	INFO_PVP("info.pvp.full","{gold} -{yellow} PVP: {white}{pvp}"),
	INFO_PVP_ON("info.pvp.on","Enabled"),
	INFO_PVP_OFF("info.pvp.off","Disabled"),
	INFO_SEED("info.seed","{gold} -{yellow} Seed: {white}{seed}"),
	INFO_TIME("info.time","{gold} -{yellow} Time: {white}{time}"),
	INFO_XP("info.xp","{gold} -{yellow} Experience: {xp} ({percent}% to next level)"),
	INFO_LEVEL("info.lvl","{gold} -{yellow} Level: {lvl}"),
	INFO_FOOD("info.food","{gold} -{yellow} Food: {food} (saturation {sat}, exhaustion {ex})"),
	// Messages relating to /clear
	CLEAR_INVALID("clear.invalid", "{rose}Invalid option."),
	CLEAR_SELF("clear.self", "{green}You have cleared your {inventory}."),
	CLEAR_YOURS("clear.yours", "{green}Your {inventory} has been cleared."),
	CLEAR_THEIRS("clear.theirs", "{white}{player}{green}'s {inventory} has been cleared."),
	CLEAR_FULL("clear.inventory", "inventory"),
	CLEAR_QUICKBAR("clear.quick-bar", "quick-bar"),
	CLEAR_PACK("clear.pack", "pack"),
	CLEAR_ARMOUR("clear.armour", "armour"),
	CLEAR_EXCEPTARMOUR("clear.inventory", "inventory"),
	// Messages relating to /give
	GIVE_BAD_AMOUNT("give.error.amount", "{rose}The amount must be an integer."),
	GIVE_BAD_ENCH("give.error.enchant", "{white}{ench}{rose} is not a valid enchantment for {white}{item}{rose}."),
	GIVE_BAD_LEVEL("give.error.enchant", "{white}{level}{rose} is not a valid level for {white}{ench}{rose}."),
	GIVE_BAD_ID("give.error.id", "{rose}Invalid item."),
	GIVE_BAD_DATA("give.error.data", "{white}{data}{rose} is not a valid data type for {white}{item}{rose}."),
	GIVE_GIFT("give.gift", "{green}Gave {white}{amount,choice,-1#infinite|0#{amount}}{green} of " +
		"{white}{item}{green} to {white}{player}{green}!"),
	GIVE_GIFTED("give.gifted", "{green}Enjoy the gift! {white}{amount,choice,-1#infinite|0#{amount}}{green} " +
		"of {white}{item}{green}!"),
	GIVE_ENJOY("give.enjoy", "{green}Enjoy! Giving {white}{amount,choice,-1#infinite|0#{amount}}{green} of " +
		"{white}{item}{green}."),
	// Messages relating to /items
	ITEMS_ENJOY("give.items.enjoy", "{green}Enjoy! Giving {white}{items}{white}!"),
	ITEMS_GIFT("give.items.gift", "{green}Giving {white}{items}{green} to {white}{player}{green}!"),
	ITEMS_GIFTED("give.items.gifted", "{green}Enjoy the gift! Giving {white}{items}{green}!"),
	ITEMS_JOINER("give.items.join", "{green}, {white}"),
	// Messages relating to /kit
	KIT_INVALID("kit.invalid", "{rose}Kit by the name of {yellow}{kit}{rose} does not exist!"),
	KIT_LIST("kit.list", "{yellow}Kits available: "),
	KIT_COOLDOWN("kit.cooldown", "{rose}You may not get this again kit so soon! Try again in " +
		"{yellow}{time}{rose} seconds."),
	KIT_GIVE("kit.give", "{green}Here you go!"),
	// Messages relating to /take
	TAKE_THEFT("take.theft", "{green}Took {white}{amount}{green} of {white}{item}{green} from " +
		"{white}{player}{green}."),
	TAKE_TOOK("take.took", "{white}{amount,choice,0#All|1#{amount}}{green} of {white}{item}{green} " +
		"was taken from you."),
	// Messages relating to /masstake
	MASSTAKE_THEFT("take.mass.theft", "{green}Took {white}{amount}{green} of {white}{items}{green} from " +
		"{white}{player}{green}."),
	MASSTAKE_TOOK("take.mass.took", "{white}{amount,choice,0#All|1#{amount}}{green} of {white}{items}{green} " +
		"was taken from you."),
	// Messages relating to /heal
	HEAL_THEM("heal.them", "{yellow}{name}{white} has been {health,choice,-10#{hurt}|0#{healed}} by {yellow}" +
		"{health,number,#0.0;#0.0}{white} hearts."),
	HEAL_YOU("heal.you", "{white}You are {health,choice,-10#{hurt}|0#{healed}} by {yellow}" +
		"{health,number,#0.0;#0.0}{white} hearts."),
	HEAL_HURT("heal.hurt", "hurt"),
	HEAL_HEALED("heal.healed", "healed"),
	// Messages relating to /mobspawn
	MOB_NO_DEST("mob.error.no-dest", "{rose}Can't determine where to spawn the mob; you're not a player and " +
		"either didn't specify a destination, or tried to specify an invalid destination."),
	MOB_TOO_FEW("mob.error.too-few", "{rose}Cannot spawn less than one mob."),
	MOB_BAD_TYPE("mob.error.bad-type", "{rose}Invalid {mob} type: {type}"),
	MOB_MOB("mob.mob", "mob"),
	// Messages relating to /time
	TIME_NONE("time.none", "{rose}Time has no meaning here."),
	TIME_DAY("time.day", "Day"),
	TIME_NIGHT("time.night", "Night"),
	TIME_DAWN("time.dawn", "Dawn"),
	TIME_DUSK("time.dusk", "Dusk"),
	TIME_NOON("time.noon", "Noon"),
	TIME_WITCH("time.witch", "Midnight"),
	TIME_SET("time.set", "Time set to {time}!"),
	TIME_SET_NAME("time.set-name", "Time set to {name}: {time}!"),
	TIME_ADVANCE("time.advance", "Time advanced by {time}!"),
	TIME_REWIND("time.rewind", "Time set back by {time}!"),
	TIME_CURRENT("time.current", "Current Time: {name} {time}"),
	TIME_BAD_DURATION("time.error.duration", "{rose}Invalid duration format."),
	TIME_BAD_TIME("time.error.time", "{rose}Invalid time format."),
	TIME_FORMAT("time.format","{hours} hours and {minutes} minutes"),
	TIME_HOURS("time.hours","{hours} hours"),
	TIME_MINUTES("time.minutes","{minutes} minutes"),
	TIME_TICKS("time.ticks","{ticks} ticks"),
	// Messages related to /weather
	WEATHER_NEGATIVE("weather.error.negative", "{rose}Only positive durations accepted for weather."),
	WEATHER_BAD_THUNDER("weather.error.thunder", "{rose}Duration too large for thunder."),
	WEATHER_NETHER("weather.storm.nether", "{rose}The nether doesn't have weather!"),
	WEATHER_ACTIVE("weather.storm.active", "{blue}World '{white}{world}{blue}' has a storm active for " +
		"{white}{duration}{blue}."),
	WEATHER_INACTIVE("weather.storm.inactive", "{blue}World '{white}{world}{blue}' " +
		"does not have a storm active."),
	WEATHER_START("weather.storm.start", "{blue}Weather storm started!"),
	WEATHER_STOP("weather.storm.stop", "{blue}Weather storm stopped!"),
	WEATHER_CHANGE("weather.storm.change", "{blue}Weather storm will stop in {time}!"),
	WEATHER_SET("weather.storm.set", "{blue}Weather storm started for {time}!"),
	WEATHER_LIGHTNING("weather.lightning", "{yellow}Lightning strike!"),
	THUNDER_NETHER("weather.thunder.nether", "{rose}Only normal worlds have thunder."),
	THUNDER_ACTIVE("weather.thunder.active", "{yellow}World '{white}{world}{yellow}' " +
		"is thundering for {duration}."),
	THUNDER_INACTIVE("weather.thunder.inactive", "{yellow}World '{white}{world}{yellow}' is not thundering."),
	THUNDER_START("weather.thunder.start", "{yellow}Thunder started!"),
	THUNDER_STOP("weather.thunder.stop", "{yellow}Thunder stopped!"),
	THUNDER_CHANGE("weather.thunder.change", "{yellow}Thunder will stop in {time} ticks!"),
	THUNDER_SET("weather.thunder.set", "{yellow}Thunder started for {time} ticks!"),
	// Messages related to /teleport
	TELEPORT_SELF("teleport.self", "{white}You teleported to {blue}{destination}{white}!"),
	TELEPORT_OTHER("teleport.other", "{white}You teleported {blue}{target}{white} to " +
		"{blue}{destination}{white}!"),
	TELEPORT_WHOA("teleport.whoa", "{white}You have been teleported to {blue}{destination}{white}!"),
	TELEPORT_WARMUP("teleport.warmup", "{yellow}Warming up. Teleport will commence in {time} ticks."),
	// Messages related to /setspawn
	SETHOME("setspawn.home", "{yellow}Home position of player '{white}{player}{yellow}' changed to " +
		"{white}({x},{y},{z})"),
	SETSPAWN("setspawn.no-world", "{yellow}Spawn position changed to {white}({x],{y},{z})"),
	SETSPAWN_WORLD("setspawn.world", "{yellow}Spawn position in world '{white}{world}{yellow}' changed to " +
		"{white}({x},{y},{z})"),
	SETSPAWN_HERE("setspawn.here", "{yellow}Spawn position changed to where you are standing."),
	SETSPAWN_PLAYER("setspawn.player", "{yellow}Spawn position changed to where {player} is standing."),
	SETSPAWN_ERROR("setspawn.error", "{rose}There was an error setting the spawn location. " +
		"It has not been changed."),
	// Messages related to destinations
	DESTINATION_WORLD("destination.world", "other worlds"),
	DESTINATION_PLAYER("destination.player", "other players"),
	DESTINATION_COORDS("destination.coords", "specific coordinates"),
	DESTINATION_HOME("destination.home", "player homes"),
	DESTINATION_HOME_OTHER("destination.home_other", "other players' homes"),
	DESTINATION_SPAWN("destination.spawn", "spawn"),
	DESTINATION_SPAWN_OTHER("destination.spawn_other", "spawn by player"),
	DESTINATION_TARGET("destination.target", "the targeted block"),
	DESTINATION_TARGET_OTHER("destination.target_other", "other players' targeted block"),
	DESTINATION_COMPASS("destination.compass", "the compass target"),
	DESTINATION_COMPASS_OTHER("destination.compass", "other players' compass target"),
	DESTINATION_BAD("destination.bad", "{rose}Invalid destination."),
	DESTINATION_THEIR_HOME("destination.their_home", "{player}'s home"),
	DESTINATION_THEIR_COMPASS("destination.their_compass", "{player}'s compass"),
	DESTINATION_OTHER("destination.other", "other people's places"),
	// Messages relating to targets
	TARGET_BAD("target.bad", "{rose}Invalid target."),
	TARGET_SELF("target.self", "yourself"),
	TARGET_SOMEONE("target.someone", "someone"),
	TARGET_SEVERAL("target.several", "several people"),
	TARGET_NEARBY("target.nearby", "several mobs"),
	TARGET_ONE_MOB("target.one-mob", "a {mob}"),
	TARGET_MOB("target.mob", "mob"),
	TARGET_MOBS("target.mobs", "mobs"),
	TARGET_OTHER("target.other", "others"),
	TARGET_NO_PLAYERS("target.no-players", "{rose}No players nearby."),
	TARGET_NO_MOBS("target.no-mobs", "{rose}No mobs nearby."),
	TARGET_NO_TARGET("target.no-target", "{rose}No-one there."),
	TARGET_WORLD("target.world", "the world"),
	// Messages relating to help
	HELP_UNAVAILABLE("help.unavail", "{rose}Help topic unavailable."),
	// Messages relating to the message of the day
	MOTD_UNKNOWN("motd.unknown", "unknown"),
	MOTD_UNAVAILABLE("motd.unavail", "{rose}No message of the day available."),
	MOTD_NOONE("motd.no-one", "(no-one)"),
	// Messages relating to item info
	ITEMINFO_BAD_SLOT("iteminfo.badslot", "Slot {slot} does not exist!"),
	ITEMINFO_INFO("iteminfo.info", "Name: {name}\nDetail: {amount} x {item}@{data} ({dataname})\nEnchantments: {ench}"),
	// Messages relating to the /general subcommands
	ECONOMY_NO_PLAYER("econ.no-player", "Please specify the player you would like to dry-run the command as."),
	PERMISSIONS_RESTRICT("permissions.restrict", "Permission '{node}' has been added to the " +
		"list of permissions restricted to ops."),
	PERMISSIONS_RELEASE("permissions.release", "Permission '{node}' has been removed from the " +
		"list of permissions restricted to ops."),
	GENERAL_RELOAD("general.reload", "{purple}General config reloaded."),
	GENERAL_SAVE("general.save", "{purple}General config saved."),
	KIT_NEW("kit.new", "{green}New kit '{yellow}{kit}{green}' created."),
	KIT_ADD("kit.add", "{yellow}{amount}{green} of {yellow}{item}{green} added to kit " +
		"'{yellow}{kit}{green}'."),
	KIT_NOT_IN("kit.not-in", "{green}The kit '{yellow}{kit}{green}' does not include " +
		"{yellow}{item}{green}."),
	KIT_REMOVE("kit.remove", "{yellow}{amount,choice,0#All|1#{amount}}{green} of {yellow}{item}{green} " +
		"removed from kit '{yellow}{kit}{green}'."),
	KIT_BAD_DELAY("kit.bad-delay", "{rose}Invalid delay."),
	KIT_DELAY("kit.delay", "{green}Delay of kit '{yellow}{kit}{green}' set to {yellow}{delay}{green} " +
		"milliseconds."),
	KIT_BAD_COST("kit.bad-cost", "{rose}Invalid cost."),
	KIT_COST("kit.cost", "{green}Cost of kit '{yellow}{kit}{green}' set to {yellow}{cost}{green}."),
	KIT_TRASH("kit.trash", "{green}Kit '{yellow}{kit}{green}' has been deleted."),
	KIT_CONTAINS("kit.contains", "{green}Kit '{yellow}{kit}{green}' contains: {white}{items"),
	KIT_INFO("kit.info", "{green}Its delay is {yellow}{delay}{green} and its recorded cost is " +
		"{yellow}{cost}{green}."),
	// Log messages
	LOG_CONFIG_DEFAULT("log.config.default", "Configuration file {file} does not exist. " +
		"Attempting to create default one..."),
	LOG_CONFIG_ERROR("log.config.error", "Could not read and/or write {file}! Continuing with default values!"),
	LOG_CONFIG_SAVE_ERROR("log.config.nosave", "Error saving config.yml: {msg}"),
	LOG_CONFIG_SUCCESS("log.config.success", "Default configuration created successfully! You can now "
		+ "stop the server and edit plugins/General/config.yml."),
	LOG_ITEM_NO_HOOKS("log.item.no-hooks", "Hooks are missing."),
	LOG_ITEM_BAD_HOOK("log.item.bad-hook", "Invalid hook: {hook}"),
	LOG_ITEM_BAD_ALIAS("log.item.bad-alias", "Invalid item alias assignment '{alias}'."),
	LOG_ITEM_NO_ALIASES("log.item.no-aliases", "No aliases were defined in items.yml."),
	LOG_ITEM_BAD_KEY("log.item.bad-key", "Invalid item alias assignment for '{alias}'."),
	LOG_ITEM_NO_NAMES("log.item.no-names", "Names of items are missing."),
	LOG_ITEM_BAD_NAMES("log.item.bad-names", "The names section of items.yml is missing or invalid."),
	LOG_ITEM_BAD_NAME("log.item.bad-name", "Invalid keys in the names section of items.yml " +
			"(eg {name}; {count} bad keys in total)"),
	LOG_KIT_BAD_METHOD("log.kit.bad-method", "Invalid method for kit costing; falling back to default of " +
		"'individual'"),
	LOG_KIT_NO_ITEMS("log.kit.no-items", "Kit '{kit}' has no items and has been skipped."),
	LOG_KIT_BAD_ITEM("log.kit.bad-item", "Kit '{kit}' has an invalid item '{item}' which has been skipped."),
	LOG_KIT_BAD("log.kit.error", "Kit '{kit}' has a malformed entry: \"{item}\""),
	LOG_MOB_BAD("log.mob.error", "Mob ID {mob} has a malformed entry: \"{name}\""),
	LOG_COMMAND_NO_ALIASES("log.cmd.no-aliases","No command aliases defined; did you forget to copy the aliases section " +
		"from the example config.yml?"),
	LOG_COMMAND_REG_ERROR("log.cmd.reg-error", "Command [{command}] could not be registered."),
	LOG_COMMAND_IRC_REG_ERROR("log.cmd.reg-error", "Command [{command}] could not be registered with CraftIRC."),
	LOG_COMMAND_TAKEN("log.cmd.taken", "Command alias {alias} was not registered because [{plugin}] " +
		"claimed it."),
	LOG_COMMAND_ERROR("log.cmd.error", "There was an error with command [{command}] during {errorPlace}!" +
		" Please report this!"),
	LOG_COMMAND_ERROR_INFO("log.cmd.error-info", "Full command string: [{command}]"),
	LOG_COMMAND_USED("log.cmd.used", "{sender} used command: {command}"),
	LOG_HELP_ENABLED("log.help.enabled", "[Help {version}] support enabled."),
	LOG_HELP_MISSING("log.help.missing", "[Help] isn't detected. No /help support; instead use /general help"),
	LOG_TWICE("log.twice", "Seems to have loaded twice for some reason; skipping initialization. " +
		"Use /general reload if you actually want to reload General."),
	LOG_SUCCESS("log.success", "Plugin successfully loaded!"),
	LOG_DISABLED("log.disabled", "Plugin disabled!"),
	LOG_NO_ECONOMY("log.economy.none", "Economy is disabled in the config file; no economy will be used."),
	LOG_MISSING_ECONOMY("log.economy.missing", "You've enabled economy, but have no economy system installed! " +
			"You must either install an economy plugin or choose a currency item in order to use economy."),
	LOG_ECONOMY_ERROR("log.economy.error", "Error loading AllPay: {msg}"),
	// Messages relating to missing permissions
	PERMISSION_LACK("permissions.lacking", "{rose}You don't have permission to {action}. ({permission})"),
	PERMISSION_ERROR("permissions.error", "{rose}Error checking permissions."),
	LACK_AWAY("permissions.away","set your away status"),
	LACK_TELL("permissions.tell","send private messages to players"),
	LACK_GETPOS("permissions.getpos","check your location"),
	LACK_GETPOS_OTHER("permissions.getpos_other","check someone else's location"),
	LACK_PLAYERLIST("permissions.playerlist","view the player list"),
	LACK_WHO("permissions.who","view info on users other than yourself"),
	LACK_WORLDINFO("permissions.worldinfo","view info on worlds"),
	LACK_CLEAR("permissions.clear","clear your inventory"),
	LACK_CLEAR_OTHER("permissions.clear_other","clear someone else's inventory"),
	LACK_GIVE("permissions.give","give items"),
	LACK_GIVE_OTHER("permissions.give_other","give items to others"),
	LACK_GIVE_INFINITE("permissions.give_infinite","give infinite stacks of items"),
	LACK_GIVE_MASS("permissions.give_mass","give many items at once"),
	LACK_GIVE_ITEM("permissions.give_item","get {item}."),
	LACK_KIT("permissions.kit","get kits"),
	LACK_KIT_NAME("permissions.kit_name","get the kit '{kit}'."),
	LACK_TAKE("permissions.take","remove items from your inventory"),
	LACK_TAKE_OTHER("permissions.take_other","take items from someone else's inventory"),
	LACK_TAKE_MASS("permissions.take_mass","massively remove items from your inventory"),
	LACK_HURT("permissions.hurt","hurt players"),
	LACK_HEAL("permissions.heal","heal players"),
	LACK_MOBSPAWN_MASS("permissions.mobspawn_mass","spawn mobs en masse"),
	LACK_MOBSPAWN_CREEPER_POWERED("permissions.mobspawn_creeper_powered", "spawn powered creepers"),
	LACK_MOBSPAWN_PIG_SADDLED("permissions.mobspawn_pig_saddled", "spawn saddled pigs"),
	LACK_MOBSPAWN_PIG_ZOMBIE_ANGRY("permissions.mobspawn_pig-zombie_angry","spawn angry zombie pigmen"),
	LACK_MOBSPAWN_SHEEP_SHEARED("permissions.mobspawn_sheep_sheared","spawn sheared sheep"),
	LACK_MOBSPAWN_SHEEP_COLOURED("permissions.mobspawn_sheep_coloured","spawn coloured sheep of colour {colour}"),
	LACK_MOBSPAWN_SLIME("permissions.mobspawn_slime","spawn {size} slimes"),
	LACK_MOBSPAWN_WOLF("permissions.mobspawn_wolf","spawn {attitude} wolves"),
	LACK_MOBSPAWN_ENDERMAN("permissions.mobspawn_enderman","spawn endermen carrying {block}"),
	LACK_MOBSPAWN_MOB("permissions.mobspawn_mob", "spawn {mobs}"),
	LACK_MOBSPAWN_VILLAGER("permissions.mobspawn_villager", "spawn {role} villagers"),
	LACK_TIME_VIEW("permissions.time","see the time"),
	LACK_TIME_SET("permissions.time_set","set the time"),
	LACK_WEATHER_VIEW("permissions.weather","see the weather report"),
	LACK_WEATHER_THUNDER("permissions.weather_thunder","control thunder"),
	LACK_WEATHER_SET("permissions.weather_set","control the weather"),
	LACK_WEATHER_ZAP("permissions.weather_zap","summon lightning"),
	LACK_TELEPORT("permissions.teleport","teleport {target} to {destination} in world '{world}'"),
	LACK_TELEPORT_MASS("permissions.teleport_mass","teleport en masse"),
	LACK_SETSPAWN("permissions.setspawn_world","set the spawn location of {target} to {destination} in {world}"),
	LACK_GAMEMODE_VIEW("permissions.gamemode_view","see people's game mode"),
	LACK_GAMEMODE_SET("permissions.gamemode_set","set your game mode"),
	LACK_GAMEMODE_SET_OTHER("permissions.gamemode_set_other","set people's game mode"),
	LACK_INSTANT("permissions.instant", "{action} so soon after the last time"),
	LACK_ITEMINFO("permissions.iteminfo", "View info for your items."),
	LACK_ITEMINFO_OTHER("permissions.iteminfo_other", "View info for other people's items."),
	LACK_ADMIN_RELOAD("permissions.admin_reload", "reload the General configuration"),
	LACK_ADMIN_ITEM("permissions.admin_item", "edit the item aliases"),
	LACK_ADMIN_SAVE("permissions.admin_save", "save the General configuration"),
	LACK_ADMIN_RESTRICT("permissions.admin_restrict", "make a permission op-only"),
	LACK_ADMIN_RELEASE("permissions.admin_release", "make a permission not op-only"),
	LACK_ADMIN_KIT("permissions.admin_kit", "edit the kit definitions"),
	LACK_ADMIN_GENLANG("permissions.admin_genlang", "generate the language keys"),
	LACK_ADMIN_ECONOMY("permissions.admin_economy", "edit the command costs"),
	LACK_ADMIN_SET("permissions.admin_set", "edit configuration values"),
	LACK_DEOP("permissions.deop", "deop players"),
	LACK_OP("permissions.op", "op players"),
	LACK_KICK("permissions.kick", "kick players"),
	LACK_BAN("permissions.ban", "ban players"),
	LACK_PARDON("permissions.pardon", "pardon banned players"),
	// Cooldown-related messages
	IN_COOLDOWN("misc.cooldown.in", "{rose}Please wait {duration} before trying to {action} again."),
	COOLDOWN_WEATHER("misc.cooldown.weather", "change the weather in {world}"),
	COOLDOWN_THUNDER("misc.cooldown.thunder", "change the weather in {world}"),
	COOLDOWN_LIGHTNING("misc.cooldown.lightning", "summon lightning in {world}"),
	COOLDOWN_TIME("misc.cooldown.time", "change the time in {world}"),
	COOLDOWN_TELEPORT("misc.cooldown.teleport", "teleport"),
	COOLDOWN_KIT("misc.cooldown.kit", "use the {kit} kit"),
	// Miscellaneous common messages
	MISC_BAD_PLAYER("misc.bad-player", "{rose}There is no player named {white}{name}{rose}."),
	MISC_BAD_WORLD("misc.bad-world", "{rose}There is no world named {white}{name}{rose}."),
	MISC_BAD_NUMBER("misc.bad-num", "{rose}Invalid number {white}{num}{rose}."),
	MISC_LOCATION("misc.location", "({x}, {y}, {z}) facing ({yaw}, {pitch})"),
	ECONOMY_SHOW_COST("econ.show-cost", "{yellow}That would cost {white}{cost}{yellow}."),
	ECONOMY_PAY("econ.pay", "{yellow}You pay {white}{cost}{yellow}."),
	ECONOMY_EARN("econ.earn", "{yellow}You have earned {white}{income}{yellow}!"),
	ECONOMY_INSUFFICIENT("econ.insuff", "{rose}Unfortunately, you don't have that much."),
	MISC_KICKED("misc.kicked", "Kicked by administrator"),
	MISC_KICKING("misc.kicking", "Kicking player {player}: {reason}"),
	MISC_OPPING("misc.opping", "Promoted players to operator: {ops}"),
	MISC_DEOPPING("misc.deopping", "Demoted players from operator: {ops}"),
	MISC_OPPED("misc.opped", "You are now op!"),
	MISC_DEOPPED("misc.deopped", "You are no longer op."),
	MISC_BAD_MODE("misc.badmode", "{rose}Unknown game mode."),
	MISC_IN_MODE("misc.inmode", "{player} {yellow}is in{white} {mode} {yellow}mode."),
	MISC_SET_MODE("misc.setmode", "{yellow}Setting mode of {white}{player}{yellow} to {white}{mode}{yellow}."),
	MISC_SET_OWN_MODE("misc.setownmode", "{yellow}Setting your mode to {white}{mode}{yellow}."),
	MISC_BAN_ANYWAY("misc.bananyway", "{yellow}Note: invalid players are assumed offline and will still be banned."),
	MISC_BANNED("misc.banned","{player} {yellow}has been banned."),
	MISC_PARDONED("misc.pardoned","{player} {yellow}has been pardoned."),
	ADMIN_ECON_WHICH_TIME("admin.econ.time.which", "{rose}Which time?"),
	ADMIN_ECON_BAD_TIME("admin.econ.time.bad", "{rose}Invalid time."),
	ADMIN_ECON_WHICH_WEATHER("admin.econ.weather.which", "{rose}Which weather?"),
	ADMIN_ECON_BAD_WEATHER("admin.econ.weather.bad", "{rose}Invalid weather."),
	ADMIN_ECON_WHICH_MOB("admin.econ.mob.which", "{rose}Which mob?"),
	ADMIN_ECON_BAD_MOB("admin.econ.mob.bad.mob", "{rose}Invalid mob."),
	ADMIN_ECON_BAD_MOBSPEC("admin.econ.mob.bad.spec", "{rose}Invalid mob specification."),
	ADMIN_ECON_BAD_MOBDATA("admin.econ.mob.bad.data", "{rose}Invalid mob data."),
	ADMIN_ECON_BAD_MOUNT("admin.econ.mob.bad.mount", "{rose}Invalid mob for mount."),
	ADMIN_ECON_BAD_TARGET_TELE("admin.econ.badtarg.tele", "{rose}Invalid teleport target."),
	ADMIN_ECON_BAD_TARGET_SPAWN("admin.econ.badtarg.spawn", "{rose}Invalid setspawn target."),
	ADMIN_ECON_BAD_TARGET("admin.econ.badtarg.general", "Invalid target."),
	ADMIN_ECON_BAD_DEST("admin.econ.destbad", "Invalid destination."),
	ADMIN_ECON_BAD_WORLD("admin.econ.badworld", "Invalid world."),
	ADMIN_ECON_SET("admin.econ.set", "Set economy value '{path}' to {value}!"),
	ADMIN_VAR_BOOL("admin.var.bool", "{rose}Must be a boolean."),
	ADMIN_VAR_INT("admin.var.int", "{rose}Must be an integer."),
	ADMIN_VAR_NUM("admin.var.real", "{rose}Must be a number."),
	ADMIN_VAR_ECONTAKE("admin.var.econtake", "{rose}Invalid economy-take method (must be trash or sell)."),
	ADMIN_VAR_ECONCLEAR("admin.var.econclear", "{rose}Invalid economy-clear method (must be trash or sell)."),
	ADMIN_VAR_ECONKIT("admin.var.econkit", "{rose}Invalid economy-kits method (must be individual, cumulative, " +
		"or discount)."),
	ADMIN_VAR_UNKNOWN("admin.var.unknown", "{rose}Unknown variable: {var}"),
	ADMIN_VAR_SET("admin.var.set", "Variable {var} set to {value}."),
	ADMIN_ITEM_ALIAS_REMOVE("admin.item.alias.remove", "Alias {alias} removed."),
	ADMIN_ITEM_ALIAS_SHOW("admin.item.alias.show", "The alias {alias} refers to {item}."),
	ADMIN_ITEM_ALIAS_ADD("admin.item.alias.add", "Alias {alias} added for {item}."),
	ADMIN_ITEM_VARIANT_SHOW("admin.item.variant.show,", "Variant names for {item}: {variants}"),
	ADMIN_ITEM_VARIANT_CHANGE("admin.item.variant.change", "Variant names for {item} are now: {variants}"),
	ADMIN_ITEM_NAME_SHOW("admin.item.name.show", "The name of item {item} is {name}."),
	ADMIN_ITEM_NAME_CHANGE("admin.item.name.change", "Item {item} is now called {name}"),
	ADMIN_ITEM_HOOK_SHOW("admin.item.hook.show", "The hook {hook} refers to {item}."),
	ADMIN_ITEM_HOOK_CHANGE("admin.item.hook.change", "The hook {hook} now refers to {item}."),
	ADMIN_ITEM_GROUP_EMPTY("admin.item.group.empty", "Group '{group}' does not exist or is empty."),
	ADMIN_ITEM_GROUP_SHOW("admin.item.group.show", "Group '{group}' contains: {items}"),
	ADMIN_ITEM_GROUP_REMOVE("admin.item.group.remove", "Group '{group}' has been deleted (if it existed)."),
	ADMIN_ITEM_GROUP_CHANGE("admin.item.group.change", "Group '{group}' now contains: {items}"),
	ADMIN_ITEM_BAD("admin.item.bad", "{rose}No such item."),
	LIST_BAD_ITEMS("give.badlist", "The following invalid items were ignored: {items}"),
	;
	
	private static YamlConfiguration config;
	private static String language;
	private String fmt;
	private String node;
	private static HashMap<String,LanguageText> byNode = new HashMap<String,LanguageText>();
	
	private LanguageText(String n, String format) {
		this.fmt = format;
		this.node = n;
	}
	
	public String value(Object... params) {
		return Messaging.format(getFormat(), params);
	}

	public String getFormat() {
		if(config == null) return fmt;
		return config.getString(language + "." + node, fmt);
	}
	
	public static YamlConfiguration setLanguage(String lang, File folder, String file) {
		language = lang;
		config = new YamlConfiguration();
		DumperOptions options = new DumperOptions();
        options.setIndent(4);
        options.setDefaultFlowStyle(FlowStyle.BLOCK);
		options.setDefaultScalarStyle(ScalarStyle.DOUBLE_QUOTED);
		try {
			Field yamlField = YamlConfiguration.class.getDeclaredField("yaml");
			yamlField.setAccessible(true);
			yamlField.set(config, new Yaml(new YamlConstructor(), new YamlRepresenter(), options));
		} catch(SecurityException e) {}
		catch(IllegalArgumentException e) {}
		catch(NoSuchFieldException e) {}
		catch(IllegalAccessException e) {}
		try {
			config.load(new File(folder, file));
		} catch(FileNotFoundException e) {
		} catch(IOException e) {
			e.printStackTrace();
		} catch(InvalidConfigurationException e) {
			e.printStackTrace();
		}
		return config;
	}
	
	static {
		for(LanguageText lang : values()) byNode.put(lang.node, lang);
	}
	
	public static LanguageText byNode(String n) {
		return byNode.get(n);
	}
}
