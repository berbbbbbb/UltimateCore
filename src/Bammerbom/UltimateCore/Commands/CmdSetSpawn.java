package Bammerbom.UltimateCore.Commands;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import Bammerbom.UltimateCore.UltimateFileLoader;
import Bammerbom.UltimateCore.r;

public class CmdSetSpawn{
	static Plugin plugin;
	public CmdSetSpawn(Plugin instance){
		plugin = instance;
		if(this instanceof Listener){
			Bukkit.getPluginManager().registerEvents((Listener) this, instance);
		}
	}
	public static void handle(CommandSender sender, String label, String[] args){
		if(r.isPlayer(sender)){
		    Player p = (Player) sender;
		    if(r.perm(p, "uc.setspawn", false, true) == false){
				return;
			}
		    YamlConfiguration data = YamlConfiguration.loadConfiguration(UltimateFileLoader.DFspawns);
		    Location loc = p.getLocation();
		    loc.getWorld().setSpawnLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		    String location = loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + loc.getYaw() + "," + loc.getPitch();
		    data.set("spawn", location);
		    try {
				data.save(UltimateFileLoader.DFspawns);
			} catch (IOException e) {
				e.printStackTrace();
			}
		    p.sendMessage(r.mes("Set.Spawn"));
		}
	}
}
