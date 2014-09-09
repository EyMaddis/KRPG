package de.kaleydra.krpg.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import de.kaleydra.krpg.KRPG;
import de.kaleydra.krpg.items.ElementalDamageType;
import de.kaleydra.krpg.items.KRPGItem;
import de.kaleydra.krpg.metadata.KRPGElementalDamageTypeMetadata;
import de.kaleydra.krpg.skills.Skill;

public class DamageListener implements Listener {
	
	KRPG plugin;
	
	public DamageListener(KRPG plugin) {
		this.plugin = plugin;
	}
	

	@EventHandler(ignoreCancelled=true,priority=EventPriority.LOW)
	public void onDamage(EntityDamageEvent event){
		if(event.getCause().equals(DamageCause.CUSTOM)) return; // ignore skills
		if(event instanceof EntityDamageByEntityEvent) return;
		
		if(!(event.getEntity() instanceof Player)) return;
		Player player = (Player) event.getEntity();
		KRPGItem krpgItem = plugin.getItemManager().get(player.getItemInHand());
		
		ElementalDamageType elementalDamage = ElementalDamageType.NONE;
		
			
		// item in hand skills . TODO: Do not apply to armor in hand
		if(krpgItem != null) {
			elementalDamage = krpgItem.getDamageType();
			for(Skill skill:krpgItem.getSkills()) {
				Skill skillCopy = skill.clone();
				skillCopy.setKRPGItem(krpgItem);
				skillCopy.onDamage(event);
			}
		}
		
		// armor skills
		for(ItemStack item : player.getInventory().getArmorContents()){
			krpgItem = plugin.getItemManager().get(item);
			if(krpgItem == null) continue;
			for(Skill skill:krpgItem.getSkills()) {
				Skill skillCopy = skill.clone();
				skillCopy.setKRPGItem(krpgItem);
				skillCopy.onDamage(event);
			}			
		}
		
		// calculate and apply
		double newDamage = ElementalDamageType.calcArmorDamage(event.getDamage(), elementalDamage, player);
		event.setDamage(newDamage);
		
				
	}
	
	@EventHandler(ignoreCancelled=true,priority=EventPriority.LOW)
	public void onProjectileDamage(EntityDamageByEntityEvent event){
		if(event.getCause().equals(DamageCause.CUSTOM)) return; // ignore skills
		if(!(event.getEntity() instanceof Player)) return;
		if(!event.getCause().equals(DamageCause.PROJECTILE)) return;
		
		Player player = (Player) event.getEntity();
		
		ElementalDamageType elementalDamage = ElementalDamageType.NONE;
		
		Projectile projectile = (Projectile) event.getDamager();
		if(projectile.hasMetadata(KRPGElementalDamageTypeMetadata.KEY)){
			KRPGElementalDamageTypeMetadata metadata = 
					(KRPGElementalDamageTypeMetadata) projectile.getMetadata(KRPGElementalDamageTypeMetadata.KEY).get(0);
			elementalDamage = metadata.getDamageType();
		}
		

		KRPGItem krpgItem = plugin.getItemManager().get(player.getItemInHand());
		
		// item in hand skills . TODO: Do not apply to armor in hand
		if(krpgItem != null) {
			elementalDamage = krpgItem.getDamageType();
			for(Skill skill:krpgItem.getSkills()) {
				Skill skillCopy = skill.clone();
				skillCopy.setKRPGItem(krpgItem);
				skillCopy.onProjectileDamage(event);
			}
		}
		
		// armor skills
		for(ItemStack item : player.getInventory().getArmorContents()){
			krpgItem = plugin.getItemManager().get(item);
			if(krpgItem == null) continue;
			for(Skill skill:krpgItem.getSkills()) {
				Skill skillCopy = skill.clone();
				skillCopy.setKRPGItem(krpgItem);
				skillCopy.onProjectileDamage(event);
			}			
		}
		
		// calculate and apply
		double newDamage = ElementalDamageType.calcArmorDamage(event.getDamage(), elementalDamage, player);
		event.setDamage(newDamage);			
	}

	
	@EventHandler(ignoreCancelled=true,priority=EventPriority.LOW)
	public void onAttack(EntityDamageByEntityEvent event){
		if(event.getCause().equals(DamageCause.CUSTOM)) return; // ignore skills

		Entity damager = event.getDamager();		
		
		if(damager == null || !(damager instanceof Player)) return;
		Player player = (Player) damager;
		KRPGItem krpgItem = plugin.getItemManager().get(player.getItemInHand());
		if(krpgItem == null) return;

		for(Skill skill:krpgItem.getSkills()) {
			Skill skillCopy = skill.clone();
			skillCopy.setKRPGItem(krpgItem);
			skillCopy.onMeleeAttack(event);
		}	
		
	}
}
