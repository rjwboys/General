
package net.craftstars.general.items;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

import net.craftstars.general.General;
import net.craftstars.general.text.LanguageText;
import net.craftstars.general.util.Option;
import net.craftstars.general.util.Toolbox;

public final class Items {
	private static Configuration config;
	private static HashMap<String, ItemID> aliases;
	private static HashMap<ItemID, String> names;
	private static HashMap<String, ItemID> hooks;
	
	public static void save() {
		for(String alias : aliases.keySet()) {
			ItemID item = aliases.get(alias);
			String code = Integer.toString(item.getId());
			if(item.getData() != null) code += "/" + item.getData();
			config.setProperty("aliases." + alias, code);
		}
		HashMap<Integer, TreeSet<ItemID>> tmpList = new HashMap<Integer, TreeSet<ItemID>>();
		for(ItemID item : names.keySet()) {
			int id = item.getId();
			if(!tmpList.containsKey(id)) {
				tmpList.put(id, new TreeSet<ItemID>());
			}
			tmpList.get(id).add(item);
		}
		for(int id : tmpList.keySet()) {
			String key = "names.item" + id;
			if(tmpList.get(id).size() == 1)
				config.setProperty(key, names.get(tmpList.get(id).first()));
			else {
				ArrayList<String> theseNames = new ArrayList<String>();
				for(ItemID item : tmpList.get(id)) {
					if(item.getData() == null) {
						theseNames.add(null);
						theseNames.add(names.get(item));
						break; // even if there are further entries, they are anomalies and we don't care about them
						// Actually, having further entries at this point would be a bug since IDs with null data
						// compare higher than IDs with non-null data.
					} else {
						while(theseNames.size() <= item.getData())
							theseNames.add("");
						theseNames.set(item.getData(), names.get(item));
					}
				}
				config.setProperty(key, theseNames);
			}
		}
		final int NAME = 0, TYPE = 1;
		for(String hook : hooks.keySet()) {
			String[] split = hook.split(":");
			String key = "hooks." + split[NAME] + "." + split[TYPE];
			ItemID item = hooks.get(hook);
			String code = Integer.toString(item.getId());
			if(item.getData() != null) code += "/" + item.getData();
			config.setProperty(key, code);
		}
		config.save();
	}
	
	private static void loadConfig() {
		try {
			File dataFolder = General.plugin.getDataFolder();
			if(!dataFolder.exists()) dataFolder.mkdirs();
			File configFile = new File(dataFolder, "items.yml");
			
			if(!configFile.exists()) General.plugin.createDefaultConfig(configFile);
			config = new Configuration(configFile);
			config.load();
		} catch(Exception ex) {
			General.logger.warn(LanguageText.LOG_CONFIG_ERROR.value("file", "items.yml"), ex);
		}
	}
	
	public static void setup() {
		Properties itemsdb = new Properties();
		loadConfig();
		try {
			itemsdb.load(new FileInputStream("items.db"));
		} catch(Exception ex) {}
		aliases = new HashMap<String, ItemID>();
		names = new HashMap<ItemID, String>();
		
		// This loads in the item names from items.yml
		loadItemNames();
		
		// try {
		loadItemAliases();
		// } catch(Exception x) {
		// General.logger.error(x.getMessage());
		// }
		
		// Load the "hooks" as well.
		loadHooks();
	}
	
	private static void loadHooks() {
		hooks = new HashMap<String, ItemID>();
		try {
			for(String key : config.getKeys("hooks")) {
				for(String val : config.getNode("hooks").getKeys(key)) {
					String x = config.getNode("hooks").getNode(key).getString(val);
					ItemID thisItem = Items.validate(x);
					if(thisItem == null) {
						General.logger.warn(LanguageText.LOG_ITEM_BAD_HOOK.value("hook", x));
					} else {
						hooks.put(key + ":" + val, thisItem);
					}
				}
			}
		} catch(NullPointerException x) {
			General.logger.warn(LanguageText.LOG_ITEM_NO_HOOKS.value());
		}
	}
	
	private static Pattern itemPat = Pattern.compile("([0-9]+)(?:[.,:/|]([0-9]+))?");
	private static void loadItemAliases() {
		List<String> ymlAliases = config.getKeys("aliases");
		if(ymlAliases == null) {
			General.logger.warn(LanguageText.LOG_ITEM_NO_ALIASES.value());
			return;
		}
		for(String alias : ymlAliases) {
			ItemID val;
			String code = config.getString("aliases." + alias);
			if(code == null) {
				General.logger.warn(LanguageText.LOG_ITEM_BAD_KEY.value());
				continue;
			}
			Matcher m = itemPat.matcher(code);
			if(!m.matches()) continue;
			int num = 0, data;
			boolean problem = false;
			try {
				num = Integer.valueOf(m.group(1));
			} catch(NumberFormatException x) {
				problem = true;
			}
			if(m.groupCount() > 1 && m.group(2) != null) {
				try {
					data = Integer.valueOf(m.group(2));
				} catch(NumberFormatException x) {
					General.logger.warn(LanguageText.LOG_ITEM_BAD_ALIAS.value("alias", m.group(1) + ":" + m.group(2)));
					continue;
				}
				val = new ItemID(num, data);
			} else if(problem) {
				General.logger.warn(LanguageText.LOG_ITEM_BAD_ALIAS.value("alias", m.group(1)));
				continue;
			} else val = new ItemID(num, null);
			aliases.put(alias, val);
		}
	}
	
	private static void loadItemNames() {
		int invalids = 0;
		String lastInvalid = null;
		List<String> keys;
		try {
			keys = config.getKeys("names");
		} catch(NullPointerException x) {
			General.logger.warn(LanguageText.LOG_ITEM_NO_NAMES.value());
			return;
		}
		if(keys == null) {
			General.logger.warn(LanguageText.LOG_ITEM_BAD_NAMES.value());
		} else {
			for(String id : keys) {
				int num;
				ItemID key;
				String name;
				try {
					num = Integer.valueOf(id.substring(4));
				} catch(NumberFormatException x) {
					lastInvalid = id;
					invalids++;
					continue;
				}
				List<String> list = config.getStringList("names." + id, null);
				if(list.isEmpty()) {
					name = config.getString("names." + id);
					key = new ItemID(num, null);
					names.put(key, name);
				} else {
					boolean atEnd = false;
					for(int i = 0; i < list.size(); i++) {
						name = list.get(i);
						if(atEnd) {
							key = new ItemID(num, null);
							names.put(key, name);
							break; // Even if there are additional items, they are meaningless at this point; skip
									// them.
						} else {
							if(name == null) {
								atEnd = true;
								continue;
							}
							key = new ItemID(num, i);
							names.put(key, name);
						}
					}
				}
			}
		}
		if(invalids > 0)
			General.logger.warn(LanguageText.LOG_ITEM_BAD_NAME.value("name", lastInvalid));
	}
	
	/**
	 * Returns the name of the item stored in the hashmap or the item name stored in the items.txt file in the hMod
	 * folder.
	 * 
	 * @param longKey The item ID and data
	 * @return Canonical name
	 */
	public static String name(ItemID longKey) {
		if(names.containsKey(longKey)) {
			return names.get(longKey);
		}
		
		// This is a redundant lookup if longKey already has null data, but that shouldn't create significant
		// overhead
		ItemID shortKey = longKey.clone().setData(null);
		if(names.containsKey(shortKey)) {
			return names.get(shortKey);
		}
		
		for(Material item : Material.values()) {
			if(item.getId() == longKey.getId()) {
				return Toolbox.formatItemName(item.toString());
			}
		}
		
		return longKey.toString();
	}
	
	/**
	 * Validate the string for an item
	 * 
	 * Valid formats:
	 * <ul>
	 * <li>[ID]</li>
	 * <li>[alias]</li>
	 * <li>[richalias]</li>
	 * <li>[ID]:[data]</li>
	 * <li>[alias]:[data]</li>
	 * <li>[ID]:[variant]</li>
	 * <li>[alias]:[variant]</li>
	 * <li>[hook]:[subset]</li>
	 * </ul>
	 * 
	 * Where : can be any of .,:/| and the variables are as follows:
	 * <dl>
	 * <dt>[ID]</dt>
	 * <dd>the numeric ID of an item</dd>
	 * <dt>[alias]</dt>
	 * <dd>an alias for the item, as defined in items.db</dd>
	 * <dt>[richalias]</dt>
	 * <dd>an alias for the item combined with a data value, as defined in items.db</dd>
	 * <dt>[data]</dt>
	 * <dd>the numeric data value for the item</dd>
	 * <dt>[variant]</dt>
	 * <dd>a variant name for the item, as defined in the variants section of items.yml</dd>
	 * <dt>[hook]</dt>
	 * <dd>a hook name, as defined in the hooks section of items.yml</dd>
	 * <dt>[subset]</dt>
	 * <dd>a subset name, as defined in the hooks section of items.yml</dd>
	 * </dl>
	 * 
	 * @param item A string representing an item, either by name or by ID.
	 * @return null if false, the 2-part ID if true; a data value of -1 if the item is valid but the data isn't
	 */
	public static ItemID validate(String item) {
		ItemID ret;
		// First figure out what the data and ID are.
		if(Pattern.matches("([a-zA-Z0-9_'-]+)", item)) {
			ret = validateShortItem(item);
			if(ret == null) return ret;
		} else {
			try {
				String[] parts = item.split("[.,:/\\|]");
				ret = validateLongItem(parts[0], parts[1]);
			} catch(ArrayIndexOutOfBoundsException x) {
				return null;
			}
		}
		
		// Was a valid data/id obtained? If not, we're done; it's invalid.
		if(ret == null || !ret.isValid()) return ret;
		
		// Make sure it's the ID of a valid item.
		Material check = Material.getMaterial(ret.getId());
		if(check == null) return ret.invalidate(false);
		
		// Make sure the damage value is valid.
		ItemData data = ItemData.getData(check);
		if(!data.validate(ret, check)) ret.invalidate(true);
		
		ret.setName();
		
		return ret;
	}
	
	private static ItemID validateLongItem(String item, String data) {
		ItemID ret = validateShortItem(item);
		if(ret == null) { // If it wasn't valid as a short item, check the hooks.
			String key = item + ":" + data;
			if(hooks.containsKey(key)) {
				return hooks.get(key);
			}
		} else if(ret.getData() != null) { // This means a "richalias" was used, which includes the data value.
			ret.invalidate(true); // No data value is valid with a "richalias".
		} else {
			try {
				ret.setData(Integer.valueOf(data));
			} catch(NumberFormatException x) {
				ret.setData(findVariant(ret.getId(), data)).setVariant(data);
			}
		}
		return ret;
	}
	
	private static ItemID validateShortItem(String item) {
		ItemID ret = null;
		try {
			ret = new ItemID(Integer.valueOf(item));
		} catch(NumberFormatException x) {
			if(aliases == null)
				General.logger.error("aliases is null");
			else for(String alias : aliases.keySet()) {
				if(!alias.equalsIgnoreCase(item)) continue;
				ret = new ItemID(aliases.get(alias));
				ret.setName(alias, true);
			}
			if(ret == null) {
				for(Material material : Material.values()) {
					String mat = material.toString();
					if(mat.equalsIgnoreCase(item) || mat.replace("_", "-").equalsIgnoreCase(item)
							|| mat.replace("_", "").equalsIgnoreCase(item)) {
						ret = new ItemID(material).setName(material.toString(), true);
					}
				}
			}
		}
		return ret;
	}
	
	private static Integer findVariant(int id, String data) {
		// Special case for maps
		if(id == Material.MAP.getId() && data.startsWith("z")) {
			int zoom = 90000;
			try {
				zoom += Integer.parseInt(data.substring(1));
				return zoom;
			} catch(NumberFormatException e) {}
		}
		ConfigurationNode thisItem = config.getNode("variants.item" + Integer.toString(id));
		if(thisItem == null) return null;
		int i = 0;
		List<String> thisVariant;
		do {
			thisVariant = thisItem.getStringList("type" + Integer.toString(i), null);
			if(thisVariant.contains(data)) return i;
			i++;
		} while(!thisVariant.isEmpty());
		ItemData dataType = ItemData.getData(Material.getMaterial(id));
		return dataType.fromName(data);
	}
	
	public static boolean checkID(int id) {
		for(Material item : Material.values()) {
			if(item.getId() == id) {
				return true;
			}
		}
		return false;
	}
	
	public static int maxStackSize(int id) {
		return Material.getMaterial(id).getMaxStackSize();
	}
	
	public static void giveItem(Player who, ItemID x, Integer amount) {
		PlayerInventory i = who.getInventory();
		HashMap<Integer, ItemStack> excess = i.addItem(x.getStack(amount, who));
		for(ItemStack leftover : excess.values())
			who.getWorld().dropItemNaturally(who.getLocation(), leftover);
	}
	
	public static void setItemName(ItemID id, String name) {
		names.put(id, name);
	}
	
	public static List<String> variantNames(ItemID id) {
		if(id != null && id.getData() != null)
			return config.getStringList("variants.item" + id.getId() + ".type" + id.getData(), null);
		return null;
	}
	
	public static void addVariantName(ItemID id, String name) {
		if(id != null && id.getData() != null) {
			List<String> variants = variantNames(id);
			variants.add(name);
			setVariantNames(id, variants);
		}
	}
	
	public static void removeVariantName(ItemID id, String name) {
		if(id != null && id.getData() != null) {
			List<String> variants = variantNames(id);
			variants.remove(name);
			setVariantNames(id, variants);
		}
	}
	
	public static void setVariantNames(ItemID id, List<String> variants) {
		config.setProperty("variants.item" + id.getId() + ".type" + id.getData(), variants);
	}
	
	public static void addAlias(String name, ItemID id) {
		aliases.put(name, id);
	}
	
	public static void removeAlias(String name) {
		aliases.remove(name);
	}
	
	public static ItemID getAlias(String name) {
		for(String x : aliases.keySet()) {
			if(x.equalsIgnoreCase(name)) return aliases.get(x);
		}
		return null;
	}
	
	public static ItemID getHook(String main, String sub) {
		return hooks.get(main + ":" + sub);
	}
	
	public static void setHook(String main, String sub, ItemID id) {
		hooks.put(main + ":" + sub, id);
	}

	public static boolean dataEquiv(ItemID id, int data) {
		if(ToolDamage.isDamageable(id.getId())) return true;
		if(id.getData() == null) return true;
		return data == id.getData();
	}
	
	public static String getPersistentName(ItemID id) {
		Material material = Material.getMaterial(id.getId());
		String itemName = material.toString();
		if(id.getData() == null) return itemName;
		ItemData data = ItemData.getData(material);
		String dataName = data.getName(id.getData());
		if(dataName.equals("0")) return itemName;
		return itemName + '/' + dataName;
	}

	public static void setGroupItems(String groupName, List<String> groupItems) {
		ArrayList<Integer> items = new ArrayList<Integer>();
		for(String item : groupItems) {
			ItemID thisItem = validate(item);
			if(thisItem.isValid()) items.add(thisItem.getId());
		}
		Option.GROUP(groupName).set(items);
	}

	public static List<Integer> groupItems(String groupName) {
		return Option.GROUP(groupName).get();
	}

	public static void addGroupItem(String groupName, String item) {
		List<Integer> group = groupItems(groupName);
		ItemID thisItem = validate(item);
		if(thisItem.isValid()) group.add(thisItem.getId());
		Option.GROUP(groupName).set(group);
	}

	public static void removeGroupItem(String groupName, String item) {
		List<Integer> group = groupItems(groupName);
		ItemID thisItem = validate(item);
		if(thisItem.isValid()) group.remove(thisItem.getId());
		Option.GROUP(groupName).set(group);
	}
}
