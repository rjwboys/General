
package net.craftstars.general.command;

import net.craftstars.general.General;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class whoCommand extends GeneralCommand {
    private String name;
    private String displayName;
    private String bar;
    private String location;
    private String world;

    @Override
    public boolean fromPlayer(General plugin, Player sender, Command command, String commandLabel,
            String[] args) {
        if(args.length < 1 || commandLabel.equalsIgnoreCase("whoami")) {
            // No argument, using sender as target.
            this.getPlayerInfo(sender);
        } else if(args.length >= 1) {
            if(args[0].equals("help"))
                return false;
            if(Toolbox.lacksPermission(plugin, sender, "general.who")) return true;
            Player who = Toolbox.getPlayer(args[0], sender);
            if(who == null) return true;
            this.getPlayerInfo(who);
        } else return false;
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
        // TODO: AFK System [Plutonium239]
        Messaging.send(sender, "&6 -&e Status: &f" + "Around.");
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
    }

    @Override
    public boolean fromConsole(General plugin, CommandSender sender, Command command,
            String commandLabel, String[] args) {
        if(args.length != 1) return false;
        if(args[0].equals("help"))
            return false;
        Player who = Toolbox.getPlayer(args[0], sender);
        if(who == null) return true;
        this.getPlayerInfo(who);
        showPlayerInfo(sender);
        return true;
    }

}
