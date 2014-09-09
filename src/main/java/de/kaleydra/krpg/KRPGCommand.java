package de.kaleydra.krpg;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import de.kaleydra.krpg.builder.ItemGenerator;
import de.kaleydra.krpg.items.ItemRarity;
import de.kaleydra.krpg.items.KRPGItem;

public class KRPGCommand implements CommandExecutor {

	KRPG plugin;

	public KRPGCommand(KRPG plugin){
		this.plugin = plugin;
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
				
		if(!sender.isOp()) {
			sender.sendMessage(ChatColor.RED+"Nope!");
			return true;
		}
		sender.sendMessage(ChatColor.GREEN+"KRPG Version: "+plugin.getDescription().getVersion());
		
		if(args.length < 1) return false;
		
		
		
		String subcommand1 = args[0];
		if(subcommand1.equalsIgnoreCase("generate")){			
			if(!(sender instanceof Player)) return false;

			int amount = 1;
			if(args.length > 1){
				try{
					amount = Integer.parseInt(args[1]);				
				} catch(NumberFormatException e){
					sender.sendMessage(ChatColor.DARK_RED+args[1] +" is not a number!");
					return true;
				}
			}
			Player player = (Player) sender;
			
			KRPGItem krpgItem;
			int i;
			final PlayerInventory inventory = player.getInventory();
			for(i = 0; i<amount; i++){				
				krpgItem = plugin.getNameGenerator().generate();
				if(krpgItem == null){
					sender.sendMessage("Error, could not generate!");
					continue;
				}
				inventory.addItem(krpgItem.getItem());
				plugin.getLogger().info("Item given to "+player.getName());
				player.sendMessage("Generated: "+krpgItem.getCustomName());
			}
			player.sendMessage(ChatColor.GREEN+"Generated "+i+" items!");
			return true;
		}
		else if(subcommand1.equalsIgnoreCase("give")){
			if(!(sender instanceof Player)) return false;
			if(args.length < 2) {
				sender.sendMessage(ChatColor.DARK_RED+"You forgot the name as an argument");
				return false;
			}
			String itemName = "";
			ItemRarity rarity = ItemRarity.COMMON;
			for(int i = 1; i < args.length; i++){
				if(i != 1) {
					itemName += " ";
				} else {
					try{
						rarity = ItemRarity.valueOf(args[i].toUpperCase());		
					} catch(Exception e){
						sender.sendMessage("unknown rarity: "+args[i]+" defaulted to "+rarity.name());
					}
					continue;
				}
				itemName += args[i];
			}
			itemName = itemName.trim();
			
			KRPGItem item = plugin.getItemManager().get(rarity+itemName);
			if(item == null){
				sender.sendMessage(ChatColor.DARK_RED+"Unknown item: \""+ itemName +"\"");
				return false;
			}
			
			((Player) sender).getInventory().addItem(item.getItem());
			sender.sendMessage(ChatColor.GREEN+"You received:");
			sender.sendMessage(item.getInfo());
		}
		else if(subcommand1.equalsIgnoreCase("list")){
			for(KRPGItem item: plugin.getItemManager().getData()){
				sender.sendMessage(item.getIdentifier());
			}
		}
		else if(subcommand1.equalsIgnoreCase("test")){
			if(!(sender instanceof Player)) return false;
			
			ItemStack item = plugin.getItemManager().getItemGenerator().generateTest();
			
			((Player)sender).getInventory().addItem(item);
			sender.sendMessage(ChatColor.GREEN+"You received: "+ChatColor.GOLD+item.getItemMeta().getDisplayName());
			return true;
		}
		else if(subcommand1.equalsIgnoreCase("hook")){
			if(!(sender instanceof Player)) return false;
			
			ItemStack item = ItemGenerator.GRAPPLING_HOOK;
			((Player)sender).getInventory().addItem(item);
			sender.sendMessage(ChatColor.GREEN+"You received: "+item.getItemMeta().getDisplayName());
			return true;
		}
		else if(subcommand1.equalsIgnoreCase("reload")){
			plugin.getSkillManager().load();
			plugin.loadNameParts();
			plugin.getItemManager().load();
			
			sender.sendMessage(ChatColor.DARK_GREEN+"Reloaded!");
			return true;
		}
		else if(subcommand1.equalsIgnoreCase("save")){
			plugin.save();
			sender.sendMessage(ChatColor.DARK_GREEN+"saved!");
			return true;
		}
		else if(subcommand1.equalsIgnoreCase("info")){
			if(!(sender instanceof Player)) return false;
			Player player = (Player) sender;
			KRPGItem item = plugin.getItemManager().get(player.getItemInHand());
			if(item == null){
				sender.sendMessage(ChatColor.DARK_RED+"Unknown item!");
				return true;
			}
			
			sender.sendMessage(item.getInfo());
		}
		else {
			sender.sendMessage(ChatColor.RED+"Unknown Subcommand");
		}
		return false;
	}

}
