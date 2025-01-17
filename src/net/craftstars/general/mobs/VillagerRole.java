package net.craftstars.general.mobs;

import java.lang.reflect.Field;

import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftVillager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;

import net.craftstars.general.General;
import net.craftstars.general.text.LanguageText;
import net.craftstars.general.util.Toolbox;
import net.minecraft.server.EntityVillager;

public class VillagerRole extends MobData {
	enum Role {
		BUTCHER, FARMER, LIBRARIAN, PRIEST, BLACKSMITH, PEASANT;
		public boolean matches(String test) {
			String me = toString().toLowerCase();
			if(Toolbox.equalsOne(test, MobType.VILLAGER.getDataList(me)))
				return true;
			return me.equals(test);
		}

		public String getName() {
			return toString().toLowerCase();
		}
	}
	Role role;
	
	VillagerRole() {
		super(MobType.VILLAGER);
		role = Role.FARMER;
	}

	@Override
	public String getPermission(String base) {
		return base + "." + role.getName();
	}
	
	@Override
	public void setForMob(LivingEntity entity) {
		if(!(entity instanceof Villager)) return;
		// Begin accessing Minecraft code
		// TODO: Remove access of Minecraft code
		CraftVillager testificate = (CraftVillager) entity;
		EntityVillager who = testificate.getHandle();
		try {
			Field profession = EntityVillager.class.getDeclaredField("profession");
			profession.setAccessible(true);
			profession.set(who, role.ordinal());
			General.logger.info("Set profession to " + profession.get(who));
		} catch(SecurityException e) {
			e.printStackTrace();
		} catch(NoSuchFieldException e) {
			e.printStackTrace();
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		}
		// End accessing Minecraft code
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
		if(role == null) invalidate(data);
	}
	
	@Override
	public String getCostNode(String baseNode) {
		return baseNode + '.' + role.getName();
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
	protected LanguageText getLangKey() {
		return LanguageText.LACK_MOBSPAWN_VILLAGER;
	}
	
	@Override
	protected Object[] getLangParams() {
		return new Object[] {"role", role.getName()};
	}
}
