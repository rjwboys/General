
package net.craftstars.general.items;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;

import net.craftstars.general.General;
import net.craftstars.general.items.Kit.Key;
import net.craftstars.general.text.LanguageText;

public final class Kits {
	static HashMap<String, Kit> kits = new HashMap<String, Kit>();
	private Kits() {}
	
	public static void load() {
		kits.clear();
		File folder = General.plugin.getDataFolder();
		File kitsFile = new File(folder, "kits.yml");
		if(!kitsFile.exists()) return;
		YamlConfiguration kitsYml = YamlConfiguration.loadConfiguration(kitsFile);
		for(String key : kitsYml.getKeys(false)) {
			ConfigurationSection kitNode = kitsYml.getConfigurationSection(key);
			List<?> itemsNode = kitNode.getList("items");
			if(itemsNode == null) {
				General.logger.warn(LanguageText.LOG_KIT_NO_ITEMS.value("kit", key));
				continue;
			}
			int delay = kitNode.getInt("delay", 0);
			double cost = kitNode.getDouble("cost", 0.0);
			Kit kit = new Kit(key, delay, cost);
			for(Object id : itemsNode) {
				String itemName;
				int amount = 1;
				Map<Enchantment,Integer> ench = null;
				Map<?,?> parseEnch = null;
				if(id instanceof String)
					itemName = (String) id;
				else if(id instanceof Integer)
					itemName = ((Integer) id).toString();
				else if(id instanceof Map) {
					Map<?,?> map = (Map<?,?>) id;
					if(map.size() != 1) {
						warnMalformed(key, id);
						continue;
					}
					Object[] keys = map.keySet().toArray(), values = map.values().toArray();
					if(keys[0] instanceof String) itemName = (String) keys[0];
					else if(keys[0] instanceof Integer) itemName = ((Integer) keys[0]).toString();
					else {
						warnMalformed(key, id);
						continue;
					}
					if(values[0] instanceof Integer) amount = (Integer) values[0];
					else if(values[0] instanceof Map) {
						parseEnch = (Map<?,?>)values[0];
					} else {
						warnMalformed(key, id);
						continue;
					}
				} else {
					warnMalformed(key, id);
					continue;
				}
				ItemID item;
				try {
					item = Items.validate(itemName);
				} catch(InvalidItemException e) {
					General.logger.warn(LanguageText.LOG_KIT_BAD_ITEM.value("kit", key, "item", id));
					continue;
				}
				if(parseEnch != null) {
					ench = new HashMap<Enchantment,Integer>();
					ItemData enchData = ItemData.enchanting(item.getMaterial());
					for(Entry<?,?> oo : parseEnch.entrySet()) {
						if(!(oo.getKey() instanceof String || oo.getKey() instanceof Integer)) {
							warnMalformed(key, oo.getKey()); // TODO?
							continue;
						}
						String enchName = String.valueOf(oo.getKey());
						int enchID = enchData.fromName(enchName);
						Enchantment magic = Enchantment.getById(enchID);
						if(!enchData.validate(enchID)) {
							General.logger.warn(LanguageText.LOG_KIT_BAD_ENCH.value("kit", key, "ench", enchName,
								"item", id));
							continue;
						}
						int power = 1;
						if(oo.getValue() instanceof Integer) power = (Integer) oo.getValue();
						else if(oo.getValue() != null) {
							warnMalformed(key, oo.getValue()); // TODO?
							continue;
						}
						if(power == 0) power = magic.getMaxLevel();
						if(power > magic.getMaxLevel()) {
							General.logger.warn(LanguageText.LOG_KIT_BAD_ENCH.value("kit", key, "ench", enchName,
								"item", id));
							continue;
						}
						ench.put(magic, power);
					}
				}
				try {
					kit.add(item, amount, ench);
				} catch(IllegalArgumentException e) {
					warnMalformed(key, id); // TODO?
				}
			}
			kits.put(key, kit);
		}
	}

	private static void warnMalformed(String kit, Object id) {
		General.logger.warn(LanguageText.LOG_KIT_BAD.value("kit", kit, "item", String.valueOf(id)));
	}
	
	public static void save() {
		File kitsFile = new File(General.plugin.getDataFolder(), "kits.yml");
		YamlConfiguration kitsYml = new YamlConfiguration();
		for(String key : kits.keySet()) {
			HashMap<String, Object> yaml = new HashMap<String, Object>();
			Kit kit = kits.get(key);
			yaml.put("delay", kit.delay);
			yaml.put("cost", kit.getCost());
			ArrayList<Object> items = new ArrayList<Object>();
			for(Key entry : kit.keySet()) {
				String itemName = Items.getPersistentName(entry.item);
				if(kit.get(entry) != 1)
					items.add(Collections.singletonMap(itemName, kit.get(entry)));
				else if(entry.ench != null) {
					Map<String,Integer> enchantments = new HashMap<String,Integer>();
					ItemData enchant = ItemData.enchanting(entry.item.getMaterial());
					for(Entry<Enchantment,Integer> ench : entry.ench.entrySet())
						enchantments.put(enchant.getName(ench.getKey().getId()), ench.getValue());
					items.add(Collections.singletonMap(itemName, enchantments));
				} else items.add(itemName);
			}
			yaml.put("items", items);
			kitsYml.set(key, yaml);
		}
		try {
			kitsYml.save(kitsFile);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public static Kit get(String name) {
		return kits.get(name);
	}
	
	public static Collection<Kit> all() {
		return kits.values();
	}

	public static boolean exists(String kitName) {
		return kits.containsKey(kitName);
	}

	public static void put(String kitName, Kit kit) {
		kits.put(kitName, kit);
	}

	public static void remove(String kitName) {
		kits.remove(kitName);
	}
}
