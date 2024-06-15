package net.minecraft.world.item.enchantment;

public class ArrowInfiniteEnchantment extends Enchantment {
   public ArrowInfiniteEnchantment(Enchantment.EnchantmentDefinition var1) {
      super(var1);
   }

   @Override
   public boolean checkCompatibility(Enchantment var1) {
      return var1 instanceof MendingEnchantment ? false : super.checkCompatibility(var1);
   }
}
