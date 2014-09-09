package de.kaleydra.krpg.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.kaleydra.licetia.wrappers.SerializablePlayerInventory;

import de.kaleydra.krpg.KRPG;
import de.kaleydra.krpg.KRPGUtils;
import de.kaleydra.krpg.items.KRPGItem;
import de.kaleydra.krpg.skills.Skill;

public class PlayerListener implements Listener {

	KRPG plugin;

	public PlayerListener(KRPG plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(ignoreCancelled=false)
	public void onInteract(PlayerInteractEvent event){
		if(event.getAction().equals(Action.PHYSICAL)) return;

		KRPGItem krpgItem = plugin.getItemManager().get(event.getItem());
		
		if(krpgItem == null) return;
		
		if(event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK))
		{
			for(Skill skill:krpgItem.getSkills()) {
				Skill skillCopy = skill.clone();
				skillCopy.setKRPGItem(krpgItem);
				skillCopy.onLeftClick(event);
			}
		} else // right click
		{
			for(Skill skill:krpgItem.getSkills()) {
				Skill skillCopy = skill.clone();
				skillCopy.setKRPGItem(krpgItem);
				skillCopy.onRightClick(event);
			}
		}
	}
	
	@EventHandler
	public void onConsume(PlayerItemConsumeEvent event){
		KRPGItem krpgItem = plugin.getItemManager().get(event.getItem());
		if(krpgItem == null) return;

		for(Skill skill:krpgItem.getSkills()) {
			Skill skillCopy = skill.clone();
			skillCopy.setKRPGItem(krpgItem);
			skillCopy.onConsume(event);
		}
	}

	
//	@EventHandler
//	public void onSkillDamage(KRPGSkillDamageEvent event){
//
//		Player damaged = event.getDamaged().getPlayer();
//		Player damager = event.getDamager().getPlayer();
//		
//		// damaged
//		KRPGItem krpgItem = plugin.getItemManager().get(damaged.getItemInHand());
//		if(krpgItem != null) 
//			for(Skill skill:krpgItem.getSkills()) {
//				Skill skillCopy = skill.clone();
//				skillCopy.setKRPGItem(krpgItem);
//				skillCopy.onSkillAttack(event);
//			}
//		
//		// damager
//		krpgItem = plugin.getItemManager().get(damager.getItemInHand());
//		if(krpgItem != null) 
//			for(Skill skill:krpgItem.getSkills()) {
//				Skill skillCopy = skill.clone();
//				skillCopy.setKRPGItem(krpgItem);
//				skillCopy.onSkillAttack(event);
//			}
//	}
	
	@EventHandler
	public void onSpawn(PlayerRespawnEvent event){
		Map<String,SerializablePlayerInventory> tempDrops = plugin.getTempDrops();
		Player player = event.getPlayer();
		if(tempDrops.containsKey(player.getName())){
			if(player.getWorld().getGameRuleValue("keepInventory").equals("true")){
				KRPG.logInfo(player.getName()+" is in a keepInventory world!");
			} else {
				tempDrops.get(player.getName()).applyInventory(player);
				tempDrops.remove(player.getName());
			}
		}
		
		// skills
		KRPGItem krpgItem = plugin.getItemManager().get(player.getItemInHand());
		if(krpgItem == null) return;

		for(Skill skill:krpgItem.getSkills()) {
			Skill skillCopy = skill.clone();
			skillCopy.setKRPGItem(krpgItem);
			skillCopy.onRespawn(event);
		}
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event){
		
		// selective death drops
		List<ItemStack> drops = event.getDrops();
		ItemStack item;
		Map<String,SerializablePlayerInventory> tempDrops = plugin.getTempDrops();
		
		Player player = event.getEntity();
		
		final PlayerInventory inventory = player.getInventory();
		final ItemStack[] invContents = inventory.getContents();
		ItemStack[] invSave = new ItemStack[invContents.length];
		ItemStack currentItem;
		final boolean inCreative = player.getGameMode().equals(GameMode.CREATIVE);
		
		// main inventory
		for(int i = 0; i < invContents.length; i++){
			currentItem = invContents[i];
			if(inCreative || KRPGUtils.shouldSaveItemOnRespawn(currentItem)) {
				invSave[i] = currentItem;
			} else invSave[i] = null;
		}
		
		// armor inventory
		final ItemStack[] armorContents = inventory.getArmorContents();
		ItemStack[] armorSave = new ItemStack[armorContents.length];
		for(int i = 0; i < armorContents.length; i++){
			currentItem = armorContents[i];
			if(inCreative || KRPGUtils.shouldSaveItemOnRespawn(currentItem)) {
				armorSave[i] = currentItem;
			} else armorSave[i] = null;
		}
		
		SerializablePlayerInventory newInv = new SerializablePlayerInventory(inventory);
		newInv.setContents(invSave);
		newInv.setArmorContents(armorSave);
		tempDrops.put(player.getName(), newInv);
		
		List<ItemStack> allowedItems = new ArrayList<ItemStack>(drops.size());
		
		// do not drop special items
		for(int i = 0; i < drops.size(); i++){
			item = drops.get(i);		
			// ignore non-special items and clear deaths of creative players
			if(inCreative || KRPGUtils.shouldSaveItemOnRespawn(item)){
//				KRPG.logInfo("removed: "+ item);
				drops.remove(i); // do not drop it!
			} else {
				allowedItems.add(item);
			}
		}
		drops.clear();
		drops.addAll(allowedItems);
		
		// item skills
		KRPGItem krpgItem = plugin.getItemManager().get(event.getEntity().getItemInHand());
		if(krpgItem == null) return;

		for(Skill skill:krpgItem.getSkills()) {
			Skill skillCopy = skill.clone();
			skillCopy.setKRPGItem(krpgItem);
			skillCopy.onDeath(event);		
		}
	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void onItemHeld(PlayerItemHeldEvent event){
		Player player = event.getPlayer();
		ItemStack item = player.getInventory().getItem(event.getPreviousSlot());
		KRPGItem krpgItem = plugin.getItemManager().get(item);
		if(krpgItem != null) {

			for(Skill skill:krpgItem.getSkills()) skill.onItemOutOfHand(event);
		}

		item = player.getInventory().getItem(event.getNewSlot());
		krpgItem = plugin.getItemManager().get(item);

		if(krpgItem != null) {

			for(Skill skill:krpgItem.getSkills()) {
				Skill skillCopy = skill.clone();
				skillCopy.setKRPGItem(krpgItem);
				skillCopy.onItemInHand(event);
			}
		}
	}
	
	
	@EventHandler
	public void onFish(PlayerFishEvent event){
		final Player player = event.getPlayer();
		
		ItemStack item = player.getInventory().getItemInHand();
		KRPGItem krpgItem = plugin.getItemManager().get(item);
		if(krpgItem == null) return;
		
		for(Skill skill:krpgItem.getSkills()) {
			Skill skillCopy = skill.clone();
			skillCopy.setKRPGItem(krpgItem);
			skillCopy.onFish(event);
		}
	}
}
