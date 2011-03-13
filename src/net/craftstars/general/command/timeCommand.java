
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
            showTicks = General.plugin.config.getBoolean("time.show-ticks", true);
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
    private boolean showTicks;
    
    private String formatTime(long time, TimeFormat fmt) {
        String suffix = "", formatString = "";
        long minutes = time % 1000, ticks = time;
        minutes = Math.round(((double) minutes) * 0.06);
        time /= 1000;
        time += 6;
        while(time > 24) time -= 24;
        switch(fmt){
        case TWELVE_HOUR:
            if(time > 12) {
                time -= 12;
                suffix = (time == 12) ? "am" : "pm";
            } else if(time < 12) {
                suffix = "am";
            } else {
                suffix = "pm";
            }
            formatString = "%d";
        break;
        case TWENTY_FOUR_HOUR:
            if(time == 24) time = 0;
            suffix = "h";
            formatString = "%02d";
        break;
        }
        formatString += ":%02d%s";
        Formatter fmtr = new Formatter();
        if(showTicks) return fmtr.format(formatString + " (%d)", time, minutes, suffix, ticks).toString();
        else return fmtr.format(formatString, time, minutes, suffix).toString();
    }
    
    private long twelveHourToTicks(long hour, boolean isPM) {
        if(isPM) hour += 12;
        if(hour == 12) hour = 0;
        else if(hour == 24) hour = 12;
        return twentyFourHourToTicks(hour);
    }
    
    private long twentyFourHourToTicks(long hour) {
        hour += 18;
        while(hour >= 24) hour -= 24;
        return hour;
    }

    private static Pattern pat12hr = Pattern.compile("(0?[1-9]|1[0-2]):([0-5]?[0-9])([aApP][mM])");
    private static Pattern pat24hr = Pattern.compile("([01]?[0-9]|2[0-3]?):([0-5]?[0-9])[hH]?");
    private static Pattern patShort = Pattern.compile("([01]?[0-9]|2[0-3]?)([hH]|[aApP][mM])");
    private long extractTime(String time) {
        Matcher m;
        boolean matched = false;
        long hour = 0, minutes = 0;
        // First try 24-hour
        m = pat24hr.matcher(time);
        if(m.matches()) {
            hour = twentyFourHourToTicks(Long.valueOf(m.group(1)));
            minutes = Long.valueOf(m.group(2));
            matched = true;
        }
        m = pat12hr.matcher(time);
        if(m.matches()) {
            String suffix = m.group(3);
            hour = twelveHourToTicks(Long.valueOf(m.group(1)), suffix.equalsIgnoreCase("pm"));
            minutes = Long.valueOf(m.group(2));
            matched = true;
        }
        if(!matched) {
            m = patShort.matcher(time);
            if(m.matches()) {
                String suffix = m.group(2);
                if(suffix.equalsIgnoreCase("h"))
                    hour = twentyFourHourToTicks(Long.valueOf(m.group(1)));
                else hour = twelveHourToTicks(Long.valueOf(m.group(1)), suffix.equalsIgnoreCase("pm"));
            } else throw new NumberFormatException();
        }
        hour *= 1000;
        minutes = Math.round(((double) minutes) / 0.06);
        return hour + minutes;
    }
    
    private String formatDuration(long time) {
        long minutes = time % 1000, hours = time / 1000;
        minutes = Math.round(((double) minutes) * 0.06);
        Formatter fmtr = new Formatter();
        if(minutes + hours == 0) return fmtr.format("%d ticks", time).toString();
        if(minutes == 0) return fmtr.format("%d hours", hours).toString();
        if(hours == 0) return fmtr.format("%d minutes", minutes).toString();
        return fmtr.format("%d hours and %d minutes", hours, minutes).toString();
    }
    
    //private static Pattern patDuration = Pattern.compile("(\\d*[hH])(\\d*[mM])");
    private static Pattern patHour = Pattern.compile("(\\d+)[hH].*");
    private static Pattern patMin  = Pattern.compile(".*?(\\d+)[mM]");
    private long extractDuration(String time) {
        if(time.isEmpty()) return 0;
        Matcher m;
        boolean matched = false;
        long hours = 0, minutes = 0;
        m = patHour.matcher(time);
        if(m.matches()) {
            hours = Long.valueOf(m.group(1));
            matched = true;
        }
        m = patMin.matcher(time);
        if(m.matches()) {
            minutes = Long.valueOf(m.group(1));
            matched =  true;
        }
        if(matched) return (hours * 1000) + Math.round(((double) minutes) / 0.06);
        return Long.valueOf(time);
    }
    
    private boolean setTime(CommandSender sender, String time) {
        // TODO: Add human-friendly time (=4pm, +4s, +4m, etc)
        if(time.equalsIgnoreCase("day")) { // 6am
            this.world.setTime(this.getStartTime());
            Messaging.send(sender,"Time set to day: " + formatTime(0,currentFormat) + "!");
            return true;
        } else if(time.equalsIgnoreCase("night")) { // 7:48pm
            this.world.setTime(this.getStartTime() + 13800);
            Messaging.send(sender,"Time set to night: " + formatTime(13800,currentFormat) + "!");
            return true;
        } else if(Toolbox.equalsOne(time, "dusk", "sunset", "evening")) { // 6pm
            this.world.setTime(this.getStartTime() + 12000);
            Messaging.send(sender,"Time set to dusk: " + formatTime(12000,currentFormat) + "!");
            return true;
        } else if(Toolbox.equalsOne(time, "dawn", "sunrise", "morning")) { // 4:12am
            this.world.setTime(this.getStartTime() + 22200);
            Messaging.send(sender,"Time set to dawn: " + formatTime(22200,currentFormat) + "!");
            return true;
        } else if(Toolbox.equalsOne(time, "midday", "noon")) { // 12am
            this.world.setTime(this.getStartTime() + 6000);
            Messaging.send(sender,"Time set to noon: " + formatTime(6000,currentFormat) + "!");
            return true;
        } else if(Toolbox.equalsOne(time, "midnight")) { // 12pm
            this.world.setTime(this.getStartTime() + 18000);
            Messaging.send(sender,"Time set to midnight: " + formatTime(18000,currentFormat) + "!");
            return true;
        } else if(time.startsWith("+")) {
            try {
                long now = this.world.getTime();
                long ticks = extractDuration(time.substring(1));
                this.world.setTime(now + ticks);
                Messaging.send(sender,"Time advanced by " + formatDuration(ticks) + "!");
            } catch(NumberFormatException x) {
                Messaging.send(sender, "&rose;Invalid duration format.");
            } catch(Exception ex) {
                ex.printStackTrace();
                return Toolbox.USAGE;
            }
        } else if(time.startsWith("-")) {
            try {
                long now = this.world.getTime();
                long ticks = extractDuration(time.substring(1));
                this.world.setTime(now - ticks);
                Messaging.send(sender,"Time set back by " + formatDuration(ticks) + "!");
            } catch(NumberFormatException x) {
                Messaging.send(sender, "&rose;Invalid duration format.");
            } catch(Exception ex) {
                ex.printStackTrace();
                return Toolbox.USAGE;
            }
        } else {
            if(time.startsWith("=")) time = time.substring(1);
            try {
                long ticks = extractTime(time);
                this.world.setTime(ticks);
                Messaging.send(sender,"Time set to " + formatTime(ticks,currentFormat) + "!");
            } catch(NumberFormatException x) {
                Messaging.send(sender, "&rose;Invalid time format.");
            } catch(Exception ex) {
                return Toolbox.USAGE;
            }
        }
        return Toolbox.USAGE;
    }

    private void showTime(CommandSender sender) {
        int time = (int) this.world.getTime();
        Messaging.send(sender,"Current Time: " + this.getFriendlyTime(time) + " " + formatTime(time,currentFormat));
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
