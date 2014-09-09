package de.kaleydra.krpg;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;

import com.kaleydra.licetia.api.Manager;
import com.kaleydra.licetia.wrappers.SerializablePlayerInventory;

import de.kaleydra.krpg.builder.GeneratorConfiguration;
import de.kaleydra.krpg.builder.ItemGenerator;
import de.kaleydra.krpg.builder.ItemModifier;
import de.kaleydra.krpg.builder.MaterialName;
import de.kaleydra.krpg.builder.Suffix;
import de.kaleydra.krpg.builder.Tier;
import de.kaleydra.krpg.items.ElementalDamageType;
import de.kaleydra.krpg.items.ItemManager;
import de.kaleydra.krpg.items.KRPGItem;
import de.kaleydra.krpg.listener.DamageListener;
import de.kaleydra.krpg.listener.PlayerListener;
import de.kaleydra.krpg.listener.ProjectileListener;
import de.kaleydra.krpg.player.Adventurer;
import de.kaleydra.krpg.skills.SkillManager;


public class KRPG extends JavaPlugin {
	private static KRPG instance;

	ItemGenerator nameGenerator = new ItemGenerator(this);
	
	private ItemManager itemManager;
	private Map<String,SerializablePlayerInventory> drops = new HashMap<String,SerializablePlayerInventory>();
	private SkillManager skillManager;
	
	
	private Manager<Adventurer> adventurerManager = new Manager<Adventurer>();

	public ItemManager getItemManager() {
		return itemManager;
	}

	@Override
	public void onEnable(){
		instance = this;
		ConfigurationSerialization.registerClass(Tier.class, "KRPGTier");
		ConfigurationSerialization.registerClass(Suffix.class, "KRPGSuffix");
		ConfigurationSerialization.registerClass(MaterialName.class, "KRPGMaterialName");
		ConfigurationSerialization.registerClass(ItemModifier.class, "KRPGItemModifier");
		
		getConfig().options().copyDefaults(true);
		getConfig().addDefault("weaknessMultiplier", 1.5);
		ElementalDamageType.weaknessMultiplier = getConfig().getDouble("weaknessMultiplier");
		
		
		skillManager = new SkillManager(this);
		
		getCommand("krpg").setExecutor(new KRPGCommand(this));

		skillManager.load();
		ConfigurationSerialization.registerClass(KRPGItem.class, "KRPGItem");
				
		itemManager = new ItemManager(this);
		itemManager.load();

//		getServer().getPluginManager().registerEvents(new FoodListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		getServer().getPluginManager().registerEvents(new DamageListener(this), this);
		getServer().getPluginManager().registerEvents(new ProjectileListener(this), this);

		loadDrops();
		
		
//		getServer().getPluginManager().registerEvents(new com.kaleydra.licetia.listeners.CustomProjectileHelper(), this);
		
		loadDefaultGeneratorConfigurations();
	}
	
	@Override
	public void onDisable(){
//		itemManager.save();
		skillManager.closeLoaders();
		saveDrops();
		saveConfig();
	}
	
	/**
	 * loads the items a player lost and will get back once he respawns
	 */
	public void loadDrops(){
		File file = new File(getDataFolder(),"tempDrops.yml");
		YamlConfiguration yml = new YamlConfiguration();
		try {
			if(!file.exists()) {
				file.createNewFile();
			} else {
				yml.load(file);
				if(yml.contains("drops")){
					drops.clear();
					final ConfigurationSection configurationSection = yml.getConfigurationSection("drops");
					for(String key: configurationSection.getValues(false).keySet()){
						drops.put(key, (SerializablePlayerInventory) configurationSection.get(key));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void saveDrops(){
		File file = new File(getDataFolder(),"tempDrops.yml");
		YamlConfiguration yml = new YamlConfiguration();
		try {
			if(!file.exists()) file.createNewFile();
			
			yml.set("drops", drops);
			yml.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void save(){
		saveDrops();
		Tier.allTiers.save(new File(getDataFolder(), "tiers.yml"));
		MaterialName.allMaterials.save(new File(getDataFolder(), "materials.yml"));
		Suffix.allSuffixes.save(new File(getDataFolder(),"suffixes.yml"));
		
		saveConfig();
	}
	
	public void loadDefaultGeneratorConfigurations(){
		
//		Tier.allTiers = new HashMap<String, >
		
		loadNameParts();
		
		
		


		// TODO: load Tiers from File!
		ItemModifier demoModifier = new ItemModifier() {
			@Override
			public void onGenerate(KRPGItem item) {
//				item.addAttribute(Attribute.newBuilder().name("attack").amount(10)
//				.type(AttributeType.GENERIC_ATTACK_DAMAGE).operation(Operation.ADD_NUMBER).build());
				item.addEnchantment(Enchantment.LOOT_BONUS_MOBS, 3);
			}			
		};
		final Tier tier1 = new Tier("Netheric", demoModifier, ElementalDamageType.FIRE);
		Tier.allTiers.add(tier1);
		Tier.allTiers.add(new Tier("Marine", demoModifier, ElementalDamageType.WATER));
		Tier.allTiers.add(new Tier("Guardian", new ItemModifier(), ElementalDamageType.WITHER));
		Tier.allTiers.add(new Tier("Warlock", demoModifier, ElementalDamageType.ARCANE));
		

		Suffix.allSuffixes.add(new Suffix("of Doom", new ItemModifier()));
		Suffix.allSuffixes.add(new Suffix("of Destruction", new ItemModifier()));
		
		MaterialName.allMaterials.add(new MaterialName(tier1,Material.DIAMOND_SWORD, "Hellblade", new ItemModifier()));
		
//		defaultGeneratorConfig  = new GeneratorConfiguration();
		
		GeneratorConfiguration.defaultConfig = new GeneratorConfiguration();
		
		// TODO: from materials.yml
		Map<String,List<String>> diamondNames = new HashMap<String,List<String>>();
		diamondNames.put(Material.DIAMOND_SWORD.name(), Arrays.asList("Hellblade")); 
		
		// item level range
		GeneratorConfiguration config = GeneratorConfiguration.defaultConfig;
		config.setMaxLevel(50);
		config.setMinLevel(0);
		
		Map<String, List<String>> materials = new HashMap<String, List<String>>();
		for(MaterialName name:MaterialName.allMaterials.getData()){
			List<String> names = materials.get(name.getMaterial());
			if(names == null){
				names = new ArrayList<String>();
				materials.put(name.getMaterial().name(), names);				
			}
			names.add(name.getText());
		}
		config.setMaterialsNames(materials);
		
		// tiers
		List<String> tiers = new ArrayList<String>();
		for(Tier tier:Tier.allTiers.getData()){
			tiers.add(tier.getText());
		}
		config.setTiers(tiers);
		
		
		// power sources 
		List<String> suffixes = new ArrayList<String>();
		for(Suffix suffix:Suffix.allSuffixes.getData()){
			suffixes.add(suffix.getText());
		}
		config.setSuffixes(suffixes);
		
		YamlConfiguration yml = new YamlConfiguration();
		File file = new File(getDataFolder(), "defaultConfig.yml");
		yml.set("defaultConfig", config);
		try {
			yml.save(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
	}

	public void loadNameParts() {
		// tier first!
		Tier.allTiers.load(new File(getDataFolder(), "tiers.yml"));
		MaterialName.allMaterials.load(new File(getDataFolder(), "materials.yml"));
		Suffix.allSuffixes.load(new File(getDataFolder(),"suffixes.yml"));
	}
	
	public ItemGenerator getNameGenerator(){
		return nameGenerator;
	}
	
	public Manager<Adventurer> getAdventurerManager(){
		return adventurerManager;
	}
	
	
	/**
	 * @return the skillManager
	 */
	public SkillManager getSkillManager() {
		return skillManager;
	}

	public static KRPG getInstance(){
		return instance;
	}
	
	public Map<String, SerializablePlayerInventory> getTempDrops(){
		return drops;
	}

	public static void logInfo(String message){
		instance.getLogger().info(message);
	}
	public static void logWarning(String message){
		instance.getLogger().warning(message);
	}
	public static void logSevere(String message){
		instance.getLogger().severe(message);
	}

}
