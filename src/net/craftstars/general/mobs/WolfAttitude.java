package net.craftstars.general.mobs;

import java.util.HashMap;

import net.craftstars.general.util.LanguageText;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

public class WolfAttitude extends MobData {
	enum Attitude {
		WILD() {
			@Override
			public boolean hasPermission(CommandSender byWhom) {
				return true;
			}
		},
		ANGRY() {
			@Override
			public boolean hasPermission(CommandSender byWhom) {
				return Toolbox.hasPermission(byWhom, "general.mobspawn.wolf.angry","general.mobspawn.neutral.angry");
			}
		},
		TAME() {
			@Override
			public boolean hasPermission(CommandSender byWhom) {
				return Toolbox.hasPermission(byWhom, "general.mobspawn.wolf.tamed");
			}
		};
		private static HashMap<String, Attitude> mapping = new HashMap<String, Attitude>();
		public abstract boolean hasPermission(CommandSender byWhom);
		
		static void setup() {
			mapping.clear();
			for(Attitude x : values())
				mapping.put(x.toString().toLowerCase(), x);
		}
		
		public static Attitude match(String name) {
			return mapping.get(name);
		}
		
		public String getName() {
			String name = toString().toLowerCase();
			try {
				return MobType.WOLF.getDataList(name)[0];
			} catch(IndexOutOfBoundsException e) {
				return name;
			}
		}
		
		public void addMappings(String[] dataList) {
			for(String alias : dataList) mapping.put(alias, this);
		}
	}
	private Attitude attitude = Attitude.WILD;
	private Player player;
	
	public static void setup() {
		Attitude.setup();
		for(Attitude att : Attitude.values()) {
			att.addMappings(MobType.WOLF.getDataList(att.toString().toLowerCase()));
		}
	}
	
	@Override
	public boolean hasPermission(CommandSender byWhom) {
		if(Toolbox.hasPermission(byWhom, "general.mobspawn.variants")) return true;
		return attitude.hasPermission(byWhom);
	}
	
	@Override
	public void setForMob(LivingEntity mob) {
		if(!(mob instanceof Wolf)) return;
		Wolf dog = (Wolf) mob;
		switch(attitude) {
		case TAME:
			dog.setTamed(true);
			dog.setOwner(player);
			break;
		case ANGRY:
			dog.setAngry(true);
			break;
		case WILD:
			break;
		}
	}

	@Override
	public void parse(CommandSender setter, String data) {
		attitude = Attitude.match(data);
		if(attitude == null) {
			attitude = Attitude.TAME;
			player = Toolbox.matchPlayer(data);
			if(player == null) {
				if(setter != null)
					Messaging.invalidPlayer(setter, data);
				invalidate();
			}
		} else if(attitude == Attitude.TAME && player == null && setter instanceof Player) {
			player = (Player) setter;
		}
	}

	@Override
	public String getCostNode(String base) {
		return base + "." + attitude.toString().toLowerCase();
	}

	@Override
	public void lacksPermission(CommandSender fromWhom) {
		String node = "general.mobspawn.wolf." + attitude.toString().toLowerCase();
		Messaging.lacksPermission(fromWhom, node, LanguageText.LACK_MOBSPAWN_WOLF, "attitude", attitude.getName());
	}

	@Override
	public String[] getValues() {
		int nAttitudes = Attitude.values().length;
		String[] values = new String[nAttitudes];
		for(int i = 0; i < nAttitudes; i++)
			values[i] = Attitude.values()[i].toString().toLowerCase();
		return values;
	}
	
}
