package de.kaleydra.krpg.builder;

import java.util.Map;

import org.bukkit.configuration.serialization.SerializableAs;

import com.kaleydra.licetia.api.SerializableManager;

@SerializableAs("KRPGSuffix")
public class Suffix extends NamePart {
	public static SerializableManager<Suffix> allSuffixes = new SerializableManager<Suffix>();

	public Suffix(String name, ItemModifier itemModifier) {
		super(name, itemModifier);
	}
	
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> output = super.serialize();
		return output;
	}

}
