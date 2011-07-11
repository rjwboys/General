
package net.craftstars.general.command;

import net.craftstars.general.General;

import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public abstract class CommandBase {
	public static final boolean SHOW_USAGE = false; // Change to true to not spew out usage notes on incorrect syntax
	protected final General plugin;
	
	protected CommandBase(General instance) {
		plugin = instance;
	}
	
	public boolean runCommand(CommandSender sender, Command command, String commandLabel,
			String[] args) {
		if(sender instanceof Player) {
			boolean result = this.fromPlayer((Player) sender, command, commandLabel, args);
			General.plugin.unfreeze((Player) sender);
			return result;
		} else if(sender instanceof ConsoleCommandSender) {
			return this.fromConsole((ConsoleCommandSender) sender, command, commandLabel, args);
		} else {
			return this.fromUnknown(sender, command, commandLabel, args);
		}
	}
	
	public abstract boolean fromPlayer(Player sender, Command command, String commandLabel,
			String[] args);
	
	public abstract boolean fromConsole(ConsoleCommandSender sender, Command command, String commandLabel,
			String[] args);
	
	@SuppressWarnings("unused")
	public boolean fromUnknown(CommandSender sender, Command command, String commandLabel, String[] args) {
		throw new CommandException("Unknown sender type, aborting command.");
	}
	
	protected String[] prependArg(String[] args, String first) {
		String[] newArgs = new String[args.length + 1];
		newArgs[0] = first;
		for(int i = 0; i < args.length; i++)
			newArgs[i + 1] = args[i];
		return newArgs;
	}
	
	protected String[] appendArg(String[] args, String last) {
		String[] newArgs = new String[args.length + 1];
		newArgs[args.length] = last;
		for(int i = 0; i < args.length; i++)
			newArgs[i] = args[i];
		return newArgs;
	}
}
