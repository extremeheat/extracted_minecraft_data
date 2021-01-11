package net.minecraft.util;

import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;

public class WeightedRandomFishable extends WeightedRandom.Item {
   private final ItemStack field_150711_b;
   private float field_150712_c;
   private boolean field_150710_d;

   public WeightedRandomFishable(ItemStack var1, int var2) {
      super(var2);
      this.field_150711_b = var1;
   }

   public ItemStack func_150708_a(Random var1) {
      ItemStack var2 = this.field_150711_b.func_77946_l();
      if (this.field_150712_c > 0.0F) {
         int var3 = (int)(this.field_150712_c * (float)this.field_150711_b.func_77958_k());
         int var4 = var2.func_77958_k() - var1.nextInt(var1.nextInt(var3) + 1);
         if (var4 > var3) {
            var4 = var3;
         }

         if (var4 < 1) {
            var4 = 1;
         }

         var2.func_77964_b(var4);
      }

      if (this.field_150710_d) {
         EnchantmentHelper.func_77504_a(var1, var2, 30);
      }

      return var2;
   }

   public WeightedRandomFishable func_150709_a(float var1) {
      this.field_150712_c = var1;
      return this;
   }

   public WeightedRandomFishable func_150707_a() {
      this.field_150710_d = true;
      return this;
   }
}
