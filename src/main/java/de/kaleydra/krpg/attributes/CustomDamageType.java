package de.kaleydra.krpg.attributes;

public enum CustomDamageType {
	/**
	 * Strahlen und so
	 */
	ARKANE,
	UNKNOWN,
	

    /**
     * Damage caused when an entity contacts a block such as a Cactus.
     * <p>
     * Damage: 1 (Cactus)
     */
    CONTACT,
    /**
     * Damage caused when an entity attacks another entity.
     * <p>
     * Damage: variable
     */
    ENTITY_ATTACK,
    /**
     * Damage caused when attacked by a projectile.
     * <p>
     * Damage: variable
     */
    PROJECTILE,
    /**
     * Damage caused by being put in a block
     * <p>
     * Damage: 1
     */
    SUFFOCATION,
    /**
     * Damage caused when an entity falls a distance greater than 3 blocks
     * <p>
     * Damage: fall height - 3.0
     */
    FALL,
    /**
     * Damage caused by direct exposure to fire
     * <p>
     * Damage: 1
     */
    FIRE,
    /**
     * Damage caused due to burns caused by fire
     * <p>
     * Damage: 1
     */
    FIRE_TICK,
    /**
     * Damage caused due to a snowman melting
     * <p>
     * Damage: 1
     */
    MELTING,
    /**
     * Damage caused by direct exposure to lava
     * <p>
     * Damage: 4
     */
    LAVA,
    /**
     * Damage caused by running out of air while in water
     * <p>
     * Damage: 2
     */
    DROWNING,
    /**
     * Damage caused by being in the area when a block explodes.
     * <p>
     * Damage: variable
     */
    BLOCK_EXPLOSION,
    /**
     * Damage caused by being in the area when an entity, such as a
     * Creeper, explodes.
     * <p>
     * Damage: variable
     */
    ENTITY_EXPLOSION,
    /**
     * Damage caused by falling into the void
     * <p>
     * Damage: 4 for players
     */
    VOID,
    /**
     * Damage caused by being struck by lightning
     * <p>
     * Damage: 5
     */
    LIGHTNING,
    /**
     * Damage caused by committing suicide using the command "/kill"
     * <p>
     * Damage: 1000
     */
    SUICIDE,
    /**
     * Damage caused by starving due to having an empty hunger bar
     * <p>
     * Damage: 1
     */
    STARVATION,
    /**
     * Damage caused due to an ongoing poison effect
     * <p>
     * Damage: 1
     */
    POISON,
    /**
     * Damage caused by being hit by a damage potion or spell
     * <p>
     * Damage: variable
     */
    MAGIC,
    /**
     * Damage caused by Wither potion effect
     */
    WITHER,
    /**
     * Damage caused by being hit by a falling block which deals damage
     * <p>
     * <b>Note:</b> Not every block deals damage
     * <p>
     * Damage: variable
     */
    FALLING_BLOCK,
    /**
     * Damage caused in retaliation to another attack by the Thorns
     * enchantment.
     * <p>
     * Damage: 1-4 (Thorns)
     */
    THORNS;
	
//	String name;
	
//	CustomDamageType(String name){
//		this.name = name;
//	}
	
//	public String getName(){
//		return name;
//	}

}
