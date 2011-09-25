package net.craftstars.general.mobs;

import java.util.EnumSet;
import java.util.Random;

import net.craftstars.general.items.ItemID;
import net.craftstars.general.items.Items;
import net.craftstars.general.text.LanguageText;
import net.craftstars.general.text.Messaging;
import net.craftstars.general.util.Option;
import net.craftstars.general.util.Toolbox;

import org.bukkit.DyeColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Sheep;

public class SheepState extends MobData {
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
	public boolean hasPermission(CommandSender byWhom) {
		if(sheared) return Toolbox.hasPermission(byWhom, "general.mobspawn.sheep.sheared");
		else return Toolbox.hasPermission(byWhom, "general.mobspawn.sheep." + getColourName());
	}
	
	@Override
	public void setForMob(LivingEntity mob) {
		if(!(mob instanceof Sheep)) return;
		Sheep sheep = (Sheep) mob;
		if(sheared) sheep.setSheared(true);
		else sheep.setColor(clr);
	}
	
	@Override
	public void parse(CommandSender setter, String data) {
		if(Toolbox.equalsOne(data, MobType.SHEEP.getDataList("bald"))) {
			sheared = true;
		} else if(Toolbox.equalsOne(data, MobType.SHEEP.getDataList("natural"))) {
			clr = natural.toArray(new DyeColor[0])[generator.nextInt(natural.size())];
		} else if(Toolbox.equalsOne(data, MobType.SHEEP.getDataList("random"))) {
			clr = DyeColor.getByData((byte) generator.nextInt(16));
		} else {
			sheared = false;
			ItemID wool = Items.validate("35:" + data);
			if(wool == null || !wool.isValid()) invalidate();
			else clr = DyeColor.getByData((byte) (int) wool.getData());
		}
	}
	
	@Override
	public String getCostNode(String base) {
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
	public void lacksPermission(CommandSender fromWhom) {
		if(sheared) Messaging.lacksPermission(fromWhom, "general.mobspawn.sheep.sheared");
		else {
			String colour = getColourName();
			String node = "general.mobspawn.sheep." + colour;
			Messaging.lacksPermission(fromWhom, node, LanguageText.LACK_MOBSPAWN_SHEEP_COLOURED, "colour", colour);
		}
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
		return values;
	}
	
}
