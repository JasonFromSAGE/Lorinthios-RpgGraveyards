package me.TheNukeDude.Commands;

import me.TheNukeDude.Data.Graveyard;
import me.TheNukeDude.Managers.GraveyardManager;
import me.TheNukeDude.Util.OutputHandler;
import me.TheNukeDude.Util.TryParse;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class GraveyardCommand implements CommandExecutor {

	@SuppressWarnings("serial")
	private ArrayList<String> commands = new ArrayList<String>(){{
		add("/gy list");
		add("/gy add <name> [discoverDistance]");
		add("/gy set <id> [name]"); //<> = required arg. [] = optional arg
		add("/gy remove <id>");
		add("/gy tp <id>");
		add("/gy info [id]");
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
			else if(action.equalsIgnoreCase("set"))
				setGraveyard(player, args);
			else if (action.equalsIgnoreCase("remove"))
				removeGraveyard(player, args);
			else if (action.equalsIgnoreCase("list"))
				listGraveyards(player);
			else if (action.equalsIgnoreCase("info"))
			    printInfo(player, args);
			else if (action.equalsIgnoreCase("teleport") || action.equalsIgnoreCase("tp"))
				teleportToGraveyard(player, args);
			else
				printOutCommands(player);
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

				if(args.length == 3){
				    double distance = Double.parseDouble(args[2]);
				    graveyard.setDistanceToDiscover(distance);
                }
			}else{
				OutputHandler.PrintError(player, "Usage : /gy add (name)");
			}
		}
	}

	private void printInfo(Player player, String[] args){
        if(checkPermission(player, "rpgraveyard.admin")) {
            //Closest graveyard
            if (args.length == 1){
                Graveyard graveyard = GraveyardManager.GetClosestGraveyard(player.getLocation());
                if(graveyard == null){
                    OutputHandler.PrintError(player, "No graveyards in your current world!");
                    return;
                }

                graveyard.printInfo(player);
            }
            //Graveyard with id
            else if (args.length >= 2) {
                if (!TryParse.parseInt(args[1])) {
                    OutputHandler.PrintError(player, "Please use the graveyard's ID.");
                    return;
                }

                Integer id = Integer.parseInt(args[1]);
                Graveyard graveyard = GraveyardManager.GetGraveyardByID(id);
                if(graveyard == null){
                    OutputHandler.PrintError(player, "No graveyard with the id, " + OutputHandler.HIGHLIGHT + id);
                    return;
                }
                graveyard.printInfo(player);
            } else{
                OutputHandler.PrintError(player, "Usage : /gy remove (ID)");
            }
        }
    }

	private void setGraveyard(Player player, String[] args){
        if(checkPermission(player, "rpgraveyard.admin")){
            if(args.length >= 2){
                if(!TryParse.parseInt(args[1])){
                    OutputHandler.PrintError(player, "Please use the graveyard's ID.");
                    return;
                }

                Integer id = Integer.parseInt(args[1]);
                Graveyard graveyard = GraveyardManager.GetGraveyardByID(id);
                if(graveyard != null){
                    graveyard.setLocation(player.getLocation());

                    //Handle update name
                    if(args.length >= 3){
                        graveyard.setName(args[2]);
                    }
                    OutputHandler.PrintCommandInfo(player, "Updated location of, " + graveyard.getName() + OutputHandler.COMMAND + " with id, " + OutputHandler.HIGHLIGHT + id);
                }
            }else{
                OutputHandler.PrintError(player, "Usage : /gy set <id> [name]");
            }
        }
    }

    private void removeGraveyard(Player player, String[] args){
		if(checkPermission(player, "rpgraveyard.admin")) {
			if (args.length >= 2) {
                if (!TryParse.parseInt(args[1])) {
                    OutputHandler.PrintError(player, "Please use the graveyard's ID.");
                    return;
                }

                Integer id = Integer.parseInt(args[1]);
                Graveyard graveyard = GraveyardManager.RemoveGraveyard(id);
                if(graveyard != null)
                    OutputHandler.PrintCommandInfo(player, "Deleted Graveyard, " + graveyard.getName());
                else
                    OutputHandler.PrintError(player, "No graveyard with the id, " + OutputHandler.HIGHLIGHT + id);
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
        if(args.length == 1 && checkPermission(player, "rpgraveyard.admin", "rpgraveyard.teleport", "rpgraveyard.teleport.closest")){
            Graveyard graveyard = GraveyardManager.GetClosestGraveyard(player.getLocation());
            if(graveyard != null) {
                graveyard.respawn(player);
            }else
                OutputHandler.PrintError(player, "No graveyard to teleport to in this world");
        }
        else if (args.length >= 2 && checkPermission(player, "rpgraveyard.admin", "rpgraveyard.teleport")){
            if(!TryParse.parseInt(args[1])){
                OutputHandler.PrintError(player, "Please use the graveyard's ID.");
                return;
            }
            Integer id = Integer.parseInt(args[1]);
            Graveyard graveyard = GraveyardManager.GetGraveyardByID(id);
            if(graveyard != null)
                graveyard.respawn(player);
            else
                OutputHandler.PrintError(player, "No graveyard with the id, " + OutputHandler.HIGHLIGHT + id);
        }
        else if(checkPermission(player, "rpgraveyard.admin", "rpgraveyard.teleport"))
            OutputHandler.PrintError(player, "Usage : /gy teleport (ID)");
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
