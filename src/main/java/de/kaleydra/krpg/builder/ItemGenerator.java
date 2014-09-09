package de.kaleydra.krpg.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.collections4.map.LRUMap;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.kaleydra.krpg.KRPG;
import de.kaleydra.krpg.KRPGUtils;
import de.kaleydra.krpg.items.ElementalDamageType;
import de.kaleydra.krpg.items.ItemRarity;
import de.kaleydra.krpg.items.KRPGItem;
import de.kaleydra.krpg.skills.Skill;

public class ItemGenerator {
	public static final String LORE_SECTION_SEPARATOR = ChatColor.BLACK + "" + ChatColor.BLACK + "" + ChatColor.BLACK;
	public static final String LABEL_COLOR = ChatColor.GREEN.toString();
	public static final String SKILLS_COLOR = ChatColor.GRAY.toString();
	public static final String VALUE_COLOR = ChatColor.GOLD.toString();
	public static final String DAMAGE_TYPE_LABEL = "Element";
	public static final String REQUIRED_LEVEL_LABEL = "Required Level";
	
	

	// Create Grappling Hook
	public static final ItemStack GRAPPLING_HOOK;
	static {
		KRPGItem krpgItem = new KRPGItem("Grappling Hook", Material.FISHING_ROD, ItemRarity.MAGICAL);
		krpgItem.setDamageType(ElementalDamageType.NONE);
		ItemStack itemStack = krpgItem.getItem();
		ItemMeta itemMeta = itemStack.getItemMeta();

		List<String> lore = Arrays.asList(
				SKILLS_COLOR + "Grappling Hook"
		);
		itemMeta.setLore(lore);
		itemStack.setItemMeta(itemMeta);
		GRAPPLING_HOOK = itemStack;
	}

	// fields
	private LRUMap<String, KRPGItem> itemCache = new LRUMap<String, KRPGItem>(1024);

	private enum LorePhase {
		SKILLS, DAMAGETYPE, ITEMLEVEL, LORE;
	}

	private LorePhase[] lorePhaseOrder = new LorePhase[] { LorePhase.SKILLS, LorePhase.DAMAGETYPE, LorePhase.ITEMLEVEL,
			LorePhase.LORE };

	KRPG plugin;

	public ItemGenerator(KRPG plugin) {
		this.plugin = plugin;
	}

	public KRPGItem generate(){
		return generate(GeneratorConfiguration.defaultConfig);
	}
	
	public KRPGItem generate(GeneratorConfiguration config) {
		if(config == null){
			KRPG.logSevere("Generator Configuration is null!");
			return null;
		}
		
		Random random = new Random();
		
		String tierString;
		Tier tier;
		
		String suffixString = null;
		Suffix suffix = null;
		
		String materialNameString;
		MaterialName materialName = null;
		
		ItemRarity rarity = ItemRarity.getWeightedRandom(); // TODO: From Config!
		
		// get random tier
		tierString = (String) getRandom(config.getTiers());
		tier = Tier.allTiers.get(tierString);
		if(tier == null){
			KRPG.logWarning("Generator Configuration has an invalid tier name: "+tierString);
			return null;
		}
		ElementalDamageType damageType = tier.getDamageType();
		
		KRPG.logInfo("Generated Tier \""+tier.getText()+"\"with: "+damageType.toString());
		
		// TODO: Item Material braucht noch Wertung, Diamond > Holz
		
		// get material
//		ArrayList<MaterialName> materials = new ArrayList<MaterialName>();
//		for(ItemCategory category:config.getAllowedItemCategories()){
//			materials.addAll(category.getMaterials());
//		}
//		for(MaterialName namePart : config.getMaterials().values()){
//			materials.add(namePart.material);
//		}
		
		
		// avoid duplicates
		Set<String> filteredMaterialNames = new HashSet<String>();
		
		if(config.getMaterialWhitelist() != null){
			List<String> materialNames;
			for(String material:config.getMaterialWhitelist()){
				 materialNames = config.getMaterialsNames().get(material);
				 if(materialNames == null) continue;
				 filteredMaterialNames.addAll(materialNames);
			}
		} else {
			for(List<String> names: config.getMaterialsNames().values()){
				filteredMaterialNames.addAll(names);
			}
		}
		if(filteredMaterialNames.isEmpty()){
			KRPG.logWarning("Generator Configuration has no possible names after filtering (Material Whitelist)");
			return null;
		}
		
		// TODO: it does not default to DIAMOND_SWORD.default as a name
//		boolean toDefault = false;
		for(int i=0; i < 30; i++){
			materialNameString = (String) getRandom(filteredMaterialNames);
			
//			if(toDefault) materialNameString = ""
			materialName = MaterialName.allMaterials.get(materialNameString); 
			if(materialName == null){
				KRPG.logWarning("Generator Configuration has an invalid material name: "+materialNameString);
				continue;
			}
//			if(i == 28){
//				toDefault = true;
//			}
			break;
		}
		if(materialName == null){
			
			KRPG.logWarning("Generator Configuration has no valid material names!");
			return null;
		}
		
		
		// get name of material
//		if(namesPerMaterial != null){
//			materialNameString = namesPerMaterial.get(damageType.name());
//			if(materialNameString == null || materialNameString.equals("")){
//				materialNameString = namesPerMaterial.get("default");
//			}
//		}
//		if(materialNameString == null || materialNameString.equals("")){
//			materialNameString = material.toString();
//		}
		
		// get level within range
		int level;
		if(config.getMinLevel() >= config.getMaxLevel()){
			level = Math.min(config.getMinLevel(), config.getMaxLevel());
		} else {
			level = config.getMinLevel() + random.nextInt(config.getMaxLevel() - config.getMinLevel());
		}
		KRPG.logInfo("Generated level: "+level);

		////////////////////
		// initialize item
		////////////////////
		
		KRPGItem item = new KRPGItem("temp", materialName.getMaterial(), rarity);
		item.setRequiredLevel(level);
		item.setDamageType(damageType);
		
		// phase 2
		
		for(int i=0; i < 30; i++){
			suffixString = (String)getRandom(config.getSuffixes()); //, config.powerSourceChance
			suffix = Suffix.allSuffixes.get(suffixString);
			if(suffix == null) {
				KRPG.logWarning("Generator Configuration has an invalid suffix: "+suffixString);				
				continue;	
			}
		}
		if(suffix == null) {
			KRPG.logWarning("Generator Configuration has no valid suffix!");				
			return null;	
		}
		
		KRPG.logInfo("Generated suffix: \""+suffix+"\"");
		
		///////////////
		// build name
		///////////////
		
		StringBuilder sb = new StringBuilder(rarity.toString());
		sb.append(tier.getText()+" ");
		
		sb.append(materialName);
		
		if(suffix != null){
			sb.append(" "+suffix.getText());
		}
		
		item.setCustomName(sb.toString());
		
		// apply modificators
		materialName.getModifier().onGenerate(item);
		tier.getModifier().onGenerate(item);
		if(suffix != null) suffix.getModifier().onGenerate(item);
		
		return item;
	}

	public ItemStack generateTest() {
		final String customName = "Grappling Hook";
		KRPGItem item = new KRPGItem(customName, Material.FISHING_ROD, ItemRarity.MAGICAL);
		ItemStack itemStack = item.getItem();
		ItemMeta itemMeta = itemStack.getItemMeta();
	
		List<String> fakeLore = Arrays.asList(
				SKILLS_COLOR + "Grappling Hook", LORE_SECTION_SEPARATOR, LABEL_COLOR + "Element: " + VALUE_COLOR
						+ ElementalDamageType.FIRE.name(), LABEL_COLOR + "Required Level: " + VALUE_COLOR + "1"
	
		);
		itemMeta.setLore(fakeLore);
		itemStack.setItemMeta(itemMeta);
	
		return itemStack;
	}


	public KRPGItem analyseItem(ItemStack item) {
			if (item == null)
				return null;
			if (!item.hasItemMeta())
				return null;
	
			final ItemMeta itemMeta = item.getItemMeta();
			if (!itemMeta.hasDisplayName())
				return null;
			final String displayName = itemMeta.getDisplayName();
	
			if (!itemMeta.hasLore()) {
	
	//			KRPG.logInfo("No Lore!");
				return null;
			}
	
			final String itemCacheName = displayName + "|" + itemMeta.getLore().hashCode();
			KRPGItem krpgItem = itemCache.get(itemCacheName);
			if (krpgItem != null)
				return krpgItem;
	
			/**
			 * <Enchantments> <Skill> (Level in Roman) <Skill> (Level in Roman)
			 * <Skill> (Level in Roman)
			 * 
			 * DamageType: <Elemental Damage>
			 */
	
			int phaseIndex = 0;
			ItemRarity rarity = ItemRarity.getByPrefix(displayName);
			if (rarity == null) {
	//			KRPG.logInfo("No Rarity!");
				return null;
			}
	
			krpgItem = new KRPGItem(displayName, item.getType(), rarity);
	
			LoreLoop: for (String line : itemMeta.getLore()) {
				LorePhase phase = lorePhaseOrder[phaseIndex];
	//			KRPG.logInfo("Lore line: " + line);
				// next phase
				if (line.equals(LORE_SECTION_SEPARATOR)) {
					phaseIndex++;
					if (phaseIndex >= lorePhaseOrder.length)
						break;
					continue;
				}
	
				String colorLess = ChatColor.stripColor(line);
	
				switch (phase) {
				case SKILLS:
	
					// get Skill name and level (in roman numeral)
					String[] splitted = colorLess.split(" ");
					int lastIndex = splitted.length - 1;
					String levelString = splitted[lastIndex];
	
					int currentLevel;
					try{
						currentLevel = KRPGUtils.romanToArabic(levelString);					
					} catch(IllegalArgumentException e){
						currentLevel = 1;
					}
					if (currentLevel <= 0) {
						// plugin.getLogger().severe(displayName+": "+skillName+" has an invalid level \""+levelString+"\", resulted in "+currentLevel);
						// return null;
						currentLevel = 1; // default
						lastIndex++; // there is no level
					}
	
					String skillName = "";
					for (int i = 0; i < lastIndex; i++) {
						skillName += splitted[i];
						if (i < lastIndex - 1)
							skillName += " ";
					}
	
//					// instantiate skill object
//					Class<? extends Skill> skillClass = plugin.getSkillManager().getSkillClassByDisplayName(skillName);
//					if (skillClass == null) {
//						KRPG.logInfo("unknown skill: " + skillName);
//						continue;
//					}
//					Skill skill;
//					try {
//						skill = (Skill) skillClass.getConstructor().newInstance();
//					} catch (Exception e) {
//						e.printStackTrace();
//						continue;
//					}
//	
//					skill.setKRPGItem(krpgItem);
//					skill.setCurrentLevel(currentLevel);
//					skill.setCurrentDisplayName(skillName);				
//					krpgItem.addSkill(skill);
					plugin.getSkillManager().applySkillToItem(krpgItem, skillName, currentLevel);
					
	//				KRPG.logInfo("loaded skill \"" + skill.getIdentifier() + "\" for item: " + displayName);
					break;
				case ITEMLEVEL:
	
					int requiredLevel = getValueInLore(displayName, colorLess, REQUIRED_LEVEL_LABEL, -1);
					if (requiredLevel > 0)
						krpgItem.setRequiredLevel(requiredLevel);
	
					// to damagetype
					phaseIndex++;
	
					break;
				case DAMAGETYPE:
					String damageTypeString = getValueInLore(displayName, colorLess, DAMAGE_TYPE_LABEL, "");
					try {
						ElementalDamageType damageType = ElementalDamageType.valueOf(damageTypeString.toUpperCase());
						krpgItem.setDamageType(damageType);
					} catch (Exception e) {
						KRPG.logSevere(displayName + ": has an invalid damageType: \"" + damageTypeString + "\"");
						return null;
					}
	
					phaseIndex++;
					break;
				default: // ignore lore!
					break LoreLoop;
				}
			}
			itemCache.put(itemCacheName, krpgItem);
			return krpgItem;
		}


	private static int getValueInLore(String displayName, String colorlessLine, String label, int def) {

		String[] splittedLevel = colorlessLine.split(": ");
		if (splittedLevel.length < 2 || !splittedLevel[0].equalsIgnoreCase(label)) {
			KRPG.logSevere(displayName + ": " + colorlessLine + " is not a valid label and value string!");
			return def;
		}
		try {
			return Integer.parseInt(splittedLevel[1]);
		} catch (NumberFormatException e) {
			KRPG.logSevere(displayName + ": " + colorlessLine + " does not contain a valid integer!");
		}
		return def;
	}

	private static String getValueInLore(String displayName, String colorlessLine, String label, String def) {

		String[] splittedLevel = colorlessLine.split(": ");
		if (splittedLevel.length < 2 || !splittedLevel[0].equalsIgnoreCase(label)) {
			KRPG.logSevere(displayName + ": " + colorlessLine + " is not a valid label and value string!");
			return def;
		}
		return splittedLevel[1];
	}
	

	public static Object getRandom(Collection<?> list){
		return getRandom(list, 0.0);
	}

	public static Object getRandom(Collection<?> list, double nullProbability){
		if(Math.random() < nullProbability) return null;
		int length = list.size();
		Random random = new Random();
		if(length == 0) return null;
		
		int i = random.nextInt(length);
		return  list.toArray(new Object[0])[i];
	}
	
	public static List<String> getCalculatedLore(KRPGItem item){
		List<String> lore = new ArrayList<String>();
		
		for(Skill skill: item.getSkills()){
			lore.add(SKILLS_COLOR+skill.getIdentifier());
		}
		
		boolean addedSeperator = false;
		if(!item.getDamageType().equals(ElementalDamageType.NONE)) {
			lore.add(LORE_SECTION_SEPARATOR);
			addedSeperator = true;
			lore.add(LABEL_COLOR+"Element: "+ VALUE_COLOR + item.getDamageType());
		}
		int requiredLevel = item.getRequiredLevel();
		if(requiredLevel > 1){
			if(!addedSeperator){
				lore.add(LORE_SECTION_SEPARATOR);
				addedSeperator = true;
			}
			lore.add(LABEL_COLOR+"Required Level: "+VALUE_COLOR+requiredLevel);
		}
		
		return lore;		
	}
	
}
