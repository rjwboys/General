package net.craftstars.general.command.admin;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.General;
import net.craftstars.general.command.CommandBase;
import net.craftstars.general.text.LanguageText;
import net.craftstars.general.text.Messaging;
import net.craftstars.general.util.Time;
import net.craftstars.general.util.Toolbox;

public class shutdownCommand extends CommandBase {
	public shutdownCommand(General instance) {
		super(instance);
	}
	
	@Override
	public Map<String,Object> parse(CommandSender sender, Command command, String label, String[] args, boolean isPlayer) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("delay", 0L);
		String message = null;
		if(args.length > 0) {
			try {
				long ticks = Time.extractDuration(args[0]);
				map.put("delay", ticks);
				if(args.length > 1) message = Toolbox.join(args, 1);
			} catch(NumberFormatException e) {
				message = Toolbox.join(args, 0);
			}
		}
		map.put("msg", message == null ? LanguageText.MISC_STOPPING.value()
			: LanguageText.MISC_STOPPING_BECAUSE.value("reason", message));
		return map;
	}
	
	@Override
	public boolean execute(CommandSender sender, String command, Map<String,Object> args) {
		long delay = (Long)args.get("delay");
		final boolean now = delay < 100 || Bukkit.getOnlinePlayers().length == 0;
		final String message = (String)args.get("msg");
		if(delay > 0) Messaging.broadcast(LanguageText.MISC_STOPPING_SOON.value("when", Time.formatDuration(delay),
			"player", sender.getName()));
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override public void run() {
				General.players.reject();
				for(Player player : Bukkit.getOnlinePlayers())
					player.kickPlayer(message);
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
					@Override public void run() {
						Bukkit.shutdown();
					}
				}, now ? 0 : 300);
			}
		}, delay);
		return true;
	}
}
