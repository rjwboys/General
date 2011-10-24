
package net.craftstars.general.teleport;

import net.craftstars.general.text.LanguageText;

public enum TargetType {
	SELF, OTHER, MOBS, WORLD; // world is only for setspawn, mobs is only for teleport
	
	public String getPermission(String base) {
		return base + "." + this.toString().toLowerCase();
	}
	
	public String getName() {
		String node = "target." + toString().toLowerCase();
		return LanguageText.byNode(node).value();
	}
}
