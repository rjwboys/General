package net.craftstars.general.items;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class GotKit {
	private String who;
	private String kit;
	private int id;
	
	public GotKit(CommandSender sender, Kit which) {
		init(sender);
		this.kit = which.getName();
		this.id = this.who.hashCode();
	}

	private void init(CommandSender sender) {
		if(sender instanceof Player) init((Player)sender);
		else if(sender instanceof ConsoleCommandSender) init((ConsoleCommandSender)sender);
		else {
			this.who = sender.getClass().getCanonicalName();
			this.id = 0;
		}
	}

	private void init(@SuppressWarnings("unused") ConsoleCommandSender sender){
		this.who = "[CONSOLE]";
	}
	
	private void init(Player sender) {
		this.who = sender.getName();
	}
	
	@Override
	public int hashCode() {
		int items = kit.hashCode();
		// return (id << 16) | (item & 0xFFFF);
		return id ^ items;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof GotKit) {
			GotKit other = (GotKit) obj;
			if(this.kit.equals(other.kit) && this.who.equals(other.who)) return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "(" + who + "[" + id + "], " + kit + ")";
	}
}