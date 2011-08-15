package net.craftstars.general.util;

import java.io.File;
import java.util.HashMap;

import net.craftstars.general.General;

import org.bukkit.util.config.Configuration;

/**
 * Manager for localized messages for built-in functions.
 * @author SpaceManiac
 */
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
	WHISPER_SELF("whisper.self","&c;You can't message yourself!"),
	NO_REPLY("no-reply", "{rose}No-one has messaged you yet."),
	// Messages related to /getpos
	GETPOS_INVALID("getpos.invalid", "{rose}Invalid getpos option."),
	GETPOS_ANGLE("getpos.angle", "{yellow} ({white}{angle}{yellow} deg)"),
	GETPOS_COMPASS("getpos.compass", "{yellow}Compass: {white}{direction}"),
	GETPOS_ROTATION("getpos.rotation", "{yellow}Rotation: {white}{yaw}{yellow} Pitch: {white}{pitch}"),
	GETPOS_WORLD("getpos.world", "{yellow} in '{white}{world}{yellow}'"),
	GETPOS_POS("getpos.pos", "{yellow}Pos X: {white}{x}{yellow} Y: {white}" + "{y}{yellow} Z: {white}{z}{yellow}"),
	GETPOS_DIR("getpos.dir", "{yellow}Direction: {white}{direction}"),
	// Messages relating to /online
	ONLINE_ALL("online.all","&eOnline Players ({count}):"),
	ONLINE_WORLD("online.world","&eOnline Players in world '{world}' ({count}):"),
	// Messages relating to /who and /worldinfo
	INFO_DIVIDER("info.divider","{white}------------------------------------------------"),
	INFO_TITLE_PLAYER("info.title.player","{yellow} Player {white}[{name}]{yellow} Info"),
	INFO_TITLE_CONSOLE("info.title.console","{yellow} The Minecraft Console Info"),
	INFO_TITLE_WORLD("info.title.world","{yellow} World {white}[{name}]{yellow} Info"),
	INFO_TITLE_UNKNOWN("info.title.unknown","{yellow} Unknown Command Sender"),
	INFO_USERNAME("info.username","{gold} Username: {white}{name}"),
	INFO_DISPLAYNAME("info.display","{gold} Display Name: {white}{name}"),
	INFO_HEALTH("info.health","{gold} -{yellow} Health: {white}{bar}{value,choice,0#{rose}|3#{yellow}|7#{green}}{value}"),
	INFO_HEALTHBAR("info.healthbar","[{value,choice,0#{rose}|3#{yellow}|7#{green}}{health}{silver}{filler}{white}]  "),
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
	GIVE_BAD_ID("give.error.id", "{rose}Invalid item."),
	GIVE_BAD_DATA("give.error.data", "{white}{data}{rose} is not a valid data type for {white}{item}{rose}."),
	GIVE_GIFT("give.gift", "{green}Gave {white}{amount,choice,-1#infinite|0#{amount}}{green} of {white}{item}{green} " +
		"to {white}{player}{green}!"),
	GIVE_GIFTED("give.gifted", "{green}Enjoy the gift! {white}{amount,choice,-1#infinite|0#{amount}}{green} of " +
		"{white}{item}{green}!"),
	GIVE_ENJOY("give.enjoy", "{green}Enjoy! Giving {white}{amount,choice,-1#infinite|0#{amount}}{green} of " +
		"{white}{item}{green}."),
	// Messages relating to /items
	ITEMS_ENJOY("give.items.enjoy", "&2Enjoy! Giving &f{items}&f!"),
	ITEMS_GIFT("give.items.gift", "Giving &f{items}&2 to &f{player}&f!"),
	ITEMS_GIFTED("give.items.gifted", "&2Enjoy the gift! Giving &f{items}&f!"),
	ITEMS_JOINER("give.items.join", "{green}, {white}"),
	// Messages relating to /kit
	KIT_INVALID("kit.invalid", "{rose}Kit by the name of {yellow}{kit}{rose} does not exist!"),
	KIT_LIST("kit.list", "{yellow}Kits available: "),
	KIT_COOLDOWN("kit.cooldown", "{rose}You may not get this again kit so soon! Try again in " +
		"{yellow}{time}{rose} seconds."),
	KIT_GIVE("kit.give", "{green}Here you go!"),
	// Messages relating to /take
	TAKE_THEFT("take.theft", "{green}Took {white}{amount}{green} of {white}{item}{green} from {white}{player}{green}."),
	TAKE_TOOK("take.took", "{white}{amount,choice,0#All|1#{amount}}{green} of {white}{item}{green} was taken from you."),
	// Messages relating to /masstake
	MASSTAKE_THEFT("take.mass.theft", "{green}Took {white}{amount}{green} of {white}{items}{green} from " +
		"{white}{player}{green}."),
	MASSTAKE_TOOK("take.mass.took", "{white}{amount,choice,0#All|1#{amount}}{green} of {white}{items}{green} was " +
		"taken from you."),
	// Messages relating to /heal
	HEAL_THEM("heal.them", "{yellow}{name}{white} has been {health,choice,-10#{hurt}|0#{healed}} by {yellow}" +
		"{health,number,#0.0;#0.0}{white} hearts."),
	HEAL_YOU("heal.you", "{white}You are {health,choice,-10#{hurt}|0#{healed}} by {yellow}" +
		"{health,number,#0.0;#0.0}{white} hearts."),
	HEAL_HURT("heal.hurt", "hurt"),
	HEAL_HEALED("heal.healed", "healed"),
	// Messages relating to /mobspawn
	MOB_NO_DEST("mob.error.no-dest", "{rose}Can't determine where to spawn the mob; you're not a player and either " +
		"didn't specify a destination, or tried to specify an invalid destination."),
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
	// Messages related to /weather
	WEATHER_NEGATIVE("weather.error.negative", "{rose}Only positive durations accepted for weather."),
	WEATHER_BAD_THUNDER("weather.error.thunder", "{rose}Duration too large for thunder."),
	WEATHER_NETHER("weather.storm.nether", "{rose}The nether doesn't have weather!"),
	WEATHER_ACTIVE("weather.storm.active", "{blue}World '{white}{world}{blue}' has a storm active for " +
		"{white}{duration}{blue}."),
	WEATHER_INACTIVE("weather.storm.inactive", "{blue}World '{white}{world}{blue}' does not have a storm active."),
	WEATHER_START("weather.storm.start", "{blue}Weather storm stopped!"),
	WEATHER_STOP("weather.storm.stop", "{blue}Weather storm started!"),
	WEATHER_CHANGE("weather.storm.change", "{blue}Weather storm will stop in {time}!"),
	WEATHER_SET("weather.storm.set", "{blue}Weather storm started for {time}!"),
	WEATHER_LIGHTNING("weather.lightning", "{yellow}Lightning strike!"),
	THUNDER_NETHER("weather.thunder.nether", "{rose}Only normal worlds have thunder."),
	THUNDER_ACTIVE("weather.thunder.active", "{yellow}World '{white}{world}{yellow}' is thundering for {duration}."),
	THUNDER_INACTIVE("weather.thunder.inactive", "{yellow}World '{white}{world}{yellow}' is not thundering."),
	THUNDER_START("weather.thunder.start", "{yellow}Thunder started!"),
	THUNDER_STOP("weather.thunder.stop", "{yellow}Thunder stopped!"),
	THUNDER_CHANGE("weather.thunder.change", "{yellow}Thunder will stop in {time} ticks!"),
	THUNDER_SET("weather.thunder.set", "{yellow}Thunder started for {time} ticks!"),
	// Messages related to /teleport
	TELEPORT_SELF("teleport.self", "{white}You teleported to {blue}{destination}{white}!"),
	TELEPORT_OTHER("teleport.other", "{white}You teleported {blue}{target}{white} to {blue}{destination}{white}!"),
	TELEPORT_WHOA("teleport.whoa", "{white}You have been teleported to {blue}{destination}{white}!"),
	TELEPORT_WARMUP("teleport.warmup", "{yellow}Warming up. Teleport will commence in {time} ticks."),
	// Messages related to /setspawn
	SETHOME("setspawn.home", "{yellow}Home position of player '{white}{player}{yellow}' changed to {white}({x},{y},{z})"),
	SETSPAWN("setspawn.no-world", "{yellow}Spawn position changed to {white}({x],{y},{z})"),
	SETSPAWN_WORLD("setspawn.world", "{yellow}Spawn position in world '{white}{world}{yellow}' changed to " +
		"{white}({x},{y},{z})"),
	SETSPAWN_HERE("setspawn.here", "{yellow}Spawn position changed to where you are standing."),
	SETSPAWN_PLAYER("setspawn.player", "{yellow}Spawn position changed to where {player} is standing."),
	SETSPAWN_ERROR("setspawn.error", "{rose}There was an error setting the spawn location. It has not been changed."),
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
	// Messages relating to help
	HELP_UNAVAILABLE("help.unavail", "{rose}Help topic unavailable."),
	// Messages relating to the message of the day
	MOTD_UNKNOWN("motd.unknown", "unknown"),
	MOTD_UNAVAILABLE("motd.unavail", "{rose}No message of the day available."),
	MOTD_NOONE("motd.no-one", "(no-one)"),
	// Messages relating to the /general subcommands
	ECONOMY_NO_PLAYER("econ.no-player", "Please specify the player you would like to dry-run the command as."),
	PERMISSIONS_RESTRICT("permissions.restrict", "Permission '{node}' has been added to the list of permissions " +
		"restricted to ops."),
	PERMISSIONS_RELEASE("permissions.release", "Permission '{node}' has been removed from the list of permissions " +
		"restricted to ops."),
	GENERAL_RELOAD("general.reload", "{purple}General config reloaded."),
	GENERAL_SAVE("general.save", "{purple}General config saved."),
	KIT_NEW("kit.new", "{green}New kit '{yellow}{kit}{green}' created."),
	KIT_ADD("kit.add", "{yellow}{amount}{green} of {yellow}{item}{green} added to kit '{yellow}{kit}{green}'."),
	KIT_NOT_IN("kit.not-in", "{green}The kit '{yellow}{kit}{green}' does not include {yellow}{item}{green}."),
	KIT_REMOVE("kit.remove", "{yellow}{amount,choice,0#All|1#{amount}}{green} of {yellow}{item}{green} removed from " +
		"kit '{yellow}{kit}{green}'."),
	KIT_BAD_DELAY("kit.bad-delay", "{rose}Invalid delay."),
	KIT_DELAY("kit.delay", "{green}Delay of kit '{yellow}{kit}{green}' set to {yellow}{delay}{green} milliseconds."),
	KIT_BAD_COST("kit.bad-cost", "{rose}Invalid cost."),
	KIT_COST("kit.cost", "{green}Cost of kit '{yellow}{kit}{green}' set to {yellow}{cost}{green}."),
	KIT_TRASH("kit.trash", "{green}Kit '{yellow}{kit}{green}' has been deleted."),
	KIT_CONTAINS("kit.contains", "{green}Kit '{yellow}{kit}{green}' contains: {white}{items"),
	KIT_INFO("kit.info", "{green}Its delay is {yellow}{delay}{green} and its recorded cost is {yellow}{cost}{green}."),
	// Log messages
	LOG_CONFIG_DEFAULT("log.config.default", "Configuration file {file} does not exist. Attempting to create " +
		"default one..."),
	LOG_CONFIG_ERROR("log.config.error", "Could not read and/or write {file}! Continuing with default values!"),
	LOG_CONFIG_SUCCESS("log.config.success", "Default configuration created successfully! You can now "
		+ "stop the server and edit plugins/General/config.yml."),
	LOG_ITEM_NO_HOOKS("log.item.no-hooks", "Hooks are missing."),
	LOG_ITEM_BAD_HOOK("log.item.bad-hook", "Invalid hook: {hook}"),
	LOG_ITEM_BAD_ALIAS("log.item.bad-alias", "Invalid item alias assignment '{alias}'."),
	LOG_ITEM_NO_ALIASES("log.item.no-aliases", "No aliases were defined in items.yml."),
	LOG_ITEM_CONVERTED("log.item.converted", "Your items.db aliases will be inserted into the items.yml upon " +
		"shutdown,\nor you can force it earlier using /general save"),
	LOG_ITEM_BAD_KEY("log.item.bad-key", "Invalid item alias assignment for '{alias}'."),
	LOG_ITEM_NO_NAMES("log.item.no-names", "Names of items are missing."),
	LOG_ITEM_BAD_NAMES("log.item.bad-names", "The names section of items.yml is missing or invalid."),
	LOG_ITEM_BAD_NAME("log.item.bad-name", "Invalid keys in the names section of items.yml (eg {name})"),
	LOG_KIT_BAD_METHOD("log.kit.bad-method", "Invalid method for kit costing; falling back to default of 'individual'"),
	LOG_KIT_NO_ITEMS("log.kit.no-items", "Kit '{kit}' has no items and has been skipped."),
	LOG_KIT_BAD_ITEM("log.kit.bad-item", "Kit '{kit}' has an invalid item '{item}' which has been skipped."),
	LOG_KIT_BAD("log.kit.error", "Kit '{kit}' has a malformed entry: \"{item}\""),
	LOG_KIT_CONVERTED("log.kit.converted", "A general.kits file was found and converted to the new kits.yml format. " +
		"You may now delete the general.kits file without information loss."),
	LOG_MOB_BAD("log.mob.error", "Mob ID {mob} has a malformed entry: \"{name}\""),
	LOG_ICONOMY_5NOT4("log.iconomy.5not4", "Was looking for iConomy 4 but found iConomy 5 instead. Please either " +
		"downgrade iConomy, or edit your config.yml to specify iConomy5."),
	LOG_ICONOMY_4NOT5("log.iconomy.4not5", "Was looking for iConomy 4 but found iConomy 5 instead. Please either " +
		"update iConomy, or edit your config.yml to specify iConomy5."),
	LOG_PERMISSIONS_ERROR("log.permissions.error","Error loading Permissions. Please report to the Permissions dev."),
	LOG_PERMISSIONS_MISSING("log.permissions.missing","[{system}] not detected; falling back to [Basic] permissions."),
	LOG_PERMISSIONS_FAIL("log.permissions.fail","There was a big problem loading permissions system [{system}]!" +
		" Please report this error!"),
	LOG_PERMISSIONS_NOTE("log.permissions.note", "Note: Using permissions [{system}]"),
	LOG_PERMISSIONS_RESULT("log.permissions.result", "Using [{system} {version}] for permissions."),
	LOG_COMMAND_NO_ALIASES("log.cmd.no-aliases","No command aliases defined; did you forget to copy the aliases section " +
		"from the example config.yml?"),
	LOG_COMMAND_REG_ERROR("log.cmd.reg-error", "Command [{command}] could not be registered."),
	LOG_COMMAND_TAKEN("log.cmd.taken", "Command alias {alias} was not registered because [{plugin}] claimed it."),
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
	LACK_GIVE_INFINITE("permissions.give_infinte","give infinite stacks of items"),
	LACK_GIVE_MASS("permissions.give_mass","give many items at once"),
	LACK_GIVE_ITEM("permissions.give_item","get {item}."),
	LACK_KIT("permissions.kit","get kits"),
	LACK_KIT_NAME("permissions.kit_name","get the kit '{kit}'."),
	LACK_TAKE("permissions.take","remove items from your inventory"),
	LACK_TAKE_OTHER("permissions.take_other","take items from someone else's inventory"),
	LACK_TAKE_MASS("permissions.take_mass","massively remove items from your inventory"),
	LACK_HURT("permissions.hurt","hurt players"),
	LACK_HEAL("permissions.heal","heal players"),
	LACK_MOBSPAWN("permissions.mobspawn","spawn mobs"),
	LACK_MOBSPAWN_MASS("permissions.mobspawn_mass","spawn mobs en masse"),
	LACK_MOBSPAWN_CREEPER_POWERED("permissions.mobspawn_creeper_powered", "spawn powered creepers"),
	LACK_MOBSPAWN_PIG_SADDLED("permissions.mobspawn_pig_saddled", "spawn saddled pigs"),
	LACK_MOBSPAWN_PIG_ZOMBIE_ANGRY("permissions.mobspawn_pig-zombie_angry","spawn angry zombie pigmen"),
	LACK_MOBSPAWN_SHEEP_SHEARED("permissions.mobspawn_sheep_sheared","spawn sheared sheep"),
	LACK_MOBSPAWN_SHEEP_COLOURED("permissions.mobspawn_sheep_coloured","spawn coloured sheep of that colour"),
	LACK_MOBSPAWN_SLIME("permissions.mobspawn_slime","spawn {size} slimes"),
	LACK_MOBSPAWN_WOLF("permissions.mobspawn_wolf","spawn {attitude} wolves"),
	LACK_MOBSPAWN_MOB("permissions.mobspawn_mob", "spawn {mobs}"),
	LACK_TIME("permissions.time","see the time"),
	LACK_TIME_SET("permissions.time_set","set the time"),
	LACK_WEATHER("permissions.weather","see or change the weather"),
	LACK_WEATHER_THUNDER("permissions.weather_thunder","control thunder"),
	LACK_WEATHER_SET("permissions.weather_set","control the weather"),
	LACK_WEATHER_ZAP("permissions.weather_zap","summon lightning"),
	LACK_TELEPORT("permissions.teleport","teleport"),
	LACK_TELEPORT_TARGET("permissions.teleport_target","teleport {target}"),
	LACK_TELEPORT_TO("permissions.teleport_to","teleport to {destination}"),
	LACK_TELEPORT_INTO("permissions.teleport_into","teleport into world '{destination}'"),
	LACK_TELEPORT_FROM("permissions.teleport_from","teleport out of world '{destination}'"),
	LACK_TELEPORT_MASS("permissions.teleport_mass","teleport en masse"),
	LACK_SETSPAWN_WORLD("permissions.setspawn_world","set the spawn location"),
	LACK_SETSPAWN_OTHER("permissions.setspawn_other","set someone else's home location"),
	LACK_SETSPAWN_SELF("permissions.setspawn_self","set your home location"),
	LACK_SETSPAWN_TO("permissions.setspawn_to","set the spawn location to {destination}"),
	LACK_SETSPAWN_INTO("permissions.setspawn_into","set the spawn location of world '{destination}'"),
	LACK_SETSPAWN_FROM("permissions.setspawn_from","set the spawn location from world '{destination}'"),
	LACK_INSTANT("permissions.instant", "{action} so soon after the last time"),
	LACK_ADMIN_RELOAD("permissions.admin_reload", "reload the General configuration"),
	LACK_ADMIN_ITEM("permissions.admin_item", "edit the item aliases"),
	LACK_ADMIN_SAVE("permissions.admin_save", "save the General configuration"),
	LACK_ADMIN_RESTRICT("permissions.admin_restrict", "make a permission op-only"),
	LACK_ADMIN_RELEASE("permissions.admin_release", "make a permission not op-only"),
	LACK_ADMIN_KIT("permissions.admin_kit", "edit the kit definitions"),
	LACK_ADMIN_ECONOMY("permissions.admin_economy", "edit the command costs"),
	LACK_ADMIN_SET("permissions.admin_set", "edit configuration values"),
	LACK_DEOP("permissions.deop", "deop players"),
	LACK_OP("permissions.op", "op players"),
	LACK_KICK("permissions.kick", "kick players"),
	// Miscellaneous common messages
	MISC_BAD_PLAYER("misc.bad-player", "{rose}There is no player named {white}{name}{rose}."),
	MISC_BAD_WORLD("misc.bad-world", "{rose}There is no world named {white}{name}{rose}."),
	MISC_BAD_NUMBER("misc.bad-num", "{rose}Invalid number {white}{num}{rose}."),
	MISC_LOCATION("misc.location", "({x}, {y}, {z}) facing ({yaw}, {pitch})"),
	ECONOMY_SHOW_COST("econ.show-cost", "{yellow}That would cost {white}{cost}{yellow}."),
	ECONOMY_PAY("econ.pay", "{yellow}You pay {white}{cost}{yellow}."),
	ECONOMY_EARN("econ.earn", "{yellow}You have earned {white}{income}{yellow}!"),
	MISC_KICKED("misc.kicked", "Kicked by administrator"),
	MISC_KICKING("misc.kicking", "Kicking player {player}: {reason}"),
	MISC_OPPING("misc.opping", "Promoted players to operator: {ops}"),
	MISC_DEOPPING("misc.deopping", "Demoted players from operator: {ops}"),
	MISC_OPPED("misc.opped", "You are now op!"),
	MISC_DEOPPED("misc.deopped", "You are no longer op."),
	;
	
	private static Configuration config;
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
		return config.getString(language + "." + node, fmt);
	}
	
	public static Configuration setLanguage(String lang, File folder, String file) {
		language = lang;
		config = new Configuration(new File(folder, file));
		config.load();
		return config;
	}
	
	static {
		for(LanguageText lang : values())
			byNode.put(lang.node, lang);
	}
	
	public static LanguageText byNode(String n) {
		return byNode.get(n.toUpperCase());
	}
}
