package net.craftstars.general.items;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.util.config.ConfigurationNode;

import net.craftstars.general.General;

public class PotionData extends ItemData {
	private static final String[] potions = {
		"Nothing","Regeneration","Swiftness","Fire Resistance","Poison","Healing","Nothing","Nothing",
		"Weakness","Strength","Slowness","Nothing","Harming","Nothing","Nothing","Nothing"
	};
	private static final int SPLASH = 0x4000, EXTEND = 0x40, IMPROVE = 0x20;
	
	@Override
	public boolean validate(ItemID id, Material check) {
		// TODO Auto-generated method stub
		return true;
	}
	
	private static int isSplash(int data) {
		return data & SPLASH;
	}
	
	private static int isExtended(int data) {
		return data & EXTEND;
	}
	
	private static int isImproved(int data) {
		return data & IMPROVE;
	}
	
	public String getName(int data) {
		StringBuilder name = new StringBuilder();
		if(isSplash(data) > 0) name.append("Splash ");
		name.append("Potion of ");
		name.append(potions[data & 0xF]);
		if(isExtended(data) > 0) name.append(" (X)");
		else if(isImproved(data) > 0) name.append("(II)");
		return name.toString();
	}
	
	public Integer fromName(String name) {
		int data = 0x2000;
		name = name.toLowerCase();
		if(name.startsWith("splash")) data |= SPLASH;
		if(name.endsWith("+") || name.endsWith("(X)")) data |= EXTEND;
		else if(name.endsWith("2") || name.endsWith("(II)")) data |= IMPROVE;
		name = name.replaceAll("splash|\\+|\\d|\\((X|II)\\)", "");
		General.logger.info("Trying to give potion: " + name);
		for(int i = 0; i < 16; i++) {
			String key = potions[i].toLowerCase().replace(" ", "");
			List<String> thisItem = Items.getPotions(key);
			if(thisItem.contains(name) || key.equals(name)) data |= i;
		}
		return data;
	}
}
