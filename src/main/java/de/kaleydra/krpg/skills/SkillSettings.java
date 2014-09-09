package de.kaleydra.krpg.skills;

import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import de.kaleydra.krpg.KRPG;

public final class SkillSettings {
	private ConfigurationSection settings;
	
	public SkillSettings(ConfigurationSection baseSettings, ConfigurationSection settingsPerSkill) {
		
		YamlConfiguration yml = new YamlConfiguration();
		this.settings = yml.getRoot();
		KRPG.getInstance().getLogger().info("Loading SkillSettings!");
		if(baseSettings != null){
			for(String key: baseSettings.getKeys(true)){
				KRPG.getInstance().getLogger().info(key+": " + baseSettings.getString(key));
				if(settingsPerSkill != null && settingsPerSkill.contains(key)){
					this.settings.set(key, settingsPerSkill.get(key));
				} else {
					this.settings.set(key, baseSettings.get(key));
				}
			}
		} else { throw new RuntimeException("base settings null!"); }
		
	}

	/**
	 * get an integer from the current settings for the skill
	 * @param ymlKey
	 * @return
	 */
	public int getInt(String ymlKey){
		checkExistence(ymlKey);
		return settings.getInt(ymlKey);
	}
	
	/**
	 * get a long from the current settings for the skill
	 * @param ymlKey
	 * @return
	 */
	public long getLong(String ymlKey){
		checkExistence(ymlKey);
		return settings.getLong(ymlKey);
	}
	
	/**
	 * get a string from the current settings for the skill
	 * @param ymlKey
	 * @return
	 */
	public String getString(String ymlKey){
		checkExistence(ymlKey);
		return settings.getString(ymlKey);
	}
	
	/**
	 * get a boolean from the current settings for the skill
	 * @param ymlKey
	 * @return
	 */
	public boolean getBoolean(String ymlKey){
		checkExistence(ymlKey);
		return settings.getBoolean(ymlKey);
	}
	
	/**
	 * get a color from the current settings for the skill
	 * @param ymlKey
	 * @return
	 */
	public Color getColor(String ymlKey){
		checkExistence(ymlKey);
		return settings.getColor(ymlKey);
	}
	/**
	 * get a double from the current settings for the skill
	 * @param ymlKey
	 * @return
	 */
	public double getDouble(String ymlKey){
		checkExistence(ymlKey);
		return settings.getDouble(ymlKey);
	}
	
	/**
	 * get an item stack from the current settings for the skill
	 * @param ymlKey
	 * @return
	 */
	public ItemStack getItemStack(String ymlKey){
		checkExistence(ymlKey);
		return settings.getItemStack(ymlKey);
	}
	
	/**
	 * get an integer from the current settings for the skill
	 * @param ymlKey
	 * @return
	 */
	public Vector getVector(String ymlKey){
		checkExistence(ymlKey);
		return settings.getVector(ymlKey);
	}
	
	/**
	 * get an integer from the current settings for the skill
	 * @param ymlKey
	 * @return
	 */
	public Object get(String ymlKey){
		checkExistence(ymlKey);
		return settings.get(ymlKey);
	}
	
	/**
	 * @param ymlKey
	 * @throws MissingSkillSettingsKeyException if the ymlKey does not exist in the base settings
	 */
	private void checkExistence(String ymlKey) {
		if(!settings.contains(ymlKey)) 
			throw new MissingSkillSettingsKeyException(ymlKey);
	}
	
}
