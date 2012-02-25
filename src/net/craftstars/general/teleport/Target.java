
package net.craftstars.general.teleport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.craftstars.general.mobs.MobType;
import net.craftstars.general.option.Options;
import net.craftstars.general.text.LanguageText;
import net.craftstars.general.text.Messaging;
import net.craftstars.general.util.Toolbox;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class Target {
	private List<LivingEntity> teleportees;
	private World world;
	private TargetType type;
	private String title;
	
	private Target(List<LivingEntity> l, World w, TargetType t) {
		teleportees = l;
		type = t;
		world = w;
		switch(t) {
		case SELF:
			title = LanguageText.TARGET_SELF.value();
		break;
		case OTHER:
			if(l.size() > 1)
				title = LanguageText.TARGET_SEVERAL.value();
			else {
				LivingEntity entity = l.get(0);
				if(entity instanceof Player)
					title = ((Player) entity).getDisplayName();
				else if(entity instanceof HumanEntity)
					title = ((HumanEntity) entity).getName();
				else title = LanguageText.TARGET_SOMEONE.value();
			}
		break;
		case MOBS:
			if(l.size() > 1)
				title = LanguageText.TARGET_NEARBY.value();
			else {
				String entity = MobType.fromEntity(l.get(0)).getName();
				if(entity.isEmpty()) entity = LanguageText.TARGET_MOB.value();
				title = LanguageText.TARGET_ONE_MOB.value("mob", entity);
			}
		break;
		default:
		}
	}
	
	private Target(World w) {
		type = TargetType.WORLD;
		title = w.getName();
		teleportees = new ArrayList<LivingEntity>();
	}
	
	public boolean hasMassPermission(CommandSender sender) {
		boolean canMass;
		if(teleportees.size() > 1) {
			canMass = sender.hasPermission("general.teleport.mass");
			if(!canMass) Messaging.lacksPermission(sender, "general.teleport.mass");
		} else canMass = true;
		return canMass;
	}
	
	public void teleport(Destination to) {
		for(LivingEntity victim : teleportees) {
			float pitch = victim.getLocation().getPitch();
			float yaw = victim.getLocation().getYaw();
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
			loc.setPitch(pitch);
			loc.setYaw(yaw);
			victim.teleport(loc);
			if(victim instanceof Player) {
				Player who = (Player) victim;
				if(type != TargetType.SELF)
					Messaging.send(who, LanguageText.TELEPORT_WHOA.value("destination", to.getName()));
			}
		}
	}
	
	public String getCostClass() {
		return type.getPermission("economy.teleport");
	}
	
	public int count() {
		return teleportees.size();
	}
	
	public static Target get(String targ, Player teleporter) {
		CommandSender notify;
		if(teleporter == null)
			notify = Bukkit.getConsoleSender();
		else notify = teleporter;
		// Is it a player? Optionally prefixed by !
		LivingEntity victim = Toolbox.matchPlayer(targ.replaceFirst("^!", ""));
		if(victim != null) {
			TargetType tt = TargetType.OTHER;
			if(victim.equals(teleporter)) tt = TargetType.SELF;
			return new Target(Arrays.asList(victim), victim.getWorld(), tt);
		}
		// Is it a world? Optionally prefixed by @
		World globe = Toolbox.matchWorld(targ.replaceFirst("^@", ""));
		if(globe != null) {
			List<Player> players = globe.getPlayers();
			return new Target(new ArrayList<LivingEntity>(players), globe, TargetType.OTHER);
		}
		// Is it a wildcard?
		if(targ.equals("*")) {
			ArrayList<LivingEntity> players = new ArrayList<LivingEntity>();
			for(World flat : Bukkit.getWorlds())
				players.addAll(flat.getPlayers());
			return new Target(players, null, TargetType.OTHER);
		}
		// Is it a list of players?
		if(targ.contains(",")) {
			String[] split = targ.split(",");
			ArrayList<LivingEntity> players = new ArrayList<LivingEntity>();
			for(String p : split)
				players.add(Toolbox.matchPlayer(p));
			return new Target(players, null, TargetType.OTHER);
		}
		if(teleporter != null) {
			// Is it a special keyword?
			int range = Options.SUMMON_RANGE.get();
			if(Toolbox.equalsOne(targ, "near", "$near")) {
				List<Entity> near = teleporter.getNearbyEntities(range, range, range);
				ArrayList<LivingEntity> players = new ArrayList<LivingEntity>();
				for(Entity what : near) {
					if(what instanceof Player) players.add((LivingEntity) what);
				}
				if(players.size() > 0) {
					return new Target(players, teleporter.getWorld(), TargetType.OTHER);
				} else {
					Messaging.send(notify, LanguageText.TARGET_NO_PLAYERS);
				}
			}
			if(Toolbox.equalsOne(targ, "nearmob", "$nearmob")) {
				List<Entity> near = teleporter.getNearbyEntities(range, range, range);
				ArrayList<LivingEntity> victims = new ArrayList<LivingEntity>();
				for(Entity what : near) {
					if(what instanceof LivingEntity && ! (what instanceof Player)) victims.add((LivingEntity) what);
				}
				if(victims.size() > 0) {
					return new Target(victims, teleporter.getWorld(), TargetType.MOBS);
				} else {
					Messaging.send(notify, LanguageText.TARGET_NO_MOBS);
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
					else tt = TargetType.MOBS;
					return new Target(Arrays.asList(victim), victim.getWorld(), tt);
				} else {
					Messaging.send(notify, LanguageText.TARGET_NO_TARGET);
				}
			}
			if(Toolbox.equalsOne(targ, "self", "$self", "me")) return fromPlayer(teleporter);
		}
		// No more ideas, so just give up.
		Messaging.send(notify, LanguageText.TARGET_BAD.value());
		return null;
	}
	
	public static Target fromPlayer(Player player) {
		return new Target(Arrays.asList((LivingEntity) player), player.getWorld(), TargetType.SELF);
	}
	
	public static Target fromWorld(World world) {
		return new Target(world);
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
		if(type == TargetType.MOBS) return;
		if(teleportees.size() != 1) return;
		LivingEntity e = teleportees.get(0);
		if(who.equals(e))
			type = TargetType.SELF;
		else type = TargetType.OTHER;
	}
	
	public TargetType getType() {
		return type;
	}

	public String getPermission(String base) {
		return type.getPermission(base);
	}

	public void makeOther() {
		if(type == TargetType.SELF) type = TargetType.OTHER;
	}

	public World getWorld() {
		return world;
	}
}
