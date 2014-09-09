package de.kaleydra.krpg.player;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.kaleydra.licetia.api.Identifiable;

import de.kaleydra.krpg.events.KRPGSkillDamageEvent;
import de.kaleydra.krpg.items.ElementalDamageType;
import de.kaleydra.krpg.skills.Skill;

public class Adventurer implements Identifiable {

	/** index is level-1 (lvl 1 is at 0) */
	public final static long[] levelByExp = new long[50];
	
	static{ // precalculate the levels
		
		int expIncrease = 50;
		long expNeeded = 0;
		for(int i = 0; i < 50; i++){
			levelByExp[i] = expNeeded;
			if(i >= 10) expIncrease += 10;
			expNeeded += expIncrease;
		}
	}
	
	private String player;
	private long exp;
	
	/** the calculated level, use updateLevel() */
	//	private int level;
	
	
	public Adventurer(@Nonnull Player player, @Nonnegative long exp) {
		this.player = player.getName();
		this.exp = exp;
	}
	
	public Adventurer(Map<String,Object> input){
		this.player = input.get("player").toString();
		this.exp = (long)input.get("exp");
	}
	
	public Map<String, Object> serialize() {
		Map<String,Object> output = new LinkedHashMap<String,Object>();
		output.put("player", player);
		output.put("exp", exp);
		
		return output;
	}



	@Override
	public String getIdentifier() {
		return player;
	}
	
	public int getLevel(){
		return getLevelByExp(exp);
	}
	
	/**
	 * returns the level, beginning at 1 until 50
	 * @param exp
	 * @return
	 */
	public static int getLevelByExp(long exp){
		// TODO: custom binary search
		for(int i=0; i < levelByExp.length; i++){
			if(exp < levelByExp[i]) return i;
		}
		return -1;
	}
	
	public Player getPlayer(){
		return Bukkit.getPlayerExact(player);
	}
	
	public void damage(double damage, ElementalDamageType damageType, Skill skill, Adventurer damager){
		if(player == null) return;
		
		KRPGSkillDamageEvent event = new KRPGSkillDamageEvent(this, damageType, damage, null);
		Bukkit.getServer().getPluginManager().callEvent(event);
		if(event.isCancelled()) return;
		Player player = getPlayer();
		player.damage(damage, damager.getPlayer());
	}
	
}
