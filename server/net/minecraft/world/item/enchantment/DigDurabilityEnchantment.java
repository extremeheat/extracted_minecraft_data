package net.minecraft.world.item.enchantment;

import java.util.Random;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

public class DigDurabilityEnchantment extends Enchantment {
   protected DigDurabilityEnchantment(Enchantment.Rarity var1, EquipmentSlot... var2) {
      super(var1, EnchantmentCategory.BREAKABLE, var2);
   }

   public int getMinCost(int var1) {
      return 5 + (var1 - 1) * 8;
   }

   public int getMaxCost(int var1) {
      return super.getMinCost(var1) + 50;
   }

   public int getMaxLevel() {
      return 3;
   }

   public boolean canEnchant(ItemStack var1) {
      return var1.isDamageableItem() ? true : super.canEnchant(var1);
   }

   public static boolean shouldIgnoreDurabilityDrop(ItemStack var0, int var1, Random var2) {
      if (var0.getItem() instanceof ArmorItem && var2.nextFloat() < 0.6F) {
         return false;
      } else {
         return var2.nextInt(var1 + 1) > 0;
      }
   }
}
