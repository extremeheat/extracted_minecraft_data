package net.minecraft.world.item.enchantment;

import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

public class DigDurabilityEnchantment extends Enchantment {
   protected DigDurabilityEnchantment(Enchantment.Rarity var1, EquipmentSlot... var2) {
      super(var1, ItemTags.DURABILITY_ENCHANTABLE, var2);
   }

   @Override
   public int getMinCost(int var1) {
      return 5 + (var1 - 1) * 8;
   }

   @Override
   public int getMaxCost(int var1) {
      return super.getMinCost(var1) + 50;
   }

   @Override
   public int getMaxLevel() {
      return 3;
   }

   @Override
   public boolean canEnchant(ItemStack var1) {
      return var1.has(DataComponents.UNBREAKABLE) ? false : super.canEnchant(var1);
   }

   public static boolean shouldIgnoreDurabilityDrop(ItemStack var0, int var1, RandomSource var2) {
      if (var0.getItem() instanceof ArmorItem && var2.nextFloat() < 0.6F) {
         return false;
      } else {
         return var2.nextInt(var1 + 1) > 0;
      }
   }
}
