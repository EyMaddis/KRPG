package de.kaleydra.krpg.skills;

import javax.annotation.Nonnull;

import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import com.kaleydra.licetia.api.Identifiable;

import de.kaleydra.krpg.items.KRPGItem;

/**
 * Base class for skills.
 * Override the methods your skill needs
 * 
 * it must implement the default constructor and {@link SkillDeclaration}
 * @author MTN
 *
 */
public abstract class Skill implements Cloneable, Identifiable {
	
	private String identifier;
	protected SkillSettings settings;
	
	private int currentLevel;
//	int maxLevel = 1;

	protected KRPGItem krpgItem;
	private ItemStack item;

	/**
	 * will get called once the skill loads!
	 * <b>Note that not all information are avaiable at this point, for example the item!</b>
	 * @param settings
	 */
	public Skill(SkillSettings settings) { // needed for first instantiation at skill jar loading
//		maxLevel = getClass().getAnnotation(SkillDeclaration.class).maxLevel();
	}
	
//	/**
//	 * @return the currentLevel
//	 */
	public int getCurrentLevel() {
		return currentLevel;
	}
	protected void setCurrentLevel(int currentLevel){
		this.currentLevel = currentLevel;
	}

//	/**
//	 * @return the maxLevel
//	 */
//	public int getMaxLevel(){
//		return maxLevel;
//	}
	
	/**
	 * get the settings of this skill, it will automatically check either the skill.yml file
	 * within your skill .jar or the overriden settings from the skills.yml in the folder of the plugin
	 * @return
	 */
	public SkillSettings getSkillSettings(){
		return settings;
	}
	
	/**
	 * <b>Only available once the skill gets applied, not while it loads!</b>
	 * @return the KRPGItem that triggered the skill
	 */
	public KRPGItem getKRPGItem(){
		return krpgItem;
	}
	
	public void setKRPGItem(@Nonnull KRPGItem krpgItem){
		this.krpgItem = krpgItem;
	}
	
	/**
	 * <b>Only available once the skill gets applied, not while it loads!</b>
	 * @return the ItemStack that triggered the skill
	 */
	public ItemStack getItem(){
		return item;
	}
	
	public void setItem(@Nonnull ItemStack item){
		this.item = item;
	}
	
//	public String getCurrentDisplayName(){
//		return getIdentifier();
//	}
//	
//	/**
//	 * <b>Do not use!</b> Only used in the loading process of loading
//	 * @param displayName
//	 */
//	public void setCurrentDisplayName(String displayName){
//		this.identifier = displayName;
//	}
	
	@Override
	public String getIdentifier() {
		return identifier;
	}
	
	protected void setIdentifier(String identifier){
		this.identifier = identifier;
	}

//	public void onSkillAttack(KRPGSkillDamageEvent event) {
//		
//	}
	
	public void onMeleeAttack(EntityDamageByEntityEvent event) {
		
	}
	
//	public void onSkillDamage(KRPGSkillDamageEvent event) {
//		
//	}

	public void onDamage(EntityDamageEvent event) {
		
	}
	
	public void onProjectileDamage(EntityDamageByEntityEvent event) {
		
	}
	
	public void onProjectileLaunch(ProjectileLaunchEvent event) {
		
	}
	
//	public void onProjectileHit(ProjectileHitEvent event) {
//		
//	}

	public void onConsume(PlayerItemConsumeEvent event) {
		
	}

	public void onDeath(PlayerDeathEvent event) {
		
	}

	public void onItemInHand(PlayerItemHeldEvent event) {
		
	}

	public void onItemOutOfHand(PlayerItemHeldEvent event) {
		
	}

	public void onRightClick(PlayerInteractEvent event) {
		
	}

	public void onLeftClick(PlayerInteractEvent event) {
		
	}
	
	public void onRespawn(PlayerRespawnEvent event){
		
	}
	
	public void onFish(PlayerFishEvent event){
		
	}

	public Skill clone(){  
	    try{  
	        return (Skill) super.clone();  
	    }catch(Exception e){ 
	        return null; 
	    }
	}
}
