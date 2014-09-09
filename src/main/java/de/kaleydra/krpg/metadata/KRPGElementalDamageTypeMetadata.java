package de.kaleydra.krpg.metadata;

import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import de.kaleydra.krpg.KRPG;
import de.kaleydra.krpg.items.ElementalDamageType;

public class KRPGElementalDamageTypeMetadata implements MetadataValue {

	public static final String KEY = "krpg_projectile_damage";
	
	ElementalDamageType damageType;
	
	public KRPGElementalDamageTypeMetadata(ElementalDamageType damageType) {
		this.damageType = damageType;
	}

	/**
	 * @return the damageType
	 */
	public ElementalDamageType getDamageType() {
		return damageType;
	}

	@Override
	public boolean asBoolean() {
		return false;
	}

	@Override
	public byte asByte() {
		return 0;
	}

	@Override
	public double asDouble() {
		return 0;
	}

	@Override
	public float asFloat() {
		return 0;
	}

	@Override
	public int asInt() {
		return 0;
	}

	@Override
	public long asLong() {
		return 0;
	}

	@Override
	public short asShort() {
		return 0;
	}

	@Override
	public String asString() {
		return damageType.toString();
	}

	@Override
	public Plugin getOwningPlugin() {
		// TODO Auto-generated method stub
		return KRPG.getInstance();
	}

	@Override
	public void invalidate() {

	}

	@Override
	public Object value() {
		return damageType;
	}

}
