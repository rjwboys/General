
package net.craftstars.general.teleport;

import net.craftstars.general.text.LanguageText;
import net.craftstars.general.text.Messaging;
import net.craftstars.general.util.Toolbox;

import org.bukkit.command.CommandSender;

public enum DestinationType {
	WORLD, PLAYER, COORDS, HOME(true), SPAWN(true), TARGET(true), COMPASS(true), OTHER;
	private boolean spec;
	
	private DestinationType() {
		this.spec = false;
	}
	
	private DestinationType(boolean special) {
		this.spec = special;
	}
	
	public boolean hasPermission(CommandSender who, String base, Target what) {
		if(Toolbox.hasPermission(who, getPermission(base))) return true;
		Messaging.lacksPermission(who, getPermission(base), LanguageText.LACK_TELEPORT,
			"destination", getName(true), "world", what.getWorld().getName(), "target", what.getName());
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
