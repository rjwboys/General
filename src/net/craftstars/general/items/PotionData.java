package net.craftstars.general.items;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.potion.Potion;

public class PotionData extends ItemData {
	@SuppressWarnings("hiding")
	private enum Suffix {SPLASH, EXTEND, IMPROVE, NONE_THIS_LOOP}
	private static final int SPLASH = 0x4000, EXTEND = 0x40, IMPROVE = 0x20, BASIC = 0x2000;

	protected PotionData() {}
	
	@Override
	public boolean validate(int data) {
		Potion potion = Potion.fromDamage(data);
		// TODO: Currently this does nothing; update it for new API
		try {
			potion.setHasExtendedDuration(potion.hasExtendedDuration());
			if(potion.getType() != null) potion.setTier(potion.getTier());
		} catch(IllegalArgumentException e) {
			return false;
		}
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
	
	@Override
	public String getName(int data) {
		if(data == 0) return "waterbottle";
		boolean mundane = false;
		List<String> list = Items.variantNames("potion.basic", data & 0xF);
		if(list == null || list.isEmpty()) {
			list = Items.variantNames("potion.mundane", data & 0x3F);
			mundane = true;
		}
		if(list == null || list.isEmpty()) return super.getName(data);
		StringBuilder name = new StringBuilder();
		name.append(list.get(0));
		if(isExtended(data) > 0) name.append("x");
		else if(!mundane && isImproved(data) > 0) name.append("2");
		if(isSplash(data) > 0) name.append("!");
//		if(isSplash(data) > 0) name.append("Splash ");
//		name.append("Potion of ");
//		name.append(potions[data & 0xF]);
//		if(isExtended(data) > 0) name.append(" (X)");
//		else if(isImproved(data) > 0) name.append("(II)");
		return name.toString();
	}
	
	@Override
	protected int parseData(String name) {
		if(name.matches("[0-9]+")) return super.parseData(name);
		name = name.toLowerCase();
		if(listContains("potion.zero", name, Arrays.asList("waterbottle"))) return 0;
		int data = BASIC;
		List<String> splash = Items.variantNames("potion.mod.splash");
		if(splash == null) splash = Arrays.asList("splash","!");
		splash = allToLower(splash);
		List<String> extend = Items.variantNames("potion.mod.time");
		if(extend == null) extend = Arrays.asList("+","X","(X)");
		extend = allToLower(extend);
		List<String> improve = Items.variantNames("potion.mod.strength");
		if(improve == null) improve = Arrays.asList("2","II","(II)");
		improve = allToLower(improve);
		Set<Suffix> status = EnumSet.noneOf(Suffix.class);
		// Try splash prefix first
		for(String prefix : splash) {
			if(name.startsWith(prefix)) {
				data |= SPLASH;
				status.add(Suffix.SPLASH);
				name = name.replace(prefix, "");
				break;
			}
		}
		while(!status.contains(Suffix.NONE_THIS_LOOP)) {
			status.add(Suffix.NONE_THIS_LOOP);
			// Splash suffix first
			if(!status.contains(Suffix.SPLASH))
				for(String suffix : splash) {
					if(name.endsWith(suffix)) {
						data |= SPLASH;
						data &= ~BASIC;
						status.add(Suffix.SPLASH);
						status.remove(Suffix.NONE_THIS_LOOP);
						name = name.replace(suffix, "");
						break;
					}
				}
			// Improve suffix
			if(!status.contains(Suffix.IMPROVE))
				for(String suffix : improve) {
					if(name.endsWith(suffix)) {
						data |= IMPROVE;
						status.add(Suffix.IMPROVE);
						status.remove(Suffix.NONE_THIS_LOOP);
						name = name.replace(suffix, "");
						break;
					}
				}
			// Extend suffix
			if(!status.contains(Suffix.EXTEND))
				for(String suffix : extend) {
					if(name.endsWith(suffix)) {
						data |= EXTEND;
						status.add(Suffix.EXTEND);
						status.remove(Suffix.NONE_THIS_LOOP);
						name = name.replace(suffix, "");
						break;
					}
				}
		}
		// Now the potion name itself
		for(int i = 0; i < 16; i++) {
			List<String> list = Items.variantNames("potion.basic", i);
			if(list == null || list.isEmpty()) continue;
			list = allToLower(list);
			if(list.contains(name)) {
				data |= i;
				return data;
			}
		}
		// Try basic names if we didn't find an actually valid potion
		for(int i = 0; i < 64; i++) {
			List<String> list = Items.variantNames("potion.mundane", i);
			if(list == null || list.isEmpty()) continue;
			list = allToLower(list);
			if(list.contains(name)) data |= i;
		}
//		if(name.startsWith("splash")) data |= SPLASH;
//		if(name.endsWith("+") || name.endsWith("(X)")) data |= EXTEND;
//		else if(name.endsWith("2") || name.endsWith("(II)")) data |= IMPROVE;
//		name = name.replaceAll("splash|\\+|\\d|\\((X|II)\\)", "");
//		General.logger.info("Trying to give potion: " + name);
//		for(int i = 0; i < 16; i++) {
//			String key = potions[i].toLowerCase().replace(" ", "");
//			List<String> thisItem = Items.getPotions(key);
//			if(thisItem.contains(name) || key.equals(name)) data |= i;
//		}
		return data;
	}
	
	@Override
	public int init(int data, Player who) {
		if(data == 0) return 0;
		return data | 0x2000;
	}
}
