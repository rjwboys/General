
package net.craftstars.general.command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;

import net.craftstars.general.General;
import net.craftstars.general.util.HelpHandler;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public abstract class CommandBase implements CommandExecutor {
	enum FailurePlace {INIT, HELP, PARSE, EXECUTE, NONE};
	public static boolean SHOW_USAGE = false; // Change to true to not spew out usage notes on incorrect syntax
	private static HashSet<String> frozenAccounts = new HashSet<String>();
	protected final General plugin;
	private String cmdToExecute;
	
	protected CommandBase(General instance) {
		plugin = instance;
	}
	
	@Override
	public synchronized boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		String cmdStr = commandLabel + " " + Toolbox.join(args);
		String senderName = getName(sender);
		boolean commandResult = SHOW_USAGE;
		FailurePlace error = FailurePlace.NONE, at = FailurePlace.INIT;
		if(plugin.config.getBoolean("log-commands", false))
			General.logger.info(senderName + " used command: " + cmdStr);
		try {
			if(isHelpCommand(command, commandLabel, args)) {
				at = FailurePlace.HELP;
				String topic = getHelpTopic(command, commandLabel, args);
				commandResult = HelpHandler.displayEntry(sender, topic);
				if(!commandResult) error = at;
			} else {
				setCommand(command.getName());
				at = FailurePlace.PARSE;
				Map<String,Object> parsedArgs = parse(sender, command, commandLabel, args, sender instanceof Player);
				if(parsedArgs != null) {
					at = FailurePlace.EXECUTE;
					commandResult = execute(sender, cmdToExecute, parsedArgs);
					if(!commandResult) error = at;
				} else error = at;
			}
			/*
			} else if(sender instanceof Player) {
				boolean result = this.fromPlayer((Player) sender, command, commandLabel, args);
				unfreeze((Player) sender);
				return result;
			} else if(sender instanceof ConsoleCommandSender) {
				return this.fromConsole((ConsoleCommandSender) sender, command, commandLabel, args);
			} else {
				return this.fromUnknown(sender, command, commandLabel, args);
			}
			*/
		} catch(Exception e) {
			error = at;
			General.logger.error(Messaging.format("There was an error with command [{command}] during {errorPlace}!" +
					" Please report this!", "command", command.getName(), "errorPlace", error));
			General.logger.error(Messaging.format("Full command string: [{command}]", "command", cmdStr));
			e.printStackTrace();
			commandResult = false;
		}
		return commandResult;
	}
	
	public abstract Map<String, Object> parse(CommandSender sender, Command command, String label, String[] args, boolean isPlayer);
	
	public abstract boolean execute(CommandSender sender, String command, Map<String, Object> args);

	protected String getName(CommandSender sender) {
		if(sender instanceof ConsoleCommandSender) return "CONSOLE";
		Class<? extends CommandSender> clazz = sender.getClass();
		try {
			Method getName = clazz.getMethod("getName");
			return getName.invoke(sender).toString();
		} catch(NoSuchMethodException e) {
			return clazz.getSimpleName();
		} catch(IllegalArgumentException e) {
			return clazz.getSimpleName();
		} catch(IllegalAccessException e) {
			return clazz.getSimpleName();
		} catch(InvocationTargetException e) {
			return clazz.getSimpleName();
		}
	}
	
	@SuppressWarnings("unused")
	protected boolean isHelpCommand(Command command, String commandLabel, String[] args) {
		if(args.length == 1 && args[0].equalsIgnoreCase("help")) return true;
		return false;
	}
	
	@SuppressWarnings("unused")
	protected String getHelpTopic(Command command, String commandLabel, String[] args) {
		return command.getName();
	}

	public static boolean isFrozen(Player sender) {
		return frozenAccounts.contains(sender.getName());
	}
	
	public static void freeze(Player sender) {
		frozenAccounts.add(sender.getName());
	}
	
	public static void unfreeze(Player sender) {
		frozenAccounts.remove(sender.getName());
	}
	
	protected void setCommand(String command) {
		cmdToExecute = command;
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
	
	protected String[] dropFirstArg(String[] args) {
		String[] newArgs = new String[args.length-1];
		for(int i = 1; i < args.length; i++)
			newArgs[i] = args[i];
		return newArgs;
	}
	
	protected String[] dropLastArg(String[] args) {
		String[] newArgs = new String[args.length-1];
		for(int i = 0; i < args.length - 1; i++)
			newArgs[i] = args[i];
		return newArgs;
	}
}
