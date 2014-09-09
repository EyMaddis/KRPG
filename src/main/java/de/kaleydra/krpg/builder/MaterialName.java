package de.kaleydra.krpg.builder;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.SerializableAs;

import com.kaleydra.licetia.api.SerializableManager;

import de.kaleydra.krpg.KRPG;

@SerializableAs("KRPGMaterialName")
public class MaterialName extends NamePart {
	public static SerializableManager<MaterialName> allMaterials = new SerializableManager<MaterialName>();

	Tier tier;
	Material material;
	
	public MaterialName(Tier tier, Material material, String name, ItemModifier itemModifier) {
		super(name, itemModifier);
		this.tier = tier;
		this.material = material;
	}
	
	public MaterialName(Map<String,Object> input){
		super(input);
		String tierString = input.get("tier").toString();
		this.tier = Tier.allTiers.get(tierString);
		if(tier == null) KRPG.logSevere("missing or invalid tier at "+getText());

		String materialString = input.get("material").toString();
		try{
			this.material = Material.valueOf(materialString);
		} catch(IllegalArgumentException e){
			KRPG.logSevere("missing or invalid material: "+materialString);
		}
	}

	/**
	 * @return the tier
	 */
	public Tier getTier() {
		return tier;
	}

	/**
	 * @return the material
	 */
	public Material getMaterial() {
		return material;
	}
	
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> output = super.serialize();
		output.put("tier", tier.getText());
		output.put("material", material.name());
		return output;
	}

}
