package net.minecraft.world.item.enchantment;

import net.minecraft.util.WeighedRandom;

public class EnchantmentInstance extends WeighedRandom.WeighedRandomItem {
   public final Enchantment enchantment;
   public final int level;

   public EnchantmentInstance(Enchantment var1, int var2) {
      super(var1.getRarity().getWeight());
      this.enchantment = var1;
      this.level = var2;
   }
}
