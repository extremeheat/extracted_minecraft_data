package net.minecraft.world.item.enchantment;

import net.minecraft.core.Holder;
import net.minecraft.util.random.WeightedEntry;

public class EnchantmentInstance extends WeightedEntry.IntrusiveBase {
   public final Holder<Enchantment> enchantment;
   public final int level;

   public EnchantmentInstance(Holder<Enchantment> var1, int var2) {
      super(((Enchantment)var1.value()).getWeight());
      this.enchantment = var1;
      this.level = var2;
   }
}
