package net.craftstars.general.command;

import net.craftstars.general.General;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class whoCommand extends GeneralCommand
{
    private Player player;
    private String name;
    private String displayName;
    private String bar;
    private String location;
    
    @Override
    public boolean fromPlayer(General plugin, Player sender, Command command, String commandLabel, String[] args)
    {
        if (args.length < 1)
        {
            // No argument, using sender as target.
            this.getPlayerInfo(sender);
        }
        else if (args.length >= 1)
        {
            if (args[0].equals("help"))
            {
                return false;
            }
            
            this.getPlayerInfo(args[0]);
        }
        
        if (player != null)
        {
            Messaging.send(sender, "&f------------------------------------------------");
            Messaging.send(sender, "&e Player &f["+this.name+"]&e Info");
            Messaging.send(sender, "&f------------------------------------------------");
            Messaging.send(sender, "&6 Username: &f" + this.name);
            Messaging.send(sender, "&6 DisplayName: &f" + this.displayName);
            // TODO: Hide health and location with a config option? [Plutonium239]
            Messaging.send(sender, "&6 -&e Health: &f" + this.bar);
            Messaging.send(sender, "&6 -&e Location: &f" + this.location);
            // TODO: AFK System [Plutonium239]
            Messaging.send(sender, "&6 -&e Status: &f" + "Around.");
            Messaging.send(sender, "&f------------------------------------------------");
        }
        else
        {
            Messaging.send(sender, "&4;ERROR&f;: Couldn't find player. Please try again.");
        }
        
        return true;
    }
    
    private void getPlayerInfo(String playerName)
    {
        Player player = Toolbox.playerMatch(playerName);
        
        if (player != null)
        {
            this.getPlayerInfo(player);
        }
    }
    
    private void getPlayerInfo(Player player)
    {
        this.player = player;
        this.name = player.getName();
        this.displayName = player.getDisplayName();
        
        int health = player.getHealth();
        int length = 10;
        int bars = Math.round(health/2);
        int remainder = length-bars;
        String hb_color = ((bars >= 7) ? "&2" : ((bars < 7 && bars >= 3) ? "&e" : ((bars < 3) ? "&c" : "&2")));
        this.bar = " &f["+ hb_color + Toolbox.repeat('|', bars) + "&7" + Toolbox.repeat('|', remainder) + "&f]";
        
        int x = (int)player.getLocation().getX();
        int y = (int)player.getLocation().getY();
        int z = (int)player.getLocation().getZ();
        this.location = x+"x, "+y+"y, "+z+"z";
    }
    
}