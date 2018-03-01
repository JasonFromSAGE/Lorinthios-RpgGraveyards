package me.TheNukeDude;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GraveyardCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        //Do graveyard command stuff here
    	if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length == 0)
            {
            	OutputHandler.PrintCommandInfo(p, "/gy list, /gy add (name), /gy remove (name)");
            	return false;
            }
            else if (args.length >= 1)
        	{
            	String action = args[0];
        		if (action.equalsIgnoreCase("add"))
        		{
        			if (args.length >= 2)
        			{
        				String name = ChatColor.translateAlternateColorCodes('&', args[1]).replaceAll("_", " ");
        				Graveyard graveyard = new Graveyard(name, (Player) sender);
        				GraveyardManager.AddGraveyard(graveyard);
        			}
        		}
        		else if (action.equalsIgnoreCase("remove"))
        		{
        			if (TryParse.parseInt(args[1]))
        			{
	        			Integer ID = Integer.parseInt(args[1]);
	        			Graveyard graveyard = GraveyardManager.RemoveGraveyard(ID);
	        			OutputHandler.PrintCommandInfo(p, "Deleted Graveyard, " + graveyard.getName());
        			}
        			else
        			{
        				OutputHandler.PrintError(p, "Expected ID");
        			}
        		}
        		else if (action.equalsIgnoreCase("list"))
        		{
        			for(Graveyard graveyard: GraveyardManager.GetGraveyardsOfWorld(p.getWorld()))
        			{
        				OutputHandler.PrintCommandInfo(p, graveyard.getName() + OutputHandler.COMMAND + ", " + OutputHandler.HIGHLIGHT + graveyard.getID());
        			}
        		}
        		else
        		{
        			OutputHandler.PrintError(p, "/gy list, /gy add (name), /gy remove (name)");
        		}
        	}
        }
        return false;
    }
}	
