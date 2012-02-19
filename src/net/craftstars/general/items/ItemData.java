package net.craftstars.general.items;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.craftstars.general.util.range.Range;

public class ItemData implements Cloneable {
	private final static Map<Material,Class<? extends ItemData>> map;
	static {
		map = new HashMap<Material,Class<? extends ItemData>>();
		for(Material item : Material.values()) {
			if(item.getMaxDurability() > 0) {
				map.put(item, ToolDamage.class);
			}
		}
		map.put(Material.WOOL, ColourData.class);
		map.put(Material.INK_SACK, ColourData.class);
		map.put(Material.LOG, TreeData.class);
		map.put(Material.LEAVES, TreeData.class);
		map.put(Material.SAPLING, TreeData.class);
		map.put(Material.COAL, CoalData.class);
		map.put(Material.STEP, StepData.class);
		map.put(Material.MAP, MapData.class);
		map.put(Material.SMOOTH_BRICK, StoneBrickData.class);
		map.put(Material.MONSTER_EGG, MobSpawnerData.class);
		map.put(Material.BOOK, BookWormData.class); // BookWorm support
		map.put(Material.LONG_GRASS, LongGrassData.class);
		map.put(Material.HUGE_MUSHROOM_1, BigShroomData.class);
		map.put(Material.HUGE_MUSHROOM_2, BigShroomData.class);
		map.put(Material.POTION, PotionData.class);
	}
	protected Material material;
	private String name;
	private boolean defaultName = true;
	
	private ItemData setItem(Material mat) {
		material = mat;
		name = Items.name(mat);
		return this;
	}
	
	public void setDisplayName(String newName) {
		name = newName;
		defaultName = false;
	}
	
	public boolean isNameCustom() {
		return !defaultName;
	}
	
//	public abstract boolean validate(ItemID id, Material check);
	
	public static ItemData getData(Material item) {
		Class<? extends ItemData> data = map.get(item);
		if(data != null) {
			try {
				return data.newInstance().setItem(item);
			} catch(InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch(IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return new ItemData().setItem(item);
	}
	
	protected List<String> allToLower(List<String> list) {
		for(int j = 0; j < list.size(); j++) list.set(j, list.get(j).toLowerCase());
		return list;
	}
	
	public boolean validate(int data) {
		int max = Items.getMaxData(material);
		if(max > 0) return data <= max && data >= 0;
		Range<Integer> range = Items.getDataRange(material);
		if(range != null) return range.contains(data);
		List<Range<Integer>> ranges = Items.getDataRanges(material);
		if(ranges != null) {
			for(Range<Integer> ran : ranges) {
				if(ran.contains(data)) return true;
			}
			return false;
		}
		return data == 0;
	}
	
	public String getName(int data) {
		return Integer.toString(data);
	}
	
	public String getDisplayName() {
		return name;
	}
	
	public int fromName(String data) {
		try {
			return Integer.parseInt(data);
		} catch(NumberFormatException e) {
			return 0;
		}
	}
	
	public int init(int data, @SuppressWarnings("unused") Player forWhom) {
		return data;
	}
	
	@Override
	public ItemData clone() {
		try {
			return (ItemData)super.clone();
		} catch(CloneNotSupportedException e) {
			return null;
		}
	}
}
