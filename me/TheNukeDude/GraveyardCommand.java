package me.TheNukeDude;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class GraveyardCommand implements CommandExecutor {

	private ArrayList<String> commands = new ArrayList<String>(){{
		add("/gy list");
		add("/gy add (name)");
		add("/gy remove (ID)");
		add("/gy teleport (ID)");
	}};

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
    	if(!(sender instanceof Player)){
    		OutputHandler.PrintError("Only players can use graveyard commands!");
    		return false;
		}

		Player player = (Player) sender;
		if (args.length >= 1)
		{
			String action = args[0];
			if (action.equalsIgnoreCase("add"))
				addGraveyard(player, args);
			else if (action.equalsIgnoreCase("remove"))
				removeGraveyard(player, args);
			else if (action.equalsIgnoreCase("list"))
				listGraveyards(player);
			else if (action.equalsIgnoreCase("teleport") || action.equalsIgnoreCase("tp"))
				teleportToGraveyard(player, args);
			else
				OutputHandler.PrintError(player, "/gy list, /gy add (name), /gy remove (ID), /gy teleport (ID)");
		}
		else
			printOutCommands(player);

        return false;
    }

    private void printOutCommands(Player player) {
		for(String command : commands){
			OutputHandler.PrintCommandInfo(player, command);
		}
	}

    private void addGraveyard(Player player, String[] args){
		if(checkPermission(player, "rpgraveyard.admin")){
			if (args.length >= 2){
				String name = ChatColor.translateAlternateColorCodes('&', args[1]).replaceAll("_", " ");
				Graveyard graveyard = new Graveyard(name, player);
				GraveyardManager.AddGraveyard(graveyard);
			}else{
				OutputHandler.PrintError(player, "Usage : /gy add (name)");
			}
		}
	}

    private void removeGraveyard(Player player, String[] args){
		if(checkPermission(player, "rpgraveyard.admin")) {
			if (args.length >= 2) {
				if (TryParse.parseInt(args[1])) {
					Integer ID = Integer.parseInt(args[1]);
					Graveyard graveyard = GraveyardManager.RemoveGraveyard(ID);
					OutputHandler.PrintCommandInfo(player, "Deleted Graveyard, " + graveyard.getName());
				} else {
					OutputHandler.PrintError(player, "Expected ID");
				}
			} else{
				OutputHandler.PrintError(player, "Usage : /gy remove (ID)");
			}
		}
	}

    private void listGraveyards(Player player) {
		if(checkPermission(player, "rpgraveyard.admin")) {
			for (Graveyard graveyard : GraveyardManager.GetGraveyardsOfWorld(player.getWorld())) {
				OutputHandler.PrintCommandInfo(player, graveyard.getName() + OutputHandler.COMMAND + ", " + OutputHandler.HIGHLIGHT + graveyard.getID());
			}
		}
	}

	private void teleportToGraveyard(Player player, String[] args){
		if(checkPermission(player, "rpgraveyard.admin", "rpgraveyard.teleport")) {
			if (args.length >= 2){
				if (TryParse.parseInt(args[1])) {
					Integer id = Integer.parseInt(args[1]);
					Graveyard graveyard = GraveyardManager.GetGraveyardByID(id);
					player.teleport(graveyard.getLocation());
				} else {
					OutputHandler.PrintError(player, "Please use the graveyard's ID.");
				}
			} else{
				OutputHandler.PrintError(player, "Usage : /gy teleport (ID)");
			}
		}
	}

    private boolean checkPermission(Player player, String... permission){
    	boolean hasPerm = false;
    	for(String perm : permission){
    		if(player.hasPermission(perm)) {
				hasPerm = true;
				break;
			}
		}
		if(!hasPerm){
			OutputHandler.PrintError(player, "Insufficient Permission.");
			return false;
		}
		return true;
	}
}	
