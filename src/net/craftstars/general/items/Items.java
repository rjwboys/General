
package net.craftstars.general.items;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

import net.craftstars.general.General;
import net.craftstars.general.util.Toolbox;

public class Items {
    private static class VariantsMap {
        @SuppressWarnings("hiding")
        private ConfigurationNode variants;

        VariantsMap(ConfigurationNode var) {
            variants = var;
        }

        public Integer findVariant(int id, String data) {
            ConfigurationNode thisItem = variants.getNode("item" + Integer.toString(id));
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
    }

    private static HashMap<String, ItemID> aliases;
    private static HashMap<ItemID, String> names;
    private static HashMap<String,HashMap<String,ItemID>> hooks;
    private static VariantsMap variants;

    private static Configuration loadConfig() {
        Configuration itemsyml = null;
        try {
            File dataFolder = General.plugin.getDataFolder();
            if(!dataFolder.exists()) dataFolder.mkdirs();
            File configFile = new File(dataFolder, "items.yml");

            if(!configFile.exists()) {
                General.logger
                        .info("Configuration file does not exist. Attempting to create default one...");
                InputStream defaultConfig = General.plugin.getClass().getResourceAsStream(
                        File.separator + "items.yml");
                FileWriter out = new FileWriter(configFile);
                for(int i = 0; (i = defaultConfig.read()) > 0;)
                    out.write(i);
                out.flush();
                out.close();
                defaultConfig.close();
            }
            itemsyml = new Configuration(configFile);
            itemsyml.load();
        } catch(Exception ex) {
            General.logger.warn(
                    "Could not read and/or write items.yml! Continuing with default values!", ex);
        }
        return itemsyml;
    }

    public static void setup() {
        Properties itemsdb = new Properties();
        Configuration itemsyml = loadConfig();
        try {
            itemsdb.load(new FileInputStream("items.db"));
        } catch(Exception ex) {
            General.logger.warn("Could not open items.db!");
        }
        aliases = new HashMap<String, ItemID>();
        names = new HashMap<ItemID, String>();

        // This loads in the item names from items.yml
        loadItemNames(itemsyml);

        // Now load the aliases from items.db
        try {
            loadItemAliases(itemsdb);
        } catch(NumberFormatException x) {
            General.logger.error(x.getMessage());
        }

        // And then the variant names
        loadItemVariantNames(itemsyml);

        // Load the "hooks" as well.
        loadHooks(itemsyml);
    }

    private static void loadHooks(Configuration itemsyml) {
        hooks = new HashMap<String,HashMap<String,ItemID>>();
        for(String key : itemsyml.getKeys("hooks")) {
            HashMap<String,ItemID> thisHook = new HashMap<String,ItemID>();
            for(String val : itemsyml.getNode("hooks").getKeys(key)) {
                String x = itemsyml.getNode("hooks").getNode(key).getString(val);
                ItemID thisItem = Items.validate(x);
                if(thisItem == null) {
                    General.logger.warn("Invalid hook: " + x);
                } else {
                    thisHook.put(val,thisItem);
                }
            }
            hooks.put(key, thisHook);
        }
    }

    private static void loadItemVariantNames(Configuration itemsyml) {
        try {
            variants = new VariantsMap(itemsyml.getNode("variants"));
        } catch(NullPointerException x) {
            General.logger.warn("List of item variants is missing.");
        }
    }

    private static void loadItemAliases(Properties itemsdb) {
        Pattern itemPat = Pattern.compile("([0-9]+)(?:[.,:/|]([0-9]+))?");
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
                    General.logger.warn("Invalid items.db assignment '" + m.group(1) + ":"
                            + m.group(2) + "'.");
                    continue;
                }
                val = new ItemID(num, data);
            } else if(problem) {
                General.logger.warn("Invalid items.db assignment '" + m.group(1) + "'.");
                continue;
            } else val = new ItemID(num, null);
            aliases.put(alias, val);
        }
    }

    private static void loadItemNames(Configuration itemsyml) {
        int invalids = 0;
        String lastInvalid = null;
        List<String> keys;
        try {
            keys = itemsyml.getKeys("names");
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
                List<String> list = itemsyml.getStringList("names." + id, null);
                if(list.isEmpty()) {
                    name = itemsyml.getString("names." + id);
                    key = new ItemID(num, null);
                    names.put(key, name);
                } else {
                    for(int i = 0; i < list.size(); i++) {
                        name = list.get(i);
                        key = new ItemID(num, i);
                        names.put(key, name);
                    }
                }
            }
        }
        if(invalids > 0) General.logger.warn("Invalid keys in the names section of items.yml (eg "
                + lastInvalid + ")");
    }

    /**
     * Returns the name of the item stored in the hashmap or the item name stored in the items.txt
     * file in the hMod folder.
     * 
     * @param longKey The item ID and data
     * @return Canonical name
     */
    public static String name(ItemID longKey) {
        if(names.containsKey(longKey)) {
            return names.get(longKey);
        }

        for(Material item : Material.values()) {
            if(item.getId() == longKey.getId()) {
                return Toolbox.camelToPhrase(item.toString());
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
     * @return null if false, the 2-part ID if true; a data value of -1 if the item is valid but the
     *         data isn't
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
            if(isInvalid) ret.invalidate(true);
            else if(ret.getData() > check.getMaxDurability()) ret.invalidate(true);
        }
        // --- resume hacky workaround for missing MaterialData classes ---
        case INK_SACK: case WOOL: case COAL: case WOOD: case STEP: case DOUBLE_STEP:
        }
        // --- end hacky workaround for missing MaterialData classes ---

        return ret;
    }
    
    private static ItemID validateLongItem(String item, String data) {
        ItemID ret = validateShortItem(item);
        if(ret == null) { // If it wasn't valid as a short item, check the hooks.
            if(hooks.containsKey(item) && hooks.get(item).containsKey(data)) {
                return hooks.get(item).get(data);
            }
        } else if(ret.getData() != null) { // This means a "richalias" was used, which includes the data value.
            ret.invalidate(true); // No data value is valid with a "richalias".
        } else {
            try {
                ret.setData(Integer.valueOf(data));
            } catch(NumberFormatException x) {
                Integer d = variants.findVariant(ret.getId(), data);
                if(d != null) ret.setData(d).setVariant(data);
            }
        }
        return ret;
    }

    private static ItemID validateShortItem(String item) {
        ItemID ret = null;
        try {
            ret = new ItemID(Integer.valueOf(item));
        } catch(NumberFormatException x) {
            if(aliases == null) General.logger.error("aliases is null");
            else for(String alias : aliases.keySet()) {
                if(!alias.equalsIgnoreCase(item)) continue;
                ret = new ItemID(aliases.get(alias));
                ret.setName(alias);
            }
            if(ret == null) {
                for(Material material : Material.values()) {
                    if(material.toString().equalsIgnoreCase(item)) {
                        ret = new ItemID(material).setName(material.toString());
                    }
                }
            }
        }
        return ret;
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
        return Material.getMaterial(id).getMaxDurability() != -1;
    }

    public static int maxStackSize(int id) {
        // TODO: Get rid of hack
        // --- begin hacky workaround for incorrect getMaxStackSize
        if(id == 346) return 64;
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
}
