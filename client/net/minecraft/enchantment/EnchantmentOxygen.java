package net.minecraft.enchantment;

import net.minecraft.util.ResourceLocation;

public class EnchantmentOxygen extends Enchantment {
   public EnchantmentOxygen(int var1, ResourceLocation var2, int var3) {
      super(var1, var2, var3, EnumEnchantmentType.ARMOR_HEAD);
      this.func_77322_b("oxygen");
   }

   public int func_77321_a(int var1) {
      return 10 * var1;
   }

   public int func_77317_b(int var1) {
      return this.func_77321_a(var1) + 30;
   }

   public int func_77325_b() {
      return 3;
   }
}
