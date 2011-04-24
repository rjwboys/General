package net.craftstars.general.mobs;

import java.util.HashMap;

public enum SlimeSize {
    TINY(1), SMALL(2), MEDIUM(3), LARGE(4), HUGE(8), COLOSSAL(16);
    private int n;
    private static HashMap<String, SlimeSize> mapping = new HashMap<String, SlimeSize>();
    
    private SlimeSize(int sz) {
        n = sz;
    }
    
    public int getSize() {
        return n;
    }
    
    static {
        for(SlimeSize x : values())
            mapping.put(x.toString().toLowerCase(), x);
    }
    
    public static SlimeSize fromName(String name) {
        return mapping.get(name.toLowerCase());
    }
}
