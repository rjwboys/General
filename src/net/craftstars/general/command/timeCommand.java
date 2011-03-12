
package net.craftstars.general.command;

import java.util.Formatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    
    public timeCommand() {
        currentFormat = TimeFormat.TWELVE_HOUR;
        try {
            boolean in24hr = General.plugin.config.getBoolean("time.format-24-hour", false);
            if(in24hr) currentFormat = TimeFormat.TWENTY_FOUR_HOUR;
        } catch(Exception x) {
            
        }
    }
    
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
            if(args[0].equalsIgnoreCase("help")) {
                showHelp(sender);
                return true;
            }
            
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

    private void showHelp(CommandSender sender) {
        Messaging.send(sender, "&c/time day|night|nood|midday|midnight");
        Messaging.send(sender, "&c/time dawn|sunrise|morning|dusk|sunset|evening");
        Messaging.send(sender, "&c/time +&7[ticks]&f : Fast-forward time.");
        Messaging.send(sender, "&c/time -&7[ticks]&f : Rewind time.");
        Messaging.send(sender, "&c/time +&7[ticks]&f : Set time.");
    }
    
    private enum TimeFormat {TWENTY_FOUR_HOUR, TWELVE_HOUR};
    private TimeFormat currentFormat;
    
    private String formatTime(long time, TimeFormat fmt) {
        String suffix = "";
        long minutes = time % 1000;
        minutes *= 0.06;
        time /= 1000;
        time += 6;
        while(time > 24) time -= 12;
        switch(fmt){
        case TWELVE_HOUR:
            if(time > 12) {
                time -= 12;
                suffix = (time == 12) ? "am" : "pm";
            } else if(time < 12) {
                suffix = "am";
            } else suffix = "pm";
        break;
        case TWENTY_FOUR_HOUR:
            if(time == 24) time = 0;
            suffix = "h";
        break;
        }
        Formatter fmtr = new Formatter();
        return fmtr.format("%d:%02d%s", time, minutes, suffix).toString();
    }

    private static Pattern pat12hr = Pattern.compile("(\\d\\d?):(\\d\\d)([aApP][mM])");
    private static Pattern pat24hr = Pattern.compile("(\\d\\d?):(\\d\\d)[hH]?");
    private long extractTime(String time) {
        Matcher m;
        boolean matched = false;
        long hour = 0, minutes = 0;
        // First try 24-hour
        m = pat24hr.matcher(time);
        if(m.matches()) {
            hour = Long.valueOf(m.group(1)) - 6;
            minutes = Long.valueOf(m.group(2));
            matched = true;
        }
        m = pat12hr.matcher(time);
        if(m.matches()) {
            hour = Long.valueOf(m.group(1));
            minutes = Long.valueOf(m.group(2));
            String suffix = m.group(3);
            if(suffix.equalsIgnoreCase("pm")) hour += 6;
            else hour -= 6;
            matched = true;
        }
        if(!matched) throw new NumberFormatException();
        if(hour <= 0) hour += 24;
        hour *= 1000;
        minutes /= 0.06;
        return hour + minutes;
    }
    
    private boolean setTime(CommandSender sender, String time) {
        // TODO: Add human-friendly time (=4pm, +4s, +4m, etc)
        if(time.equalsIgnoreCase("day")) { // 6am
            this.world.setTime(this.getStartTime());
            Messaging.send(sender,"Time set to day (" + formatTime(0,currentFormat) + ")!");
            return true;
        } else if(time.equalsIgnoreCase("night")) { // 7:48pm
            this.world.setTime(this.getStartTime() + 13800);
            Messaging.send(sender,"Time set to night (" + formatTime(13800,currentFormat) + "!");
            return true;
        } else if(Toolbox.equalsOne(time, "dusk", "sunset", "evening")) { // 6pm
            this.world.setTime(this.getStartTime() + 12000);
            Messaging.send(sender,"Time set to dusk (" + formatTime(12000,currentFormat) + "!");
            return true;
        } else if(Toolbox.equalsOne(time, "dawn", "sunrise", "morning")) { // 4:12am
            this.world.setTime(this.getStartTime() + 22200);
            Messaging.send(sender,"Time set to dawn (" + formatTime(22200,currentFormat) + "!");
            return true;
        } else if(Toolbox.equalsOne(time, "midday", "noon")) { // 12am
            this.world.setTime(this.getStartTime() + 6000);
            Messaging.send(sender,"Time set to noon (" + formatTime(6000,currentFormat) + "!");
            return true;
        } else if(Toolbox.equalsOne(time, "midnight")) { // 12pm
            this.world.setTime(this.getStartTime() + 18000);
            Messaging.send(sender,"Time set to midnight (" + formatTime(18000,currentFormat) + "!");
            return true;
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
                Messaging.send(sender,"Time set back by " + t + " ticks!");
            } catch(Exception ex) {
                return Toolbox.USAGE;
            }
        } else {
            if(time.startsWith("=")) time = time.substring(1);
            try {
                long ticks = extractTime(time);
                this.world.setTime(ticks);
                Messaging.send(sender,"Time set to " + formatTime(ticks,currentFormat) + " (" + ticks + ")!");
            } catch(Exception ex) {
                return Toolbox.USAGE;
            }
        }
        return Toolbox.USAGE;
    }

    private void showTime(CommandSender sender) {
        int time = (int) this.world.getTime();
        Messaging.send(sender,"Time: " + this.getFriendlyTime(time) + " " + formatTime(time,TimeFormat.TWELVE_HOUR) + " (" + time + ")");
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
            if(args[0].equalsIgnoreCase("help")) {
                showHelp(sender);
                return true;
            }
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
