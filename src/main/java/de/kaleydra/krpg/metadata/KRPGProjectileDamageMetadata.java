package de.kaleydra.krpg.metadata;

import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public class KRPGProjectileDamageMetadata implements MetadataValue {
	
	public final static String KEY = "krpg_projectile_damage";
	
	double damage;

	public KRPGProjectileDamageMetadata(double damage){
		this.damage = damage;
	}
	
	@Override
	public boolean asBoolean() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public byte asByte() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double asDouble() {
		return damage;
	}

	@Override
	public float asFloat() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int asInt() {
		return Math.round((float)damage);
	}

	@Override
	public long asLong() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public short asShort() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String asString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Plugin getOwningPlugin() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void invalidate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object value() {
		// TODO Auto-generated method stub
		return null;
	}

}
