package de.kaleydra.krpg.items;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.kaleydra.licetia.api.SerializableManager;

import de.kaleydra.krpg.KRPG;
import de.kaleydra.krpg.builder.ItemGenerator;

public class ItemManager extends SerializableManager<KRPGItem> {
	private static final String FILENAME = "customItems/";	
	
	private ItemGenerator itemGenerator;
	
	private KRPG plugin;
	
	public ItemManager(KRPG plugin){
		this.plugin = plugin;
		this.itemGenerator = new ItemGenerator(plugin);
	}
	
	public boolean contains(ItemStack item){
		if (item == null) return false;
		if(!item.hasItemMeta()) return false;
		if(!item.getItemMeta().hasDisplayName()) return false;
		return super.contains(item.getItemMeta().getDisplayName());
	}
	
	
	public KRPGItem get(ItemStack item){
		if (item == null) return null;
		if(!item.hasItemMeta()) return null;
		final ItemMeta itemMeta = item.getItemMeta();
		if(!itemMeta.hasDisplayName()) return null;
		final String displayName = itemMeta.getDisplayName();
		
		// is predefined custom Item
		if(super.contains(displayName)) return super.get(displayName);
		
		KRPGItem krpgItem = itemGenerator.analyseItem(item);
		return krpgItem;
	}
	
	
	/**
	 * @return the itemGenerator
	 */
	public ItemGenerator getItemGenerator() {
		return itemGenerator;
	}

	public void load(){
		File file;
		try {
			file = new File(plugin.getDataFolder()+"/"+FILENAME);
			if(!file.exists()) {
				file = Files.createDirectory(new File(plugin.getDataFolder()+"/"+FILENAME).toPath()).toFile();
			}
			if(!super.load(file)) plugin.getLogger().severe("Error while loading items!");
			plugin.getLogger().info("Loaded "+super.getData().size()+" items:");
		    for(KRPGItem item : super.getData()){
		    	plugin.getLogger().info(item.getIdentifier());
		    }
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
	}
	
	public void save(){
		plugin.getLogger().info("saving items");
		
		File file;
		try {
			file = new File(plugin.getDataFolder()+"/"+FILENAME);
			if(!file.exists()) {
				file = Files.createDirectory(new File(plugin.getDataFolder()+"/"+FILENAME).toPath()).toFile();
			}
			
			FileNameGenerator<KRPGItem> namer = new FileNameGenerator<KRPGItem>() {

				@Override
				public String getName(KRPGItem item) {
					return item.getCustomName()+".yml";
				}
				
			};
			
			if(!super.save(file, namer)) plugin.getLogger().severe("Could not save items!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
