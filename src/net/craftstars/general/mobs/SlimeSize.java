
package net.craftstars.general.mobs;

import java.util.HashMap;

import net.craftstars.general.text.LanguageText;
import net.craftstars.general.text.Messaging;
import net.craftstars.general.util.Option;

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
		
		static void setup() {
			mapping.clear();
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
		
		public String getName() {
			String name = toString().toLowerCase();
			try {
				return MobType.SLIME.getDataList(name)[0];
			} catch(IndexOutOfBoundsException e) {
				return name;
			}
		}

		public void addMappings(String[] dataList) {
			for(String alias : dataList) mapping.put(alias, this);
		}
	}
	private NamedSize size = NamedSize.MEDIUM;
	private int sz = 0;
	
	public static void setup() {
		NamedSize.setup();
		for(NamedSize size : NamedSize.values()) {
			size.addMappings(MobType.SLIME.getDataList(size.toString().toLowerCase()));
		}
	}
	
	public SlimeSize() {
		super(MobType.SLIME);
	}
	
	@Override
	public boolean hasPermission(CommandSender byWhom) {
		return byWhom.hasPermission(size.getPermission());
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
				invalidate(e, data);
				size = NamedSize.MEDIUM;
			}
		} else sz = size.getSize();
	}

	@Override
	public String getCostNode(String base) {
		String node = base + "." + size.toString().toLowerCase();
		if(Option.nodeExists(node)) return node;
		else return base + ".default";
	}

	@Override
	public void lacksPermission(CommandSender fromWhom) {
		Messaging.lacksPermission(fromWhom, size.getPermission(), LanguageText.LACK_MOBSPAWN_SLIME,
			"size", size.getName());
	}

	@Override
	public String[] getValues() {
		int nSizes = NamedSize.values().length;
		String[] values = new String[nSizes];
		for(int i = 0; i < nSizes; i++)
			values[i] = NamedSize.values()[i].toString().toLowerCase();
		return values;
	}
	
	@Override
	public String getBasic() {
		return "medium";
	}
}