package net.minecraft.enchantment;

import net.minecraft.util.ResourceLocation;

public class EnchantmentWaterWorker extends Enchantment {
   public EnchantmentWaterWorker(int var1, ResourceLocation var2, int var3) {
      super(var1, var2, var3, EnumEnchantmentType.ARMOR_HEAD);
      this.func_77322_b("waterWorker");
   }

   public int func_77321_a(int var1) {
      return 1;
   }

   public int func_77317_b(int var1) {
      return this.func_77321_a(var1) + 40;
   }

   public int func_77325_b() {
      return 1;
   }
}
