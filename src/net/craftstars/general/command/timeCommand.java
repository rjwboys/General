package net.craftstars.general.command;

import net.craftstars.general.General;
import net.craftstars.general.util.Messaging;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class timeCommand extends GeneralCommand
{
	private World world;
	
	@Override
	public boolean fromPlayer(General plugin, Player sender, Command command, String commandLabel, String[] args)
	{
		if (args.length < 1)
		{
			// No arguments, assuming get current time for current world.
			int time = (int)sender.getWorld().getTime();
			Messaging.send(sender, "Time: "+this.getFriendlyTime(time)+" ("+time+")");
		}
		else
		{
			String arg = args[0];
			this.world = sender.getWorld();
			
			if (arg.equalsIgnoreCase("day"))
			{
				this.world.setTime(this.getStartTime(world));
				Messaging.send(sender, "Time set to day!");
			}
			else if (arg.equalsIgnoreCase("night"))
			{
				this.world.setTime(this.getStartTime(world)+13800);
				Messaging.send(sender, "Time set to night!");
			}
			else if (arg.equalsIgnoreCase("dusk"))
			{
				this.world.setTime(this.getStartTime(world)+12000);
				Messaging.send(sender, "Time set to dusk!");
			}
			else if (arg.equalsIgnoreCase("dawn"))
			{
				this.world.setTime(this.getStartTime(world)+22200);
				Messaging.send(sender, "Time set to dawn!");
			}
			else if (arg.startsWith("="))
			{
				try
				{
					this.world.setTime(Long.parseLong(arg.substring(1)));
				}
				catch (Exception ex)
				{
					return false;
				}
			}
			else if (arg.startsWith("+"))
			{
				try
				{
					long time = this.world.getTime();
					this.world.setTime(time+Long.parseLong(arg.substring(1)));
				}
				catch (Exception ex)
				{
					return false;
				}
			}
			else if (arg.startsWith("-"))
			{
				try
				{
					long time = this.world.getTime();
					this.world.setTime(time-Long.parseLong(arg.substring(1)));
				}
				catch (Exception ex)
				{
					return false;
				}
			}
		}
		
		return true;
	}
	
	private long getTime(World world)
	{
		return world.getTime();
	}
	
	private long getRelativeTime(World world)
	{
        return (this.getTime(world) % 24000);
    }
	
	private long getStartTime(World world)
	{
        return (this.getTime(world)-this.getRelativeTime(world));
    }
	
	public String getFriendlyTime(int time)
	{
		if (time >= 12000 && time < 13800)
		{
			return "Dusk";
		}
		else if (time >= 13800 && time < 22200)
		{
			return "Night";
		}
		else if (time >= 22200 && time < 24000)
		{
			return "Dawn";
		}
		else
		{
			return "Day";
		}
	}
}