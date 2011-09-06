
package net.craftstars.general.command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.craftstars.general.General;
import net.craftstars.general.text.HelpHandler;
import net.craftstars.general.text.LanguageText;
import net.craftstars.general.util.Option;
import net.craftstars.general.util.Toolbox;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import com.ensifera.animosity.craftirc.BasePoint;
import com.ensifera.animosity.craftirc.CommandEndPoint;
import com.ensifera.animosity.craftirc.CraftIRC;
import com.ensifera.animosity.craftirc.RelayedCommand;
import com.ensifera.animosity.craftirc.RelayedMessage;

public abstract class CommandBase implements CommandExecutor {
	enum FailurePlace {INIT, HELP, PARSE, EXECUTE, NONE};
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
		boolean commandResult = Option.SHOW_USAGE.get();
		FailurePlace error = FailurePlace.NONE, at = FailurePlace.INIT;
		if(Option.LOG_COMMANDS.get())
			General.logger.info(LanguageText.LOG_COMMAND_USED.value("sender", senderName, "command", cmdStr));
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
		} catch(Exception e) {
			error = at;
			General.logger.error(LanguageText.LOG_COMMAND_ERROR.value("command", command.getName(), "errorPlace", error));
			General.logger.error(LanguageText.LOG_COMMAND_ERROR_INFO.value("command", cmdStr));
			e.printStackTrace();
			commandResult = false;
		}
		commandResult = commandResult || Option.SHOW_USAGE.get();
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
	
	public class CraftIRCForwarder extends BasePoint implements CommandEndPoint {
		public CraftIRCForwarder(CraftIRC irc, String tag) {
			irc.registerEndPoint(tag, this);
			// Second argument below is the command name
			irc.registerCommand(tag, tag); // TODO: Make the command name friendly; configurable?
		}

		@Override
		public Type getType() {
			return Type.MINECRAFT;
		}
	
		@Override
		public void commandIn(RelayedCommand cmd) {
			String commandLabel = cmd.getField("command");
			String cmdStr = commandLabel + " " + cmd.getField("args");
			String senderName = cmd.getField("sender");
			String[] args = cmd.getField("args").split(" ");
			Command command = plugin.getCommand(commandLabel);
			FailurePlace error = FailurePlace.NONE, at = FailurePlace.INIT;
			if(Option.LOG_COMMANDS.get())
				General.logger.info(LanguageText.LOG_COMMAND_USED.value("sender", senderName, "command", cmdStr));
			try {
				IRCReturnSender sender = new IRCReturnSender(cmd);
				if(isHelpCommand(command, commandLabel, args)) {
					at = FailurePlace.HELP;
					String topic = getHelpTopic(command, commandLabel, args);
					boolean commandResult = HelpHandler.displayEntry(sender, topic);
					if(!commandResult) error = at;
				} else {
					setCommand(commandLabel);
					at = FailurePlace.PARSE;
					Map<String,Object> parsedArgs = parse(sender, command, commandLabel, args, false);
					if(parsedArgs != null) {
						at = FailurePlace.EXECUTE;
						boolean commandResult = execute(sender, cmdToExecute, parsedArgs);
						if(!commandResult) error = at;
					} else error = at;
				}
			} catch(Exception e) {
				error = at;
				General.logger.error(LanguageText.LOG_COMMAND_ERROR.value("command", command.getName(), "errorPlace", error));
				General.logger.error(LanguageText.LOG_COMMAND_ERROR_INFO.value("command", cmdStr));
				e.printStackTrace();
			}
		}
		
		private class IRCReturnSender implements CommandSender {
			private char prefix;
			private String channel;
			private String command;
			
			public IRCReturnSender(RelayedCommand from) {
				prefix = from.getField("ircPrefix").charAt(0);
				channel = from.getField("srcChannel");
				command = from.getField("command");
			}

			@Override
			public boolean isOp() {
				return prefix == '@';
			}

			@Override
			public void sendMessage(String message) {
				CraftIRC irc = (CraftIRC) plugin.getServer().getPluginManager().getPlugin("CraftIRC");
				RelayedMessage msg = irc.newMsgToTag(CraftIRCForwarder.this, channel, "generalReply");
				msg.setField("command", command);
				msg.setField("message", message);
				msg.post();
			}

			@Override
			public Server getServer() {
				return plugin.getServer();
			}

			@Override
			public boolean isPermissionSet(String name) {
				return false;
			}

			@Override
			public boolean isPermissionSet(Permission perm) {
				return false;
			}

			@Override
			public boolean hasPermission(String name) {
				Permission perm = getServer().getPluginManager().getPermission(name);
				if(perm == null) return isOp();
				return hasPermission(perm);
			}

			@Override
			public boolean hasPermission(Permission perm) {
				return getServer().getPluginManager().getDefaultPermissions(isOp()).contains(perm);
			}

			@Override
			public PermissionAttachment addAttachment(Plugin p, String name, boolean value) {
				return null;
			}

			@Override
			public PermissionAttachment addAttachment(Plugin p) {
				return null;
			}

			@Override
			public PermissionAttachment addAttachment(Plugin p, String name, boolean value, int ticks) {
				return null;
			}

			@Override
			public PermissionAttachment addAttachment(Plugin p, int ticks) {
				return null;
			}

			@Override
			public void removeAttachment(PermissionAttachment attachment) {
			}

			@Override
			public Set<PermissionAttachmentInfo> getEffectivePermissions() {
				return null;
			}

			@Override public void setOp(boolean value) {}

			@Override public void recalculatePermissions() {}
		}
	}
}
