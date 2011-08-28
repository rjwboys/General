
package net.craftstars.general.teleport;

import net.craftstars.general.util.LanguageText;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

import org.bukkit.command.CommandSender;

public enum TargetType {
	SELF {
		@Override
		public boolean hasPermission(CommandSender who) {
			if(Toolbox.hasPermission(who, "general.teleport.basic")) return true;
			return super.hasPermission(who);
		}
	}, OTHER, MOBS;
	
	public boolean hasPermission(CommandSender sender) {
		if(Toolbox.hasPermission(sender, getPermission("general.teleport"))) return true;
		Messaging.lacksPermission(sender, getPermission("general.teleport"), LanguageText.LACK_TELEPORT_TARGET,
				"target", getName());
		return false;
	}
	
	public String getPermission(String base) {
		return base + "." + this.toString().toLowerCase();
	}
	
	public String getName() {
		String node = "target." + toString().toLowerCase();
		return LanguageText.byNode(node).value();
	}

	public boolean hasInstant(CommandSender sender) {
		return Toolbox.hasPermission(sender, getPermission("general.teleport") + ".instant");
	}
}
