package de.kaleydra.krpg.items;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.SerializableAs;

import de.kaleydra.krpg.InvalidItemException;
// TODO Consume Skill
@SerializableAs("KRPGFood")
public class KRPGFood extends KRPGItem {

	int feedAmount;
	
	public KRPGFood(String customName, Material material, int feedAmount, ItemRarity rarity) {
		super(customName,material,rarity);
		this.feedAmount = feedAmount;
	}
	public KRPGFood(Map<String, Object> input) throws InvalidItemException{
		super(input);

		if(!input.containsKey("feedAmount")) throw new InvalidItemException("missing feedAmount");
		this.feedAmount = (int)input.get("feedAmount");
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = super.serialize();
		map.put("feedAmount", feedAmount);
		return map;
	}


}
