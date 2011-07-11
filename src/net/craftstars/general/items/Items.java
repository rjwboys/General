
package net.craftstars.general.items;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

import com.nisovin.bookworm.Book;
import com.nisovin.bookworm.BookWorm;

import net.craftstars.general.General;
import net.craftstars.general.util.Toolbox;
import net.minecraft.server.World;
import net.minecraft.server.WorldMapCollection;

public class Items {
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
			
			if(!configFile.exists()) {
				General.logger.info("Configuration file does not exist. Attempting to create default one...");
				InputStream defaultConfig =
						General.plugin.getClass().getResourceAsStream(File.separator + "items.yml");
				FileWriter out = new FileWriter(configFile);
				for(int i = 0; (i = defaultConfig.read()) > 0;)
					out.write(i);
				out.flush();
				out.close();
				defaultConfig.close();
			}
			config = new Configuration(configFile);
			config.load();
		} catch(Exception ex) {
			General.logger.warn("Could not read and/or write items.yml! Continuing with default values!", ex);
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
		
		// Now load the aliases from items.db
		try {
			loadItemsDB(itemsdb);
		} catch(Exception x) {
			General.logger.error(x.getMessage());
		}
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
						General.logger.warn("Invalid hook: " + x);
					} else {
						hooks.put(key + ":" + val, thisItem);
					}
				}
			}
		} catch(NullPointerException x) {
			General.logger.warn("Hooks are missing.");
		}
	}
	
	private static final String invalidItemAlias = "I am not a valid item.";
	private static Pattern itemPat = Pattern.compile("([0-9]+)(?:[.,:/|]([0-9]+))?");
	@Deprecated
	private static void loadItemsDB(Properties itemsdb) {
		for(String alias : itemsdb.stringPropertyNames()) {
			ItemID val;
			String code = itemsdb.getProperty(alias);
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
					General.logger.warn("Invalid item alias assignment '" + m.group(1) + ":" + m.group(2) + "'.");
					continue;
				}
				val = new ItemID(num, data);
			} else if(problem) {
				General.logger.warn("Invalid item alias assignment '" + m.group(1) + "'.");
				continue;
			} else val = new ItemID(num, null);
			aliases.put(alias, val);
		}
	}
	
	private static void loadItemAliases() {
		List<String> ymlAliases = config.getKeys("aliases");
		if(ymlAliases == null) {
			General.logger.warn("No aliases were defined in items.yml.");
			if(!aliases.isEmpty()) {
				General.logger.info("Your items.db aliases will be inserted into the items.yml upon shutdown,");
				General.logger.info("or you can force it earlier using /general save");
			}
			return;
		}
		for(String alias : ymlAliases) {
			ItemID val;
			String code = config.getString("aliases." + alias, invalidItemAlias);
			if(code.equals(invalidItemAlias)) {
				General.logger.warn("Invalid item alias assignment  for '" + alias + "'.");
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
					General.logger.warn("Invalid item alias assignment '" + m.group(1) + ":" + m.group(2) + "'.");
					continue;
				}
				val = new ItemID(num, data);
			} else if(problem) {
				General.logger.warn("Invalid item alias assignment '" + m.group(1) + "'.");
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
			General.logger.warn("Names of items are missing.");
			return;
		}
		if(keys == null) {
			General.logger.warn("The names section of items.yml is missing or invalid.");
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
			General.logger.warn("Invalid keys in the names section of items.yml (eg " + lastInvalid + ")");
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
		if(Pattern.matches("([a-zA-Z0-9a-zA-Z_'-]+)", item)) {
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
		// TODO: Get rid of hack
		// --- begin hacky workaround for missing MaterialData classes ---
		switch(check) {
		default:
			// --- pause hacky workaround for missing MaterialData classes ---
			if(ret.getData() != null && ret.getData() != 0) {
				boolean isInvalid = true;
				if(isDamageable(ret.getId())) isInvalid = false;
				if(check.getData() != null) isInvalid = false;
				if(isInvalid)
					ret.invalidate(true);
				// TODO: Get rid of hack
				// --- begin hacky workaround for incorrect getMaxDurability
				else if(ret.getId() == 359)
					if(ret.getData() > 238) ret.invalidate(true); 
				// --- begin hacky workaround for incorrect getMaxDurability
				else if(ret.getData() > check.getMaxDurability()) ret.invalidate(true);
			}
			// --- resume hacky workaround for missing MaterialData classes ---
		break;
		case INK_SACK:
		case WOOL:
			if(ret.getData() == null) break;
			if(ret.getData() > 15 || ret.getData() < 0) ret.invalidate(true);
		break;
		case COAL:
			if(ret.getData() == null) break;
			if(ret.getData() > 1 || ret.getData() < 0) ret.invalidate(true);
		break;
		case SAPLING:
		case LOG:
		case LEAVES:
			if(ret.getData() == null) break;
			if(ret.getData() > 2 || ret.getData() < 0) ret.invalidate(true);
		break;
		case STEP:
		case DOUBLE_STEP:
			if(ret.getData() == null) break;
			if(ret.getData() > 3 || ret.getData() < 0) ret.invalidate(true);
		break;
		case MOB_SPAWNER: // creaturebox support TODO: Why do I need this here?
			if(ret.getData() == null) break;
			if(Bukkit.getServer().getPluginManager().getPlugin("creaturebox") == null) {
				ItemID tmp = ret.clone();
				tmp.setData(null);
				ret.setName(name(tmp));
				if(ret.getData() != 0) ret.invalidate(true);
			}
			if(ret.getData() > 14 || ret.getData() < 0) ret.invalidate(true);
		break;
		case MAP:
			// No action; any data value is presumed valid...
			if(ret.getData() == null) ret.setData(0);
			{ // TODO: Rewrite to use Bukkit API
				World w = ((CraftWorld) Bukkit.getServer().getWorlds().get(0)).getHandle();
				String mapName = "map_" + ret.getData();
				Field mapMap;
				try {
					mapMap = WorldMapCollection.class.getDeclaredField("b");
					mapMap.setAccessible(true);
					@SuppressWarnings("rawtypes")
					Object map = ((Map) mapMap.get(w.worldMaps)).get(mapName);
					if(map == null) ret.invalidate(true);
				} catch(SecurityException e) {}
				catch(NoSuchFieldException e) {}
				catch(IllegalArgumentException e) {}
				catch(IllegalAccessException e) {}
			}
		break;
		case BOOK:
			Plugin bookworm = Bukkit.getServer().getPluginManager().getPlugin("BookWorm");
			if(bookworm == null) {
				if(ret.getData() != 0) ret.invalidate(true);
				break;
			}
			if(ret.getData() == null) ret.setData(0);
			if(ret.getData() > 0) { // TODO: Reflecting into someone else's plugin... ugh...
				File bookFile = new File(bookworm.getDataFolder(), ret.getData() + ".txt");
				if(!bookFile.exists()) {
					// TODO: If it doesn't exist, it may be of the form <num>_<author>_<title>.txt; how to catch that?
					ret.invalidate(true);
				} else {
					Book book = BookWorm.getBook(ret.getData().shortValue());
					if(book != null)
						ret.setName('"' + book.getTitle() + '"' + " by " + book.getAuthor());
					else ret.invalidate(true);
				}
			}
		break;
		}
		// --- end hacky workaround for missing MaterialData classes ---
		
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
		ConfigurationNode thisItem = config.getNode("variants.item" + Integer.toString(id));
		if(thisItem == null) return null;
		int i = 0;
		List<String> thisVariant;
		do {
			thisVariant = thisItem.getStringList("type" + Integer.toString(i), null);
			if(thisVariant.contains(data)) return i;
			i++;
		} while(!thisVariant.isEmpty());
		return null;
	}
	
	public static boolean checkID(int id) {
		for(Material item : Material.values()) {
			if(item.getId() == id) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isDamageable(int id) {
		// TODO: Get rid of hack
		// --- begin hacky workaround for incorrect getMaxDurability
		if(id == 359) return true;
		// --- end hacky workaround for incorrect getMaxDurability
		return Material.getMaterial(id).getMaxDurability() != -1;
	}
	
	public static int maxStackSize(int id) {
		// TODO: Get rid of hack
		// --- begin hacky workaround for incorrect getMaxStackSize
		if(id == 335 || id == 349 || id == 350 || id == 355) return 1;
		// --- end hacky workaround for incorrect getMaxStackSize
		return Material.getMaterial(id).getMaxStackSize();
	}
	
	public static void giveItem(Player who, ItemID x, Integer amount) {
		PlayerInventory i = who.getInventory();
		HashMap<Integer, ItemStack> excess = i.addItem(x.getStack(amount));
		for(ItemStack leftover : excess.values()) {
			if(i.getBoots().getType() == Material.AIR) {
				i.setBoots(leftover);
				continue;
			}
			if(i.getLeggings().getType() == Material.AIR) {
				i.setLeggings(leftover);
				continue;
			}
			if(i.getChestplate().getType() == Material.AIR) {
				i.setChestplate(leftover);
				continue;
			}
			if(i.getHelmet().getType() == Material.AIR) {
				i.setHelmet(leftover);
				continue;
			}
			who.getWorld().dropItemNaturally(who.getLocation(), leftover);
		}
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
		if(isDamageable(id.getId())) return true;
		if(id.getData() == null) return true;
		return data == id.getData();
	}
}
