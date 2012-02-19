
package net.craftstars.general.command.misc;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import net.craftstars.general.General;
import net.craftstars.general.command.CommandBase;
import net.craftstars.general.mobs.InvalidMobException;
import net.craftstars.general.mobs.MobData;
import net.craftstars.general.mobs.MobType;
import net.craftstars.general.teleport.Destination;
import net.craftstars.general.text.LanguageText;
import net.craftstars.general.text.Messaging;
import net.craftstars.general.util.EconomyManager;
import net.craftstars.general.util.Option;

public class mobspawnCommand extends CommandBase {
	public mobspawnCommand(General instance) {
		super(instance);
	}
	private Random offsetGenerator = new Random();

	@Override
	public Map<String, Object> parse(CommandSender sender, Command command, String label, String[] args, boolean isPlayer) {
		HashMap<String,Object> params = new HashMap<String,Object>();
		String[] economyNodes;
		SpawnResult spawn = null;
		Destination dest = null;
		int numMobs = 1;
		switch(args.length) {
		case 1: // /mob <mob>
			spawn = parseSimpleMobName(sender, args[0]);
		break;
		case 2: // /mob <mob> <number>
			try {
				numMobs = Integer.valueOf(args[1]);
			} catch(NumberFormatException e) {
				// /mob <mob> <destination>
				dest = Destination.get(args[1], isPlayer ? (Player) sender : null);
				// /mob <mob> <mount>
				if(dest == null) spawn = parseCompoundMobName(sender, args[0], args[1]);
			}
			if(spawn == null) spawn = parseSimpleMobName(sender, args[0]);
		break;
		case 3: // /mob <mob> <number> <destination>
			try {
				numMobs = Integer.valueOf(args[1]);
				dest = Destination.get(args[1], isPlayer ? (Player) sender : null);
				spawn = parseSimpleMobName(sender, args[0]);
			} catch(NumberFormatException e) {
				// /mob <mob> <mount> <number>
				spawn = parseCompoundMobName(sender, args[0], args[1]);
				try {
					numMobs = Integer.valueOf(args[2]);
				} catch(NumberFormatException x) {
					// /mob <mob> <mount> <destination>
					dest = Destination.get(args[2], isPlayer ? (Player) sender : null);
				}
			}
		break;
		case 4: // /mob <mob> <mount> <number> <destination>
			spawn = parseCompoundMobName(sender, args[0], args[1]);
			dest = Destination.get(args[3], isPlayer ? (Player) sender : null);
			try {
				numMobs = Integer.valueOf(args[2]);
			} catch(NumberFormatException e) {
				Messaging.invalidNumber(sender, args[2]);
				return null;
			}
		break;
		default:
			return null;
		}
		if(dest == null) {
			if(isPlayer) dest = Destination.targetOf((Player)sender);
			else {
				Messaging.send(sender, LanguageText.MOB_NO_DEST);
				return null;
			}
		}
		if(spawn == null) return null;
		if(numMobs < 1) {
			Messaging.send(sender, LanguageText.MOB_TOO_FEW);
			return null;
		}
		economyNodes = spawn.getCostClass();
		// And now the parsing is complete; set the params and go!
		params.put("mob", spawn);
		params.put("dest", dest);
		params.put("num", numMobs);
		params.put("economy", economyNodes);
		return params;
	}

	private SpawnResult parseSimpleMobName(CommandSender sender, String mobName) {
		SpawnResult spawn;
		if(mobName.equalsIgnoreCase("SpiderJockey")) {
			spawn = new SpawnResult(MobType.SKELETON);
			spawn = new SpawnResult(MobType.SPIDER, spawn);
		} else spawn = parseMobName(sender, mobName);
		return spawn;
	}
	
	private SpawnResult parseCompoundMobName(CommandSender sender, String mobName, String mountName) {
		SpawnResult rider = parseMobName(sender, mobName);
		SpawnResult spawn = parseMobName(sender, mountName);
		spawn.rider = rider;
		return spawn;
	}
	
	private SpawnResult parseMobName(CommandSender sender, String mobName) {
		String[] split;
		split = mobName.split("[.,:/\\|]", 2);
		MobType mob = MobType.getMob(split[0]);
		if(mob == null) throw new InvalidMobException(LanguageText.MOB_BAD_TYPE,
			"mob", LanguageText.MOB_MOB.value(), "type", split[0]);
		MobData data;
		if(split.length == 2) data = MobData.parse(mob, sender, split[1]);
		else data = mob.getNewData();
		return new SpawnResult(mob, data);
	}

	@Override
	public boolean execute(CommandSender sender, String command, Map<String, Object> args) {
		String[] economyNodes = (String[]) args.get("economy");
		SpawnResult spawn = (SpawnResult) args.get("mob");
		Destination dest = (Destination) args.get("dest");
		int numMobs = (Integer) args.get("num");
		if(numMobs > 5 && !sender.hasPermission("general.mobspawn.mass"))
			return Messaging.lacksPermission(sender, "general.mobspawn.mass");
		boolean canPay = Option.NO_ECONOMY.get();
		if(!canPay) canPay = EconomyManager.canPay(sender, numMobs, economyNodes);
		if(canPay) {
			while(numMobs-- > 0) doSpawn(sender, spawn, dest.getLoc());
		}
		return true;
	}
	
	private LivingEntity doSimpleSpawn(CommandSender sender, MobType mob, MobData data, Location where) {
		double xOffset = offsetGenerator.nextDouble(), zOffset = offsetGenerator.nextDouble();
		Location actual = where.add(xOffset, 0, zOffset);
		while(actual.getBlock().getType() != Material.AIR)
			actual = actual.add(0, 1, 0);
		return mob.spawn(sender, actual, data);
	}
	
	private LivingEntity doSpawn(CommandSender sender, SpawnResult mob, Location where) {
		if(mob.rider == null) return doSimpleSpawn(sender, mob.type, mob.data, where);
		LivingEntity rider = doSimpleSpawn(sender, mob.rider.type, mob.rider.data, where);
		if(rider == null) return null;
		LivingEntity mount = doSimpleSpawn(sender, mob.type, mob.data, where);
		if(mount == null) {
			rider.remove();
			return null;
		}
		mount.setPassenger(rider);
		return mount;
	}
	
	private static class SpawnResult {
		public MobType type;
		public MobData data;
		SpawnResult rider;
		
		SpawnResult(MobType t, MobData d) {
			type = t;
			data = d;
			rider = null;
		}
		
		SpawnResult(MobType t) {
			type = t;
			data = t.getNewData();
			rider = null;
		}
		
		SpawnResult(MobType t, SpawnResult ride) {
			type = t;
			data = t.getNewData();
			rider = ride;
		}
		
		String[] getCostClass() {
			if(rider == null) return new String[] {type.getCostClass(data)};
			String riderNode = rider.type.getCostClass(rider.data);
			String mountNode = type.getCostClass(data);
			String completeNode = rider.type.getMountedCostClass(riderNode, data);
			if(completeNode.equals(""))
				return new String[] {riderNode, mountNode};
			else return new String[] {completeNode}; 
		}
	}
}
