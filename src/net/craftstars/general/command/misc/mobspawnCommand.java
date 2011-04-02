package net.craftstars.general.command.misc;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import net.craftstars.general.command.CommandBase;
import net.craftstars.general.General;
import net.craftstars.general.mobs.MobType;
import net.craftstars.general.mobs.TargetBlock;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

public class mobspawnCommand extends CommandBase {
    private static class Splitter {
        public String mobName, mobData, mountName, mountData;
        public Splitter(String s) {
            String mob, mount = null;
            if(s.contains(";")) {
                String[] split = s.split(";");
                mob = split[0];
                mount = split[1];
            } else mob = s;
            if(mob.contains(":")) {
                String[] split = mob.split(":");
                this.mobName = split[0];
                this.mobData = split[1];
            } else this.mobName = mob;
            if(mount != null && mount.contains(":")) {
                String[] split = mount.split(":");
                this.mountName = split[0];
                this.mountData = split[1];
            } else this.mountName = mount;
        }
    }
    
    @Override
    public boolean fromConsole(General plugin, CommandSender sender, Command command,
            String commandLabel, String[] args) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean fromPlayer(General plugin, Player player, Command command, String commandLabel,
            String[] args) {
        if(Toolbox.lacksPermission(plugin, player, "spawn mobs", "general.mobspawn")) return true;
        int[] ignore = {8, 9};
        if (0 < args.length && args.length < 3) {
            Splitter what = new Splitter(args[0]);
            MobType mobType, mountType = null;
            mobType = MobType.fromName(what.mobName);
            if(mobType == null) {
                Messaging.send(player, "&rose;Invalid mob type: " + what.mobName);
                return true;
            }
            if(!(General.plugin.permissions.hasPermission(player, "general.mobspawn." + mobType.toString().toLowerCase()))){
                player.sendMessage("You can't spawn a " + what.mobName + ".");
                return true;
            }
            if(what.mountName != null) {
                mountType = MobType.fromName(what.mountName);
                if(mountType == null) {
                    Messaging.send(player, "&rose;Invalid mob type: " + what.mountName);
                    return true;
                }
                if(!(General.plugin.permissions.hasPermission(player, "general.mobspawn." + mountType.toString().toLowerCase()))){
                    player.sendMessage("You can't spawn a " + what.mountName + ".");
                    return true;
                }
            }
            Location loc = (new TargetBlock(player, 300, 0.2, ignore)).getTargetBlock().getLocation();
            loc.setY(1 + loc.getY()); // TODO: Make mobs spawn on blocks, not in them. This is a quick and dirty partial solution.
            LivingEntity spawned = null, mounted = null;
            try {
                spawned = mobType.spawn(player, plugin, loc);
                spawned.hashCode();
                mobType.setData(spawned, what.mobData);
            } catch (Exception e) {
                Messaging.send(player, "&rose;Unable to spawn mob.");
                if(spawned != null) spawned.remove();
                General.logger.info("Failed to spawn mob " + mobType.getName() + ".",e);
                return true;
            }
            if(mountType != null) {
                try {
                    mounted = mountType.spawn(player, plugin, loc);
                    mounted.hashCode();
                    mobType.setData(mounted, what.mobData);
                } catch (Exception e) {
                    Messaging.send(player, "&rose;Unable to spawn mob.");
                    if(mounted != null) mounted.remove();
                    spawned.remove();
                    General.logger.info("Failed to spawn mob " + mountType.getName() + ".",e);
                    return true;
                }
                mounted.setPassenger(spawned);
            }
            Messaging.send(player, mobType.getName() + (mountType != null ? " riding a " + mountType.getName() : "") + " spawned.");
            return true;
        }
        return SHOW_USAGE;
    }
}