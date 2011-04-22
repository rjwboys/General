package net.craftstars.general.command.misc;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.General;
import net.craftstars.general.command.CommandBase;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

public class weatherCommand extends CommandBase {
    Random lightning = new Random();
    
    @Override
    public boolean fromConsole(General plugin, CommandSender sender, Command command,
            String commandLabel, String[] args) {
        switch(args.length) {
        case 2:
            World world = Toolbox.getWorld(args[0], sender);
            if(world == null) return true;
            doWeather(sender, args[1], world, world.getSpawnLocation());
            return true;
        default:
            return SHOW_USAGE;
        }
    }

    @Override
    public boolean fromPlayer(General plugin, Player sender, Command command, String commandLabel,
            String[] args) {
        switch(args.length) {
        case 0:
            if(sender.getWorld().hasStorm())
                doWeather(sender, sender.getWorld(), 0);
            else doWeather(sender, sender.getWorld(), -1);
            return true;
        case 1:
            doWeather(sender, args[0], sender.getWorld(), sender.getLocation());
            return true;
        case 2:
            World world = Toolbox.getWorld(args[0], sender);
            Location loc;
            if(world == null) {
                Player player = Toolbox.getPlayer(args[0], sender);
                if(player == null) return true;
                world = player.getWorld();
                loc = player.getLocation();
            } else loc = world.getSpawnLocation();
            doWeather(sender, args[1], world, loc);
            return true;
        default:
            return SHOW_USAGE;
        }
    }

    private void doWeather(CommandSender sender, String key, World world, Location loc) {
        try {
            int duration = Integer.valueOf(key);
            doWeather(sender, world, duration);
        } catch(NumberFormatException e) {
            if(Toolbox.equalsOne(key, "lightning", "strike", "zap"))
                doLightning(sender, world, loc);
//            else if(Toolbox.equalsOne(key, "thunder", "boom"))
//                doThunder(sender, world);
            else if(Toolbox.equalsOne(key, "on", "start"))
                doWeather(sender, world,-1);
            else if(Toolbox.equalsOne(key, "off", "stop"))
                doWeather(sender, world,0);
            else Messaging.send(sender, "&cInvalid argument.");
        }
    }
    
//    private void doThunder(Player sender, World world) {
//        world.setThundering(true);
//        Messaging.send(sender, "Thunder started!");
//        world.setThunderDuration(100);
//    }

    private void doWeather(CommandSender sender, World world, int duration) {
        boolean state = duration != 0;
        boolean hasStorm = world.hasStorm();
        world.setStorm(state);
        if(state && duration != -1)
            world.setWeatherDuration(duration);
        if(duration == 0)
            Messaging.send(sender, "&blue;Weather storm stopped!");
        else if(duration == -1)
            Messaging.send(sender, "&blue;Weather storm started!");
        else if(hasStorm)
            Messaging.send(sender, "&blue;Weather storm will stop in " + duration + " ticks!");
        else Messaging.send(sender, "&blue;Weather storm started for " + duration + " ticks!");
    }
    
    private void doLightning(CommandSender sender, World world, Location centre) {
        int x, y, z;
        x = centre.getBlockX();
        y = 127;
        z = centre.getBlockZ();
        Block block = world.getBlockAt(x, y, z);
        while(block.getType() == Material.AIR)
            block = block.getRelative(BlockFace.DOWN);
        int range = General.plugin.config.getInt("lightning-range", 20);
        x += lightning.nextInt(range * 2) - range;
        y = block.getLocation().getBlockY();
        z += lightning.nextInt(range * 2) - range;
        world.strikeLightning(new Location(world, x, y, z));
        Messaging.send(sender, "&yellow;Lightning strike!");
    }
}