package net.craftstars.general.command;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.CommandBase;
import net.craftstars.general.General;
import net.craftstars.general.util.Items;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;
import net.craftstars.general.util.Items.ItemID;

public class kitCommand extends CommandBase {
    private static HashMap<String,Kit> kits = new HashMap<String,Kit>();
    private static HashMap<GotKit,Long> players = new HashMap<GotKit,Long>();

    public static class Kit {
        private HashMap<ItemID,Integer> items;
        private int delay;
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
    
    private static class GotKit {
        private String who;
        private String which;
        private int id;
        
        @SuppressWarnings("hiding")
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
    
    @Override
    public boolean fromConsole(General plugin, CommandSender sender, Command command,
            String commandLabel, String[] args) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean fromPlayer(General plugin, Player sender, Command command, String commandLabel,
            String[] args) {
        if(Toolbox.lacksPermission(plugin, sender, "general.kit")) return true;
        if (args.length == 0) {
            String msg = "&cKits available: ";
            for (String thisKit : kits.keySet()) {
                if(canGetKit(sender, thisKit)) {
                    msg += thisKit + " ";
                }
            }
            Messaging.send(sender, msg);
        } else if (args.length >= 1) {
            Kit kit = kits.get(args[0]);
            if (kit == null)
                Messaging.send(sender, "&cKit by the name of &e" + args[0] + "&c does not exist!");
            else {
                if (!canGetKit(sender, args[0].toLowerCase())) {
                    Messaging.send(sender, "&rose;You do not have permission for that kit.");
                    return true;
                }

                GotKit check = new GotKit(sender, kit);

                // Player did not request any kit previously
                if(!canBypassDelay(sender) && players.containsKey(check)) {
                    long time = System.currentTimeMillis() / 1000;
                    long left = kit.delay - (time - players.get(check));
                    
                    // Time did not expire yet
                    if (left > 0) {
                        Messaging.send(sender, "&cYou may not get this kit so soon! Try again in &e" + left + "&c seconds.");
                        return true;
                    }
                }
                // Add the kit and timestamp into the list
                insertIntoPlayerList(check);
                // Receive the kit
                getKit(sender, kit);
            }
        }
        return true;
    }
    
    private boolean canGetKit(Player sender, String kit) {
        if(General.plugin.permissions.hasPermission(sender, "general.kit." + kit)) return true;
        return false;
    }
    
    private boolean canBypassDelay(Player sender) {
        return General.plugin.permissions.hasPermission(sender, "general.kit-now");
    }
    
    private void insertIntoPlayerList(GotKit what) {
        long time = System.currentTimeMillis() / 1000;
        players.put(what, time);
    }
    
    private void getKit(Player sender, Kit kit) {
        HashMap<ItemID, Integer> items = kit.items;
        for (ItemID x : items.keySet()) {
            Items.giveItem(sender, x, items.get(x));
        }
        Messaging.send(sender, "&2Here you go!");
    }
    
    public static boolean loadKits() {
        try {
            File dataFolder = General.plugin.getDataFolder();
            BufferedReader br = new BufferedReader(new FileReader(new File(dataFolder, "general.kits")));
            String l;
            int lineNumber = 1;
            kits.clear();
            String list;
            String[] listing;
            Pattern idPat = Pattern.compile("^([0-9]+).*");
            Pattern dataPat = Pattern.compile(".*\\+([0-9]+).*");
            Pattern nPat = Pattern.compile(".*-([0-9]+)$");
            while ((l = br.readLine()) != null) {
                list = l.trim();
                if (!list.startsWith("#")) {
                    listing = list.split(":");
                    try {
                        int delay = Integer.valueOf(listing[2]);
                        String[] stuff = listing[1].split(",");
                        HashMap<ItemID,Integer> components = new HashMap<ItemID,Integer>();
                        //ItemID[] components = new ItemID[stuff.length];
                        //int[] amounts = new int[stuff.length];
                        for(String item : stuff) {
                            int id, data = 0, n = 1;
                            Matcher m;
                            item = item.trim();
                            m = idPat.matcher(item);
                            if(m.matches()) {
                                id = Integer.valueOf(m.group(1));
                            } else throw new InputMismatchException();
                            m = dataPat.matcher(item);
                            if(m.matches()) {
                                data = Integer.valueOf(m.group(1));
                            }
                            m = nPat.matcher(item);
                            if(m.matches()) {
                                n = Integer.valueOf(m.group(1));
                            }
                            if(Items.validate(id + ":" + data).ID == -1)
                                throw new IllegalArgumentException();
                            components.put(new ItemID(id, data), n);
                        }
                        Kit theKit = new Kit(listing[0], components, delay);
                        kits.put(listing[0].toLowerCase(), theKit);
                        if(listing.length > 3) {
                            General.logger.warn("Note: line " + lineNumber + " in general.kits has more than three components; excess ignored");
                        }
                    } catch(Exception x) {
                        General.logger.warn("Note: line " + lineNumber + " in general.kits is improperly defined and is ignored (" + x.getClass().getName() + ")");
                    }
                }
                lineNumber++;
            }
        } catch (Exception e) {
            General.logger.warn("An error occured: either general.kits does not exist or it could not be read; kits ignored");
        }
        // Return success
        return true;
    }
}
