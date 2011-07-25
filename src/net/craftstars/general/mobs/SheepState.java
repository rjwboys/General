package net.craftstars.general.mobs;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Random;

import net.craftstars.general.General;
import net.craftstars.general.items.ItemID;
import net.craftstars.general.items.Items;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

import org.bukkit.DyeColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Sheep;
import org.bukkit.util.config.Configuration;

public class SheepState extends MobData {
	private boolean sheared = false;
	private DyeColor clr = DyeColor.WHITE;
	private Random generator = new Random();
	private static EnumSet<DyeColor> natural = EnumSet.of(
		DyeColor.WHITE, DyeColor.BLACK,
		DyeColor.GRAY, DyeColor.SILVER,
		DyeColor.BROWN, DyeColor.PINK
	);
	
	@Override
	public boolean hasPermission(CommandSender byWhom) {
		if(Toolbox.hasPermission(byWhom, "general.mobspawn.variants")) return true;
		if(sheared) return Toolbox.hasPermission(byWhom, "general.mobspawn.sheep.sheared");
		else {
			if(clr == DyeColor.WHITE) return true;
			ArrayList<String> permissions = new ArrayList<String>();
			permissions.add("general.mobspawn.sheep.coloured");
			if(natural.contains(clr))
				permissions.add("general.mobspawn.sheep.coloured.natural");
			String colour = getColourName();
			permissions.add("general.mobspawn.sheep.coloured." + colour);
			int n = permissions.size();
			for(int i = 0; i < n; i++)
				permissions.add(permissions.get(i).replace("colour", "color"));
			return Toolbox.hasPermission(byWhom, permissions.toArray(new String[0]));
		}
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
		if(Toolbox.equalsOne(data, "sheared", "nude", "naked", "bald", "bare", "shorn")) {
			sheared = true;
		} else if(Toolbox.equalsOne(data, "natural")) {
			clr = natural.toArray(new DyeColor[0])[generator.nextInt(natural.size())];
		} else if(Toolbox.equalsOne(data, "artificial", "random")) {
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
		Configuration config = General.plugin.config;
		String node = base + "." + getColourName();
		if(Toolbox.nodeExists(config, node)) return node;
		node = base + ".natural";
		if(natural.contains(clr) && Toolbox.nodeExists(config, node)) return node;
		node = base + ".dyed";
		if(!natural.contains(clr) && Toolbox.nodeExists(config, node)) return node;
		return base + ".default";
	}

	private String getColourName() {
		return clr.toString().toLowerCase().replace('_', '-');
	}
	
	@Override
	public void lacksPermission(CommandSender fromWhom) {
		if(sheared) Messaging.lacksPermission(fromWhom, "spawn sheared sheep");
		else Messaging.lacksPermission(fromWhom, "spawn coloured sheep");
	}

	@Override
	public String[] getValues() {
		int nColours = DyeColor.values().length;
		String[] values = new String[nColours + 1];
		for(int i = 0; i < nColours; i++) {
			DyeColor thisColour = DyeColor.getByData((byte) i);
			if(thisColour == DyeColor.WHITE)
				values[i] = "default";
			else values[i] = thisColour.toString().toLowerCase().replace('_', '-');
		}
		values[nColours] = "sheared";
		return values;
	}
	
}
