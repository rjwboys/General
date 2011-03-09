package net.craftstars.general.command;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Formatter;
import java.util.Scanner;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.CommandBase;
import net.craftstars.general.General;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

public class generalCommand extends CommandBase {

    @Override
    public boolean fromConsole(General plugin, CommandSender sender, Command command,
            String commandLabel, String[] args) {
        if(args.length < 1) return Toolbox.USAGE;
        if(args[0].equalsIgnoreCase("reload")) {
            doReload(sender);
        } else if(args[0].equalsIgnoreCase("die")) {
            die(sender);
        } else if(args[0].equalsIgnoreCase("help")) {
            if(args.length == 1) {
                showHelp(sender, "console.help");
                return true;
            } else if(args.length == 2) {
                showHelp(sender, args[1] + ".help");
                return true;
            }
        } else if(args[0].equalsIgnoreCase("motd")) {
            showMotD(sender);
        }
        return Toolbox.USAGE;
    }

    @Override
    public boolean fromPlayer(General plugin, Player sender, Command command, String commandLabel,
            String[] args) {
        if(args.length < 1) return Toolbox.USAGE;
        if(args[0].equalsIgnoreCase("reload")) {
            if(Toolbox.lacksPermission(plugin, sender, "general.admin")) return true;
            doReload(sender);
        } else if(args[0].equalsIgnoreCase("help")) {
            if(args.length == 1) {
                showHelp(sender, "player.help");
                return true;
            } else if(args.length == 2) {
                showHelp(sender, args[1] + ".help");
                return true;
            }
        } else if(args[0].equalsIgnoreCase("motd")) {
            showMotD(sender);
        }
        return Toolbox.USAGE;
    }

    private void showHelp(CommandSender sender, String filename) {
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
        showFile(sender, f, false);
    }

    private static void showFile(CommandSender sender, Scanner f, boolean motd) {
        while(f.hasNextLine()) {
            String line = f.nextLine();
            if(motd) line = parseMotD(sender, line);
            do {
                StringBuilder splitter = new StringBuilder();
                splitter.append(line);
                int splitAt = 0;
                for(int i = Math.min(54, line.length() - 1); i >= 0; i--) {
                    char c = line.charAt(i);
                    if(c == ' ') {
                        splitAt = i + 1;
                        break;
                    }
                }
                if(line.length() > 54 && splitAt < line.length()) {
                    line = line.substring(splitAt);
                    splitter.delete(splitAt, splitter.length());
                }
                Messaging.send(sender, splitter.toString());
            } while(line.length() > 54);
        }
    }

    private static String parseMotD(CommandSender sender, String original) {
        return Messaging.argument(
                original,
                new String[]{
                        "++",
                        "+dname,+d,&dname;",
                        "+name,+n,&name;",
                        "+location,+l,&location;",
                        "+health,+h,&health;",
                        "+ip,+a,&ip;",
                        "+balance,+$,&balance;",
                        "+online,+c,&online;",
                        "+world,+w,&world;",
                        "+time,+t,&time;",
                        "~!@#$%^&*()"
                },
                new Object[]{
                        "~!@#$%^&*()",
                        getDisplayName(sender),
                        getName(sender),
                        getLocation(sender),
                        getHealth(sender),
                        getAddress(sender),
                        getBalance(sender),
                        General.plugin.getServer().getOnlinePlayers().length,
                        getWorld(sender),
                        getTime(sender),
                        "+"
                }
            );
    }
    
    private static String getAddress(CommandSender sender) {
        if(sender instanceof Player) return ((Player) sender).getAddress().getAddress().getHostAddress();
        return "127.0.0.1";
    }

    private static int getBalance(CommandSender sender) {
        // TODO: Get iConomy balance
        return 0;
    }

    private static double getHealth(CommandSender sender) {
        if(sender instanceof Player) return ((double) ((Player) sender).getHealth()) / 2.0;
        return 0;
    }

    private static long getTime(CommandSender sender) {
        if(sender instanceof Player) {
            long t = ((Player) sender).getWorld().getTime();
            General.logger.debug("Time is " + t);
            return t;
        }
        return 0;
    }

    private static String getLocation(CommandSender sender) {
        if(sender instanceof Player) {
            Formatter fmt = new Formatter();
            Location loc = ((Player) sender).getLocation();
            return fmt.format("(%d, %d, %d)", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()).toString();
        }
        return "null";
    }
    
    private static String getWorld(CommandSender sender) {
        if(sender instanceof Player) return ((Player) sender).getWorld().getName();
        return "null";
    }

    private static String getDisplayName(CommandSender sender) {
        if(sender instanceof Player) return ((Player) sender).getDisplayName();
        return "CONSOLE";
    }
    
    private static String getName(CommandSender sender) {
        if(sender instanceof Player) return ((Player) sender).getName();
        return "CONSOLE";
    }

    public static void showMotD(CommandSender sender) {
        File dataFolder = General.plugin.getDataFolder();
        if(!dataFolder.exists()) dataFolder.mkdirs();
        Scanner f;
        try {
            File helpFile = new File(dataFolder, "general.motd");
            f = new Scanner(helpFile);
        } catch(FileNotFoundException e) {
            Messaging.send(sender, "&rose;No message of the day available.");
            return;
        }
        showFile(sender, f, true);
    }

    private void doReload(CommandSender sender) {
        General.plugin.getPluginLoader().disablePlugin(General.plugin);
        General.plugin.getPluginLoader().enablePlugin(General.plugin);
        Messaging.send(sender, "&5General reloaded.");
    }

    private void die(CommandSender sender) {
        General.plugin.getPluginLoader().disablePlugin(General.plugin);
        Messaging.send(sender, "&5General unloaded.");
    }

}
