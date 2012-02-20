package net.craftstars.general.mobs;

import java.util.EnumSet;
import java.util.Random;

import net.craftstars.general.items.InvalidItemException;
import net.craftstars.general.items.ItemID;
import net.craftstars.general.items.Items;
import net.craftstars.general.text.LanguageText;
import net.craftstars.general.util.Option;
import net.craftstars.general.util.Toolbox;

import org.bukkit.DyeColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Sheep;

public class SheepState extends AnimalData {
	private boolean sheared = false;
	private DyeColor clr = DyeColor.WHITE;
	private Random generator = new Random();
	private static EnumSet<DyeColor> natural = EnumSet.of(
		DyeColor.WHITE, DyeColor.BLACK,
		DyeColor.GRAY, DyeColor.SILVER,
		DyeColor.BROWN, DyeColor.PINK
	);
	
	public SheepState() {
		super(MobType.SHEEP);
	}
	
	@Override
	public String getPermission(String base) {
		return super.getPermission(base) + (sheared? ".sheared." : ".") + getColourName();
	}
	
	@Override
	public void setForMob(LivingEntity mob) {
		super.setForMob(mob);
		if(!(mob instanceof Sheep)) return;
		Sheep sheep = (Sheep) mob;
		if(sheared) sheep.setSheared(true);
		else sheep.setColor(clr);
	}
	
	@Override
	public void parse(CommandSender setter, String data) {
		for(String component : data.split("[.,:/\\|]", 2)) {
			if(Toolbox.equalsOne(component, MobType.SHEEP.getDataList("bald"))) {
				sheared = true;
			} else if(Toolbox.equalsOne(component, MobType.SHEEP.getDataList("natural"))) {
				clr = natural.toArray(new DyeColor[0])[generator.nextInt(natural.size())];
			} else if(Toolbox.equalsOne(component, MobType.SHEEP.getDataList("random"))) {
				clr = DyeColor.getByData((byte) generator.nextInt(16));
			} else {
				ItemID wool;
				try {
					wool = Items.validate("35/" + component);
					clr = DyeColor.getByData((byte) (int) wool.getData());
					sheared = false;
				} catch(InvalidItemException e) {
					try {
						super.parse(setter, component);
					} catch(InvalidMobException x) {
						invalidate(e, component);
					}
				}
			}
		}
	}
	
	@Override
	public String getCostNode(String base) {
		base = super.getCostNode(base);
		if(sheared) return base + ".sheared";
		String node = base + "." + getColourName();
		if(Option.nodeExists(node)) return node;
		node = base + ".natural";
		if(natural.contains(clr) && Option.nodeExists(node)) return node;
		node = base + ".dyed";
		if(!natural.contains(clr) && Option.nodeExists(node)) return node;
		return base + ".default";
	}

	private String getColourName() {
		return clr.toString().toLowerCase().replace('_', '-');
	}
	
	@Override
	protected LanguageText getLangKey() {
		return LanguageText.LACK_MOBSPAWN_SHEEP_COLOURED;
	}
	
	@Override
	protected Object[] getLangParams() {
		return new Object[] {"colour", getColourName()};
	}

	@Override
	public String[] getValues() {
		int nColours = DyeColor.values().length;
		String[] values = new String[nColours + 1];
		for(int i = 0; i < nColours; i++) {
			DyeColor thisColour = DyeColor.getByData((byte) i);
			values[i] = thisColour.toString().toLowerCase().replace('_', '-');
		}
		values[nColours] = "sheared";
		return Toolbox.cartesianProduct(super.getValues(), values, '.');
	}
	
}
