
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
    public static String argument(String original, String[] arguments, String[] points) {
        for(int i = 0; i < arguments.length; i++) {
            if(arguments[i].contains(",")) {
                for(String arg : arguments[i].split(",")) {
                    original = original.replace(arg, points[i]);
                }
            } else {
                original = original.replace(arguments[i], points[i]);
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
        return original.replaceAll("(&([a-f0-9]))", "\u00A7$2").replace("&&", "&");
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
     * Messaging.colorize(&quot;Hello &amp;green;world!&quot;); // returns: Hello �2world!
     * </pre>
     * 
     * </blockquote>
     * 
     * @param original Original string to be parsed against group of color names.
     * 
     * @return <code>String</code> - The parsed string after conversion.
     */
    public static String colorize(String original) {
        return original.replace("&black;", ChatColor.BLACK.toString()).replace("&navy;",
                ChatColor.DARK_BLUE.toString()).replace("&green;", ChatColor.DARK_GREEN.toString())
                .replace("&teal;", ChatColor.DARK_AQUA.toString()).replace("&red;",
                        ChatColor.DARK_RED.toString()).replace("&purple;",
                        ChatColor.DARK_PURPLE.toString()).replace("&gold;",
                        ChatColor.GOLD.toString()).replace("&silver;", ChatColor.GRAY.toString())
                .replace("&gray;", ChatColor.DARK_GRAY.toString()).replace("&grey;",
                        ChatColor.DARK_GRAY.toString())
                .replace("&blue;", ChatColor.BLUE.toString()).replace("&lime;",
                        ChatColor.GREEN.toString()).replace("&aqua;", ChatColor.AQUA.toString())
                .replace("&rose;", ChatColor.RED.toString()).replace("&pink;",
                        ChatColor.LIGHT_PURPLE.toString()).replace("&yellow;",
                        ChatColor.YELLOW.toString()).replace("&white;", ChatColor.WHITE.toString());
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
    public static void send(@SuppressWarnings("hiding") CommandSender player, String message) {
        player.sendMessage(parse(message));
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
