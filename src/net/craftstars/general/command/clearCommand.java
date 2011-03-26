
package net.craftstars.general.command;

import net.craftstars.general.CommandBase;
import net.craftstars.general.General;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

public class clearCommand extends CommandBase {
    private enum CleanType {
        FULL("inventory"),
        QUICKBAR("quick-bar"),
        PACK("pack"),
        ARMOUR("armour");
        private String name;
        
        private CleanType(String nm) {
            name = nm;
        }
        
        public String getName() {
            return name;
        }
    }
    @Override
    public boolean fromConsole(General plugin, CommandSender sender, Command command,
            String commandLabel, String[] args) {
        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("help")) return Toolbox.SHOW_USAGE;
            Player who = Toolbox.getPlayer(args[0], sender);
            if(who == null) return true;
            doClean(who, sender, CleanType.FULL);
        } else if(args.length == 2) {
            Player who = Toolbox.getPlayer(args[0], sender);
            if(who == null) return true;
            if(args[1].equalsIgnoreCase("pack")) {
                doClean(who, sender, CleanType.PACK);
            } else if(args[1].equalsIgnoreCase("quickbar")) {
                doClean(who, sender, CleanType.QUICKBAR);
            } else if(Toolbox.equalsOne(args[1], "armour", "armor")) {
                doClean(who, sender, CleanType.ARMOUR);
            } else if(args[1].equalsIgnoreCase("all")) {
                doClean(who, sender, CleanType.FULL);
            }
        } else return Toolbox.SHOW_USAGE;
        return true;
    }

    @Override
    public boolean fromPlayer(General plugin, Player sender, Command command, String commandLabel,
            String[] args) {
        if(Toolbox.lacksPermission(plugin, sender, "clear your inventory", "general.clear")) return true;
        if(args.length == 0) {
            doClean(sender, sender, CleanType.FULL);
        } else if(args.length == 1) {
            if(args[0].equalsIgnoreCase("help")) return Toolbox.SHOW_USAGE;
            else if(args[0].equalsIgnoreCase("pack")) {
                doClean(sender, sender, CleanType.PACK);
            } else if(args[0].equalsIgnoreCase("quickbar")) {
                doClean(sender, sender, CleanType.QUICKBAR);
            } else if(Toolbox.equalsOne(args[0], "armour", "armor")) {
                doClean(sender, sender, CleanType.ARMOUR);
            } else if(args[1].equalsIgnoreCase("all")) {
                doClean(sender, sender, CleanType.FULL);
            } else {
                if(Toolbox.lacksPermission(plugin, sender, "clear someone else's inventory", "general.clear.other")) return true;
                Player who = Toolbox.getPlayer(args[0], sender);
                if(who == null) return true;
                doClean(who, sender, CleanType.FULL);
            }
        } else if(args.length == 2) {
            if(Toolbox.lacksPermission(plugin, sender, "clear someone else's inventory", "general.clear.other")) return true;
            Player who = Toolbox.getPlayer(args[0], sender);
            if(who == null) return true;
            if(args[1].equalsIgnoreCase("pack")) {
                doClean(who, sender, CleanType.PACK);
            } else if(args[1].equalsIgnoreCase("quickbar")) {
                doClean(who, sender, CleanType.QUICKBAR);
            } else if(Toolbox.equalsOne(args[1], "armour", "armor")) {
                doClean(who, sender, CleanType.ARMOUR);
            } else if(args[1].equalsIgnoreCase("all")) {
                doClean(who, sender, CleanType.FULL);
            }
        } else return Toolbox.SHOW_USAGE;
        return true;
    }

    private void doClean(Player who, CommandSender fromWhom, CleanType howMuch) {
        boolean selfClear = false;
        if(fromWhom instanceof Player) {
            if( ((Player) fromWhom).getName().equalsIgnoreCase(who.getName())) selfClear = true;
        }
        PlayerInventory i = who.getInventory();
        switch(howMuch) {
        case FULL:
            i.clear();
        case ARMOUR:
            clearArmour(i);
        break;
        case QUICKBAR:
            clearQuickbar(i);
        break;
        case PACK:
            clearPack(i);
        break;
        }
        if(selfClear) {
            Messaging.send(who, "&2You have cleared your " + howMuch.getName() + ".");
        } else {
            Messaging.send(who, "&2Your " + howMuch.getName() + " has been cleared.");
            Messaging.send(fromWhom, "&f" + who.getName() + "&2's " + howMuch.getName() + " has been cleared.");
        }
    }

    private void clearArmour(PlayerInventory i) {
        i.setBoots(null);
        i.setLeggings(null);
        i.setChestplate(null);
        i.setHelmet(null);
    }

    private void clearQuickbar(PlayerInventory i) {
        for(int j = 0; j < 9; j++)
            i.setItem(j, null);
    }

    private void clearPack(PlayerInventory i) {
        for(int j = 9; j < i.getSize(); j++)
            i.setItem(j, null);
    }
}
