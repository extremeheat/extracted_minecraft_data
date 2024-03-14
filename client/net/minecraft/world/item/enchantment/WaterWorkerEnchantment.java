package net.minecraft.world.item.enchantment;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;

public class WaterWorkerEnchantment extends Enchantment {
   public WaterWorkerEnchantment(Enchantment.Rarity var1, EquipmentSlot... var2) {
      super(var1, ItemTags.HEAD_ARMOR_ENCHANTABLE, var2);
   }

   @Override
   public int getMinCost(int var1) {
      return 1;
   }

   @Override
   public int getMaxCost(int var1) {
      return this.getMinCost(var1) + 40;
   }
}
