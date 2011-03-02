
package net.craftstars.general.util;

import net.craftstars.general.General;

import org.bukkit.entity.Player;

public class Toolbox {
    public static Player playerMatch(String name) {
        if(General.plugin.getServer().getOnlinePlayers().length < 1) {
            return null;
        }

        Player[] online = General.plugin.getServer().getOnlinePlayers();

        Player lastPlayer = null;

        for(Player player : online) {
            String playerName = player.getName();
            String playerDisplayName = player.getDisplayName();

            if(playerName.equalsIgnoreCase(name)) {
                lastPlayer = player;

                break;
            } else if(playerDisplayName.equalsIgnoreCase(name)) {
                lastPlayer = player;

                break;
            }
            if(playerName.toLowerCase().indexOf(name.toLowerCase()) != -1) {
                if(lastPlayer != null) {
                    return null;
                }

                lastPlayer = player;
            } else if(playerDisplayName.toLowerCase().indexOf(name.toLowerCase()) != -1) {
                if(lastPlayer != null) {
                    return null;
                }

                lastPlayer = player;
            }
        }

        return lastPlayer;
    }

    public static String repeat(char c, int i) {
        String tst = "";
        for(int j = 0; j < i; j++) {
            tst = tst + c;
        }

        return tst;
    }

    public static String string(int i) {
        return String.valueOf(i);
    }

    /**
     * Turns "SomeName" into "Some Name" or "MyABC" into "My ABC". (Inserts a space before a capital
     * letter unless it is at the beginning of the string or preceded by a capital letter.)
     */
    public static String camelToPhrase(String str) {
        String newStr = "";
        for(int i = 0; i < str.length(); i++) {
            if(i > 0 && Character.isUpperCase(str.charAt(i))
                    && !Character.isUpperCase(str.charAt(i - 1))) newStr += ' ';
            newStr += str.charAt(i);
        }

        return newStr;
    }
}
