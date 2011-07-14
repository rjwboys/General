
package net.craftstars.general.items;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

import net.craftstars.general.General;

public class Kits {
	public static HashMap<String, Kit> kits = new HashMap<String, Kit>();
	public static HashMap<GotKit, Long> players = new HashMap<GotKit, Long>();
	
	public static boolean loadKits() {
		kits.clear();
		File folder = General.plugin.getDataFolder();
		File oldKits = new File(folder, "general.kits");
		if(oldKits.exists()) loadOldKits();
		File kitsFile = new File(folder, "kits.yml");
		if(!kitsFile.exists()) return !kits.isEmpty();
		Configuration kitsYml = new Configuration(kitsFile);
		kitsYml.load();
		for(String key : kitsYml.getKeys()) {
			ConfigurationNode kitNode = kitsYml.getNode(key);
			List<?> itemsNode = kitNode.getList("items");
			if(itemsNode == null) {
				General.logger.warn("Kit '" + key + "' has no items and has been skipped.");
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
					General.logger.warn("Kit '" + key + "' has an invalid item '" + id + "' which has been skipped.");
					continue;
				}
				kit.add(item, amount);
			}
			kits.put(key, kit);
		}
		return true;
	}

	private static void warnMalformed(String kit, Object id) {
		General.logger.warn("Kit '" + kit + "' has a malformed entry: \"" + id.toString() + "\"");
	}
	
	@Deprecated
	public static boolean loadOldKits() {
		boolean foundAnException = false;
		Exception exceptionToShow = new Exception("If you see this, something's VERY wrong.");
		try {
			File dataFolder = General.plugin.getDataFolder();
			BufferedReader br = new BufferedReader(new FileReader(new File(dataFolder, "general.kits")));
			String l;
			int lineNumber = 1;
			kits.clear();
			String list;
			String[] listing;
			Pattern idPat = Pattern.compile("^([0-9a-zA-Z_']+).*$");
			Pattern dataPat = Pattern.compile("^.*\\+([0-9a-zA-Z_']+).*$");
			Pattern nPat = Pattern.compile("^.*-([0-9a-zA-Z]+).*$");
			while( (l = br.readLine()) != null) {
				list = l.trim();
				if(!list.startsWith("#") && !list.isEmpty()) {
					listing = list.split(":");
					try {
						int delay = Integer.valueOf(listing[2]);
						String[] stuff = listing[1].split(",");
						Kit theKit = new Kit(listing[0], delay, 0);
						for(String item : stuff) {
							int n = 1;
							String id, data = "";
							Matcher m;
							item = item.trim();
							m = idPat.matcher(item);
							if(m.matches()) {
								id = m.group(1);
							} else throw new InputMismatchException(item);
							m = dataPat.matcher(item);
							if(m.matches()) {
								data = m.group(1);
							}
							m = nPat.matcher(item);
							if(m.matches()) {
								n = Integer.valueOf(m.group(1));
							}
							ItemID type;
							if(data.isEmpty())
								type = Items.validate(id);
							else type = Items.validate(id + ":" + data);
							if(type == null || !type.isValid())
								throw new IllegalArgumentException(id + ":" + data + ", null: " + (item == null));
							theKit.add(new ItemID(type), n);
						}
						kits.put(listing[0].toLowerCase(), theKit);
						if(listing.length > 3) {
							General.logger.warn("Note: line " + lineNumber
									+ " in general.kits has more than three components; excess ignored");
						}
					} catch(Exception x) {
						General.logger.warn("Note: line " + lineNumber
								+ " in general.kits is improperly defined and is ignored (" + x.getClass().getName()
								+ ", " + x.getMessage() + ")");
						if(!foundAnException) {
							foundAnException = true;
							exceptionToShow = x;
						}
					}
				}
				lineNumber++;
			}
		} catch(Exception e) {
			General.logger.warn("An error occured: either general.kits does not exist or it could not be read; kits ignored");
			return false;
		}
		if(foundAnException) {
			General.logger.error("First exception loading the kits:");
			exceptionToShow.printStackTrace();
		}
		save();
		General.logger.info("A general.kits file was found and converted to the new kits.yml format. You may now delete the general.kits file without information loss.");
		// Return success
		return true;
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
				if(kit.get(item) != 1) {
					HashMap<String, Integer> entry = new HashMap<String, Integer>();
					entry.put(itemName, kit.get(item));
					items.add(entry);
				} else items.add(itemName);
			}
			yaml.put("items", items);
			kitsYml.setProperty(key, yaml);
		}
		kitsYml.save();
	}
}
