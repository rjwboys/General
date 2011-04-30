package net.craftstars.general.teleport;

import net.craftstars.general.General;
import net.craftstars.general.util.Messaging;

import org.bukkit.entity.Player;

public enum DestinationType {
    WORLD("other worlds"),
    PLAYER("other players"),
    COORDS("specific coordinates"),
    HOME("player homes",true),
    SPAWN("spawn",true),
    TARGET("targeted block",true),
    COMPASS("compass target",true);
    private String msg;
    private boolean spec;

    private DestinationType(String message) {
        this.msg = message;
        this.spec = false;
    }

    private DestinationType(String message, boolean special) {
        this.msg = message;
        this.spec = special;
    }
    
    public boolean hasPermission(Player who, String action, String base) {
        if(who.isOp()) return true;
        if(General.plugin.permissions.hasPermission(who, getPermission(base))) return true;
        Messaging.send(who, "&cYou do not have permission to " + action + " to " + msg + ".");
        return false;
    }

    public boolean isSpecial() {
        return spec;
    }
    
    public String getPermission(String base) {
        return base + ".to." + this.toString().toLowerCase();
    }
}
