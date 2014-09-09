package de.kaleydra.krpg.builder;

import java.util.Map;

import org.bukkit.configuration.serialization.SerializableAs;

import com.kaleydra.licetia.api.SerializableManager;

import de.kaleydra.krpg.KRPG;
import de.kaleydra.krpg.items.ElementalDamageType;

@SerializableAs("KRPGTier")
public class Tier extends NamePart {

	public static SerializableManager<Tier> allTiers = new SerializableManager<Tier>();
	
	private ElementalDamageType damageType;

	public Tier(String name, ItemModifier itemModifier, ElementalDamageType damageType) {
		super(name, itemModifier);
		this.setDamageType(damageType);
	}
	
	public Tier(Map<String,Object> input){
		super(input);

		String damageType = input.get("damageType").toString();
		try{
			this.damageType = ElementalDamageType.valueOf(damageType);
		} catch(IllegalArgumentException e){
			KRPG.logSevere("missing or invalid material: "+damageType);
		}
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
	
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> output = super.serialize();
		output.put("damageType", damageType.name());
		return output;
	}
	
}
