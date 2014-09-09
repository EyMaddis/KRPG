package de.kaleydra.krpg.builder;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import com.kaleydra.licetia.api.Identifiable;

import de.kaleydra.krpg.KRPG;


public abstract class NamePart implements Comparable<NamePart>, ConfigurationSerializable, Identifiable {
	
	String text;
	ItemModifier modifier = new ItemModifier();
	int minLevel = 0;
	int maxLevel = 50;

	public NamePart(String name, ItemModifier itemModifier){
		this.setText(name);
		this.setModifier(itemModifier);
	}
	
	public NamePart(Map<String,Object> input){
		if(input.containsKey("maxLevel")) this.maxLevel = (int)input.get("maxLevel");
		if(input.containsKey("minLevel")) this.minLevel = (int)input.get("minLevel");

		if(input.containsKey("modifier")) 
			this.modifier = (ItemModifier)input.get("modifier");

		if(modifier == null) modifier = new ItemModifier();
		this.text = (String) input.get("text");
		if(this.text == null || this.text.equals("")){
			KRPG.logSevere("Item from yaml is missing \"text\" in name part declaration!");
		}
	}

	/**
	 * @return the name
	 */
	public String getText() {
		return text;
	}
	
	@Override
	public String getIdentifier(){
		return getText();
	}

	/**
	 * @param text the name to set
	 */
	public void setText(String text) {
		this.text = text;
	}
	
	/**
	 * @return the itemModifier
	 */
	public ItemModifier getModifier() {
		return modifier;
	}

	/**
	 * @param itemModifier the itemModifier to set
	 */
	public void setModifier(ItemModifier itemModifier) {
		this.modifier = itemModifier;
	}

	@Override
	public String toString(){
		return text;
	}

	@Override
	public int compareTo(@Nonnull NamePart other) {
//		if(other == null) {
//			return -1;
//		}
		return other.getText().compareToIgnoreCase(this.text);
	}
	
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> output = new LinkedHashMap<String, Object>();
		output.put("text", text);
		output.put("modifier", modifier);
		output.put("minLevel", minLevel);
		output.put("maxLevel", maxLevel);
		return output;
	}

}
