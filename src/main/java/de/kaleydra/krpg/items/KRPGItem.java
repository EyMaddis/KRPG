package de.kaleydra.krpg.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.kaleydra.licetia.api.item.CustomItem;
import com.kaleydra.licetia.nbt.Attributes;
import com.kaleydra.licetia.nbt.Attributes.Attribute;
import com.kaleydra.licetia.wrappers.SerializableAttribute;

import de.kaleydra.krpg.InvalidItemException;
import de.kaleydra.krpg.KRPG;
import de.kaleydra.krpg.KRPGUtils;
import de.kaleydra.krpg.builder.ItemGenerator;
import de.kaleydra.krpg.skills.Skill;
import de.kaleydra.krpg.skills.SkillManager;

@SerializableAs("KRPGItem")
public class KRPGItem implements CustomItem, ConfigurationSerializable {
	
	public static final int MAX_LORE_WIDTH = 50;
	String customName;
	ItemRarity rarity;
	Material material;
	int requiredLevel = 0;
	ElementalDamageType damageType = ElementalDamageType.NONE;
	
//	List<String> description = new ArrayList<String>();
	List<String> lore = new ArrayList<String>();
	List<Attribute> attributes = new ArrayList<Attribute>();
	Map<Enchantment,Integer> enchantments = new HashMap<Enchantment,Integer>();
	
	List<Skill> skills = new ArrayList<Skill>();
	
	public KRPGItem(String customName, Material material, ItemRarity rarity){
		this.customName = customName;
		this.material = material;
		this.rarity = rarity;
	}
	
	/**
	 * Override!
	 * @param input
	 * @throws InvalidItemException
	 */
	@SuppressWarnings("unchecked")
	public KRPGItem(Map<String, Object> input) throws InvalidItemException{
		if(!input.containsKey("customName") || input.get("customName") == null) 
			throw new InvalidItemException("missing customName");
		this.customName = input.get("customName").toString();
		if(input.containsKey("rarity")){
			String rarity = ""+input.get("rarity");
			try{
				this.rarity = ItemRarity.valueOf(rarity);
			} catch(IllegalArgumentException e){
				throw new InvalidItemException("unknown rarity: "+ rarity);
			}
		}
		
		if(input.containsKey("damageType")){
			String damageType = ""+input.get("damageType");
			try{
				this.damageType = ElementalDamageType.valueOf(damageType);
			} catch(IllegalArgumentException e){
				throw new InvalidItemException("unknown damage type: "+ damageType);
			}
		}

		if(input.containsKey("requiredLevel")){
			String requiredLevel = ""+input.get("requiredLevel");
			try{
				this.requiredLevel = Integer.parseInt(requiredLevel);
			} catch(IllegalArgumentException e){
				throw new NumberFormatException("required Level is invalid: "+ requiredLevel);
			}
		}
		
		if(!input.containsKey("material")) throw new InvalidItemException("missing material");

		String material = ""+input.get("material");
		try{
			this.material = Material.valueOf(material);
		} catch(IllegalArgumentException e){
			throw new InvalidItemException("unknown material: "+ material);
		}
		
		if(input.containsKey("attributes")){
			List<SerializableAttribute> attributes = (List<SerializableAttribute>) input.get("attributes");
			for(SerializableAttribute attr: attributes) this.attributes.add(attr.getAttribute());
		}
		
		if(input.containsKey("skills")){
			Map<String,Integer> skillSection  = (Map<String, Integer>) input.get("skills");
			
			for(String skillDisplayName:skillSection.keySet()){
				int currentLevel = skillSection.get(skillDisplayName);
				Skill skill = KRPG.getInstance().getSkillManager().getSkill(skillDisplayName, currentLevel);
				if(skill == null){
					KRPG.getInstance().getLogger().warning("Error at loading item "+this.customName+": skill "+skillDisplayName+" (level "+currentLevel+") not found");
					continue;
				}
				this.addSkill(skill);
			}
//			this.skills = (List<Skill>) input.get("skills");
		}
		
		if(input.containsKey("enchantments")){
			this.enchantments = (Map<Enchantment,Integer>) input.get("enchantments");
		}
	}
	
	public void addEnchantment(Enchantment ench, int level){
		if(ench == null || level < 1) return;
		enchantments.put(ench, level);
	}
	
	public void addEnchantments(Map<Enchantment, Integer> enchantments){
		if(enchantments == null || enchantments.isEmpty()) return;
		enchantments.putAll(enchantments);
	}
	
	//////////////////////////
	//  SKILLS
	//////////////////////////
	
	
	/**
	 * @param skill
	 * @return false if it does not use a known skill trigger
	 */
	public boolean addSkill(Skill skill){
		KRPG.getInstance().getLogger().info("Adding skill "+skill.getIdentifier()+" with level "+skill.getCurrentLevel()+" to item "+this.customName);
		if(this.skills.contains(skill)) return false;

		skill.setKRPGItem(this);
		return this.skills.add(skill);
	}
	
	/**
	 * gets the formatted info of the item ready to be displayed in chat
	 * @return
	 */
	public String[] getInfo(){
		List<String> text = new ArrayList<String>();
		
		text.add(ItemGenerator.LABEL_COLOR+"Name: "+ItemGenerator.VALUE_COLOR+this.getIdentifier());
		if(this.requiredLevel > 0)text.add(ItemGenerator.LABEL_COLOR+"Required Level: "+ItemGenerator.VALUE_COLOR+this.requiredLevel);
		if(!this.damageType.equals(ElementalDamageType.NONE))
			text.add(ItemGenerator.LABEL_COLOR+"Element: "+ItemGenerator.VALUE_COLOR+this.damageType.toString());
		

		text.add(ItemGenerator.LABEL_COLOR+"=== Skills ===");
		for(Skill skill: skills){
			text.add(ItemGenerator.VALUE_COLOR+skill.getIdentifier() +" "+ KRPGUtils.arabicToRoman(skill.getCurrentLevel()));
		} 
		if(skills.size() < 1){
			text.add(ChatColor.GRAY+"No Skills");
		}
		
		return text.toArray(new String[0]);
	}

	@Override
	public List<String> getLore() {
		return ItemGenerator.getCalculatedLore(this);
	}
	
	
	
	@Override
	public ItemStack getItem(){
		ItemStack item = new ItemStack(material);
		if(enchantments!= null && enchantments.size() > 0) 
			item.addUnsafeEnchantments(enchantments);
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(getIdentifier());
		itemMeta.setLore(getLore());
		item.setItemMeta(itemMeta);
		
		return applyAttributes(item);
//		return item;
	}

	public ItemStack applyAttributes(ItemStack item){
		if(this.attributes == null || this.attributes.isEmpty())
			return item;
		
		Attributes attributes = new Attributes(item);
		for(Attribute attribute:this.attributes){
			attributes.add(attribute);
		}
		return attributes.getStack();
	}
	

	@Override
	public boolean equals(ItemStack item) {
		if (item == null)
			return false;		
		if (!material.equals(item.getType()))
			return false;
		if (!item.hasItemMeta())
			return false;
		if (!customName.equals(item.getItemMeta().getDisplayName()))
			return false;
		return true;
	}

	@Override
	public String getIdentifier() {
		return rarity.getColor()+customName;
	}
	
	public String getCustomName() {
		return customName;
	}

	public void setCustomName(String customName) {
		this.customName = customName;
	}

	/**
	 * @return the rarity
	 */
	public ItemRarity getRarity() {
		return rarity;
	}
	/**
	 * @return the requiredLevel
	 */
	public int getRequiredLevel() {
		return requiredLevel;
	}

	/**
	 * @return the attributes
	 */
	public List<Attribute> getAttributes() {
		return attributes;
	}

	/**
	 * @param requiredLevel the requiredLevel to set
	 */
	public void setRequiredLevel(int requiredLevel) {
		this.requiredLevel = requiredLevel;
	}

	/**
	 * @return the material
	 */
	public Material getMaterial() {
		return material;
	}
	/**
	 * @return the damageType
	 */
	public ElementalDamageType getDamageType() {
		return damageType;
	}

	/**
	 * @param damageType the damageType to set
	 */
	public void setDamageType(ElementalDamageType damageType) {
		this.damageType = damageType;
	}

//	/**
//	 * @return the description
//	 */
//	public List<String> getDescription() {
//		return description;
//	}
	/**
	 * @param rarity the rarity to set
	 */
	public void setRarity(ItemRarity rarity) {
		this.rarity = rarity;
	}
	/**
	 * @param material the material to set
	 */
	public void setMaterial(Material material) {
		this.material = material;
	}
/**
	 * @return the enchantments
	 */
	public Map<Enchantment, Integer> getEnchantments() {
		return enchantments;
	}

	//	/**
//	 * @param description the description to set
//	 */
//	public void setDescription(List<String> description) {
//		this.description = description;
//	}
	/**
	 * @param lore the lore to set
	 */
	public void setLore(List<String> lore) {
		this.lore = lore;
	}
	
	public List<Skill> getSkills(){
		return skills;
	}
	
//	/**
//	 * Add a nbt attribute for the item
//	 * @param type
//	 * @param value
//	 */
//	public void addAttribute(@Nonnull String name, @Nonnull AttributeType type, double value){
//		attributes.add(Attribute.newBuilder().name(name).type(type).amount(value).build());
//	}
	
	public void addAttribute(@Nonnull Attribute attribute){
		attributes.add(attribute);
	}
	
	/**
	 * takes a string list (like lore) and formats it by setting a maximum width
	 * @param texts
	 * @param wrapLength maximum width
	 * @param formattingCodes color added to the beginning of each line (acknowledged in width)
	 * @return
	 */
	public static List<String> formatStringList(List<String> texts, int wrapLength, String formattingCodes){
		if(formattingCodes != null) wrapLength -= formattingCodes.length();
		List<String> output = new ArrayList<String>();
		for(String line:texts) {
			for(String wrappedLine:WordUtils.wrap(line, wrapLength, "\n", true).split("\n")){
				output.add(formattingCodes + ChatColor.translateAlternateColorCodes('&', wrappedLine));
			}
		}
		return output;
	}
	
	
	/**
	 * <b>Override!</b>
	 * serialize to map, ready for yaml configurations.
	 * @return
	 */
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> output = new LinkedHashMap<String,Object>();
		output.put("customName", this.customName);
		output.put("rarity", this.rarity.name());
		output.put("damageType", this.damageType.name());
		output.put("requiredLevel", this.requiredLevel);
//		output.put("description", description);
		output.put("material", material.name());
		output.put("enchantments", enchantments);
		output.put("lore", lore);
		List<SerializableAttribute> attrs = new ArrayList<SerializableAttribute>();
		for(Attribute attr: attributes){
			attrs.add(new SerializableAttribute(attr));
		}
		output.put("attributes", attrs);		
		
		output.put("skills", this.skills);
		return output;
	}
}
