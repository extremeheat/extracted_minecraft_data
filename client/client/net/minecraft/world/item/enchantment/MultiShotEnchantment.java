package net.minecraft.world.item.enchantment;

public class MultiShotEnchantment extends Enchantment {
   public MultiShotEnchantment(Enchantment.EnchantmentDefinition var1) {
      super(var1);
   }

   @Override
   public boolean checkCompatibility(Enchantment var1) {
      return super.checkCompatibility(var1) && var1 != Enchantments.PIERCING;
   }
}
