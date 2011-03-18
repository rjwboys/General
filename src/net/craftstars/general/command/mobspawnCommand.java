package net.craftstars.general.command;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Player;

import net.craftstars.general.CommandBase;
import net.craftstars.general.General;
import net.craftstars.general.items.ItemID;
import net.craftstars.general.items.Items;
import net.craftstars.general.mobs.xmlns.jordanneil23.Mob;
import net.craftstars.general.mobs.xmlns.jordanneil23.TargetBlock;
import net.craftstars.general.mobs.xmlns.jordanneil23.Mob.MobException;
import net.craftstars.general.util.Toolbox;
import net.minecraft.server.EntitySlime;
import net.minecraft.server.EntitySheep;
import net.minecraft.server.WorldServer;

public class mobspawnCommand extends CommandBase {
    @Override
    public boolean fromConsole(General plugin, CommandSender sender, Command command,
            String commandLabel, String[] args) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean fromPlayer(General plugin, Player p, Command command, String commandLabel,
            String[] args) {
        // TODO Refactor and stuff
        int[] ignore = {8, 9};
        if (0 < args.length && args.length < 3) {
            String[] split1 = args[0].split(":");
            String[] split0 = new String[1];
            CraftEntity spawned1 = null;
            Mob mob2 = null;
            if (split1.length == 1 && !split1[0].equalsIgnoreCase("Slime")) {
                split0 = args[0].split(";");
                split1[0] = split0[0];
            }
            if (split1.length == 2) {
                args[0] = split1[0] + "";
            }
            Mob mob = Mob.fromName(split1[0].equalsIgnoreCase("PigZombie") ? "PigZombie" : capitalCase(split1[0]));
            if (mob == null) {
                p.sendMessage("Invalid mob type.");
                return true;
            }
            if(!(General.plugin.permissions.hasPermission(p, "general.mobspawn." + mob.name.toLowerCase()))){
                p.sendMessage("You can't spawn this mob/mob type.");
                return true;
            }
            WorldServer world = ((CraftWorld) p.getWorld()).getHandle();
            CraftEntity spawned = null;
            try {
                spawned = mob.spawn(p, plugin);
            } catch (MobException e) {
                p.sendMessage("Unable to spawn mob.");
                return true;
            }
            Location loc = (new TargetBlock(p, 300, 0.2, ignore)).getTargetBlock().getLocation();
            loc.setY(1 + loc.getY()); // TODO: Make mobs spawn on blocks, not in them. This is a quick and dirty partial solution.
            spawned.teleportTo(loc);
            world.a(spawned.getHandle());
            if (split0.length == 2) {
                mob2 = Mob.fromName(split0[1].equalsIgnoreCase("PigZombie") ? "PigZombie" : capitalCase(split0[1]));
                if (mob2 == null) {
                    p.sendMessage("Invalid mob type.");
                    return true;
                }
                try {
                    spawned1 = mob2.spawn(p, plugin);
                } catch (MobException e) {
                    p.sendMessage("Unable to spawn mob.");
                    return true;
                }
                spawned1.teleportTo(spawned);
                spawned.getHandle().setPassengerOf(spawned1.getHandle());
                world.a(spawned1.getHandle());
            }
            if (split1.length == 2 && mob.name == "Slime") {
                try {
                    ((EntitySlime) spawned.getHandle()).b(Integer.parseInt(split1[1]));
                } catch (Exception e) {
                    p.sendMessage("Malformed size.");
                    return true;
                }
            } else if (split1.length == 2 && mob.name == "Sheep") {
                if(Toolbox.equalsOne(split1[1], "sheared", "nude", "naked", "bald")) {
                    ((EntitySheep) spawned.getHandle()).a(true);
                } else {
                    try {
                        ItemID wool = Items.validate("35:" + split1[1]);
                        ((EntitySheep) spawned.getHandle()).a_(wool.getData());
                    } catch (Exception e) {
                        p.sendMessage("Malformed colour.");
                        e.printStackTrace();
                        return true;
                    }
                }
            }
            if (args.length == 2) {
                try {
                    for (int i = 1; i < Integer.parseInt(args[1]); i++) {
                        spawned = mob.spawn(p, plugin);
                        spawned.teleportTo(loc);
                        if (split1.length > 1 && mob.name == "Slime") {
                            try {
                                ((EntitySlime) spawned.getHandle()).b(Integer.parseInt(split1[1]));
                            } catch (Exception e) {
                                p.sendMessage("Malformed size.");
                                return true;
                            }
                        }
                        world.a(spawned.getHandle());
                        if (split0.length == 2) {
                            if (mob2 == null) {
                                p.sendMessage("Invalid mob type.");
                                return true;
                            }
                            try {
                                spawned1 = mob2.spawn(p, plugin);
                            } catch (MobException e) {
                                p.sendMessage("Unable to spawn mob.");
                                return true;
                            }
                            spawned1.teleportTo(spawned);
                            spawned1.getHandle().setPassengerOf(spawned.getHandle());
                            world.a(spawned1.getHandle());
                        }
                    }
                    p.sendMessage(args[1] + " " + mob.name.toLowerCase() + mob.s + (split0.length == 2 ? " riding " + mob2.name.toLowerCase() + mob2.s : "") + " spawned.");
                } catch (MobException e1) {
                    p.sendMessage("Unable to spawn mobs.");
                    return true;
                } catch (java.lang.NumberFormatException e2) {
                    p.sendMessage("Malformed integer.");
                    return true;
                }
            } else {
                p.sendMessage(mob.name + (split0.length == 2 ? " riding a " + mob2.name.toLowerCase() : "") + " spawned.");
            }
            return true;
        }
        return Toolbox.USAGE;
    }
    
    private static String capitalCase(String s) {
        return s.toUpperCase().charAt(0) + s.toLowerCase().substring(1);
    }
}