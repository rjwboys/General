package net.craftstars.general.items;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import net.craftstars.general.util.range.IntRange;

public class EnchantData extends ItemData {
	
	protected EnchantData() {}
	
	@Override
	public boolean validate(int data) {
		Enchantment ench = Enchantment.getById(data);
		if(ench == null) return false;
		return ench.canEnchantItem(new ItemStack(material, 1));
	}
	
	@Override
	public String getName(int data) {
		Enchantment ench = Enchantment.getById(data);
		if(ench != null) return ench.getName();
		return super.getName(data);
	}
	
	@Override
	public int fromName(String data) {
		int id = listContainsId("enchant", data, new IntRange(0, 51));
		if(id >= 0) return id;
		return super.fromName(data);
	}
}
