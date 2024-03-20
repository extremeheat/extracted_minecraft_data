package net.minecraft.world.item.enchantment;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

public class DigDurabilityEnchantment extends Enchantment {
   protected DigDurabilityEnchantment(Enchantment.EnchantmentDefinition var1) {
      super(var1);
   }

   public static boolean shouldIgnoreDurabilityDrop(ItemStack var0, int var1, RandomSource var2) {
      if (var0.getItem() instanceof ArmorItem && var2.nextFloat() < 0.6F) {
         return false;
      } else {
         return var2.nextInt(var1 + 1) > 0;
      }
   }
}
