package de.kaleydra.krpg.listener;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import com.kaleydra.licetia.listeners.CustomProjectileHelper;

import de.kaleydra.krpg.KRPG;
import de.kaleydra.krpg.items.KRPGItem;
import de.kaleydra.krpg.metadata.KRPGElementalDamageTypeMetadata;
import de.kaleydra.krpg.skills.Skill;

public class ProjectileListener implements Listener {

	KRPG plugin;

	public ProjectileListener(KRPG plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onProjectileLaunch(ProjectileLaunchEvent event) {
		Projectile projectile = event.getEntity();
		if (projectile == null)
			return;
		LivingEntity shooter = projectile.getShooter();
		if (shooter == null)
			return;

		KRPGItem krpgItem = plugin.getItemManager().get(shooter.getEquipment().getItemInHand());
		if (krpgItem == null)
			return;
		KRPGElementalDamageTypeMetadata metadata = new KRPGElementalDamageTypeMetadata(krpgItem.getDamageType());
		projectile.setMetadata(KRPGElementalDamageTypeMetadata.KEY, metadata);
		if(CustomProjectileHelper.isCustomProjectile(projectile)) return;
		for (Skill skill : krpgItem.getSkills()) {
			Skill skillCopy = skill.clone();
			skillCopy.setKRPGItem(krpgItem);
			skillCopy.onProjectileLaunch(event);
		}
	}

	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) { //TODO
//		Projectile projectile = event.getEntity();
//		if (projectile == null)
//			return;
//		LivingEntity shooter = projectile.getShooter();
//		if (shooter == null)
//			return;
//
//		if(!projectile.hasMetadata(KRPGElementalDamageTypeMetadata.KEY)) return;
//		
////		KRPGItem krpgItem = plugin.getItemManager().get(shooter.getEquipment().getItemInHand());
//		if (krpgItem == null)
//			return;
//		KRPGElementalDamageTypeMetadata metadata = new KRPGElementalDamageTypeMetadata(krpgItem.getDamageType());
//		projectile.setMetadata(KRPGElementalDamageTypeMetadata.KEY, metadata);
//
//		for (Skill skill : krpgItem.getSkills()) {
//			Skill skillCopy = skill.clone();
//			skillCopy.setKRPGItem(krpgItem);
//			skillCopy.onProjectileLaunch(event);
//		}
	}
}
