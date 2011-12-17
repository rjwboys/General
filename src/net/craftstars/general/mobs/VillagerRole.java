package net.craftstars.general.mobs;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;

import net.craftstars.general.text.LanguageText;
import net.craftstars.general.text.Messaging;
import net.craftstars.general.util.Toolbox;

public class VillagerRole extends MobData {
	enum Role {
		BUTCHER, FARMER, LIBRARIAN, PRIEST, BLACKSMITH, PEASANT;
		public boolean matches(String test) {
			String me = toString().toLowerCase();
			if(Toolbox.equalsOne(test, MobType.VILLAGER.getDataList(me)))
				return true;
			return me.equals(test);
		}
	}
	Role role;
	
	VillagerRole() {
		super(MobType.VILLAGER);
	}

	@Override
	public boolean hasPermission(CommandSender sender) {
		return sender.hasPermission(getNode());
	}

	private String getNode() {
		return "general.mobspawn.villager." + role.toString().toLowerCase();
	}
	
	@Override
	public void setForMob(LivingEntity entity) {
		//TODO
	}
	
	@Override
	public void parse(CommandSender setter, String data) {
		role = null;
		for(Role test : Role.values()) {
			if(test.matches(data)) {
				role = test;
				break;
			}
		}
		if(role == null) invalidate();
	}
	
	@Override
	public String getCostNode(String baseNode) {
		return baseNode + '.' + role.toString().toLowerCase();
	}
	
	@Override
	public String[] getValues() {
		int nRoles = Role.values().length;
		String[] values = new String[nRoles];
		for(int i = 0; i < nRoles; i++)
			values[i] = Role.values()[i].toString().toLowerCase();
		return values;
	}
	
	@Override
	public void lacksPermission(CommandSender fromWhom) {
		Messaging.lacksPermission(fromWhom, getNode(),
			LanguageText.LACK_MOBSPAWN_VILLAGER, "role", role.toString().toLowerCase());
	}
}
