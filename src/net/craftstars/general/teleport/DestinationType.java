
package net.craftstars.general.teleport;

import java.util.Arrays;
import java.util.List;

import net.craftstars.general.General;
import net.craftstars.general.util.LanguageText;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

import org.bukkit.entity.Player;

public enum DestinationType {
	WORLD, PLAYER, COORDS, HOME(true), SPAWN(true), TARGET(true), COMPASS(true);
	private boolean spec;
	
	private DestinationType() {
		this.spec = false;
	}
	
	private DestinationType(boolean special) {
		this.spec = special;
	}
	
	public boolean hasPermission(Player who, String base) {
		if(isBasic() && Toolbox.hasPermission(who, base + ".basic")) return true;
		if(Toolbox.hasPermission(who, getPermission(base))) return true;
		Messaging.lacksPermission(who, getPermission(base), Destination.getFormat(base, "to"),
			"destination", getName(true));
		return false;
	}
	
	private boolean isBasic() {
		List<String> basics = General.plugin.config.getStringList("teleport.basics",
			Arrays.asList("world", "player", "home", "spawn"));
		return basics.contains(toString().toLowerCase().trim());
	}

	public boolean hasOtherPermission(Player who, String base) {
		if(Toolbox.hasPermission(who, getPermission(base + ".other"))) return true;
		Messaging.lacksPermission(who, getPermission(base + ".other"), Destination.getFormat(base, "to"),
			"destination", getName(true));
		return false;
	}
	
	public boolean isSpecial() {
		return spec;
	}
	
	public String getPermission(String base) {
		return base + ".to." + this.toString().toLowerCase();
	}
	
	public String getName(boolean special) {
		String node = toString().toLowerCase();
		if(spec && special)
			node += "_other";
		node = "destination." + node;
		return LanguageText.byNode(node).value();
	}
}
