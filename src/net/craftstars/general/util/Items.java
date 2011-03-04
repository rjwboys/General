
package net.craftstars.general.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

import net.craftstars.general.General;

public class Items {
    public static class ItemID implements Comparable<ItemID> {
        public int ID;
        public int data;
        public boolean dataMatters;

        ItemID(Integer id, Integer d) {
            if(id == null) this.ID = 0;
            else this.ID = id;
            if(d == null) {
                this.data = 0;
                this.dataMatters = false;
            } else {
                this.data = d;
                this.dataMatters = true;
            }
        }

        public int compareTo(ItemID arg) {
            ItemID other = (ItemID) arg;
            if(!dataMatters) return new Integer(ID).compareTo(other.ID);
            else {
                if(ID < other.ID) return -1;
                else if(ID > other.ID) return 1;
                else return new Integer(data).compareTo(other.data);
            }
        }

        @Override
        public int hashCode() {
            // System.out.println(this.toString()+" is hashed; result: "+Integer.toString((ID << 8)
            // + (data & 0xFF)));
            return (ID << 8) + (data & 0xFF);
        }
        
        @Override
        public boolean equals(Object other){
            if(other instanceof ItemID) return 0 == this.compareTo((ItemID) other);
            else return false;
        }

        public String toString() {
            return Integer.toString(ID) + ":" + Integer.toString(data);
        }
    }

    private static class VariantsMap {
        @SuppressWarnings("hiding")
        private ConfigurationNode variants;

        VariantsMap(ConfigurationNode var) {
            variants = var;
        }

        public int findVariant(int id, String data) {
            ConfigurationNode thisItem = variants.getNode("item" + Integer.toString(id));
            if(thisItem == null) return -1;
            int i = 0;
            List<String> thisVariant;
            do {
                thisVariant = thisItem.getStringList("type" + Integer.toString(i), null);
                if(thisVariant.contains(data)) return i;
                i++;
            } while(!thisVariant.isEmpty());
            return -1;
        }
    }

    private static HashMap<String, ItemID> aliases;
    private static HashMap<ItemID, String> names;
    private static VariantsMap variants;
    private static List<Integer> dmg;
    public static String lastDataError;

    // private static List<Integer> nostk;
    // private static List<Integer> smstk;

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
        dmg = itemsyml.getIntList("damageable", null);
        // variants = new HashMap<Integer, HashMap<Integer, List<String>>>();
        // nostk = itemsyml.getIntList("unstackable", null);
        // smstk = itemsyml.getIntList("smallstacks", null);

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

        // TODO: Load the "hooks" as well.
    }

    private static void loadItemVariantNames(Configuration itemsyml) {
        variants = new VariantsMap(itemsyml.getNode("variants"));
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
        List<String> keys = itemsyml.getKeys("names");
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
     * @param id Item ID
     * @param data Item data value
     * @return Canonical name
     */
    public static String name(Integer id, Integer data) {
        ItemID longKey = new ItemID(id, data);
        if(names.containsKey(longKey)) {
            return names.get(longKey);
        }

        for(Material item : Material.values()) {
            if(item.getId() == id) {
                return Toolbox.camelToPhrase(item.toString());
            }
        }

        if(data == null) return Toolbox.string(id) + ":" + Toolbox.string(data);

        return Toolbox.string(id);
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
        if(Pattern.matches("([a-zA-Z0-9]+)", item)) {
            ret = validateShortItem(item);
            // Since no data was explicitly supplied, we can assume that a -1 in the data means none
            // was found.
            if(ret.data == -1) ret.data = 0;
        } else {
            // Pattern itemPat = Pattern.compile("([a-z0-9]+)[.,:/\\|]([a-z0-9]+)",
            // Pattern.CASE_INSENSITIVE);
            // Matcher m = itemPat.matcher(item);
            String[] parts = item.split("[.,:/\\|]");
            ret = validateLongItem(parts[0], parts[1]);
        }

        // Was a valid data/id obtained? If not, we're done; it's invalid.
        if(ret.ID < 0) ret.data = -1;
        if(ret.data < 0) return ret;

        // Make sure it's the ID of a valid item.
        Material check = Material.getMaterial(ret.ID);
        if(check == null) return new ItemID(-1, -1);

        // Make sure the damage value is valid.
        // if(dmg.contains(ret.ID)) {
        // if(ret.data > check.getMaxDurability()) return new ItemID(ret.ID, -1);
        // } else {
        // // MaterialData md = check.getNewData(0);
        // }

        return ret;
    }

    private static ItemID validateLongItem(String item, String data) {
        ItemID ret = validateShortItem(item);
        if(ret.data >= 0) { // This means a "richalias" was used, which includes the data value.
            ret.data = -1; // No data value is valid with a "richalias".
        } else if(ret.ID < 0) { // If it wasn't valid as a short item, check the hooks.
            // TODO: stuff
        } else {
            try {
                ret.data = Integer.valueOf(data);
            } catch(NumberFormatException x) {
                ret.data = variants.findVariant(ret.ID, data);
            }
        }
        if(ret.data < 0) lastDataError = data;
        return ret;
    }

    private static ItemID validateShortItem(String item) {
        ItemID ret = new ItemID(-1, -1);
        try {
            ret.ID = Integer.valueOf(item);
        } catch(NumberFormatException x) {
            for(String alias : aliases.keySet()) {
                if(!alias.equalsIgnoreCase(item)) continue;
                ItemID code = aliases.get(alias);
                ret.ID = code.ID;
                if(code.dataMatters) ret.data = code.data;
            }
            if(ret.ID == -1) {
                for(Material material : Material.values()) {
                    if(material.toString().equalsIgnoreCase(item)) ret.ID = material.getId();
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
        return dmg.contains(id);
    }
}
