package de.kaleydra.krpg.builder;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import de.kaleydra.krpg.KRPG;
// <adjective> <tier> <item> <source>
// TODO: Basis für jede Generierung! Es wird eine default Config vorgehalten

public class GeneratorConfiguration implements ConfigurationSerializable{
	public static GeneratorConfiguration defaultConfig;
	
	KRPG plugin;
	
	private int minLevel = -1;
	private int maxLevel = -1;
	

//	private List<String> allowedItemCategories = null;
	private List<String> materialWhitelist = null;
	
	/** DIAMOND_SWORD -> {Dagger, Hook, ...}*/
	private Map<String, List<String>> materialsNames = null;
	private List<String> tiers = null;
	
	private List<String> powerSources = null;
//	public double powerSourceChance;
	
	public GeneratorConfiguration(){
		this.plugin = KRPG.getInstance();
	}
	
	@SuppressWarnings("unchecked")
	public GeneratorConfiguration(Map<String,Object> input){
		if(input.containsKey("minLevel")) this.minLevel = (int) input.get("minLevel");
		if(input.containsKey("maxLevel")) this.maxLevel = (int)input.get("maxLevel");
		
//		if(input.containsKey("allowedItemCategories")) 
//			this.allowedItemCategories = (List<String>) input.get("allowedItemCategories");
		
		if(input.containsKey("materialWhitelist")) 
			this.materialWhitelist = (List<String>) input.get("materialWhitelist");
		
		if(input.containsKey("materialsNames")) 
			this.materialsNames = (Map<String, List<String>>) input.get("materialsNames");
	}

	
	/**
	 * @return the minLevel
	 */
	public int getMinLevel() {
		if(minLevel < 0) return defaultConfig.minLevel;
		return minLevel;
	}

	/**
	 * @return the maxLevel
	 */
	public int getMaxLevel() {
		if(maxLevel < 0) return defaultConfig.maxLevel;
		return maxLevel;
	}
//
//	/**
//	 * @return the allowedItemCategories
//	 */
//	public List<String> getAllowedItemCategories() {
//		if(allowedItemCategories == null) return defaultConfig.allowedItemCategories;
//		return allowedItemCategories;
//	}

	/**
	 * @return the materialWhitelist
	 */
	public List<String> getMaterialWhitelist() {
		return materialWhitelist;
	}

	/**
	 * @param materialWhitelist the materialWhitelist to set
	 */
	public void setMaterialWhitelist(List<String> materialWhitelist) {
		this.materialWhitelist = materialWhitelist;
	}

	/**
	 * @return the allowedMaterials
	 */
	public Map<String, List<String>> getMaterialsNames() {
		if(materialsNames == null) return defaultConfig.materialsNames;
		return materialsNames;
	}

	/**
	 * @return the tiers
	 */
	public List<String> getTiers() {
		if(tiers == null) return defaultConfig.tiers;
		return tiers;
	}

	/**
	 * @return the powerSources
	 */
	public List<String> getSuffixes() {
		if(powerSources == null) return defaultConfig.powerSources;
		return powerSources;
	}
	
	/**
	 * @param minLevel the minLevel to set
	 */
	public void setMinLevel(int minLevel) {
		this.minLevel = minLevel;
	}

	/**
	 * @param maxLevel the maxLevel to set
	 */
	public void setMaxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
	}
//
//	/**
//	 * @param allowedItemCategories the allowedItemCategories to set
//	 */
//	public void setAllowedItemCategories(List<String> allowedItemCategories) {
//		this.allowedItemCategories = allowedItemCategories;
//	}

	/**
	 * @param allowedMaterials the allowedMaterials to set
	 */
	public void setMaterialsNames(Map<String, List<String>> allowedMaterials) {
		this.materialsNames = allowedMaterials;
	}

	/**
	 * @param tiers the tiers to set
	 */
	public void setTiers(List<String> tiers) {
		this.tiers = tiers;
	}

	/**
	 * @param powerSources the powerSources to set
	 */
	public void setSuffixes(List<String> powerSources) {
		this.powerSources = powerSources;
	}


	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> output = new LinkedHashMap<String, Object>();
		output.put("minLevel", minLevel);
		output.put("maxLevel", maxLevel);
//		output.put("allowedItemCategories", allowedItemCategories);
		output.put("materialWhitelist", materialWhitelist);
		output.put("materialsNames", materialsNames);
		output.put("tiers", tiers);
		output.put("powerSources", powerSources);
		return output;
	}
}
