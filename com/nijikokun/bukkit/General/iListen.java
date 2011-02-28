package com.nijikokun.bukkit.General;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import net.minecraft.server.WorldServer;
import org.bukkit.World;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
//import com.nijikokun.bukkit.iConomy.iConomy;


/**
 * General 1.1 & Code from iConomy 2.x
 * Coded while listening to Avenged Sevenfold - A little piece of heaven <3
 * Copyright (C) 2011  Nijikokun <nijikokun@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * iListen.java
 * <br /><br />
 * Listens for calls from hMod, and reacts accordingly.
 * 
 * @author Nijikokun <nijikokun@gmail.com>
 */
public class iListen extends PlayerListener {

    private static final Logger log = Logger.getLogger("Minecraft");
    private ArrayList<String> lines = new ArrayList<String>();

    /*
     * Miscellaneous things required.
     */
    public HashMap<Player, String> AFK = new HashMap<Player, String>();
    public List<String> Commands = new ArrayList<String>();
    public static General plugin;
    public WorldServer server;

    public iListen(General instance) {
        plugin = instance;
    }
    
    // Disabled for now because I have no idea what m, e, or o are. --celticminstrel
//    private Location spawn(Player player) {
//		double x = (server.m + 0.5D);
//		double y = server.e(this.server.m, this.server.o) + 1.5D;
//		double z = server.o + 0.5D;
//		float rotX = 0.0F;
//		float rotY = 0.0F;
//	
//		return new Location(player.getWorld(), x, y, z, rotX, rotY);
//    }

    public long getTime(World where) {
        return where.getTime();
    }

    public long getRelativeTime(World where) {
        return (getTime(where) % 24000);
    }
    
    public long getStartTime(World where) {
        return (getTime(where)-getRelativeTime(where));
    }

    public void setTime(World where, long time) {
        where.setTime(time);
    }

    private void setRelativeTime(World where, long time) {
        long margin = (time-getTime(where)) % 24000;

        if (margin < 0) {
            margin += 24000;
        }

        where.setTime(getTime(where)+margin);
    }

    protected boolean teleport(String who, String to) {
        Player destination = Misc.playerMatch(to);

        if (who.equalsIgnoreCase("*")) {
            Player[] players = plugin.getServer().getOnlinePlayers();

            for (Player player : players) {
                if (!player.equals(destination)) {
                    player.teleportTo(destination.getLocation());
                }
            }

            return true;
        } else if (who.contains(",")) {
            String[] players = who.split(",");

            for (String name : players) {
                Player player = Misc.playerMatch(name);

				if ((player == null) || (destination == null)) {
					continue;
				} else if (!player.equals(destination)) {
					player.teleportTo(destination.getLocation());
				}
            }

            return true;
        } else {
            Player player = Misc.playerMatch(who);

            if ((player == null) || (destination == null)) {
                return false;
            } else {
                player.teleportTo(destination.getLocation());
                return true;
            }
        }
    }

    public String getDirection(double degrees) {
        if (0 <= degrees && degrees < 22.5) {
            return "N";
        } else if (22.5 <= degrees && degrees < 67.5) {
            return "NE";
        } else if (67.5 <= degrees && degrees < 112.5) {
            return "E";
        } else if (112.5 <= degrees && degrees < 157.5) {
            return "SE";
        } else if (157.5 <= degrees && degrees < 202.5) {
            return "S";
        } else if (202.5 <= degrees && degrees < 247.5) {
            return "SW";
        } else if (247.5 <= degrees && degrees < 292.5) {
            return "W";
        } else if (292.5 <= degrees && degrees < 337.5) {
            return "NW";
        } else if (337.5 <= degrees && degrees < 360.0) {
            return "N";
        } else {
            return "ERR";
        }
    }

    public boolean isAFK(Player player) {
		return AFK.containsKey(player);
    }

    public void AFK(Player player, String message) {
		AFK.put(player, message);
    }

    public void unAFK(Player player) {
		AFK.remove(player);
    }

    public String[] readMotd() {
		ArrayList<String> motd = new ArrayList<String>();
	
		try {
			BufferedReader in = new BufferedReader(new FileReader(plugin.getDataFolder() + File.separator +  "general.motd"));
			String str;
			while ((str = in.readLine()) != null) {
				motd.add(str);
			}
			in.close();
		} catch (IOException e) { }
	
		return motd.toArray(new String[]{});
    }

    public String[] read_commands() {
		try {
			BufferedReader in = new BufferedReader(new FileReader(plugin.getDataFolder() + File.separator + "general.help"));
			String str;
			while ((str = in.readLine()) != null) {
				if(!lines.contains(str)) {
					lines.add(str);
				} else {
					continue;
				}
			}
			in.close();
		} catch (IOException e) { }
	
		return lines.toArray(new String[]{});
    }

    public void print_commands(int page) {
		String[] commands = read_commands();
		int amount = 0;
	
		if (page > 0) {
			amount = (page - 1) * 7;
		} else {
			amount = 0;
		}
	
		Messaging.send("&dHelp &f(&dPage &f" + (page != 0 ? page : "1") + "&d of&f " + (int)Math.ceil((double)commands.length/7D) + "&d) [] = required, () = optional:");
	
		try {
			for (int i = amount; i < amount + 7; i++) {
				if (commands.length > i) {
					Messaging.send(commands[i]);
				}
			}
		} catch (NumberFormatException ex) {
			Messaging.send("&cNot a valid page number.");
		}
    }

    public void register_command(String command, String help) {
		if(!Commands.contains(command.replace("|", "&5|&f") + help)) {
			Commands.add(command.replace("|", "&5|&f") + help);
		}
    }

    public void register_custom_command(String command) {
		if(!Commands.contains(command)) {
			Commands.add(command);
		}
    }

    public void save_command(String command, String help) {
		if(!Commands.contains(command + " &5-&3 " + help)) {
			Commands.add(command + " &5-&3 " + help);
		}
    }

    public void save_custom_command(String command) {
		if(!Commands.contains(command)) {
			Commands.add(command);
		}
    }

    public void remove_command(String command, String help) {
		if(Commands.contains(command.replace("|", "&5|&f") + " &5-&3 " + help)) {
			Commands.remove(command.replace("|", "&5|&f") + " &5-&3 " + help);
		} else {
		   // General.log.info("Help command registry does not contain "+command+" to remove!");
		}
    }

    public void remove_custom_command(String command_line) {
		if(Commands.contains(command_line)) {
			Commands.remove(command_line);
		} else {
			// General.log.info("Help command registry does not contain "+command_line+" to remove!");
		}
    }

    @Override
    public void onPlayerJoin(PlayerEvent event) {
        Player player = event.getPlayer();
		String[] motd = readMotd();
	
		if(motd == null || motd.length < 1) { return; }
	
		String location = (int)player.getLocation().getX() +"x, " + (int)player.getLocation().getY() +"y, " + (int)player.getLocation().getZ() +"z";
		String ip = player.getAddress().getAddress().getHostAddress();
		String balance = "";
	
		Plugin test = plugin.getServer().getPluginManager().getPlugin("iConomy");
	
		//if(test != null) {
		//    iConomy iConomy = (iConomy)test;
		//    balance = iConomy.db.get_balance(player.getName()) + " " + iConomy.currency;
		//}
	
		for(String line : motd) {
			Messaging.send(
			player,
			Messaging.argument(
				line,
				new String[]{
				"+dname,+d", "+name,+n", "+location,+l", "+health,+h", "+ip", "+balance", "+online"
				},
				new String[]{ 
				player.getDisplayName(),
				player.getName(),
				location,
				Misc.string(player.getHealth()),
				ip,
				balance,
				Misc.string(plugin.getServer().getOnlinePlayers().length)
				}
			)
			);
		}
    }
}
