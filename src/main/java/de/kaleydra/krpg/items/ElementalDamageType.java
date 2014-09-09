package de.kaleydra.krpg.items;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import de.kaleydra.krpg.KRPG;

/**
 * The damages weapons and skills can deal
 *
 */
public enum ElementalDamageType {

	ARCANE(DamageCause.MAGIC),
	WITHER(DamageCause.WITHER),
	
	EARTH(DamageCause.SUFFOCATION),
	AIR(DamageCause.FALL),
	
	WATER(DamageCause.DROWNING),
	FIRE(DamageCause.FIRE),
	NONE(DamageCause.CUSTOM);
//	ICE(DamageCause.CONTACT),
	
	public static double weaknessMultiplier;
			
	public DamageCause bukkitDamage;
	/**
	 * @return the bukkitDamage
	 */
	public DamageCause getBukkitDamage() {
		return bukkitDamage;
	}


	public ElementalDamageType weakness;
	
    ElementalDamageType(DamageCause bukkitDamage){
		this.bukkitDamage = bukkitDamage;
	}
    
    public static ElementalDamageType getByBukkitDamage(DamageCause bukkitDamage){
    	if(bukkitDamage == null || bukkitDamage.equals(DamageCause.CUSTOM));
    	for(ElementalDamageType type: values()){
    		if(type.getBukkitDamage().equals(bukkitDamage)) {
    			return type;
    		}
    	}
    	return null;    	
    }
    
    /**
     * get the DamageType that deals extra damage.
     * <ul>
     * 	<li>Arcane <-> Wither</li>
     * 	<li>Earth <-> Air</li>
     * 	<li>Fire <-> Water</li>
     * </ul>
     * @return
     */
    public ElementalDamageType getWeakness(){
    	switch(this){
    	case ARCANE:
    		return WITHER;
    	case WITHER:
    		return ARCANE;
    		
    	case EARTH:
    		return AIR;
    	case AIR:
    		return EARTH;
    	
    	case WATER:
    		return FIRE;
    	case FIRE:
    		return WATER;
    		
		default: // none
	    	return NONE;
    	}
    }


    /**
     * is a ElementalDamage weak against another?.
     * <ul>
     * 	<li>Arcane <-> Wither</li>
     * 	<li>Earth <-> Air</li>
     * 	<li>Fire <-> Water</li>
     * </ul>
     * @return
     */
    public boolean isWeakness(ElementalDamageType damageType){
    	final ElementalDamageType weakness = getWeakness();
    	if(weakness.equals(NONE)) return false;
		return weakness.equals(damageType);
    }

    
    public double calcDamage(double damageInput, ElementalDamageType damageInputType){    	
    	if(isWeakness(damageInputType)){
    		return damageInput*weaknessMultiplier;
    	}
    	return damageInput;    	
    }
    
    public static double calcArmorDamage(double damageInput, ElementalDamageType damageInputType, Player player){
    	if(damageInputType.equals(NONE)){
    		return damageInput;
    	}
//    	ElementalDamageType weakness = damageInputType.getWeakness();    	
    	
    	ItemManager itemManager = KRPG.getInstance().getItemManager();
    	
    	int equalCount = 0;
    	int weaknessCount = 0;
    	KRPGItem krpgItem;
    	ElementalDamageType armorType;
    	for(ItemStack armor : player.getInventory().getArmorContents()){
    		krpgItem = itemManager.get(armor);
    		if(krpgItem == null) continue;
    		armorType = krpgItem.getDamageType();
    		if(armorType.isWeakness(damageInputType)){
    			weaknessCount++;
    		} else if(damageInputType.equals(armorType)){
    			equalCount++;
    		}
    	}
    	
    	// from -1.0 to 1.0 (percent)
    	double damageModifier = getDamageModifierPerAmor(weaknessCount);
    	damageModifier -= getDamageModifierPerAmor(equalCount);
    	
    	final double calculated = weaknessMultiplier * damageModifier;
    	
//    	KRPG.logInfo("Calculated damage mod: "+calculated+" final:" + damageInput+calculated);
		return damageInput - calculated;
    }

	private static double getDamageModifierPerAmor(int armorTypeCounter) {
		double damageModifier = 0.0;
		switch(armorTypeCounter){
    	case 1:
    		damageModifier = 0.2;    		
    		break;
    	case 2:
    		damageModifier = 0.4;    		
    		break;
    	case 3:
    		damageModifier = 0.65;    		
    		break;
    	case 4:
    		damageModifier = 1.0;    		
    		break;
		default: break;		
    	}
		return damageModifier;
	}
    

}
