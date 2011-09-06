
package net.craftstars.general.teleport;

import java.util.List;

import net.craftstars.general.text.LanguageText;
import net.craftstars.general.text.Messaging;
import net.craftstars.general.util.Option;
import net.craftstars.general.util.Toolbox;

import org.bukkit.command.CommandSender;
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
	
	public boolean hasPermission(CommandSender who, String base) {
		if(isBasic() && Toolbox.hasPermission(who, base + ".basic")) return true;
		if(Toolbox.hasPermission(who, getPermission(base))) return true;
		Messaging.lacksPermission(who, getPermission(base), LanguageText.LACK_TELEPORT_TO,
			"destination", getName(true));
		return false;
	}
	
	private boolean isBasic() {
		List<String> basics = Option.TELEPORT_BASICS.get();
		return basics.contains(toString().toLowerCase().trim());
	}

	public boolean hasOtherPermission(Player who, String base) {
		if(Toolbox.hasPermission(who, getPermission(base + ".other"))) return true;
		Messaging.lacksPermission(who, getPermission(base + ".other"), LanguageText.LACK_TELEPORT_TO,
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

	public boolean hasInstant(CommandSender sender, String base) {
		return Toolbox.hasPermission(sender, getPermission(base) + ".instant");
	}
}
