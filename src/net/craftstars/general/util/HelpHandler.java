package net.craftstars.general.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import me.taylorkelly.help.Help;
import net.craftstars.general.General;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class HelpHandler {
    public static boolean gotHelp;

    public static void setup() {
        Plugin test = General.plugin.getServer().getPluginManager().getPlugin("Help");
        if (test != null) {
            Help helpPlugin = ((Help) test);
            // TODO: Some of the help should be moved to /<cmd> help; spawn and teleport, in particular.
            ////////////////////////////--------------------------------------------------
            helpPlugin.registerCommand("playerlist", "Lists online players. Alias: online", General.plugin,
                    "general.playerlist", "general.basic");
            helpPlugin.registerCommand("who ([player])",
                    "Displays information about a player. Aliases: playerinfo, whois", General.plugin,
                    "general.who", "general.basic");
            helpPlugin.registerCommand("whoami",
                    "Displays information about you.", General.plugin, "general.who");
            helpPlugin.registerCommand("time ([world])", "Displays the current time in [world].",
                    General.plugin, "general.time", "general.basic");
            helpPlugin.registerCommand("time help", "Shows syntax for setting the time.", General.plugin,
                    "general.time.set");
            helpPlugin.registerCommand("give [item](:[variant]) ([amount]) ([player])",
                    "Gives [player] [amount] of [item]. Aliases: i(tem)", General.plugin, "general.give");
            helpPlugin.registerCommand("items [item1] [item2] ... [itemN]",
                    "Give yourself several different items at once. You get one of each item.", General.plugin, "general.give.mass");
            helpPlugin.registerCommand("getpos ([player])",
                    "Get the current position of [player].", General.plugin, "general.getpos", "general.basic");
            helpPlugin.registerCommand("compass", "Show your direction.", General.plugin, "general.getpos");
            helpPlugin.registerCommand("where ([player])",
                    "Show the location of [player]; less detailed form of /getpos. Aliases: pos, coords", General.plugin,
                    "general.getpos");
            helpPlugin.registerCommand("tell [player] [message]",
                    "Whisper to a player. Aliases: msg, pm, whisper", General.plugin, "general.tell", "general.basic");
            helpPlugin.registerCommand("spawn ([player])",
                    "Teleports [player] to the spawn location.", General.plugin, "general.spawn");
            helpPlugin.registerCommand("spawn ([world]) show",
                    "Displays the current spawn location in [world].", General.plugin, "general.spawn");
            helpPlugin.registerCommand("spawn set ([player])",
                    "Sets the spawn location in [player]'s world to [player]'s location.", General.plugin,
                    "general.spawn.set");
            helpPlugin.registerCommand("spawn ([world]) set [x] [y] [z]", ".", General.plugin,
                    "general.spawn.set");
            helpPlugin.registerCommand("teleport [player]",
                    "Teleport to the location of [player]. Alias: tele", General.plugin, "general.teleport");
            helpPlugin.registerCommand("teleport [player] [to-player]",
                    "Teleports [player] to the location of [to-player]. Alias: tele", General.plugin,
                    "general.teleport.other");
            helpPlugin.registerCommand("teleport [player1],[player2],... [to-player]",
                    "Teleports several players to the location of [to-player]. Alias: tele", General.plugin,
                    "general.teleport.other.mass");
            helpPlugin.registerCommand("teleport * [player]",
                    "Teleports everyone to the location of [player]. Alias: tele", General.plugin,
                    "general.teleport.other.mass");
            helpPlugin.registerCommand("teleport|[x] [y] [z]",
                    "Teleport to the specified coordinates. Alias: tele", General.plugin, "general.teleport.coords");
            helpPlugin.registerCommand("s(ummon) [player]",
                    "Teleports a player to your location. Aliases: tphere, teleporthere", General.plugin, "general.teleport.other");
            helpPlugin.registerCommand("clear ([player]) (pack|quickbar|armo(u)r|all)",
                    "Clears [player]'s inventory.", General.plugin, "general.clear");
            helpPlugin.registerCommand("take [item](:[variant]) ([amount]) ([player])",
                    "Deletes something from [player]'s inventory.", General.plugin, "general.take");
            helpPlugin.registerCommand("heal ([player]) ([amount])",
                    "Heals [player] by [amount] hearts (0-10). If [amount] is omitted, full heal.",
                    General.plugin, "general.heal");
            helpPlugin.registerCommand("general reload", "Reloads the configuration files.",
                    General.plugin, "OP", "general.admin");
            helpPlugin.registerCommand("general die", "Kills the plugin.", General.plugin, "OP",
                    "general.admin");
            helpPlugin.registerCommand("general motd", "Displays the message of the day.", General.plugin);
            helpPlugin.registerCommand("mob(spawn) [mob](;[mount])", "Spawns a [mob] riding a [mount]. " +
            		"Both [mob] and [mount] are of the form [name](:[data]), where [data] is slime size or sheep colour.",
                    General.plugin, "general.mobspawn");
            helpPlugin.registerCommand("help General", "Help for the General plugin.", General.plugin, true);
            helpPlugin.registerCommand("away [reason]", "Sets your away status. Aliases: afk", General.plugin,
                    "general.away", "general.basic");
            helpPlugin.registerCommand("kit [kit]", "Gives you the [kit], or shows a list of available kits.",
                    General.plugin, "general.kit");
            General.logger.info("[Help " + helpPlugin.getDescription().getVersion() + "] support enabled.");
            gotHelp = true;
        } else {
            General.logger.warn("[Help] isn't detected. No /help support; instead use /general help");
            gotHelp = false;
        }
    }

    public static void showHelp(CommandSender sender, String filename) {
        File dataFolder = General.plugin.getDataFolder();
        if(!dataFolder.exists()) dataFolder.mkdirs();
        Scanner f;
        try {
            File helpFile = new File(dataFolder, filename);
            f = new Scanner(helpFile);
        } catch(FileNotFoundException e) {
            Messaging.send(sender, "&rose;Help topic unavailable.");
            return;
        }
        Toolbox.showFile(sender, f, false);
    }

}
