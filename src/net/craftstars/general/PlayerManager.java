package net.craftstars.general;

import java.util.HashMap;

import org.bukkit.entity.Player;
import static org.bukkit.event.EventPriority.MONITOR;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import net.craftstars.general.text.LanguageText;
import net.craftstars.general.text.MessageOfTheDay;
import net.craftstars.general.text.Messaging;
import net.craftstars.general.util.Option;

public final class PlayerManager implements Listener {
	private HashMap<String, String> playersAway = new HashMap<String, String>();
	private HashMap<String,String> lastMessager = new HashMap<String, String>();

	public boolean isAway(Player who) {
		return playersAway.containsKey(who.getName());
	}
	
	public void goAway(Player who, String reason) {
		playersAway.put(who.getName(), reason);
		if(Option.AWAY_SLEEP.get())
			who.setSleepingIgnored(true);
	}
	
	public void unAway(Player who) {
		playersAway.remove(who.getName());
		if(Option.AWAY_SLEEP.get())
			who.setSleepingIgnored(false);
	}
	
	public String whyAway(Player who) {
		if(isAway(who)) return playersAway.get(who.getName());
		return "";
	}
	
	public void hasMessaged(String from, String to) {
		lastMessager.put(from, to);
	}
	
	public String lastMessaged(String to) {
		return lastMessager.get(to);
	}
	
	@EventHandler(priority=MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		if(!Option.SHOW_MOTD.get()) return;
		MessageOfTheDay.showMotD(event.getPlayer());
	}
	
	@EventHandler(priority=MONITOR)
	public void onPlayerChat(PlayerChatEvent event) {
		String tag = event.getMessage().split("\\s+")[0];
		for(String who : playersAway.keySet()) {
			if(tag.equalsIgnoreCase(Option.TAG_FORMAT.get().replace("name", who))) {
				Messaging.send(event.getPlayer(), LanguageText.AWAY_BRIEF.value("name", who,
					"reason", playersAway.get(who)));
				break;
			}
		}
	}
	
	@EventHandler(priority=MONITOR)
	public void onPlayerLogin(PlayerLoginEvent event) {
		lastMessager.remove(event.getPlayer().getName());
	}
}