package net.craftstars.general.items;

import org.bukkit.Material;

public class ToolDamage extends ItemData {
	protected ToolDamage() {}
	
	@Override
	public boolean validate(int damage) {
		if(damage != 0) {
			if(!isDamageable(material.getId())) return super.validate(damage);
			else if(damage > material.getMaxDurability()) return super.validate(damage);
		}
		return true;
	}
	
	public static boolean isDamageable(int id) {
		return Material.getMaterial(id).getMaxDurability() != -1;
	}

	@Override
	public int fromName(String data) {
		if(data.endsWith("%")) {
			try {
				int n = Integer.parseInt(data.substring(0, data.length() - 1));
				if(n > 100) return n;
				double percent = n;
				percent /= 100;
				return (int)(percent * material.getMaxDurability());
			} catch(NumberFormatException e) {
				return 0;
			}
		} else return super.fromName(data);
	}
}