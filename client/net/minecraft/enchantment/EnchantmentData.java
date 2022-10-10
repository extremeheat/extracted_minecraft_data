package net.minecraft.enchantment;

import net.minecraft.util.WeightedRandom;

public class EnchantmentData extends WeightedRandom.Item {
   public final Enchantment field_76302_b;
   public final int field_76303_c;

   public EnchantmentData(Enchantment var1, int var2) {
      super(var1.func_77324_c().func_185270_a());
      this.field_76302_b = var1;
      this.field_76303_c = var2;
   }
}
