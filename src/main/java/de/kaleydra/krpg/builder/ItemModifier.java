package de.kaleydra.krpg.builder;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.enchantments.Enchantment;

import com.kaleydra.licetia.wrappers.SerializableAttribute;

import de.kaleydra.krpg.KRPG;
import de.kaleydra.krpg.items.ItemRarity;
import de.kaleydra.krpg.items.KRPGItem;
import de.kaleydra.krpg.skills.Skill;

@SerializableAs("KRPGItemModifier") // TODO, geht nicht
public class ItemModifier implements ConfigurationSerializable {
	
	Map<Enchantment, Map<ItemRarity, Integer[]>> enchantments = new HashMap<Enchantment, Map<ItemRarity, Integer[]>>();
	Map<Skill, Map<ItemRarity, Integer[]>> skills = new HashMap<Skill, Map<ItemRarity, Integer[]>>();
	Map<SerializableAttribute, Map<ItemRarity, Integer[]>> attributes = new HashMap<SerializableAttribute, Map<ItemRarity, Integer[]>>();
	
	public ItemModifier(){
		
	}
	
	public ItemModifier(Map<Enchantment, Map<ItemRarity, Integer[]>> enchantments,
			Map<Skill, Map<ItemRarity, Integer[]>> skills,
			Map<SerializableAttribute, Map<ItemRarity, Integer[]>> attributes) {
		this.enchantments = enchantments;
		this.skills = skills;
		this.attributes = attributes;
	}

	@SuppressWarnings("unchecked")
	public ItemModifier(Map<String,Object> input){		
		if(input.containsKey("enchantments")){
			try {
				this.enchantments = (Map<Enchantment, Map<ItemRarity, Integer[]>>) input.get("enchantments");
			} catch (Exception e){ 
				e.printStackTrace(); 
			}
		}		
		if(input.containsKey("skills")){
			try {
				this.skills = (Map<Skill, Map<ItemRarity, Integer[]>>) input.get("skills");
			} catch (Exception e){ 
				e.printStackTrace(); 
			}
		}		
		if(input.containsKey("attributes")){
			try {
				this.attributes = (Map<SerializableAttribute, Map<ItemRarity, Integer[]>>) input.get("attributes");
			} catch (Exception e){ 
				e.printStackTrace(); 
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void onGenerate(KRPGItem item){
		ItemRarity rarity = item.getRarity();
		
		Map<Enchantment,Integer> enchantments = (Map<Enchantment, Integer>) getFromRange(rarity, this.enchantments);
		item.addEnchantments(enchantments);
		
		Map<Skill, Integer> skills = (Map<Skill, Integer>) getFromRange(rarity, this.skills);
		for(Skill skill: skills.keySet()){
			KRPG.getInstance().getSkillManager().applySkillToItem(item, skill.getIdentifier(), skills.get(skill));
//			skill.setCurrentLevel(skills.get(skill));
//			item.addSkill(skill);
		}

		Map<SerializableAttribute, Integer> attributes = (Map<SerializableAttribute, Integer>) getFromRange(rarity, this.attributes);
		for(SerializableAttribute attribute: attributes.keySet()){
			item.addAttribute(attribute.getAttribute());
		}		
	}

	
	/**
	 * assumption: level array is nonempty, if not every rarity has an entry, it will take the next lowest rarity. 
	 * Range defaults to [1,1] 
	 * @param rarity
	 * @param input
	 * @return map of the attribute/enchantment or skill with a random level within the correct range.
	 */
	private Map<?, Integer> getFromRange(ItemRarity rarity, Map<?, Map<ItemRarity, Integer[]>> input) {
		Map<Object, Integer> output = new LinkedHashMap<Object,Integer>();
		for(Object key:input.keySet()){
			Map<ItemRarity, Integer[]> rarityRanges = input.get(key);
			Integer[] range = rarityRanges.get(rarity);
			
			if(range == null){ // rarity does not have an entry
				// get the 
				ItemRarity lastRarity = ItemRarity.COMMON;
				for (ItemRarity currentRarity:ItemRarity.values()) {
					range = rarityRanges.get(currentRarity);
					if(range != null){
						lastRarity = currentRarity;
					}
					if(currentRarity.equals(rarity)) break;
				}
				range = rarityRanges.get(lastRarity);
				if(range == null) range = new Integer[]{1,1};
			}
			
			int lastIndex = range.length-1;
			int level = range[0];
			int rangeMin = Math.min(range[0], range[lastIndex]);	
			int rangeMax = Math.max(range[0], range[lastIndex]);
			if(rangeMin != rangeMax){
				level = (int) Math.round(rangeMin + (Math.random()*(rangeMax-rangeMin)));
			}
			output.put(key, level);
		}
		return output;
	}

	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> output = new LinkedHashMap<String, Object>();
		output.put("enchantments", enchantments);
		output.put("skills", skills);
		output.put("attributes", attributes);
		return output;
	}
	
//	private int getFromRange(ItemRarity rarity, Map<ItemRarity, Integer[]>){
//		return 0;
//	}
}
