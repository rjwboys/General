package net.craftstars.general.mobs;

import java.util.HashMap;

import net.craftstars.general.text.LanguageText;
import net.craftstars.general.util.Toolbox;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

public class WolfAttitude extends AnimalData {
	enum Attitude {
		WILD, ANGRY, TAMED;
		private static HashMap<String, Attitude> mapping = new HashMap<String, Attitude>();
		
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
	private OfflinePlayer player;
	
	public static void setup() {
		Attitude.setup();
		for(Attitude att : Attitude.values()) {
			att.addMappings(MobType.WOLF.getDataList(att.toString().toLowerCase()));
		}
	}
	
	public WolfAttitude() {
		super(MobType.WOLF);
	}
	
	@Override
	public String getPermission(String base) {
		return super.getPermission(base) + "." + attitude.toString().toLowerCase();
	}
	
	@Override
	public void setForMob(LivingEntity mob) {
		super.setForMob(mob);
		if(!(mob instanceof Wolf)) return;
		Wolf dog = (Wolf) mob;
		switch(attitude) {
		case TAMED:
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
		for(String component : data.split("[.,:/\\|]", 2)) {
			attitude = Attitude.match(component);
			if(attitude == null) {
				try {
					super.parse(setter, component);
				} catch(InvalidMobException e) {
					attitude = Attitude.TAMED;
					player = Toolbox.matchPlayer(component);
					if(player == null) player = Bukkit.getOfflinePlayer(component);
				}
			} else if(attitude == Attitude.TAMED && player == null && setter instanceof Player) {
				player = (Player) setter;
			}
		}
	}

	@Override
	public String getCostNode(String base) {
		return super.getCostNode(base) + "." + attitude.toString().toLowerCase();
	}
	
	@Override
	protected LanguageText getLangKey() {
		return LanguageText.LACK_MOBSPAWN_WOLF;
	}
	
	@Override
	protected Object[] getLangParams() {
		return new Object[] {"attitude", attitude.getName()};
	}

	@Override
	public String[] getValues() {
		int nAttitudes = Attitude.values().length;
		String[] values = new String[nAttitudes];
		for(int i = 0; i < nAttitudes; i++)
			values[i] = Attitude.values()[i].toString().toLowerCase();
		return Toolbox.cartesianProduct(super.getValues(), values, '.');
	}
	
}
