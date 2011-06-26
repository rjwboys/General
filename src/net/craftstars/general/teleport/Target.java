
package net.craftstars.general.teleport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.craftstars.general.General;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class Target {
	private List<LivingEntity> teleportees;
	private TargetType type;
	private String title;
	
	private Target(List<LivingEntity> l, TargetType t) {
		teleportees = l;
		type = t;
		switch(t) {
		case SELF:
			title = "yourself";
		break;
		case OTHER:
			if(l.size() > 1)
				title = "several people";
			else {
				LivingEntity entity = l.get(0);
				if(entity instanceof Player)
					title = ((Player) entity).getDisplayName();
				else title = "someone";
			}
		break;
		case MOB:
			if(l.size() > 1)
				title = "several mobs";
			else {
				LivingEntity entity = l.get(0);
				title = "a " + entity.getClass().getSimpleName().replace("Craft", "").toLowerCase();
			}
		break;
		}
	}
	
	public boolean hasPermission(Player who) {
		if(who.isOp()) return true;
		boolean perm = type.hasPermission(who);
		if(teleportees.size() > 1) {
			boolean canMass = Toolbox.hasPermission(who, "general.teleport.mass");
			perm = perm && canMass;
			if(!canMass) Messaging.lacksPermission(who, "teleport en masse");
		}
		return perm;
	}
	
	public void teleport(Destination to) {
		for(LivingEntity victim : teleportees) {
			Location loc = to.getLoc();
			Block bloc = loc.getBlock();
			if(bloc != null) {
				Block next = bloc.getRelative(BlockFace.UP);
				while(Toolbox.isSolid(bloc) || Toolbox.isSolid(next)) {
					if(next == null) {
						loc.setY(128);
						break;
					}
					Block temp = next;
					next = next.getRelative(BlockFace.UP);
					bloc = temp;
				}
				if(next != null) loc = bloc.getLocation();
				loc.setX(loc.getX()+0.5);
				loc.setZ(loc.getZ()+0.5);
			}
			victim.teleport(loc);
			if(victim instanceof Player) {
				Player who = (Player) victim;
				if(type != TargetType.SELF)
					Messaging.send(who, "&fYou have been teleported to &9" + to.getName() + "&f!");
			}
		}
	}
	
	public static Target get(String targ, Player teleporter) {
		Server mc = General.plugin.getServer();
		CommandSender notify;
		if(teleporter == null)
			notify = new ConsoleCommandSender(mc);
		else notify = teleporter;
		// Is it a player? Optionally prefixed by !
		LivingEntity victim = Toolbox.matchPlayer(targ.replaceFirst("^!", ""));
		if(victim != null) {
			TargetType tt = TargetType.OTHER;
			if(victim.equals(teleporter)) tt = TargetType.SELF;
			return new Target(Arrays.asList(victim), tt);
		}
		// Is it a world? Optionally prefixed by @
		World globe = Toolbox.matchWorld(targ.replaceFirst("^@", ""));
		if(globe != null) {
			List<Player> players = globe.getPlayers();
			return new Target(new ArrayList<LivingEntity>(players), TargetType.OTHER);
		}
		// Is it a wildcard?
		if(targ.equals("*")) {
			ArrayList<LivingEntity> players = new ArrayList<LivingEntity>();
			for(World flat : mc.getWorlds())
				players.addAll(flat.getPlayers());
			return new Target(players, TargetType.OTHER);
		}
		// Is it a list of players?
		if(targ.contains(",")) {
			String[] split = targ.split(",");
			ArrayList<LivingEntity> players = new ArrayList<LivingEntity>();
			for(String p : split)
				players.add(Toolbox.matchPlayer(p));
			return new Target(players, TargetType.OTHER);
		}
		if(teleporter != null) {
			// Is it a special keyword?
			int range = General.plugin.config.getInt("summon-range", 30);
			if(Toolbox.equalsOne(targ, "near", "$near")) {
				List<Entity> near = teleporter.getNearbyEntities(range, range, range);
				ArrayList<LivingEntity> players = new ArrayList<LivingEntity>();
				for(Entity what : near) {
					if(what instanceof Player) players.add((LivingEntity) what);
				}
				if(players.size() > 0) {
					return new Target(players, TargetType.OTHER);
				} else {
					Messaging.send(notify, "&cNo players nearby.");
				}
			}
			if(Toolbox.equalsOne(targ, "nearmob", "$nearmob")) {
				List<Entity> near = teleporter.getNearbyEntities(range, range, range);
				ArrayList<LivingEntity> victims = new ArrayList<LivingEntity>();
				for(Entity what : near) {
					if(what instanceof LivingEntity && ! (what instanceof Player)) victims.add((LivingEntity) what);
				}
				if(victims.size() > 0) {
					return new Target(victims, TargetType.MOB);
				} else {
					Messaging.send(notify, "&cNo mobs nearby.");
				}
			}
			if(Toolbox.equalsOne(targ, "there", "$there")) {
				List<Entity> near = teleporter.getNearbyEntities(range, range, range);
				HashMap<Integer, LivingEntity> potentials = new HashMap<Integer, LivingEntity>();
				for(Entity potential : near) {
					if(! (potential instanceof LivingEntity)) continue;
					int i = findInSightLine(teleporter, (LivingEntity) potential, 50);
					if(i != -1) potentials.put(i, (LivingEntity) potential);
				}
				int min = Integer.MAX_VALUE;
				for(int i : potentials.keySet()) {
					if(i < min) min = i;
				}
				victim = potentials.get(min);
				if(victim != null) {
					TargetType tt;
					if(victim instanceof Player)
						tt = TargetType.OTHER;
					else tt = TargetType.MOB;
					return new Target(Arrays.asList(victim), tt);
				} else {
					Messaging.send(notify, "&cNo-one there.");
				}
			}
			if(Toolbox.equalsOne(targ, "self", "$self", "me")) return fromPlayer(teleporter);
		}
		// No more ideas, so just give up.
		Messaging.send(notify, "&cInvalid target.");
		return null;
	}
	
	public static Target fromPlayer(Player player) {
		return new Target(Arrays.asList((LivingEntity) player), TargetType.SELF);
	}
	
	private static int findInSightLine(Player who, LivingEntity what, int radius) {
		List<Block> sightline = who.getLineOfSight(null, radius);
		int i = 0;
		for(Block b : sightline) {
			if(b.getLocation().equals(what.getEyeLocation().getBlock().getLocation())) return i;
			if(b.getLocation().equals(what.getLocation().getBlock().getLocation())) return i;
			i++;
		}
		return -1;
	}
	
	public String getName() {
		return title;
	}
	
	public void setTeleporter(Player who) {
		if(type == TargetType.MOB) return;
		if(teleportees.size() != 1) return;
		LivingEntity e = teleportees.get(0);
		if(who.equals(e))
			type = TargetType.SELF;
		else type = TargetType.OTHER;
	}
	
	public TargetType getType() {
		return type;
	}
}
