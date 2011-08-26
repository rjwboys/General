
package net.craftstars.general.items;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

import net.craftstars.general.General;
import net.craftstars.general.util.LanguageText;

public final class Kits {
	public static HashMap<String, Kit> kits = new HashMap<String, Kit>();
	public static HashMap<GotKit, Long> players = new HashMap<GotKit, Long>();
	
	public static boolean loadKits() {
		kits.clear();
		File folder = General.plugin.getDataFolder();
		File kitsFile = new File(folder, "kits.yml");
		if(!kitsFile.exists()) return !kits.isEmpty();
		Configuration kitsYml = new Configuration(kitsFile);
		kitsYml.load();
		for(String key : kitsYml.getKeys()) {
			ConfigurationNode kitNode = kitsYml.getNode(key);
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
					else {
						warnMalformed(key, id);
						continue;
					}
				} else {
					warnMalformed(key, id);
					continue;
				}
				ItemID item = Items.validate(itemName);
				if(!item.isValid()) {
					General.logger.warn(LanguageText.LOG_KIT_BAD_ITEM.value("kit", key, "item", id));
					continue;
				}
				kit.add(item, amount);
			}
			kits.put(key, kit);
		}
		return true;
	}

	private static void warnMalformed(String kit, Object id) {
		General.logger.warn(LanguageText.LOG_KIT_BAD.value("kit", kit, "item", id.toString()));
	}
	
	public static void save() {
		File kitsFile = new File(General.plugin.getDataFolder(), "kits.yml");
		Configuration kitsYml = new Configuration(kitsFile);
		for(String key : kits.keySet()) {
			HashMap<String, Object> yaml = new HashMap<String, Object>();
			Kit kit = kits.get(key);
			yaml.put("delay", kit.delay);
			yaml.put("cost", kit.getCost());
			ArrayList<Object> items = new ArrayList<Object>();
			for(ItemID item : kit) {
				String itemName = Items.getPersistentName(item);
				if(kit.get(item) != 1)
					items.add(Collections.singletonMap(itemName, kit.get(item)));
				else items.add(itemName);
			}
			yaml.put("items", items);
			kitsYml.setProperty(key, yaml);
		}
		kitsYml.save();
	}
}
