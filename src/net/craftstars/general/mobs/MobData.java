package net.craftstars.general.mobs;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;

public abstract class MobData {
	public abstract boolean hasPermission(CommandSender sender);
	public abstract void setForMob(LivingEntity mob);
	public abstract void parse(CommandSender setter, String data);
	public abstract String getCostNode(String baseNode);
	public abstract void lacksPermission(CommandSender sender);
	public abstract String[] getValues();
	
	protected boolean valid = true;
	
	public static MobData parse(MobType mob, CommandSender setter, String data) {
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
	
	public String getBasic() {
		return getValues()[0];
	}

	private final static String[] noneValues = new String[] {"regular"};
	public static final MobData none = new MobData() {
		@Override
		public boolean hasPermission(CommandSender byWhom) {
			return true;
		}

		@Override
		public void setForMob(LivingEntity mob) {}

		@Override
		public void parse(CommandSender setter, String data) {}

		@Override
		public String getCostNode(String node) {
			return node + ".regular";
		}

		@Override
		public void lacksPermission(CommandSender sender) {}

		@Override
		public String[] getValues() {
			return noneValues.clone();
		}
	};
}
