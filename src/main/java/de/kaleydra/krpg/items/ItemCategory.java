package de.kaleydra.krpg.items;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;


public enum ItemCategory{
	ARMORS(Material.LEATHER_BOOTS, Material.LEATHER_LEGGINGS,Material.LEATHER_CHESTPLATE, Material.LEATHER_HELMET,
			Material.IRON_BOOTS, Material.IRON_LEGGINGS, Material.IRON_CHESTPLATE, Material.IRON_HELMET,
			Material.GOLD_BOOTS, Material.GOLD_LEGGINGS, Material.GOLD_CHESTPLATE, Material.GOLD_HELMET,
			Material.DIAMOND_BOOTS, Material.DIAMOND_LEGGINGS, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_HELMET),
	SWORDS(Material.WOOD_SWORD, Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD,
			Material.WOOD_AXE,Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE),
	BOWS(Material.BOW),
	TOOLS(Material.WOOD_AXE, Material.WOOD_SPADE, Material.WOOD_PICKAXE, Material.WOOD_HOE,
			Material.IRON_AXE, Material.IRON_SPADE, Material.IRON_PICKAXE, Material.IRON_HOE,
			Material.GOLD_AXE, Material.GOLD_SPADE, Material.GOLD_PICKAXE, Material.GOLD_HOE,
			Material.DIAMOND_AXE, Material.DIAMOND_SPADE, Material.DIAMOND_PICKAXE, Material.DIAMOND_HOE),
	AXES(Material.WOOD_AXE, Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE),
	PICKAXES(Material.WOOD_PICKAXE, Material.IRON_PICKAXE, Material.GOLD_PICKAXE, Material.DIAMOND_PICKAXE),
	SPADES(Material.WOOD_SPADE, Material.IRON_SPADE, Material.GOLD_SPADE, Material.DIAMOND_SPADE),
	HOES(Material.WOOD_HOE, Material.IRON_HOE, Material.GOLD_HOE, Material.IRON_HOE, Material.DIAMOND_HOE),
	FISHING_RODS(Material.FISHING_ROD);
	
	
	private Set<Material> materials;
	
	private ItemCategory(Material... materials){
		this.materials = new HashSet<Material>(materials.length);
		for(Material material:materials) this.materials.add(material);
	}
	
	public boolean contains(Material material){
		return materials.contains(material);
	}
	
	public Set<Material> getMaterials(){
		return materials;
	}
}
