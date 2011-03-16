/**
 * 
 */
package net.craftstars.general.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemID implements Comparable<ItemID>, Cloneable {
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
        this(item.ID,item.dataMatters?item.data:null);
    }

    public ItemID(Material m) {
        this(m.getId());
    }

    public int compareTo(ItemID arg) {
        ItemID other = (ItemID) arg;
        if(!dataMatters) return new Integer(ID).compareTo(other.ID);
        else {
            if(ID < other.ID) return -1;
            else if(ID > other.ID) return 1;
            else return new Integer(data).compareTo(other.data);
        }
    }

    @Override
    public int hashCode() {
        // System.out.println(this.toString()+" is hashed; result: "+Integer.toString((ID << 8)
        // + (data & 0xFF)));
        return (ID << 8) + (data & 0xFF);
    }
    
    @Override
    public boolean equals(Object other){
        if(other instanceof ItemID) return 0 == this.compareTo((ItemID) other);
        else return false;
    }

    @Override
    public String toString() {
        if(dataMatters && isValid)
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
}