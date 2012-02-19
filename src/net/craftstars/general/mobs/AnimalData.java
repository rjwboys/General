package net.craftstars.general.mobs;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Animals;
import org.bukkit.entity.LivingEntity;

import net.craftstars.general.text.LanguageText;
import net.craftstars.general.util.Toolbox;

public class AnimalData extends MobData {
	private boolean baby;
	private static String[] values = new String[] {"adult","baby"};
	
	public AnimalData(MobType creature) {
		super(creature);
	}
	
	@Override
	public void setForMob(LivingEntity mob) {
		if(!(mob instanceof Animals)) return;
		if(baby) ((Animals)mob).setBaby();
	}
	
	@Override
	public void parse(CommandSender setter, String data) {
		if(Toolbox.equalsOne(data, MobType.getDataList("baby", "baby")))
			baby = true;
		else if(!Toolbox.equalsOne(data, MobType.getDataList("adult", "adult")))
			invalidate(data);
	}
	
	@Override
	public String getCostNode(String baseNode) {
		if(baby) return baseNode + ".baby";
		return baseNode + ".adult";
	}
	
	@Override
	public String[] getValues() {
		return values.clone();
	}

	@Override
	protected String getPermission(String base) {
		return base + "." + (baby ? "baby" : "adult");
	}
	
	@Override
	protected LanguageText getLangKey() {
		if(baby) return null;
		return super.getLangKey();
	}
}
