package net.craftstars.general.command;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.craftstars.general.CommandBase;
import net.craftstars.general.General;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

public class kitCommand extends CommandBase {
    private static ArrayList<String> kits = new ArrayList<String>();
    private ArrayList<String> players = new ArrayList<String>();
    private ArrayList<ArrayList<String>> kitsreq = new ArrayList<ArrayList<String>>();

    @Override
    public boolean fromConsole(General plugin, CommandSender sender, Command command,
            String commandLabel, String[] args) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean fromPlayer(General plugin, Player sender, Command command, String commandLabel,
            String[] args) {
        General.logger.debug("Hello World");
        if(Toolbox.lacksPermission(plugin, sender, "general.kit")) return true;
        if (args.length == 0) {
            String msg = "&cKits available: ";
            for (int i = 0; i < kits.size(); i += 3) {
                if (General.plugin.permissions.hasPermission(sender, "general.kit." + kits.get(i).toString())) {
                    msg += kits.get(i) + " ";
                }
            }
            Messaging.send(sender, msg);
        } else if (args.length >= 1) {
            int kPos = kits.indexOf(args[0]);
            if (kPos == -1)
                Messaging.send(sender, "&cKit by the name of &e" + args[0] + "&c does not exist!");
            else {

                if (!General.plugin.permissions.hasPermission(sender, "general.kit." + args[0].toLowerCase())) {
                    Messaging.send(sender, "&rose;You do not have permission for that kit.");
                    return true;
                }

                String pName = sender.getName();
                int pPos = players.indexOf(pName);

                // Player did not request any kit previously
                if (pPos == -1) {
                    // Add the new player to the list
                    int newPos = players.size();
                    players.add(pName);
                    kitsreq.add(new ArrayList<String>());

                    // Add the kit and timestamp into the list
                    InsertIntoPlayerList(args[0], newPos);

                    // Receive the kit
                    GetKit(kPos + 1, sender);
                }

                // Player did previously request a kit...
                else {
                    ArrayList<String> al = kitsreq.get(pPos);
                    int alPos = al.indexOf(args[0]);

                    // ...but not the selected one
                    if (alPos == -1) {
                        InsertIntoPlayerList(args[0], pPos);
                        GetKit(kPos + 1, sender);
                    }

                    // ...and it is the selected one
                    else {
                        int left = Integer.parseInt(kits.get(kPos + 2)) - ((int) (System.currentTimeMillis() / 1000) - Integer.parseInt(al.get(alPos + 1)));

                        // Time did not expire yet
                        if (left > 0)
                            Messaging.send(sender, "&cYou may not receive this kit so soon! Try again in &e" + left + "&c seconds.");

                        // Time did expire
                        else {
                            al.remove(alPos);
                            al.remove(alPos);
                            InsertIntoPlayerList(args[0], pPos);
                            GetKit(kPos + 1, sender);
                        }
                    }
                }
            }
        }
        return true;
    }
    
    private void InsertIntoPlayerList(String cmd, int pos) {
        ArrayList<String> al = kitsreq.get(pos);
        al.add(cmd);
        al.add(Integer.toString((int) (System.currentTimeMillis() / 1000)));
    }
    
    private void GetKit(int pos, Player p) {
        String items = kits.get(pos).trim().replaceAll(" ", "");
        for (String i : items.split(",")) {
            try {
                if (i.indexOf("-") == -1)
                    p.getInventory().addItem(new ItemStack(Integer.parseInt(i), 1));
                else if (i.indexOf("+") == -1) {
                    String[] multiItem = i.split("-");
                    p.getInventory().addItem(new ItemStack(Integer.parseInt(multiItem[0]), Integer.parseInt(multiItem[1])));
                } else {
                    String[] itemVal = i.split("\\+");
                    String[] decCount = itemVal[1].split("-");
                    p.getInventory().addItem(new ItemStack(Integer.parseInt(itemVal[0]), Integer.parseInt(decCount[1]), Short.parseShort(decCount[0])));
                }
            } catch (NumberFormatException e) {
                Messaging.send(p, "&cSyntax error in kit at substring &e" + i);
                Messaging.send(p, "&cPlease report to server admin!");
            }
        }
        p.sendMessage("&2Here you go!");
    }
    
    public static boolean reload() {
        try {
            File dataFolder = General.plugin.getDataFolder();
            BufferedReader br = new BufferedReader(new FileReader(new File(dataFolder, "general.kits")));
            String l;
            int lineNumber = 1;
            kits.clear();
            String list;
            List<String> listing;
            while ((l = br.readLine()) != null) {
                list = l.trim();
                if (!list.startsWith("#")) {
                    listing = Arrays.asList(list.split(":"));
                    if (listing.size() >= 3) {
                        for (int i = 0; i < 3; i++)
                            kits.add(listing.get(i).toLowerCase());
                    } else {
                        General.logger.info("Note: line " + lineNumber + " in general.kits is improperly defined and is ignored");
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
