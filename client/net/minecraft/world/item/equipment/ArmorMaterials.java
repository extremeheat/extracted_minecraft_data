package net.minecraft.world.item.equipment;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;

public interface ArmorMaterials {
   ArmorMaterial LEATHER = new ArmorMaterial(5, makeDefense(1, 2, 3, 1, 3), 15, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, 0.0F, ItemTags.REPAIRS_LEATHER_ARMOR, EquipmentAssets.LEATHER, 0.1F);
   ArmorMaterial CHAINMAIL = new ArmorMaterial(15, makeDefense(1, 4, 5, 2, 4), 12, SoundEvents.ARMOR_EQUIP_CHAIN, 0.0F, 0.0F, ItemTags.REPAIRS_CHAIN_ARMOR, EquipmentAssets.CHAINMAIL, 0.5F);
   ArmorMaterial IRON = new ArmorMaterial(15, makeDefense(2, 5, 6, 2, 5), 9, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, 0.0F, ItemTags.REPAIRS_IRON_ARMOR, EquipmentAssets.IRON, 1.0F);
   ArmorMaterial GOLD = new ArmorMaterial(7, makeDefense(1, 3, 5, 2, 7), 25, SoundEvents.ARMOR_EQUIP_GOLD, 0.0F, 0.0F, ItemTags.REPAIRS_GOLD_ARMOR, EquipmentAssets.GOLD, 1.0F);
   ArmorMaterial DIAMOND = new ArmorMaterial(33, makeDefense(3, 6, 8, 3, 11), 10, SoundEvents.ARMOR_EQUIP_DIAMOND, 2.0F, 0.0F, ItemTags.REPAIRS_DIAMOND_ARMOR, EquipmentAssets.DIAMOND, 1.5F);
   ArmorMaterial TURTLE_SCUTE = new ArmorMaterial(25, makeDefense(2, 5, 6, 2, 5), 9, SoundEvents.ARMOR_EQUIP_TURTLE, 0.0F, 0.0F, ItemTags.REPAIRS_TURTLE_HELMET, EquipmentAssets.TURTLE_SCUTE, 0.5F);
   ArmorMaterial NETHERITE = new ArmorMaterial(37, makeDefense(3, 6, 8, 3, 11), 15, SoundEvents.ARMOR_EQUIP_NETHERITE, 3.0F, 0.1F, ItemTags.REPAIRS_NETHERITE_ARMOR, EquipmentAssets.NETHERITE, 2.0F);
   ArmorMaterial ARMADILLO_SCUTE = new ArmorMaterial(4, makeDefense(3, 6, 8, 3, 11), 10, SoundEvents.ARMOR_EQUIP_WOLF, 0.0F, 0.0F, ItemTags.REPAIRS_WOLF_ARMOR, EquipmentAssets.ARMADILLO_SCUTE, 1.0F);

   private static Map<ArmorType, Integer> makeDefense(int var0, int var1, int var2, int var3, int var4) {
      return Maps.newEnumMap(Map.of(ArmorType.BOOTS, var0, ArmorType.LEGGINGS, var1, ArmorType.CHESTPLATE, var2, ArmorType.HELMET, var3, ArmorType.BODY, var4));
   }
}
