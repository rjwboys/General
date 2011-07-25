
package net.craftstars.general.mobs;

import java.util.HashMap;

import net.craftstars.general.General;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Slime;

public class SlimeSize extends MobData {
	public enum NamedSize {
		COLOSSAL(16), HUGE(8), LARGE(4), MEDIUM(3), SMALL(2), TINY(1);
		private static HashMap<String, NamedSize> mapping = new HashMap<String, NamedSize>();
		private int n;
		
		private NamedSize(int sz) {
			n = sz;
		}
		
		public int getSize() {
			return n;
		}
		
		static {
			for(NamedSize x : values())
				mapping.put(x.toString().toLowerCase(), x);
		}
		
		public static NamedSize fromName(String name) {
			return mapping.get(name.toLowerCase());
		}
		
		public static NamedSize closestMatch(int sz) {
			if(sz <= 0) return MEDIUM;
			NamedSize match = COLOSSAL;
			for(NamedSize check : values()) {
				if(sz <= check.n) match = check;
			}
			return match;
		}

		public String getPermission() {
			return "general.mobspawn.slime." + toString().toLowerCase();
		}
	}
	private NamedSize size = NamedSize.MEDIUM;
	private int sz = 0;

	@Override
	public boolean hasPermission(CommandSender byWhom) {
		return Toolbox.hasPermission(byWhom, "general.mobspawn.variants", size.getPermission());
	}

	@Override
	public void setForMob(LivingEntity mob) {
		if(sz == 0) return; // No data was specified; leave at default
		if(!(mob instanceof Slime)) return;
		Slime goo = (Slime) mob;
		goo.setSize(sz);
	}

	@Override
	public void parse(CommandSender setter, String data) {
		size = NamedSize.fromName(data);
		if(size == null) {
			try {
				sz = Integer.parseInt(data);
				size = NamedSize.closestMatch(sz);
			} catch(NumberFormatException e) {
				invalidate();
				size = NamedSize.TINY;
			}
		} else sz = size.getSize();
		if(size == null) return;
	}

	@Override
	public String getCostNode(String base) {
		String node = base + "." + size.toString().toLowerCase();
		if(Toolbox.nodeExists(General.plugin.config, node)) return node;
		else return base + ".default";
	}

	@Override
	public void lacksPermission(CommandSender fromWhom) {
		Messaging.lacksPermission(fromWhom, "spawn " + size.toString().toLowerCase() + " slimes");
	}

	@Override
	public String[] getValues() {
		int nSizes = NamedSize.values().length;
		String[] values = new String[nSizes];
		for(int i = 0; i < nSizes; i++)
			values[i] = NamedSize.values()[i].toString().toLowerCase();
		return values;
	}
}