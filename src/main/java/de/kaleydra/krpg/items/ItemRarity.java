package de.kaleydra.krpg.items;

import org.bukkit.ChatColor;

public enum ItemRarity {
	COMMON(		"Common", 	ChatColor.WHITE.toString(), 		0.8),
	MAGICAL(	"Magical", 	ChatColor.BLUE.toString(), 			0.11),
	RARE(		"Rare", 	ChatColor.YELLOW.toString(), 		0.05),
	EPIC(		"Epic", 	ChatColor.DARK_PURPLE.toString(),	0.03),
	LEGENDARY(	"Legendary",ChatColor.GOLD.toString(),			0.01);
	
	private String name;
	private String color;
	private double chance;
	
	private ItemRarity(String name,String color, double chance){
		this.name = name;
		this.color = color;
		this.chance = chance;
	}
	
	public String getName(){
		return name;
	}
	
	/** 
	 * @return the color as a String 
	 * @see ChatColor#toString()
	 */
	public String getColor(){
		return color;
	}
	
	public static ItemRarity getByPrefix(String prefix){
		for (ItemRarity rarity : values()){
			if(prefix.startsWith(rarity.getColor())) return rarity;
		}
		return null;
	}
	
	@Override
	public String toString(){
		return getColor();
		
	}
	
	public double getChance(){
		return chance;
	}
	
	public static ItemRarity getWeightedRandom(){
		final ItemRarity[] values = values();
		int size = values.length;
		double random = Math.random();
		ItemRarity rarity = COMMON;
		for(int i = size-1; i >= 0; i--){
			rarity = values[i];
			if(rarity == COMMON || random < rarity.getChance()){ // end of iteration
				break;
			}
		}
		return rarity;
	}
}
