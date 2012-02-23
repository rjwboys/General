
package net.craftstars.general.items;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.Potion;
import org.bukkit.potion.Potion.Tier;
import org.bukkit.potion.PotionType;

import net.craftstars.general.General;
import net.craftstars.general.text.LanguageText;
import net.craftstars.general.text.Messaging;
import net.craftstars.general.util.Option;
import net.craftstars.general.util.Toolbox;
import net.craftstars.general.util.range.IntRange;
import net.craftstars.general.util.range.Range;

public final class Items {
	private static FileConfiguration config;
	private static File configFile;
	private static HashMap<String, ItemID> aliases;
	private static HashMap<ItemID, String> names;
	private static HashMap<String, ItemID> hooks;
	private static ConfigurationSection potions;
	private Items() {}
	
	public static void save() {
		for(String alias : aliases.keySet()) {
			ItemID item = aliases.get(alias);
			String code = Integer.toString(item.getId());
			if(item.getData() != null) code += "/" + item.getData();
			config.set("aliases." + alias, code);
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
				config.set(key, names.get(tmpList.get(id).first()));
			else {
				HashMap<String,String> theseNames = new HashMap<String,String>();
				for(ItemID item : tmpList.get(id)) {
					if(item.getData() == null) {
						theseNames.put("generic",names.get(item));
						break; // even if there are further entries, they are anomalies and we don't care about them
						// Actually, having further entries at this point would be a bug since IDs with null data
						// compare higher than IDs with non-null data.
					} else theseNames.put("data" + item.getData(), names.get(item));
				}
				config.set(key, theseNames);
			}
		}
		final int NAME = 0, TYPE = 1;
		for(String hook : hooks.keySet()) {
			String[] split = hook.split(":");
			String key = "hooks." + split[NAME] + "." + split[TYPE];
			ItemID item = hooks.get(hook);
			String code = Integer.toString(item.getId());
			if(item.getData() != null) code += "/" + item.getData();
			config.set(key, code);
		}
		config.set("names.potions", potions);
		try {
			config.save(configFile);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void loadConfig() {
		try {
			File dataFolder = General.plugin.getDataFolder();
			if(!dataFolder.exists()) dataFolder.mkdirs();
			configFile = new File(dataFolder, "items.yml");
			if(!configFile.exists()) General.createDefaultConfig(configFile);
			config = YamlConfiguration.loadConfiguration(configFile);
		} catch(Exception ex) {
			General.logger.warn(LanguageText.LOG_CONFIG_ERROR.value("file", "items.yml"), ex);
		}
	}
	
	public static void setup() {
		loadConfig();
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
		
		potions = config.getConfigurationSection("names.potions");
		
		// Check if classes have been overridden.
		for(Material mat : Material.values()) {
			int id = mat.getId();
			String clsName = config.getString("variants.item" + id + ".class");
			if(clsName != null) {
				try {
					Class<?> cls = Class.forName(clsName);
					if(cls != null && ItemData.class.isAssignableFrom(cls)) {
						@SuppressWarnings("unchecked")
						Class<? extends ItemData> dataClass = (Class<? extends ItemData>)cls;
						ItemData.register(mat, dataClass);
					}
				} catch(ClassNotFoundException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
			}
		}
	}
	
	private static void loadHooks() {
		hooks = new HashMap<String, ItemID>();
		try {
			for(String key : config.getConfigurationSection("hooks").getKeys(false)) {
				for(String val : config.getConfigurationSection("hooks." + key).getKeys(false)) {
					String x = config.getConfigurationSection("hooks." + key).getString(val);
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
		ConfigurationSection aliasSection = config.getConfigurationSection("aliases");
		if(aliasSection == null) {
			General.logger.warn(LanguageText.LOG_ITEM_NO_ALIASES.value());
			return;
		}
		Set<String> ymlAliases = aliasSection.getKeys(false);
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
		Set<String> keys;
		try {
			keys = config.getConfigurationSection("names").getKeys(false);
		} catch(NullPointerException x) {
			General.logger.warn(LanguageText.LOG_ITEM_NO_NAMES.value());
			return;
		}
		if(keys == null) {
			General.logger.warn(LanguageText.LOG_ITEM_BAD_NAMES.value());
		} else {
			for(String id : keys) {
				if(!id.startsWith("item")) {
					if(!id.startsWith("ench")) {
						lastInvalid = id;
						invalids++;
					}
					continue;
				}
				int num;
				ItemID key;
				String name;
				try {
					num = Integer.valueOf(id.substring(4));
				} catch(NumberFormatException x) {
					if(!id.equals("potions")) {
						lastInvalid = id;
						invalids++;
					}
					continue;
				}
				String path = "names." + id;
				Object node = config.get(path);
				if(node instanceof String) {
					name = config.getString(path);
					key = new ItemID(num, null);
					names.put(key, name);
				} else {
					Set<String> list = config.getConfigurationSection(path).getKeys(false);
					for(String data : list) {
						name = config.getString(path + "." + data);
						if(data.matches("data[0-9][0-9]?")) {
							int d = Integer.parseInt(data.substring(4));
							key = new ItemID(num, d);
							names.put(key, name);
						} else if(data.equals("generic")) {
							key = new ItemID(num, null);
							names.put(key, name);
						}
					}
				}
			}
		}
		if(invalids > 0)
			General.logger.warn(LanguageText.LOG_ITEM_BAD_NAME.value("count", invalids, "name", lastInvalid));
	}
	
	public static String name(Material item) {
		if(item == null) return "";
		return name(ItemID.bare(item.getId(), null));
	}
	
	public static String name(MaterialData item) {
		if(item == null) return "";
		return name(ItemID.bare(item.getItemTypeId(), (int)item.getData()));
	}
	
	public static String name(ItemStack item) {
		if(item == null) return "";
		return name(ItemID.bare(item.getTypeId(), (int)item.getDurability()));
	}
	
	/**
	 * Returns the name of the item stored in the hashmap or the item name stored in the items.txt file in the hMod
	 * folder.
	 * 
	 * @param longKey The item ID and data
	 * @return Canonical name
	 */
	public static String name(ItemID longKey) {
		if(longKey == null) return "";
		
		if(names.containsKey(longKey)) {
			return names.get(longKey);
		}
		
		if(longKey.getMaterial() == Material.POTION && longKey.getData() != null) {
			return potionName(longKey.getData());
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
	
	public static String potionName(int data) {
		try {
			return name(Potion.fromDamage(data));
		} catch(IllegalArgumentException e) {
			String name = potions.getString("name" + (data & 63), potions.getString("generic"));
			return formatPotionName(name, Tier.ONE, (data & 0x4000) > 0, (data & 0x40) > 0);
		}
	}
	
	public static String name(Potion potion) {
		PotionType type = potion.getType(), match = null;
		int i = 0;
		while(type != match) match = PotionType.getByDamageValue(++i);
		return formatPotionName(potions.getString("type" + i), potion.getTier(), potion.isSplash(), potion.hasExtendedDuration());
	}
	
	private static String formatPotionName(String name, Tier tier, boolean splash, boolean extend) {
		name += potions.getString("tier" + (1 + tier.ordinal()), "");
		if(extend) name += potions.getString("extend", "");
		return Messaging.format(name, "potion", potions.getString(splash ? "splash" : "generic", ""));
	}
	
	public static String name(Enchantment ench) {
		String name = config.getString("names.ench" + ench.getId());
		if(name == null) return ench.getName();
		return name;
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
			if(ret == null) throw new InvalidItemException(LanguageText.GIVE_BAD_ID);
		} else {
			try {
				String[] parts = item.split("[.,:/\\|]");
				ret = validateLongItem(parts[0], parts[1]);
			} catch(ArrayIndexOutOfBoundsException x) {
				throw new InvalidItemException(LanguageText.GIVE_BAD_DATA,
					"data", "", "item", item.substring(0, item.length() - 1));
			}
		}
		
		// Make sure it's the ID of a valid item.
		ret.validateId();
		
		// Make sure the damage value is valid.
		ret.validateData();
		
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
			// No data value is valid with a "richalias".
			throw new InvalidItemException(LanguageText.GIVE_BAD_DATA, "data",ret.getVariant(), "item",ret.getName(null));
		} else ret.setData(ret.getDataType().fromName(data));
		if(ret == null) throw new InvalidItemException(LanguageText.GIVE_BAD_ID);
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
				ret = aliases.get(alias).clone();
			}
			if(ret == null) {
				for(Material material : Material.values()) {
					String mat = material.toString();
					if(mat.equalsIgnoreCase(item) || mat.replace("_", "-").equalsIgnoreCase(item)
							|| mat.replace("_", "").equalsIgnoreCase(item)) {
						ret = new ItemID(material);
					}
				}
			}
		}
		return ret;
	}
	
	public static int maxStackSize(int id) {
		return Material.getMaterial(id).getMaxStackSize();
	}
	
	public static void giveItem(Player who, ItemID x, Integer amount, Map<Enchantment,Integer> ench) {
		PlayerInventory i = who.getInventory();
		HashMap<Integer, ItemStack> excess = i.addItem(x.getStack(amount, who, ench));
		for(ItemStack leftover : excess.values())
			who.getWorld().dropItemNaturally(who.getLocation(), leftover);
	}
	
	public static void setItemName(ItemID id, String name) {
		names.put(id, name);
	}
	
	public static List<String> variantNames(ItemID id) {
		if(id != null && id.getData() != null)
			return config.getStringList("variants.item" + id.getId() + ".type" + id.getData());
		return null;
	}
	
	public static List<String> variantNames(String key) {
		if(key == null) return null;
		return config.getStringList("special." + key);
	}
	
	public static List<String> variantNames(String key, int data) {
		if(key != null) return config.getStringList("special." + key + ".type" + data);
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
		config.set("variants.item" + id.getId() + ".type" + id.getData(), variants);
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

	public static List<String> setGroupItems(String groupName, List<String> groupItems) {
		ArrayList<Integer> items = new ArrayList<Integer>();
		ArrayList<String> bad = new ArrayList<String>();
		for(String item : groupItems) {
			try {
				ItemID thisItem = validate(item);
				items.add(thisItem.getId());
			} catch(InvalidItemException e) {
				bad.add(item);
			}
		}
		Option.GROUP(groupName).set(items);
		return bad;
	}

	public static List<Integer> groupItems(String groupName) {
		return Option.GROUP(groupName).get();
	}

	public static boolean addGroupItem(String groupName, String item) {
		List<Integer> group = groupItems(groupName);
		ItemID thisItem = validate(item);
		try {
			group.add(thisItem.getId());
		} catch(InvalidItemException e) {
			return false;
		}
		Option.GROUP(groupName).set(group);
		return true;
	}

	public static boolean removeGroupItem(String groupName, String item) {
		List<Integer> group = groupItems(groupName);
		ItemID thisItem = validate(item);
		try {
			group.remove(thisItem.getId());
		} catch(InvalidItemException e) {
			return false;
		}
		Option.GROUP(groupName).set(group);
		return true;
	}

	public static List<String> getPotions(String key) {
		return config.getStringList("variants." + key);
	}

	public static int getMaxData(Material mat) {
		int data = config.getInt("variants.item" + mat.getId() + ".max", -1);
		if(data == -1) return mat.getMaxDurability();
		return 0;
	}

	public static Range<Integer> getDataRange(Material mat) {
		String range = config.getString("variants.item" + mat.getId() + ".range");
		if(range == null) return null;
		return IntRange.parse(range);
	}

	public static List<Range<Integer>> getDataRanges(Material mat) {
		List<String> list = config.getStringList("variants.item" + mat.getId() + ".range");
		if(list == null || list.isEmpty()) return null;
		List<Range<Integer>> ranges = new ArrayList<Range<Integer>>();
		for(String range : list) {
			try {
				int n = Integer.parseInt(range);
				ranges.add(new IntRange(n));
			} catch(NumberFormatException e) {
				ranges.add(IntRange.parse(range));
			}
		}
		return ranges;
	}
}
