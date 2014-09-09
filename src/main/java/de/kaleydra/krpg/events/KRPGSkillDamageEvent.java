package de.kaleydra.krpg.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.kaleydra.krpg.items.ElementalDamageType;
import de.kaleydra.krpg.player.Adventurer;
import de.kaleydra.krpg.skills.Skill;

public class KRPGSkillDamageEvent extends Event implements Cancellable {
	private static HandlerList handlers = new HandlerList();

	Adventurer damaged;
	Adventurer damager;
	ElementalDamageType elementalDamageType;
	double damage;
	Skill skill;

	boolean canceled = false;

	public KRPGSkillDamageEvent(Adventurer adventurer, ElementalDamageType elementalDamageType, double damage,
			Skill skill) {
		this.damaged = adventurer;
		this.elementalDamageType = elementalDamageType;
		this.damage = damage;
		this.skill = skill;
	}

	public Adventurer getDamager() {
		return damager;
	}

	public Adventurer getDamaged() {
		return damaged;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	/**
	 * @return the elementalDamageType
	 */
	public ElementalDamageType getElementalDamageType() {
		return elementalDamageType;
	}

	/**
	 * @return the damage
	 */
	public double getDamage() {
		return damage;
	}

	/**
	 * @return the skill
	 */
	public Skill getSkill() {
		return skill;
	}

	/**
	 * @param elementalDamageType
	 *            the elementalDamageType to set
	 */
	public void setElementalDamageType(ElementalDamageType elementalDamageType) {
		this.elementalDamageType = elementalDamageType;
	}

	/**
	 * @param damage
	 *            the damage to set
	 */
	public void setDamage(double damage) {
		this.damage = damage;
	}

	@Override
	public boolean isCancelled() {
		return canceled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.canceled = cancel;
	}

}
