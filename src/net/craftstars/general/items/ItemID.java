/**
 * 
 */
package net.craftstars.general.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemID implements Cloneable, Comparable<ItemID> {
    private int ID;
    private int data;
    private boolean dataMatters, isValid;
    private String itemName = null, dataName = null;
    
    public ItemID() {
        this(0);
    }
    
    public ItemID(int id) {
        this(id,null);
    }
    
    public ItemID(int id, Integer d) {
        this.ID = id;
        if(d == null) {
            this.data = 0;
            this.dataMatters = false;
        } else {
            this.data = d;
            this.dataMatters = true;
        }
        this.isValid = true;
    }

    public ItemID(ItemID item) {
        this.ID = item.ID;
        this.data = item.data;
        this.dataMatters = item.dataMatters;
        this.isValid = item.isValid;
        this.itemName = item.itemName;
        this.dataName = item.dataName;
    }

    public ItemID(Material m) {
        this(m.getId());
    }

    @Override
    public String toString() {
        if(dataMatters)
            return Integer.toString(ID) + ":" + Integer.toString(data);
        return Integer.toString(ID);
    }
    
    public int getId() {
        return ID;
    }
    
    public Integer getData() {
        if(dataMatters) return data;
        return null;
    }
    
    public ItemID setData(Integer d) {
        if(d == null) {
            data = 0;
            dataMatters = false;
        } else {
            data = d;
            dataMatters = true;
        }
        return this;
    }
    
    public boolean isValid() {
        return isIdValid() && isDataValid();
    }
    
    public boolean isIdValid() {
        if(!isValid) return dataMatters;
        return true;
    }
    
    public boolean isDataValid() {
        if(!isValid) return !dataMatters;
        return true;
    }
    
    public ItemID invalidate(boolean dataOnly) {
        isValid = false;
        dataMatters = dataOnly;
        return this;
    }
    
    public String getName() {
        if(itemName == null) return Integer.toString(ID);
        return itemName;
    }
    
    public ItemID setName(String newName) {
        itemName = newName;
        return this;
    }
    
    public String getVariant() {
        if(dataName == null) return Integer.toString(data);
        return dataName;
    }
    
    public ItemID setVariant(String newVar) {
        dataName = newVar;
        return this;
    }
    
    @Override
    public ItemID clone() {
        return new ItemID(this);
    }
    
    public ItemStack getStack(int amount) {
        if(!isValid) return null;
        if(dataMatters) return new ItemStack(ID, amount, (short) data);
        return new ItemStack(ID, amount);
    }
    
    public Material getMaterial() {
        return Material.getMaterial(ID);
    }

    @Override
    public int compareTo(ItemID other) {
        if(ID < other.ID) return -1;
        else if(ID > other.ID) return 1;
        else {
            if(dataMatters == other.dataMatters)
                return dataMatters ? new Integer(data).compareTo(other.data) : 0;
            else if(!other.dataMatters)
                return -1;
            else return 1;
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ItemID) {
            ItemID other = (ItemID) obj;
            return (ID == other.ID && data == other.data && dataMatters == other.dataMatters);
        }
        return false;
    }
    
    @Override // Considering currently valid values, this should return a unique hash for each possible ItemID.
    public int hashCode() {
        int hash = data;
        hash |= ID << 12; // 1562, the damage data of a diamond tool, is 11 bits long
        if(!dataMatters) hash = ~hash;
        return hash;
    }
}