
package net.craftstars.general.command;

import net.craftstars.general.CommandBase;
import net.craftstars.general.General;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class timeCommand extends CommandBase {
    private World world;

    @Override
    public boolean fromPlayer(General plugin, Player sender, Command command, String commandLabel,
            String[] args) {
        if(args.length < 1) {
            if(Toolbox.lacksPermission(plugin, sender, "general.time")) return true;
            // No arguments, assuming get current time for current world.
            this.world = sender.getWorld();
            showTime(sender);
            return true;
        } else if(args.length < 3) {
            if(Toolbox.lacksPermission(plugin, sender, "general.time")) return true;
            if(args[0].equalsIgnoreCase("help")) return Toolbox.USAGE;
            
            int i = args.length - 1;
            this.world = General.plugin.getServer().getWorld(args[i]);
            if(i == 0 && this.world != null) {
                showTime(sender);
                return true;
            }
            
            if(Toolbox.lacksPermission(plugin, sender, "general.time.set")) return true;
            String time = args[0];
            if(args.length == 1) this.world = sender.getWorld();
            if(this.world == null) {
                Toolbox.getWorld(args[i], sender);
                return true;
            }
            return setTime(sender, time);
        } else return Toolbox.USAGE;
    }

    private boolean setTime(CommandSender sender, String time) {
        // TODO: Add human-friendly time (=4pm, +4s, +4m, etc)
        if(time.equalsIgnoreCase("day")) {
            this.world.setTime(this.getStartTime());
            Messaging.send(sender,"Time set to day!");
            return true;
        } else if(time.equalsIgnoreCase("night")) {
            this.world.setTime(this.getStartTime() + 13800);
            Messaging.send(sender,"Time set to night!");
            return true;
        } else if(Toolbox.equalsOne(time, "dusk", "sunset", "evening")) {
            this.world.setTime(this.getStartTime() + 12000);
            Messaging.send(sender,"Time set to dusk!");
            return true;
        } else if(Toolbox.equalsOne(time, "dawn", "sunrise", "morning")) {
            this.world.setTime(this.getStartTime() + 22200);
            Messaging.send(sender,"Time set to dawn!");
            return true;
        } else if(Toolbox.equalsOne(time, "midday", "noon")) {
            this.world.setTime(this.getStartTime() + 6000);
            Messaging.send(sender,"Time set to dawn!");
            return true;
        } else if(Toolbox.equalsOne("midnight")) {
            this.world.setTime(this.getStartTime() + 18000);
            Messaging.send(sender,"Time set to dawn!");
            return true;
        } else if(time.startsWith("=")) {
            try {
                String t = time.substring(1);
                this.world.setTime(Long.valueOf(t));
                Messaging.send(sender,"Time set to " + t + " ticks!");
            } catch(Exception ex) {
                return Toolbox.USAGE;
            }
        } else if(time.startsWith("+")) {
            try {
                long now = this.world.getTime();
                String t = time.substring(1);
                this.world.setTime(now + Long.parseLong(t));
                Messaging.send(sender,"Time advanced by " + t + " ticks!");
            } catch(Exception ex) {
                return Toolbox.USAGE;
            }
        } else if(time.startsWith("-")) {
            try {
                long now = this.world.getTime();
                String t = time.substring(1);
                this.world.setTime(now - Long.parseLong(t));
                Messaging.send(sender,"Time setback by " + t + " ticks!");
            } catch(Exception ex) {
                return Toolbox.USAGE;
            }
        }
        return Toolbox.USAGE;
    }

    private void showTime(CommandSender sender) {
        int time = (int) this.world.getTime();
        Messaging.send(sender,"Time: " + this.getFriendlyTime(time) + " (" + time + ")");
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
    public boolean fromConsole(General plugin, CommandSender sender, Command command,
            String commandLabel, String[] args) {
        if(args.length < 1 || args.length > 2) return Toolbox.USAGE;
        else if(args.length == 1) {
            if(args[0].equalsIgnoreCase("help")) return Toolbox.USAGE;
            this.world = Toolbox.getWorld(args[0], sender);
            if(this.world != null) {
                showTime(sender);
            }
            return true;
        } else {
            String time = args[0];
            this.world = Toolbox.getWorld(args[1], sender);
            if(this.world == null) return true;
            return setTime(sender, time);
        }
    }
}
