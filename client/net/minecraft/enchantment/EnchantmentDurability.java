package net.minecraft.enchantment;

import java.util.Random;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class EnchantmentDurability extends Enchantment {
   protected EnchantmentDurability(int var1, ResourceLocation var2, int var3) {
      super(var1, var2, var3, EnumEnchantmentType.BREAKABLE);
      this.func_77322_b("durability");
   }

   public int func_77321_a(int var1) {
      return 5 + (var1 - 1) * 8;
   }

   public int func_77317_b(int var1) {
      return super.func_77321_a(var1) + 50;
   }

   public int func_77325_b() {
      return 3;
   }

   public boolean func_92089_a(ItemStack var1) {
      return var1.func_77984_f() ? true : super.func_92089_a(var1);
   }

   public static boolean func_92097_a(ItemStack var0, int var1, Random var2) {
      if (var0.func_77973_b() instanceof ItemArmor && var2.nextFloat() < 0.6F) {
         return false;
      } else {
         return var2.nextInt(var1 + 1) > 0;
      }
   }
}
