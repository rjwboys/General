package net.craftstars.general.items;

import java.util.Collections;
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
				register(item, ToolDamage.class);
			}
		}
		register(Material.WOOL, ColourData.class);
		register(Material.INK_SACK, ColourData.class);
		register(Material.LOG, TreeData.class);
		register(Material.LEAVES, TreeData.class);
		register(Material.SAPLING, TreeData.class);
		register(Material.COAL, CoalData.class);
		register(Material.STEP, StepData.class);
		register(Material.MAP, MapData.class);
		register(Material.SMOOTH_BRICK, StoneBrickData.class);
		register(Material.MONSTER_EGG, MobSpawnerData.class);
		register(Material.BOOK, BookWormData.class); // BookWorm support
		register(Material.LONG_GRASS, LongGrassData.class);
		register(Material.HUGE_MUSHROOM_1, BigShroomData.class);
		register(Material.HUGE_MUSHROOM_2, BigShroomData.class);
		register(Material.POTION, PotionData.class);
	}
	protected Material material;
	private String name;
	private boolean defaultName = true;
	
	protected ItemData() {}
	
	private ItemData setItem(Material mat) {
		material = mat;
		name = Items.name(mat);
		return this;
	}
	
	protected static void register(Material item, Class<? extends ItemData> cls) {
		map.put(item, cls);
	}

	public void setDisplayName(String newName) {
		name = newName;
		defaultName = false;
	}
	
	public boolean isNameCustom() {
		return !defaultName;
	}
	
	public static ItemData enchanting(Material item) {
		ItemData enchant = new EnchantData();
		return enchant.setItem(item);
	}
	
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
	
	protected boolean listContains(String key, String val, List<String> dflt) {
		List<String> list = Items.variantNames(key);
		if(list == null || list.isEmpty()) list = dflt;
		list = allToLower(list);
		return list.contains(val);
	}
	
	@SuppressWarnings("unchecked")
	protected int listContainsId(String key, String val, Range<Integer> range) {
		for(int i : range) {
			if(listContains(key + ".type" + i, val, Collections.EMPTY_LIST))
				return i;
		}
		return -1;
	}
	
	protected String listContainsPrefix(String key, String val, List<String> dflt) {
		List<String> list = Items.variantNames(key);
		if(list == null || list.isEmpty()) list = dflt;
		list = allToLower(list);
		for(String prefix : list) {
			if(val.startsWith(prefix)) return prefix;
		}
		return "";
	}
	
	protected String listContainsSuffix(String key, String val, List<String> dflt) {
		List<String> list = Items.variantNames(key);
		if(list == null || list.isEmpty()) list = dflt;
		list = allToLower(list);
		for(String suffix : list) {
			if(val.endsWith(suffix)) return suffix;
		}
		return "";
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
