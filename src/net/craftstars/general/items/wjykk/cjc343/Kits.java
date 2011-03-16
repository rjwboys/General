package net.craftstars.general.items.wjykk.cjc343;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.craftstars.general.General;
import net.craftstars.general.items.ItemID;
import net.craftstars.general.items.Items;

import org.bukkit.entity.Player;

public class Kits {
    public static HashMap<String,Kit> kits = new HashMap<String,Kit>();
    public static HashMap<GotKit,Long> players = new HashMap<GotKit,Long>();

    public static class Kit {
        public HashMap<ItemID,Integer> items;
        public int delay;
        private String name;
        
        @SuppressWarnings("hiding") 
        Kit(String name, HashMap<ItemID,Integer> item, int delay) {
            this.name = name;
            this.items = item;
            this.delay = delay;
        }
        
        @Override
        public int hashCode() {
            return items.hashCode() * delay;
        }
        
        @Override
        public boolean equals(Object other) {
            if(other instanceof Kit) {
                return items.equals(((Kit) other).items);
            }
            return false;
        }
        
        public String getName() {
            return name;
        }
    }
    
    public static class GotKit {
        private String who;
        private String which;
        private int id;
        
        @SuppressWarnings("hiding")
        public
        GotKit(Player who, Kit which) {
            this.who = who.getName();
            this.which = which.getName();
            this.id = who.getEntityId();
        }
        
        @Override
        public int hashCode() {
            int items = which.hashCode();
            //return (id << 16) | (item & 0xFFFF);
            return id ^ items;
        }
        
        @Override
        public boolean equals(Object obj) {
            if(obj instanceof GotKit) {
                GotKit other = (GotKit) obj;
                if(this.which.equals(other.which) && this.who.equals(other.who))
                    return true;
            }
            return false;
        }
        
        @Override
        public String toString() {
            return "(" + who + "[" + id + "], " + which + ")";
        }
    }
    
    public static boolean loadKits() {
        boolean foundAnException = false;
        Exception exceptionToShow = null;
        try {
            File dataFolder = General.plugin.getDataFolder();
            BufferedReader br = new BufferedReader(new FileReader(new File(dataFolder, "general.kits")));
            String l;
            int lineNumber = 1;
            kits.clear();
            String list;
            String[] listing;
            Pattern idPat = Pattern.compile("^([0-9a-zA-Z_']+).*");
            Pattern dataPat = Pattern.compile(".*\\+([0-9a-zA-Z_']+).*");
            Pattern nPat = Pattern.compile(".*-([0-9a-zA-Z]+)$");
            while ((l = br.readLine()) != null) {
                list = l.trim();
                if (!list.startsWith("#") && !list.isEmpty()) {
                    listing = list.split(":");
                    try {
                        int delay = Integer.valueOf(listing[2]);
                        String[] stuff = listing[1].split(",");
                        HashMap<ItemID,Integer> components = new HashMap<ItemID,Integer>();
                        //ItemID[] components = new ItemID[stuff.length];
                        //int[] amounts = new int[stuff.length];
                        for(String item : stuff) {
                            int n = 1;
                            String id, data = "0";
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
                            ItemID type = Items.validate(id + ":" + data);
                            if(item == null || !type.isValid())
                                throw new IllegalArgumentException(id + ":" + data);
                            components.put(new ItemID(type), n);
                        }
                        Kit theKit = new Kit(listing[0], components, delay);
                        kits.put(listing[0].toLowerCase(), theKit);
                        if(listing.length > 3) {
                            General.logger.warn("Note: line " + lineNumber + " in general.kits has more than three components; excess ignored");
                        }
                    } catch(Exception x) {
                        General.logger.warn("Note: line " + lineNumber + " in general.kits is improperly defined and is ignored (" + x.getClass().getName() + ", " + x.getMessage() + ")");
                        if(!foundAnException) {
                            foundAnException = true;
                            exceptionToShow = x;
                        }
                    }
                }
                lineNumber++;
            }
        } catch (Exception e) {
            General.logger.warn("An error occured: either general.kits does not exist or it could not be read; kits ignored");
        }
        if(foundAnException) {
            General.logger.error("First exception loading the kits:");
            exceptionToShow.printStackTrace();
        }
        // Return success
        return true;
    }
}
