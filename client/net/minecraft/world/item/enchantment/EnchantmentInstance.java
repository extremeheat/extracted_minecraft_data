package net.minecraft.world.item.enchantment;

import net.minecraft.core.Holder;

public record EnchantmentInstance(Holder<Enchantment> enchantment, int level) {
   public EnchantmentInstance(Holder<Enchantment> var1, int var2) {
      super();
      this.enchantment = var1;
      this.level = var2;
   }

   public int weight() {
      return ((Enchantment)this.enchantment().value()).getWeight();
   }
}
