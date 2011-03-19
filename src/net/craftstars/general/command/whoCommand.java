
package net.craftstars.general.command;

import net.craftstars.general.CommandBase;
import net.craftstars.general.General;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class whoCommand extends CommandBase {
    private String name;
    private String displayName;
    private String bar;
    private String location;
    private String world;
    private String awayReason;
    private boolean isAway;

    @Override
    public boolean fromPlayer(General plugin, Player sender, Command command, String commandLabel,
            String[] args) {
        if(args.length < 1 || commandLabel.equalsIgnoreCase("whoami")) {
            // No argument, using sender as target.
            this.getPlayerInfo(sender);
        } else if(args.length >= 1) {
            if(args[0].equals("help"))
                return Toolbox.USAGE;
            if(Toolbox.lacksPermission(plugin, sender, "view info on users other than yourself", "general.who", "general.basic")) return true;
            Player who = Toolbox.getPlayer(args[0], sender);
            if(who == null) return true;
            this.getPlayerInfo(who);
        } else return Toolbox.USAGE;
        showPlayerInfo(sender);
        return true;
    }

    private void showPlayerInfo(CommandSender sender) {
        Messaging.send(sender, "&f------------------------------------------------");
        Messaging.send(sender, "&e Player &f[" + this.name + "]&e Info");
        Messaging.send(sender, "&f------------------------------------------------");
        Messaging.send(sender, "&6 Username: &f" + this.name);
        Messaging.send(sender, "&6 DisplayName: &f" + this.displayName);
        if(General.plugin.config.getBoolean("playerlist.show-health", true))
            Messaging.send(sender, "&6 -&e Health: &f" + this.bar);
        if(General.plugin.config.getBoolean("playerlist.show-coords", true))
            Messaging.send(sender, "&6 -&e Location: &f" + this.location);
        if(General.plugin.config.getBoolean("playerlist.show-world", false))
            Messaging.send(sender, "&6 -&e World: &f" + this.world);
        if(this.isAway)
            Messaging.send(sender, "&6 -&e Status: &f" + "Away (" + this.awayReason + ").");
        else
            Messaging.send(sender, "&6 -&e Status: &fAround.");
        Messaging.send(sender, "&f------------------------------------------------");
    }

    private void getPlayerInfo(Player player) {
        this.name = player.getName();
        this.displayName = player.getDisplayName();

        int health = player.getHealth();
        int length = 10;
        int bars = Math.round(health / 2);
        int remainder = length - bars;
        String hb_color = ( (bars >= 7) ? "&2" : ( (bars < 7 && bars >= 3) ? "&e"
                : ( (bars < 3) ? "&c" : "&2")));
        this.bar = " &f[" + hb_color + Toolbox.repeat('|', bars) + "&7"
                + Toolbox.repeat('|', remainder) + "&f]";

        int x = (int) player.getLocation().getX();
        int y = (int) player.getLocation().getY();
        int z = (int) player.getLocation().getZ();
        this.location = x + "x, " + y + "y, " + z + "z";
        this.world = player.getWorld().getName();
        this.isAway = General.plugin.isAway(player);
        this.awayReason = General.plugin.whyAway(player).trim();
    }

    @Override
    public boolean fromConsole(General plugin, CommandSender sender, Command command,
            String commandLabel, String[] args) {
        if(args.length != 1) return Toolbox.USAGE;
        if(args[0].equals("help"))
            return Toolbox.USAGE;
        Player who = Toolbox.getPlayer(args[0], sender);
        if(who == null) return true;
        this.getPlayerInfo(who);
        showPlayerInfo(sender);
        return true;
    }

}
