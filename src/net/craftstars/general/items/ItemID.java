/**
 * 
 */
package net.craftstars.general.items;

public class ItemID implements Comparable<ItemID> {
    public int ID;
    public int data;
    public boolean dataMatters;

    public ItemID(Integer id, Integer d) {
        if(id == null) this.ID = 0;
        else this.ID = id;
        if(d == null) {
            this.data = 0;
            this.dataMatters = false;
        } else {
            this.data = d;
            this.dataMatters = true;
        }
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

    public String toString() {
        return Integer.toString(ID) + ":" + Integer.toString(data);
    }
}