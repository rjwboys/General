package net.craftstars.general.mobs;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.LivingEntity;
import org.bukkit.material.MaterialData;

import net.craftstars.general.items.ItemID;
import net.craftstars.general.items.Items;
import net.craftstars.general.text.Messaging;
import net.craftstars.general.util.Toolbox;

public class EnderBlock extends MobData {
	private static List<String> values = new ArrayList<String>();
	static {
		for(Material material : Material.values()) {
			if(material.getId() > 0 && material.isBlock())
				values.add(material.toString().toLowerCase().replace('_', '-'));
		}
	}
	private Material block;
	private int data;
	
	@Override
	public boolean hasPermission(CommandSender sender) {
		if(block == null) return true;
		return Toolbox.hasPermission(sender, "general.mobspawn.variants", "general.mobspawn.enderman." + block.toString().toLowerCase().replace('_', '-'));
	}
	
	@Override
	public void setForMob(LivingEntity mob) {
		if(block == null || !(mob instanceof Enderman)) return;
		MaterialData mdata = block.getNewData((byte)data);
		if(mdata == null) mdata = new MaterialData(block, (byte)data);
		((Enderman)mob).setCarriedMaterial(mdata);
	}
	
	@Override
	public void parse(CommandSender setter, String carry) {
		ItemID item = Items.validate(carry);
		if(item == null) invalidate();
		else if(!item.isValid()) invalidate();
		else if(!item.getMaterial().isBlock()) invalidate();
		else {
			block = item.getMaterial();
			if(item.getData() != null)
				data = item.getData();
			else data = 0;
		}
	}
	
	@Override
	public String getCostNode(String baseNode) {
		if(block != null) return baseNode + "." + block.toString().toLowerCase().replace('_', '-');
		else return baseNode + ".empty";
	}
	
	@Override
	public void lacksPermission(CommandSender sender) {
		if(block != null) Messaging.lacksPermission(sender, "general.mobspawn.enderman." + block.toString().toLowerCase().replace('_', '-'));
		// TODO: Need a whole lot of LanguageText nodes for this...
	}
	
	@Override
	public String[] getValues() {
		return values.toArray(new String[values.size()]);
	}
	
}
