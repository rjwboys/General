package net.craftstars.general.mobs;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;

import net.craftstars.general.text.LanguageText;
import net.craftstars.general.text.Messaging;

public abstract class MobData {
	public abstract void setForMob(LivingEntity entity);
	public abstract void parse(CommandSender setter, String data);
	public abstract String getCostNode(String baseNode);
	public abstract String[] getValues();
	protected abstract String getPermission(String base);
	
	protected boolean valid = true;
	private MobType mob;
	
	MobData(MobType creature) {
		mob = creature;
	}
	
	public static MobData parse(MobType mob, CommandSender setter, String data) {
		MobData instance = mob.getNewData();
		try {
			instance.parse(setter, data);
			return instance;
		} catch(IllegalArgumentException e) {
		} catch(SecurityException e) {
		}
		return new NoData(mob);
	}
	
	protected void invalidate(String data) {
		throw new InvalidMobException(LanguageText.MOB_BAD_TYPE, "mob", mob.getName(), "type", data);
	}
	
	protected void invalidate(Throwable cause, String data) {
		throw new InvalidMobException(cause, LanguageText.MOB_BAD_TYPE, "mob", mob.getName(), "type", data);
	}
	
	public final boolean hasPermission(CommandSender sender) {
		return sender.hasPermission(getMobPerm());
	}
	
	public String getBasic() {
		return getValues()[0];
	}
	
	protected String getMobPerm() {
		return mob.getPermission();
	}
	
	protected LanguageText getLangKey() {
		return LanguageText.LACK_MOBSPAWN_MOB;
	}
	
	protected Object[] getLangParams() {
		return new Object[] {"mob", mob.getName(), "mobs", mob.getPluralName()};
	}
	
	public final void lacksPermission(CommandSender sender) {
		LanguageText lang = getLangKey();
		if(lang == null) Messaging.lacksPermission(sender, getPermission(getMobPerm()));
		else Messaging.lacksPermission(sender, getPermission(getMobPerm()), lang, getLangParams());
	}
}
