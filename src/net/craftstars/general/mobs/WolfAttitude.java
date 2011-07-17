package net.craftstars.general.mobs;

import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

public class WolfAttitude extends MobData {
	enum Attitude {
		WILD("spawn wild wolves", "wild", "calm", "passive", "friendly", "free") {
			@Override
			public boolean hasPermission(Player byWhom) {
				return true;
			}
		},
		ANGRY("spawn angry wolves", "angry", "mad", "hostile", "aggressive") {
			@Override
			public boolean hasPermission(Player byWhom) {
				return Toolbox.hasPermission(byWhom, "general.mobspawn.wolf.angry","general.mobspawn.neutral.angry");
			}
		},
		TAME("spawn tamed wolves", "tame", "pet", "tamed") {
			@Override
			public boolean hasPermission(Player byWhom) {
				return Toolbox.hasPermission(byWhom, "general.mobspawn.wolf.tamed");
			}
		};
		public abstract boolean hasPermission(Player byWhom);
		private String phrase;
		private String[] aliases;
		Attitude(String p, String... names) {
			phrase = p;
			aliases = names;
		}
		public static Attitude match(String name) {
			for(Attitude att : values()) {
				if(Toolbox.equalsOne(name, att.aliases))
					return att;
			}
			return null;
		}
		public String getPhrase() {
			return phrase;
		}
	}

	private Attitude attitude = Attitude.WILD;
	private Player player;
	
	@Override
	public boolean hasPermission(Player byWhom) {
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
	public void parse(Player setter, String data) {
		attitude = Attitude.match(data);
		if(attitude == null) {
			attitude = Attitude.TAME;
			player = Toolbox.matchPlayer(data);
			if(player == null) {
				if(setter != null)
					Messaging.invalidPlayer(setter, data);
				invalidate();
			}
		} else if(attitude == Attitude.TAME && player == null) {
			player = setter;
		}
	}

	@Override
	public String getCostNode(String base) {
		return base + "." + attitude.toString().toLowerCase();
	}

	@Override
	public void lacksPermission(Player fromWhom) {
		Messaging.lacksPermission(fromWhom, attitude.getPhrase());
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
