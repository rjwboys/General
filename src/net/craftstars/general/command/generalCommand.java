
package net.craftstars.general.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.command.CommandBase;
import net.craftstars.general.General;
import net.craftstars.general.items.ItemID;
import net.craftstars.general.items.Items;
import net.craftstars.general.items.Kit;
import net.craftstars.general.items.Kits;
import net.craftstars.general.mobs.InvalidMobException;
import net.craftstars.general.mobs.MobData;
import net.craftstars.general.mobs.MobType;
import net.craftstars.general.teleport.DestinationType;
import net.craftstars.general.teleport.TargetType;
import net.craftstars.general.text.HelpHandler;
import net.craftstars.general.text.LanguageText;
import net.craftstars.general.text.MessageOfTheDay;
import net.craftstars.general.text.Messaging;
import net.craftstars.general.util.EconomyManager;
import net.craftstars.general.util.Option;
import net.craftstars.general.util.PermissionManager;
import net.craftstars.general.util.Toolbox;

public class generalCommand extends CommandBase {
	public generalCommand(General instance) {
		super(instance);
	}
	
	private boolean isHelp(String help) {
		return help.equalsIgnoreCase("help");
	}
	
	@Override
	protected boolean isHelpCommand(Command command, String commandLabel, String[] args) {
		// /general
		if(args.length == 0) return true;
		// /general help [<topic>]
		if(isHelp(args[0])) return true;
		// /general economy <variable>
		if(args[0].equalsIgnoreCase("economy"))
			return !Toolbox.join(args).contains("=");
		switch(args.length) {
		case 1: // /general help
			return isHelp(args[0]);
		case 2: // /general <command> help OR /general set list
			return isHelp(args[1]) || (args[0]+args[1]).equalsIgnoreCase("setlist");
		case 3:
			// /general <command> help <subcommand>
			if(Toolbox.equalsOne(args[0], "kit", "set", "item"))
				return isHelp(args[1]);
			// /general <command> <subcommand> help
			return isHelp(args[2]);
		}
		return false;
	}
	
	@Override
	protected String getHelpTopic(Command command, String commandLabel, String[] args) {
		// /general -> about
		if(args.length == 0) return "about";
		// /general help <topic> -> <topic>
		if(isHelp(args[0]) && args.length > 1) return Toolbox.join(args, "_", 1);
		// /general economy <variable> -> general_economy_<veriable>
		if(args[0].equalsIgnoreCase("economy")) {
			String[] ecoArgs = Toolbox.join(args, "_").split("\\s*=\\s*");
			return "general_economy_" + ecoArgs[0];
		}
		switch(args.length) {
		case 1: // /general help -> toc
			return "toc";
		case 2:
			// /general set list -> general_set_list
			if((args[0]+args[1]).equalsIgnoreCase("setlist")) return "general_set_list";
			// /general <command> help -> general_<command>
			return "general_" + args[0];
		case 3:
			// /general <command> <subcommand> help -> general_<command>_<subcommand>
			if(isHelp(args[2])) return "general_" + args[0] + "_" + args[1];
			// /general <command> help <subcommand> -> general_<command>_<subcommand>
			else if(Toolbox.equalsOne(args[0], "kit", "set", "item")) return "general_" + Toolbox.join(args, "_");
		}
		return null;
	}
	
	@Override
	public boolean execute(CommandSender sender, String command, Map<String,Object> params) {
		String[] args = (String[]) params.get("args");
		if(args.length < 1) return false;
		if(args[0].equalsIgnoreCase("reload")) {
			if(!sender.hasPermission("general.admin.reload"))
				return Messaging.lacksPermission(sender, "general.admin.reload");
			doReload(sender);
			return true;
		} else if(args[0].equalsIgnoreCase("help")) {
			if(args.length == 1) {
				HelpHandler.showHelp(sender, "player.help");
				return true;
			} else if(args.length == 2) {
				HelpHandler.showHelp(sender, args[1] + ".help");
				return true;
			}
		} else if(args[0].equalsIgnoreCase("motd")) {
			MessageOfTheDay.showMotD(sender);
			return true;
		} else if(args[0].equalsIgnoreCase("item")) {
			if(!sender.hasPermission("general.admin.item"))
				return Messaging.lacksPermission(sender, "general.admin.item");
			if(args.length < 3) return false;
			return itemEdit(sender, Arrays.copyOfRange(args, 2, args.length));
		} else if(args[0].equalsIgnoreCase("save")) {
			if(!sender.hasPermission("general.admin.save"))
				return Messaging.lacksPermission(sender, "general.admin.save");
			doSave(sender);
			return true;
		} else if(args[0].equalsIgnoreCase("cost")) {
			Player who = sender instanceof Player ? (Player) sender : Toolbox.matchPlayer(args[0]);
			if(who == null) {
				Messaging.send(sender, LanguageText.ECONOMY_NO_PLAYER);
				return true;
			}
			String check = Toolbox.join(args, 1);
			if(check.charAt(0) == '/') check = check.substring(1);
			freeze(who);
			plugin.getServer().dispatchCommand(who, check);
			return true;
		} else if(args[0].equalsIgnoreCase("kit")) {
			if(!sender.hasPermission("general.admin.kit"))
				return Messaging.lacksPermission(sender, "general.admin.kit");
			if(args.length < 3) return false;
			return kitEdit(sender, Arrays.copyOfRange(args, 1, args.length));
		} else if(args[0].equalsIgnoreCase("economy")) {
			if(!sender.hasPermission("general.admin.economy"))
				return Messaging.lacksPermission(sender, "general.admin.economy");
			if(args.length < 3) return false;
			return setEconomy(sender, Arrays.copyOfRange(args, 1, args.length));
		} else if(args[0].equalsIgnoreCase("set")) {
			if(!sender.hasPermission("general.admin.set"))
				return Messaging.lacksPermission(sender, "general.admin.set");
			if(args.length < 3) return false;
			return setVar(sender, Arrays.copyOfRange(args, 1, args.length));
		} else if(args[0].equalsIgnoreCase("genlang")) {
			if(!sender.hasPermission("general.admin.genlang"))
				return Messaging.lacksPermission(sender, "general.admin.genlang");
			for(LanguageText lang : LanguageText.values())
				lang.getFormat();
			Messaging.save();
		}
		return false;
	}

	private void doReload(CommandSender sender) {
		plugin.loadAllConfigs();
		PermissionManager.refreshItemGroups();
		Messaging.send(sender, LanguageText.GENERAL_RELOAD);
	}
	
	private void doSave(CommandSender sender) {
		Option.save();
		Items.save();
		Kits.save();
		Messaging.save();
		Messaging.send(sender, LanguageText.GENERAL_SAVE);
	}
	
	private boolean kitEdit(CommandSender sender, String[] args) {
		if(args.length < 2) return false;
		String kitName = args[0];
		if(!Kits.exists(kitName) && !args[1].equals("add")) {
			Messaging.send(sender, LanguageText.KIT_INVALID.value("kit", kitName));
			return true;
		}
		if(args[1].equalsIgnoreCase("add")) {
			Kit kit;
			if(Kits.exists(kitName))
				kit = Kits.get(kitName);
			else {
				Messaging.send(sender, LanguageText.KIT_NEW.value("kit", kitName));
				kit = new Kit(kitName, 0, 0);
				Kits.put(kitName, kit);
				if(args.length < 3) return true;
			}
			if(args.length < 3) return false;
			ItemID item = Items.validate(args[2]);
			int amount = 1;
			if(args.length >= 4) try {
				amount = Integer.parseInt(args[3]);
			} catch(NumberFormatException e) {
				Messaging.send(sender, LanguageText.GIVE_BAD_AMOUNT);
				return true;
			}
			kit.add(item, amount);
			Messaging.send(sender, LanguageText.KIT_NEW.value("amount", amount, "item", item.getName(), "kit", kitName));
			return true;
		} else if(args[1].equalsIgnoreCase("remove")) {
			Kit kit = Kits.get(kitName);
			if(args.length < 3) return false;
			ItemID item = Items.validate(args[2]);
			if(!kit.contains(item)) {
				Messaging.send(sender, LanguageText.KIT_NOT_IN.value("kit", kitName, "item", item.getName()));
				return true;
			}
			int amount = Integer.MAX_VALUE;
			if(args.length >= 4) try {
				amount = Integer.parseInt(args[3]);
			} catch(NumberFormatException e) {
				Messaging.send(sender, LanguageText.GIVE_BAD_AMOUNT);
				return true;
			}
			kit.add(item, -amount);
			if(!kit.contains(item)) amount = 0;
			Messaging.send(sender, LanguageText.KIT_REMOVE.value("kit",kitName,"item",item.getName(),"amount",amount));
			return true;
		} else if(args[1].equalsIgnoreCase("delay")) {
			Kit kit = Kits.get(kitName);
			int delay;
			try {
				delay = Integer.parseInt(args[2]);
			} catch(NumberFormatException e) {
				Messaging.send(sender, LanguageText.KIT_BAD_DELAY);
				return true;
			}
			kit.delay = delay;
			Messaging.send(sender, LanguageText.KIT_DELAY.value("kit", kitName, "delay", delay));
			return true;
		} else if(args[1].equalsIgnoreCase("cost")) {
			Kit kit = Kits.get(kitName);
			double cost;
			try {
				cost = Double.parseDouble(args[2]);
			} catch(NumberFormatException e) {
				Messaging.send(sender, LanguageText.KIT_BAD_COST);
				return true;
			}
			kit.setSavedCost(cost);
			String displayCost = Double.toString(cost);
			Player player = sender instanceof Player ? (Player)sender : null;
			if(!Option.NO_ECONOMY.get())
				displayCost = EconomyManager.formatCost(player, cost);
			Messaging.send(sender, LanguageText.KIT_COST.value("kit", kitName, "cost", displayCost));
			return true;
		} else if(args[1].equalsIgnoreCase("trash")) {
			Kits.remove(kitName);
			Messaging.send(sender, LanguageText.KIT_TRASH.value("kit", kitName));
			return true;
		} else if(args[1].equalsIgnoreCase("list")) {
			Kit kit = Kits.get(kitName);
			ArrayList<String> names = new ArrayList<String>();
			for(ItemID item : kit) names.add(kit.get(item) + "x " + item.getName());
			String items = Toolbox.join(names.toArray(new String[0]), LanguageText.ITEMS_JOINER.value());
			Messaging.send(sender, LanguageText.KIT_CONTAINS.value("kit", kitName, "items", items));
			Messaging.send(sender, LanguageText.KIT_INFO.value("kit",kitName,"delay",kit.delay,"cost",kit.getCost()));
		}
		return false;
	}

	private boolean setEconomy(CommandSender sender, String[] args) {
		args = Toolbox.join(args, 0).split("\\s*=\\s*");
		if(args.length == 2) {
			double value;
			try {
				value = Double.valueOf(args[1]);
			} catch(NumberFormatException e) {
				Messaging.invalidNumber(sender, args[1]);
				return true;
			}
			args = args[0].trim().split("\\s+");
			if(args.length == 0) return false;
			String path = "economy.";
			if(Toolbox.equalsOne(args[0], "heal", "hurt")) path += args[0];
			else if(args[0].equalsIgnoreCase("time")) {
				if(args.length == 1) {
					Messaging.send(sender, LanguageText.ADMIN_ECON_WHICH_TIME);
					return true;
				} else if(Toolbox.equalsOne(args[1], "day", "night", "dawn", "dusk", "noon", "midnight", "set"))
					path += args[1];
				else {
					Messaging.send(sender, LanguageText.ADMIN_ECON_BAD_TIME);
					return true;
				}
			} else if(args[0].equalsIgnoreCase("weather")) {
				if(args.length == 1) {
					Messaging.send(sender, LanguageText.ADMIN_ECON_WHICH_WEATHER);
					return true;
				} else if(Toolbox.equalsOne(args[1], "storm", "thunder", "zap"))
					path += args[1];
				else {
					Messaging.send(sender, LanguageText.ADMIN_ECON_BAD_WEATHER);
					return true;
				}
			} else if(args[0].equalsIgnoreCase("mobspawn")) {
				if(args.length == 1) {
					Messaging.send(sender, LanguageText.ADMIN_ECON_WHICH_MOB);
					return true;
				} else {
					path += "mobspawn.";
					// Validate mob name
					MobType mob = MobType.getMob(args[1]);
					if(mob == null) {
						Messaging.send(sender, LanguageText.ADMIN_ECON_BAD_MOB);
						return true;
					}
					path += mob.toString().toLowerCase().replace('_', '-');
					// Validate mob data
					String data = null, mountName = null, dataMount = null;
					switch(args.length) {
					case 2: // mobspawn <mob>
						break;
					case 3: // mobspawn <mob> free|<data>
						if(!args[2].equalsIgnoreCase("free"))
							data = args[2];
						break;
					case 4:
						if(!args[2].equalsIgnoreCase("riding")) {
							// mobspawn <mob> <data> free
							if(!args[3].equalsIgnoreCase("free")) {
								Messaging.send(sender, LanguageText.ADMIN_ECON_BAD_MOBSPEC);
								return true;
							}
							data = args[2];
							break;
						} // mobspawn <mob> riding <mob>
						mountName = args[3];
						break;
					case 5:
						if(args[2].equalsIgnoreCase("riding")) {
							// mobspawn <mob> riding <mob> <data>
							mountName = args[3];
							dataMount = args[4];
						} else if(args[3].equalsIgnoreCase("riding")) {
							// mobspawn <mob> <data> riding <mob>
							data = args[2];
							mountName = args[4];
						} else {
							Messaging.send(sender, LanguageText.ADMIN_ECON_BAD_MOBSPEC);
							return true;
						}
						break;
					case 6: // mobspawn <mob> <data> riding <mob> <data>
						if(!args[3].equalsIgnoreCase("riding")) {
							Messaging.send(sender, LanguageText.ADMIN_ECON_BAD_MOBSPEC);
							return true;
						}
						data = args[2];
						mountName = args[4];
						dataMount = args[5];
						break;
					default:
						Messaging.send(sender, LanguageText.ADMIN_ECON_BAD_MOBSPEC);
						return true;
					}
					// Validate basic data, if present
					MobData mobData = null, mountData = null;
					if(data != null) {
						try {
							mobData = MobData.parse(mob, null, data);
						} catch(InvalidMobException e) {
							Messaging.send(sender, LanguageText.ADMIN_ECON_BAD_MOBDATA);
							return true;
						}
					}
					// Validate mount, if present
					MobType mount = null;
					if(mountName != null) {
						mount = MobType.getMob(mountName);
						if(mount == null) {
							Messaging.send(sender, LanguageText.ADMIN_ECON_BAD_MOUNT);
							return true;
						}
						// Validate mount data, if present
						if(dataMount != null) {
							mountData = MobData.parse(mob, null, dataMount);
							if(mountData == null) {
								Messaging.send(sender, LanguageText.ADMIN_ECON_BAD_MOBDATA);
								return true;
							}
						}
					}
					// Determine the minimal required path
					if(mobData != null) path += mobData.getCostNode(path);
					if(mount != null) {
						path += ".riding.";
						path += mount.toString().toLowerCase().replace('_', '-');
						if(mountData != null) path += mountData.getCostNode(path);
					}
					// Rewrite existing nodes to allow for the new path.
					if(!Option.nodeExists(path)) {
						String checkPath = path;
						do {
							int lastDot = checkPath.lastIndexOf('.');
							checkPath = checkPath.substring(0, lastDot);
							if(checkPath.endsWith("mobspawn")) break;
							if(checkPath.endsWith("riding")) continue;
							if(!Option.nodeExists(checkPath)) continue;
							Object node = Option.getProperty(checkPath);
							if(node instanceof Number) {
								Map<String, Object> map = new HashMap<String,Object>();
								// Okay, we found a leaf, but where is it?
								// There are four possibilities.
								// 1. This is a riding node, and it's the mob name of the mount.
								//  ->If our path specifies a datavalue, we need to rearrange.
								if(checkPath.contains("riding")) {
									if(checkPath.lastIndexOf(".") == checkPath.indexOf("riding") + "riding".length()) {
										if(mount != null && path.replace(checkPath + ".", "").contains(".")) {
											map.put(mount.getNewData().getCostNode("").replace(".", ""), node);
											Option.setProperty(checkPath, map);
										}
									}
								} else if(path.contains("riding")) { // but checkPath doesn't contain "riding"
									// 2. This is a riding node, and it's the mob data of the rider.
									//  ->Put the value in a "free" subnode.
									if(checkPath.replace("economy.mobspawn.", "").contains(".")) {
										map.put("free", node);
										Option.setProperty(checkPath, map);
									}
									// 3. This may or may not be a riding node, and it's the mob name of the rider.
									//  ->If our path specifies a datavalue, we need to rearrange.
									else {
										map.put(mob.getNewData().getCostNode("").replace(".", ""), node);
										Option.setProperty(checkPath, map);
									}
								}
								break;
							} else if(node instanceof Map && path.contains("riding") && !checkPath.contains("riding")) {
								if(((Map<?, ?>)node).containsKey("riding") || ((Map<?, ?>)node).containsKey("free")) {
									path = checkPath + ".free" + path.substring(lastDot);
									break;
								}
							}
						} while(checkPath.contains("."));
					}
				}
			} else if(args[0].equalsIgnoreCase("give")) {
				if(args.length == 1) {
					Messaging.send(sender, "{rose}Which item?");
					return true;
				} else {
					ItemID item = Items.validate(args[1]);
					path += "item" + item.toString();
				}
			} else if(Toolbox.equalsOne(args[0], "teleport", "setspawn")) {
				switch(args.length) {
				case 2:
					String target = null;
					if(args[0].equalsIgnoreCase("teleport") && args[1].equals("world")) {
						Messaging.send(sender, LanguageText.ADMIN_ECON_BAD_TARGET_TELE);
						return true;
					} else if(args[0].equalsIgnoreCase("setspawn") && args[1].equalsIgnoreCase("mobs")) {
						Messaging.send(sender, LanguageText.ADMIN_ECON_BAD_TARGET_SPAWN);
						return true;
					}
					for(TargetType targetType : TargetType.values()) {
						if(args[1].equalsIgnoreCase(targetType.toString())) {
							target = args[1];
							break;
						}
					}
					if(target == null) {
						Messaging.send(sender, LanguageText.ADMIN_ECON_BAD_TARGET);
						return true;
					}
					path += target;
				case 3:
					if(args[1].equalsIgnoreCase("to")) {
						String dest = null;
						for(DestinationType destType : DestinationType.values()) {
							if(args[1].equalsIgnoreCase(destType.toString())) {
								dest = args[1];
								break;
							}
						}
						if(dest == null) {
							Messaging.send(sender, LanguageText.ADMIN_ECON_BAD_DEST);
							return true;
						}
						path += "to." + dest;
					} else if(Toolbox.equalsOne(args[1], "into", "from")) {
						World world = Toolbox.matchWorld(args[2]);
						if(world == null) {
							Messaging.send(sender, LanguageText.ADMIN_ECON_BAD_WORLD);
							return true;
						}
						path += args[1] + "." + world.getName();
					}
				}
			}
			Option.ECONOMY_COST(path).set(value);
			Messaging.send(sender, LanguageText.ADMIN_ECON_SET.value("path", path, "value", value));
			return true;
		}
		return false;
	}

	private boolean setVar(CommandSender sender, String[] args) {
		Option node = null;
		Object value = null;
		if(args.length != 2) return false;
		if(args[0].equalsIgnoreCase("others-for-all")) {
			node = Option.OTHERS4ALL;
			if(!Toolbox.equalsOne(args[1], "true", "false")) {
				Messaging.send(sender, LanguageText.ADMIN_VAR_BOOL);
				return true;
			}
			value = Boolean.valueOf(args[1]);
		} else if(args[0].equalsIgnoreCase("give-mass")) {
			node = Option.GIVE_MASS;
			try {
				value = Integer.valueOf(args[1]);
			} catch(NumberFormatException e) {
				Messaging.send(sender, LanguageText.ADMIN_VAR_INT);
				return true;
			}
		} else if(args[0].equalsIgnoreCase("show-health")) {
			node = Option.SHOW_HEALTH;
			if(!Toolbox.equalsOne(args[1], "true", "false")) {
				Messaging.send(sender, LanguageText.ADMIN_VAR_BOOL);
				return true;
			}
			value = Boolean.valueOf(args[1]);
		} else if(args[0].equalsIgnoreCase("show-coords")) {
			node = Option.SHOW_COORDS;
			if(!Toolbox.equalsOne(args[1], "true", "false")) {
				Messaging.send(sender, LanguageText.ADMIN_VAR_BOOL);
				return true;
			}
			value = Boolean.valueOf(args[1]);
		} else if(args[0].equalsIgnoreCase("show-world")) {
			node = Option.SHOW_WORLD;
			if(!Toolbox.equalsOne(args[1], "true", "false")) {
				Messaging.send(sender, LanguageText.ADMIN_VAR_BOOL);
				return true;
			}
			value = Boolean.valueOf(args[1]);
		} else if(args[0].equalsIgnoreCase("show-ip")) {
			node = Option.SHOW_IP;
			if(!Toolbox.equalsOne(args[1], "true", "false")) {
				Messaging.send(sender, LanguageText.ADMIN_VAR_BOOL);
				return true;
			}
			value = Boolean.valueOf(args[1]);
		} else if(args[0].equalsIgnoreCase("show-motd")) {
			node = Option.SHOW_MOTD;
			if(!Toolbox.equalsOne(args[1], "true", "false")) {
				Messaging.send(sender, LanguageText.ADMIN_VAR_BOOL);
				return true;
			}
			value = Boolean.valueOf(args[1]);
		} else if(args[0].equalsIgnoreCase("24-hour")) {
			node = Option.TIME_FORMAT;
			if(!Toolbox.equalsOne(args[1], "true", "false")) {
				Messaging.send(sender, LanguageText.ADMIN_VAR_BOOL);
				return true;
			}
			value = Boolean.valueOf(args[1]);
		} else if(args[0].equalsIgnoreCase("show-ticks")) {
			node = Option.SHOW_TICKS;
			if(!Toolbox.equalsOne(args[1], "true", "false")) {
				Messaging.send(sender, LanguageText.ADMIN_VAR_BOOL);
				return true;
			}
			value = Boolean.valueOf(args[1]);
		} else if(args[0].equalsIgnoreCase("economy-take")) {
			node = Option.ECONOMY_TAKE_SELL;
			if(!Toolbox.equalsOne(args[1], "trash", "sell")) {
				Messaging.send(sender, LanguageText.ADMIN_VAR_ECONTAKE);
				return true;
			}
			value = args[1];
		} else if(args[0].equalsIgnoreCase("economy-clear")) {
			node = Option.ECONOMY_CLEAR_SELL;
			if(!Toolbox.equalsOne(args[1], "trash", "sell")) {
				Messaging.send(sender, LanguageText.ADMIN_VAR_ECONCLEAR);
				return true;
			}
			value = args[1];
		} else if(args[0].equalsIgnoreCase("economy-kits")) {
			node = Option.KIT_METHOD;
			if(!Toolbox.equalsOne(args[1], "individual", "cumulative", "discount")) {
				Messaging.send(sender, LanguageText.ADMIN_VAR_ECONKIT);
				return true;
			}
			value = args[1];
		} else if(args[0].equalsIgnoreCase("economy-sell")) {
			node = Option.ECONOMY_SELL;
			try {
				value = Double.valueOf(args[1]);
			} catch(NumberFormatException e) {
				Messaging.send(sender, LanguageText.ADMIN_VAR_NUM);
				return true;
			}
		} else if(args[0].equalsIgnoreCase("kits-discount")) {
			node = Option.KIT_DISCOUNT;
			try {
				value = Double.valueOf(args[1]);
			} catch(NumberFormatException e) {
				Messaging.send(sender, LanguageText.ADMIN_VAR_NUM);
				return true;
			}
		} else if(args[0].equalsIgnoreCase("chat-tag")) {
			node = Option.TAG_FORMAT;
			value = args[1];
		} else if(args[0].equalsIgnoreCase("log-commands")) {
			node = Option.LOG_COMMANDS;
			if(!Toolbox.equalsOne(args[1], "true", "false")) {
				Messaging.send(sender, LanguageText.ADMIN_VAR_BOOL);
				return true;
			}
			value = Boolean.valueOf(args[1]);
		} else if(args[0].equalsIgnoreCase("auto-save")) {
			node = Option.AUTO_SAVE;
			if(!Toolbox.equalsOne(args[1], "true", "false")) {
				Messaging.send(sender, LanguageText.ADMIN_VAR_BOOL);
				return true;
			}
			value = Boolean.valueOf(args[1]);
		} else if(args[0].equalsIgnoreCase("lightning-range")) {
			node = Option.LIGHTNING_RANGE;
			try {
				value = Integer.valueOf(args[1]);
			} catch(NumberFormatException e) {
				Messaging.send(sender, LanguageText.ADMIN_VAR_INT);
				return true;
			}
		} else if(args[0].equalsIgnoreCase("teleport-warmup")) {
			node = Option.TELEPORT_WARMUP;
			try {
				value = Integer.valueOf(args[1]);
			} catch(NumberFormatException e) {
				Messaging.send(sender, LanguageText.ADMIN_VAR_INT);
				return true;
			}
		} else if(args[0].equalsIgnoreCase("time-cooldown")) {
			node = Option.COOLDOWN("time");
			try {
				value = Integer.valueOf(args[1]);
			} catch(NumberFormatException e) {
				Messaging.send(sender, LanguageText.ADMIN_VAR_INT);
				return true;
			}
		} else if(args[0].equalsIgnoreCase("storm-cooldown")) {
			node = Option.COOLDOWN("storm");
			try {
				value = Integer.valueOf(args[1]);
			} catch(NumberFormatException e) {
				Messaging.send(sender, LanguageText.ADMIN_VAR_INT);
				return true;
			}
		} else if(args[0].equalsIgnoreCase("thunder-cooldown")) {
			node = Option.COOLDOWN("thunder");
			try {
				value = Integer.valueOf(args[1]);
			} catch(NumberFormatException e) {
				Messaging.send(sender, LanguageText.ADMIN_VAR_INT);
				return true;
			}
		} else if(args[0].equalsIgnoreCase("lighting-cooldown")) {
			node = Option.COOLDOWN("lightning");
			try {
				value = Integer.valueOf(args[1]);
			} catch(NumberFormatException e) {
				Messaging.send(sender, LanguageText.ADMIN_VAR_INT);
				return true;
			}
		} else if(args[0].equalsIgnoreCase("show-usage")) {
			node = Option.SHOW_USAGE;
			if(!Toolbox.equalsOne(args[1], "true", "false")) {
				Messaging.send(sender, LanguageText.ADMIN_VAR_BOOL);
				return true;
			}
			value = Boolean.valueOf(args[1]);
		} else Messaging.send(sender, LanguageText.ADMIN_VAR_UNKNOWN.value("var", args[0]));
		if(node != null && value != null) {
			node.set(value);
			Messaging.send(sender, LanguageText.ADMIN_VAR_SET.value("var", args[0], "value", value));
		}
		return true;
	}
	
	private boolean itemEdit(CommandSender sender, String[] args) {
		if(args.length < 2 || args.length > 3) {
			return false;
		} else if(args[0].equalsIgnoreCase("alias")) {
			switch(args.length) {
			case 2:
				if(args[1].charAt(0) == '-') {
					Items.removeAlias(args[1].substring(1));
					Messaging.send(sender, LanguageText.ADMIN_ITEM_ALIAS_REMOVE.value("alias", args[1].substring(1)));
				} else {
					Messaging.send(sender, LanguageText.ADMIN_ITEM_ALIAS_SHOW.value("alias", args[1], "item", Items.getAlias(args[1])));
				}
				return true;
			case 3:
				ItemID id = Items.validate(args[2]);
				Items.addAlias(args[1], id);
				Messaging.send(sender, LanguageText.ADMIN_ITEM_ALIAS_ADD.value("alias", args[1], "item", id.toString()));
				return true;
			}
		} else if(args[0].equalsIgnoreCase("variant")) {
			ItemID id = Items.validate(args[1]);
			if(id == null) {
				Messaging.send(sender, LanguageText.ADMIN_ITEM_BAD);
				return true;
			}
			switch(args.length) {
			case 2:
				Messaging.send(sender, LanguageText.ADMIN_ITEM_VARIANT_SHOW.value("item", id.toString(), "variants", Items.variantNames(id).toString()));
				return true;
			case 3:
				switch(args[2].charAt(0)) {
				default:
					if(args[2].contains(",")) Items.setVariantNames(id, Arrays.asList(args[2].split(",")));
					else Items.addVariantName(id, args[2]);
				break;
				case '+':
					Items.addVariantName(id, args[2].substring(1));
				break;
				case '-':
					Items.removeVariantName(id, args[2].substring(1));
				break;
				case '=':
					Items.setVariantNames(id, Arrays.asList(args[2].substring(1).split(",")));
				break;
				}
				Messaging.send(sender, LanguageText.ADMIN_ITEM_VARIANT_CHANGE.value("item", id.toString(), "variants", Items.variantNames(id).toString()));
				return true;
			}
		} else if(args[0].equalsIgnoreCase("name")) {
			ItemID id = Items.validate(args[1]);
			switch(args.length) {
			case 2:
				String name = Items.name(id);
				Messaging.send(sender, LanguageText.ADMIN_ITEM_NAME_SHOW.value("item", id.toString(), "name", name));
				return true;
			case 3:
				Items.setItemName(id, args[2].replace("_", " "));
				Messaging.send(sender, LanguageText.ADMIN_ITEM_NAME_CHANGE.value("item", id.toString(), "name", args[2].replace("_", " ")));
				return true;
			}
		} else if(args[0].equalsIgnoreCase("hook")) {
			String hook[] = args[1].split("[:/,.|]");
			switch(args.length) {
			case 2:
				Messaging.send(sender, LanguageText.ADMIN_ITEM_HOOK_SHOW.value("hook", hook[0] + "/" + hook[1], "item", Items.getHook(hook[0], hook[1]).toString()));
				return true;
			case 3:
				ItemID id = Items.validate(args[2]);
				Items.setHook(hook[0], hook[1], id);
				Messaging.send(sender, LanguageText.ADMIN_ITEM_HOOK_SHOW.value("hook", hook[0] + "/" + hook[1], "item", id.toString()));
				return true;
			}
		} else if(args[0].equalsIgnoreCase("group")) {
			String groupName = args[1];
			//List<Integer> group = plugin.config.getIntList("give.groups." + groupName, null);
			switch(args.length) {
			case 2:
				List<Integer> group = Items.groupItems(groupName);
				if(group.isEmpty())
					Messaging.send(sender, LanguageText.ADMIN_ITEM_GROUP_EMPTY.value("group", groupName));
				else {
					StringBuilder items = new StringBuilder();
					for(int id : group) {
						items.append(Items.name(new ItemID(id)));
						items.append(", ");
					}
					int lastComma = items.lastIndexOf(", ");
					if(lastComma >= 0) items.delete(lastComma, items.length());
					Messaging.send(sender, LanguageText.ADMIN_ITEM_GROUP_SHOW.value("group", groupName, "items", items.toString()));
				}
				return true;
			case 3:
				if(args[2].equalsIgnoreCase("delete")) {
					if(!Option.nodeExists("give.groups." + groupName))
						Messaging.send(sender, LanguageText.ADMIN_ITEM_GROUP_EMPTY.value("group", groupName));
					else {
						Option.GROUP(groupName).remove();
						Messaging.send(sender, LanguageText.ADMIN_ITEM_GROUP_REMOVE.value("group", groupName));
					}
				} else switch(args[2].charAt(0)) {
				default:
					if(args[2].contains(",")) Items.setGroupItems(groupName, Arrays.asList(args[2].split(",")));
					else Items.addGroupItem(groupName, args[2]);
				break;
				case '+':
					if(!Items.addGroupItem(groupName, args[2].substring(1)))
						Messaging.send(sender, LanguageText.GIVE_BAD_ID);
				break;
				case '-':
					if(!Items.removeGroupItem(groupName, args[2].substring(1)))
						Messaging.send(sender, LanguageText.GIVE_BAD_ID);
				break;
				case '=':
					List<String> bad = Items.setGroupItems(groupName, Arrays.asList(args[2].substring(1).split(",")));
					if(!bad.isEmpty()) Messaging.send(sender, LanguageText.LIST_BAD_ITEMS.value("items", bad.toString()));
				break;
				}
				Messaging.send(sender, LanguageText.ADMIN_ITEM_GROUP_CHANGE.value("group", groupName, "items", Items.groupItems(groupName).toString()));
				return true;
			}
		}
		return false;
	}

	@Override // TODO: This is cheating
	public Map<String, Object> parse(CommandSender sender, Command command, String label, String[] args, boolean isPlayer) {
		if(args.length == 0) return null;
		return Collections.singletonMap("args", (Object) args);
	}
	
}
