
package net.craftstars.general.command.misc;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import net.craftstars.general.General;
import net.craftstars.general.command.CommandBase;
import net.craftstars.general.mobs.MobData;
import net.craftstars.general.mobs.MobType;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

public class mobspawnCommand extends CommandBase {
	public mobspawnCommand(General instance) {
		super(instance);
	}
	private Random offsetGenerator = new Random();
	private String[] economyNodes;

	@Override
	public boolean fromPlayer(Player sender, Command command, String commandLabel, String[] args) {
		if(Toolbox.lacksPermission(sender, "general.mobspawn"))
			return Messaging.lacksPermission(sender, "spawn mobs");
		Location where = Toolbox.getTargetBlock(sender);
		ArrayList<SpawnResult> mobs = new ArrayList<SpawnResult>();
		switch(args.length) {
		case 1:
			if(args[0].equalsIgnoreCase("SpiderJockey"))
				mobs.add(doCompoundSpawn(sender, "Skeleton", "Spider", where));
			else mobs.add(doSimpleSpawn(sender, args[0], where));
			break;
		case 2:
			try {
				int n = Integer.valueOf(args[1]);
				if(n > 5 && Toolbox.lacksPermission(sender, "general.mobspawn.mass"))
					return Messaging.lacksPermission(sender, "spawn mobs en masse");
				if(args[0].equalsIgnoreCase("SpiderJockey"))
					while(n-- > 0)
						mobs.add(doCompoundSpawn(sender, "Skeleton", "Spider", where));
				else while(n-- > 0)
					mobs.add(doSimpleSpawn(sender, args[0], where));
			} catch(NumberFormatException e) {
				mobs.add(doCompoundSpawn(sender, args[0], args[1], where));
			}
			break;
		case 3:
			try {
				int n = Integer.valueOf(args[2]);
				if(n > 5 && Toolbox.lacksPermission(sender, "general.mobspawn.mass"))
					return Messaging.lacksPermission(sender, "spawn mobs en masse");
				if(args[0].equals("-") || args[1].equals("-"))
					while(n-- > 0)
						mobs.add(doSimpleSpawn(sender, (args[0] + args[1]).replace("-", ""), where));
				else while(n-- > 0)
					mobs.add(doCompoundSpawn(sender, args[0], args[1], where));
			} catch(NumberFormatException e) {
				return false;
			}
			break;
		default:
			return false;
		}
		// After the mobs are spawned, look at them to determine their cost, and
		// despawn them if the player can't pay.
		// TODO: Not an ideal way of doing this.
		if(General.plugin.economy != null) {
			while(mobs.contains(null)) mobs.remove(null);
			if(!Toolbox.canPay(sender, mobs.size(), economyNodes)) {
				for(SpawnResult mob : mobs) {
					if(mob.mob.getPassenger() != null)
						mob.mob.getPassenger().remove();
					mob.mob.remove();
				}
			}
		}
		return true;
	}
	
	private SpawnResult doSimpleSpawn(Player sender, String mobName, Location where) {
		String[] split;
		split = mobName.split("[.,:/\\|]", 2);
		MobType mob = MobType.getMob(split[0]);
		if(mob == null) {
			Messaging.send(sender, "&cInvalid mob type: " + mobName);
			return null;
		}
		double xOffset = offsetGenerator.nextDouble(), zOffset = offsetGenerator.nextDouble();
		Location actual = where.add(xOffset, 0, zOffset);
		while(actual.getBlock().getType() != Material.AIR)
			actual = actual.add(0, 1, 0);
		LivingEntity entity = mob.spawn(sender, actual);
		MobData data;
		if(split.length == 2) {
			data = MobData.parse(mob, sender, split[1]);
			if(data == null) Messaging.send(sender, "&cError setting the data.");
			else mob.setData(entity, sender, data);
			economyNodes = new String[] {mob.getCostClass(data)};
		} else {
			data = mob.getNewData();
			economyNodes = new String[] {mob.getCostClass(null)};
		}
		return new SpawnResult(entity, mob, data);
	}
	
	private SpawnResult doCompoundSpawn(Player sender, String riderName, String mountName, Location where) {
		SpawnResult rider = doSimpleSpawn(sender, riderName, where);
		String riderNode = economyNodes[0];
		if(rider.mob == null) return null;
		SpawnResult mount = doSimpleSpawn(sender, mountName, where);
		String mountNode = economyNodes[0];
		if(mount.mob == null) {
			rider.mob.remove();
			return null;
		}
		mount.mob.setPassenger(rider.mob);
		String completeNode = mount.type.getMountedCostClass(riderNode, mount.data);
		if(completeNode.equals(""))
			economyNodes = new String[] {riderNode, mountNode};
		else economyNodes = new String[] {completeNode}; 
		mount.rider = rider;
		return mount;
	}
	
	@Override
	public boolean fromConsole(ConsoleCommandSender sender, Command command, String commandLabel,
			String[] args) {
		Messaging.send(sender, "&cSorry, this command can only be used by a player.");
		return true;
	}
	
	private static class SpawnResult {
		public LivingEntity mob;
		public MobType type;
		public MobData data;
		SpawnResult rider;
		
		SpawnResult(LivingEntity e, MobType t, MobData d) {
			mob = e;
			type = t;
			data = d;
			rider = null;
		}
	}
}
