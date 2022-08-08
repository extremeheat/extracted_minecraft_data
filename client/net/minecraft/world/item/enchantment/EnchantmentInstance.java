package net.minecraft.world.item.enchantment;

import net.minecraft.util.random.WeightedEntry;

public class EnchantmentInstance extends WeightedEntry.IntrusiveBase {
   public final Enchantment enchantment;
   public final int level;

   public EnchantmentInstance(Enchantment var1, int var2) {
      super(var1.getRarity().getWeight());
      this.enchantment = var1;
      this.level = var2;
   }
}
