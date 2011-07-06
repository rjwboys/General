
package net.craftstars.general.command.inven;

import net.craftstars.general.command.CommandBase;
import net.craftstars.general.items.ItemID;
import net.craftstars.general.General;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class clearCommand extends CommandBase {
	private boolean sell;
	private enum CleanType {
		FULL("inventory"), QUICKBAR("quick-bar"), PACK("pack"), ARMOUR("armour");
		private String name;
		
		private CleanType(String nm) {
			name = nm;
		}
		
		public String getName() {
			return name;
		}
	}

	public clearCommand(General instance) {
		super(instance);
	}
	
	@Override
	public boolean fromConsole(ConsoleCommandSender sender, Command command, String commandLabel,
			String[] args) {
		if(args.length == 1) {
			if(args[0].equalsIgnoreCase("help")) return SHOW_USAGE;
			Player who = Toolbox.matchPlayer(args[0]);
			if(who == null) return Messaging.invalidPlayer(sender, args[0]);
			doClean(who, sender, CleanType.FULL);
		} else if(args.length == 2) {
			Player who = Toolbox.matchPlayer(args[0]);
			if(who == null) return Messaging.invalidPlayer(sender, args[0]);
			if(args[1].equalsIgnoreCase("pack")) {
				doClean(who, sender, CleanType.PACK);
			} else if(args[1].equalsIgnoreCase("quickbar")) {
				doClean(who, sender, CleanType.QUICKBAR);
			} else if(Toolbox.equalsOne(args[1], "armour", "armor")) {
				doClean(who, sender, CleanType.ARMOUR);
			} else if(args[1].equalsIgnoreCase("all")) {
				doClean(who, sender, CleanType.FULL);
			}
		} else return SHOW_USAGE;
		return true;
	}
	
	@Override
	public boolean fromPlayer(Player sender, Command command, String commandLabel, String[] args) {
		if(Toolbox.lacksPermission(sender, "general.clear"))
			return Messaging.lacksPermission(sender, "clear your inventory");
		if(args.length == 0) {
			doClean(sender, sender, CleanType.FULL);
		} else if(args.length == 1) {
			if(args[0].equalsIgnoreCase("help"))
				return SHOW_USAGE;
			else if(args[0].equalsIgnoreCase("pack")) {
				doClean(sender, sender, CleanType.PACK);
			} else if(args[0].equalsIgnoreCase("quickbar")) {
				doClean(sender, sender, CleanType.QUICKBAR);
			} else if(Toolbox.equalsOne(args[0], "armour", "armor")) {
				doClean(sender, sender, CleanType.ARMOUR);
			} else if(args[1].equalsIgnoreCase("all")) {
				doClean(sender, sender, CleanType.FULL);
			} else {
				if(Toolbox.lacksPermission(sender, "general.clear.other"))
					return Messaging.lacksPermission(sender, "clear someone else's inventory");
				Player who = Toolbox.matchPlayer(args[0]);
				if(who == null) return Messaging.invalidPlayer(sender, args[0]);
				doClean(who, sender, CleanType.FULL);
			}
		} else if(args.length == 2) {
			if(Toolbox.lacksPermission(sender, "general.clear.other"))
				return Messaging.lacksPermission(sender, "clear someone else's inventory");
			Player who = Toolbox.matchPlayer(args[0]);
			if(who == null) return Messaging.invalidPlayer(sender, args[0]);
			if(args[1].equalsIgnoreCase("pack")) {
				doClean(who, sender, CleanType.PACK);
			} else if(args[1].equalsIgnoreCase("quickbar")) {
				doClean(who, sender, CleanType.QUICKBAR);
			} else if(Toolbox.equalsOne(args[1], "armour", "armor")) {
				doClean(who, sender, CleanType.ARMOUR);
			} else if(args[1].equalsIgnoreCase("all")) {
				doClean(who, sender, CleanType.FULL);
			}
		} else return SHOW_USAGE;
		return true;
	}
	
	private void doClean(Player who, CommandSender fromWhom, CleanType howMuch) {
		boolean selfClear = false;
		double revenue = 0.0;
		if(fromWhom instanceof Player) {
			if(((Player) fromWhom).getName().equalsIgnoreCase(who.getName())) selfClear = true;
		}
		sell = selfClear;
		PlayerInventory i = who.getInventory();
		switch(howMuch) {
		case FULL:
			if(sell) for(ItemStack item : i.getContents())
				revenue += Toolbox.sellItem(new ItemID(item), item.getAmount());
			i.clear();
			// Case fallthrough intentional
		case ARMOUR:
			if(sell) for(ItemStack item : i.getArmorContents())
				revenue += Toolbox.sellItem(new ItemID(item), item.getAmount());
			clearArmour(i);
		break;
		case QUICKBAR:
			if(sell) for(int j = 0; j < 9; j++)
				revenue += Toolbox.sellItem(new ItemID(i.getItem(j)), i.getItem(j).getAmount());
			clearQuickbar(i);
		break;
		case PACK:
			if(sell) for(int j = 9; j < i.getSize(); j++)
				revenue += Toolbox.sellItem(new ItemID(i.getItem(j)), i.getItem(j).getAmount());
			clearPack(i);
		break;
		}
		if(selfClear) {
			Messaging.send(who, "&2You have cleared your " + howMuch.getName() + ".");
		} else {
			Messaging.send(who, "&2Your " + howMuch.getName() + " has been cleared.");
			Messaging.send(fromWhom, "&f" + who.getName() + "&2's " + howMuch.getName() + " has been cleared.");
		}
		if(sell) Messaging.earned(who, revenue);
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
