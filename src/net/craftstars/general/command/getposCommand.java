
package net.craftstars.general.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.General;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

public class getposCommand extends GeneralCommand {
    private enum Alias {
        MAIN, COMPASS, WHERE
    };

    @Override
    public boolean fromPlayer(General plugin, Player sender, Command command, String commandLabel,
            String[] args) {
        if(Toolbox.lacksPermission(plugin, sender, "general.getpos")) return true;
        if(args.length == 0) {
            showPos(sender, sender, commandLabel);
            return true;
        } else if(args.length == 1) {
            if(Toolbox.equalsOne(args[0], "compass", "coords")) {
                showPos(sender, sender, args[0]);
                return true;
            }
            if(Toolbox.lacksPermission(plugin, sender, "general.getpos.other")) return true;
            Player who = Toolbox.getPlayer(args[0], sender);
            if(who != null) showPos(sender, who, commandLabel);
            return true;
        } else if(args.length == 2) {
            if(Toolbox.lacksPermission(plugin, sender, "general.getpos.other")) return true;
            Player who = Toolbox.getPlayer(args[1], sender);
            if(who != null) 
                showPos(sender, who, commandLabel.equalsIgnoreCase("getpos") ? args[0] : commandLabel);
            return true;
        } else return false;
    }

    private void showPos(CommandSender sender, Player whose, String subcmd) {
        Alias which = Alias.MAIN;
        if(Toolbox.equalsOne(subcmd,"compass"))
            which = Alias.COMPASS;
        else if((Toolbox.equalsOne(subcmd,"where","pos","coords")))
            which = Alias.WHERE;
        double degrees = getRotation(whose);
        switch(which) {
        case MAIN:
        case WHERE:
            Messaging.send(sender, "Pos X: " + whose.getLocation().getX() + " Y: "
                    + whose.getLocation().getY() + " Z: " + whose.getLocation().getZ());
            if(which != Alias.MAIN) break;
            Messaging.send(whose, "Rotation: " + whose.getLocation().getYaw() + " Pitch: "
                    + whose.getLocation().getPitch());
        case COMPASS:
            Messaging.send(sender, "Compass: " + this.getDirection(degrees)
                    + (which == Alias.MAIN ? " (" + (Math.round(degrees * 10) / 10.0) + ")" : ""));
        }
    }

    private double getRotation(Player sender) {
        double degreeRotation = ( (sender.getLocation().getYaw() - 90) % 360);

        if(degreeRotation < 0) {
            degreeRotation += 360.0;
        }
        return degreeRotation;
    }

    private String getDirection(double degrees) {
        if(0 <= degrees && degrees < 22.5) return "N";
        else if(22.5 <= degrees && degrees < 67.5) return "NE";
        else if(67.5 <= degrees && degrees < 112.5) return "E";
        else if(112.5 <= degrees && degrees < 157.5) return "SE";
        else if(157.5 <= degrees && degrees < 202.5) return "S";
        else if(202.5 <= degrees && degrees < 247.5) return "SW";
        else if(247.5 <= degrees && degrees < 292.5) return "W";
        else if(292.5 <= degrees && degrees < 337.5) return "NW";
        else if(337.5 <= degrees && degrees < 360.0) return "N";
        else return "ERR";
    }

    @Override
    public boolean fromConsole(General plugin, CommandSender sender, Command command,
            String commandLabel, String[] args) {
        if(args.length < 1 || args.length > 2) return false;
        if(args.length == 1) {
            Player who = Toolbox.getPlayer(args[0], sender);
            if(who != null) showPos(sender, who, commandLabel);
            return true;
        } else if(args.length == 2) {
            Player who = Toolbox.getPlayer(args[1], sender);
            if(who != null) 
                showPos(sender, who, commandLabel.equalsIgnoreCase("getpos") ? args[0] : commandLabel);
            return true;
        } else return false;
    }
}
