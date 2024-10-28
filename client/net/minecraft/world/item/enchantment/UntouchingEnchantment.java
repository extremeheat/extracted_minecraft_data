package net.minecraft.world.item.enchantment;

public class UntouchingEnchantment extends Enchantment {
   protected UntouchingEnchantment(Enchantment.EnchantmentDefinition var1) {
      super(var1);
   }

   public boolean checkCompatibility(Enchantment var1) {
      return super.checkCompatibility(var1) && var1 != Enchantments.FORTUNE;
   }
}
