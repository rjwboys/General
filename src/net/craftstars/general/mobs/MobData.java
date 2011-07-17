package net.craftstars.general.mobs;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public abstract class MobData {
	public abstract boolean hasPermission(Player byWhom);
	public abstract void setForMob(LivingEntity mob);
	public abstract void parse(Player setter, String data);
	public abstract String getCostNode(String baseNode);
	public abstract void lacksPermission(Player fromWhom);
	public abstract String[] getValues();
	
	protected boolean valid = true;
	
	public static MobData parse(MobType mob, Player setter, String data) {
		MobData instance = mob.getNewData();
		try {
			instance.parse(setter, data);
			if(instance.isValid()) return instance;
		} catch(IllegalArgumentException e) {
		} catch(SecurityException e) {
		}
		return null;
	}
	
	protected void invalidate() {
		valid = false;
	}
	
	protected boolean isValid() {
		return valid;
	}

	private final static String[] noneValues = new String[] {"regular"};
	public static final MobData none = new MobData() {
		@Override
		public boolean hasPermission(Player byWhom) {
			return true;
		}

		@Override
		public void setForMob(LivingEntity mob) {}

		@Override
		public void parse(Player setter, String data) {}

		@Override
		public String getCostNode(String node) {
			return node + ".regular";
		}

		@Override
		public void lacksPermission(Player fromWhom) {}

		@Override
		public String[] getValues() {
			return noneValues.clone();
		}
	};
}
