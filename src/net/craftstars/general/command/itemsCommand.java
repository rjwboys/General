package net.craftstars.general.command;

import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.CommandBase;
import net.craftstars.general.General;
import net.craftstars.general.items.ItemID;
import net.craftstars.general.items.Items;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

public class itemsCommand extends CommandBase {

    @Override
    public boolean fromConsole(General plugin, CommandSender sender, Command command,
            String commandLabel, String[] args) {
        if(args.length < 2) return Toolbox.SHOW_USAGE;
        Player toWhom = Toolbox.getPlayer(args[0], sender);
        doGive(toWhom, sender, Arrays.copyOfRange(args, 1, args.length));
        return Toolbox.SHOW_USAGE;
    }

    private void doGive(Player toWhom, CommandSender sender, String[] items) {
        StringBuilder text = new StringBuilder("Giving &f");
        for(String item : items) {
            ItemID what = Items.validate(item);
            if(what == null || !what.isValid()) continue;
            text.append(Items.name(what));
            text.append("&2, &f");
            Items.giveItem(toWhom, what, 1);
        }
        text.delete(text.lastIndexOf("&2, &f"), text.length());
        if(toWhom == sender) {
            Messaging.send(sender, "&2Enjoy! " + text + "&f!");
        } else {
            Messaging.send(toWhom, "&2Enjoy the gift! " + text + "&f!");
            Messaging.send(sender, text + "&2 to &f" + toWhom.getName() + "&f!");
        }
    }

    @Override
    public boolean fromPlayer(General plugin, Player sender, Command command, String commandLabel,
            String[] args) {
        if(Toolbox.lacksPermission(plugin, sender, "give many items at once", "general.give.mass")) return true;
        doGive(sender, sender, args);
        return Toolbox.SHOW_USAGE;
    }

}
