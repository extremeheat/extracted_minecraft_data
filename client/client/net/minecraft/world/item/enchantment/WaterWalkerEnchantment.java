package net.minecraft.world.item.enchantment;

public class WaterWalkerEnchantment extends Enchantment {
   public WaterWalkerEnchantment(Enchantment.EnchantmentDefinition var1) {
      super(var1);
   }

   @Override
   public boolean checkCompatibility(Enchantment var1) {
      return super.checkCompatibility(var1) && var1 != Enchantments.FROST_WALKER;
   }
}
