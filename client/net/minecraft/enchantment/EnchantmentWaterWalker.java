package net.minecraft.enchantment;

import net.minecraft.util.ResourceLocation;

public class EnchantmentWaterWalker extends Enchantment {
   public EnchantmentWaterWalker(int var1, ResourceLocation var2, int var3) {
      super(var1, var2, var3, EnumEnchantmentType.ARMOR_FEET);
      this.func_77322_b("waterWalker");
   }

   public int func_77321_a(int var1) {
      return var1 * 10;
   }

   public int func_77317_b(int var1) {
      return this.func_77321_a(var1) + 15;
   }

   public int func_77325_b() {
      return 3;
   }
}
