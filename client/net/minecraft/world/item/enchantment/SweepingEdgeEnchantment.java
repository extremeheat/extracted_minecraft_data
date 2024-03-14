package net.minecraft.world.item.enchantment;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;

public class SweepingEdgeEnchantment extends Enchantment {
   public SweepingEdgeEnchantment(Enchantment.Rarity var1, EquipmentSlot... var2) {
      super(var1, ItemTags.SWORD_ENCHANTABLE, var2);
   }

   @Override
   public int getMinCost(int var1) {
      return 5 + (var1 - 1) * 9;
   }

   @Override
   public int getMaxCost(int var1) {
      return this.getMinCost(var1) + 15;
   }

   @Override
   public int getMaxLevel() {
      return 3;
   }

   public static float getSweepingDamageRatio(int var0) {
      return 1.0F - 1.0F / (float)(var0 + 1);
   }
}
