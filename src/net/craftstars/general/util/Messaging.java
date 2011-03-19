
package net.craftstars.general.util;

import net.craftstars.general.General;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * General 1.1 & Code from iConomy 2.x Copyright (C) 2011 Nijikokun <nijikokun@gmail.com>
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */

/**
 * Messaging.java <br />
 * <br />
 * Lets us do fancy pantsy things with colors, messages, and broadcasting :D!
 * 
 * @author Nijikokun <nijikokun@gmail.com>
 */
public class Messaging {
    /**
     * Converts a list of arguments into points.
     * 
     * @param original The original string necessary to convert inside of.
     * @param arguments The list of arguments, multiple arguments are seperated by commas for a
     *        single point.
     * @param points The point used to alter the argument.
     * 
     * @return <code>String</code> - The parsed string after converting arguments to variables
     *         (points)
     */
    public static String argument(String original, String[] arguments, Object[] points) {
        for(int i = 0; i < arguments.length; i++) {
            if(arguments[i].contains(",")) {
                for(String arg : arguments[i].split(",")) {
                    original = original.replace(arg, points[i].toString());
                }
            } else {
                original = original.replace(arguments[i], points[i].toString());
            }
        }

        return original;
    }

    /**
     * Parses the original string against color specific codes. This one converts &[code] to
     * §[code] <br />
     * <br />
     * Example: <blockquote>
     * 
     * <pre>
     * Messaging.parse(&quot;Hello &amp;2world!&quot;); // returns: Hello §2world!
     * </pre>
     * 
     * </blockquote>
     * 
     * @param original The original string used for conversions.
     * 
     * @return <code>String</code> - The parsed string after conversion.
     */
    public static String parse(String original) {
        original = colorize(original);
        original = original.replace("&&","~!@#$%^&*()")
                       .replaceAll("(&([a-fA-F0-9]))", "\u00A7$2")
                       .replace("~!@#$%^&*()", "&");
        return splitLines(original);
    }
    
    /**
     * Splits a message into lines of no more than 54 characters. Colour codes, as indicated
     * by §[0-9a-f], are not counted in the line length. Make sure you pass through colorize()
     * first to convert the colour codes to the § syntax.
     * 
     * Splitting at a space or hyphen will be preferred. Any newlines already present in the string
     * will be preserved. Colour codes will be duplicated at the beginning of wrapped lines.
     * 
     * @author Celtic Minstrel
     * @param original The string to split into lines.
     * @return The string with newlines inserted as required.
     */
    public static String splitLines(String original) {
        StringBuilder splitter = new StringBuilder(original);
        int splitAt = 0;
        int effectiveLen = 0;
        char lastColourCode = ' ';
        for(int i = 0; i < splitter.length(); i++) {
            if(splitter.charAt(i) == '\u00A7') { // §
                try {
                    char c = splitter.charAt(i + 1);
                    if((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F')) {
                        lastColourCode = c;
                        i++;
                        continue;
                    }
                } catch(IndexOutOfBoundsException x) {
                    
                }
            }
            effectiveLen++;
            char c = splitter.charAt(i);
            if(c == ' ' || c == '-') splitAt = i;
            if(effectiveLen > 60) {
                if(splitAt == 0) splitAt = i; // as a last resort, just split at the limit
                effectiveLen = i - splitAt;
                String toAdd = "\n";
                if(lastColourCode != ' ') {
                    toAdd += '\u00A7';
                    toAdd += lastColourCode;
                    i += 2;
                }
                splitter.insert(splitAt+1, toAdd);
                if(splitter.charAt(splitAt) == ' ')
                    splitter.deleteCharAt(splitAt);
                else i++;
                splitAt = 0;
            }
        }
        return splitter.toString();
    }

    /**
     * Converts color codes into the simoleon code. Sort of a HTML entity format color code tag.
     * <p>
     * Color codes allowed: black, navy, green, teal, red, purple, gold, silver, gray, blue, lime,
     * aqua, rose, pink, yellow, white.
     * </p>
     * Example: <blockquote
     * 
     * <pre>
     * Messaging.colorize(&quot;Hello &amp;green;world!&quot;); // returns: Hello §2world!
     * </pre>
     * 
     * </blockquote>
     * 
     * @param original Original string to be parsed against group of color names.
     * 
     * @return <code>String</code> - The parsed string after conversion.
     */
    public static String colorize(String original) {
        return original.replace("&black;", ChatColor.BLACK.toString())
                       .replace("&navy;", ChatColor.DARK_BLUE.toString())
                       .replace("&green;", ChatColor.DARK_GREEN.toString())
                       .replace("&teal;", ChatColor.DARK_AQUA.toString())
                       .replace("&red;", ChatColor.DARK_RED.toString())
                       .replace("&purple;", ChatColor.DARK_PURPLE.toString())
                       .replace("&gold;", ChatColor.GOLD.toString())
                       .replace("&silver;", ChatColor.GRAY.toString())
                       .replace("&gray;", ChatColor.DARK_GRAY.toString())
                       .replace("&grey;", ChatColor.DARK_GRAY.toString())
                       .replace("&blue;", ChatColor.BLUE.toString())
                       .replace("&lime;", ChatColor.GREEN.toString())
                       .replace("&aqua;", ChatColor.AQUA.toString())
                       .replace("&rose;", ChatColor.RED.toString())
                       .replace("&pink;", ChatColor.LIGHT_PURPLE.toString())
                       .replace("&yellow;", ChatColor.YELLOW.toString())
                       .replace("&white;", ChatColor.WHITE.toString());
    }

    /**
     * Helper function to assist with making brackets. Why? Dunno, lazy.
     * 
     * @param message The message inside of brackets.
     * 
     * @return <code>String</code> - The message inside [brackets]
     */
    public static String bracketize(String message) {
        return "[" + message + "]";
    }

    /**
     * Sends a message to a specific player. <br />
     * <br />
     * Example: <blockquote>
     * 
     * <pre>
     * Messaging.send(player, &quot;This will go to the player saved.&quot;);
     * </pre>
     * 
     * </blockquote>
     * 
     * @param player Player we are sending the message to.
     * @param message The message to be sent.
     */
    public static void send(CommandSender player, String message) {
        message = parse(message);
        for(String line : message.split("[\n\r]"))
            player.sendMessage(line);
    }

    /**
     * Broadcast a message to every player online.
     * 
     * @param message - The message to be sent.
     */
    public static void broadcast(String message) {
        for(Player p : General.plugin.getServer().getOnlinePlayers()) {
            p.sendMessage(parse(message));
        }
    }
}
