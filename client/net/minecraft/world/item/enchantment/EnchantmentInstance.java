package net.minecraft.world.item.enchantment;

import net.minecraft.core.Holder;

public record EnchantmentInstance(Holder<Enchantment> enchantment, int level) {
   public EnchantmentInstance {
      super();
   }

   public int weight() {
      return ((Enchantment)this.enchantment().value()).getWeight();
   }
}
