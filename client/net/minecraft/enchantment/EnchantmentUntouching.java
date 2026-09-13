package net.minecraft.enchantment;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class EnchantmentUntouching extends Enchantment {
   protected EnchantmentUntouching(int var1, int var2) {
      super(var1, var2, EnumEnchantmentType.digger);
      this.func_77322_b("untouching");
   }

   @Override
   public int func_77321_a(int var1) {
      return 15;
   }

   @Override
   public int func_77317_b(int var1) {
      return super.func_77321_a(var1) + 50;
   }

   @Override
   public int func_77325_b() {
      return 1;
   }

   @Override
   public boolean func_77326_a(Enchantment var1) {
      return super.func_77326_a(var1) && var1.field_77352_x != field_77346_s.field_77352_x;
   }

   @Override
   public boolean func_92089_a(ItemStack var1) {
      return var1.func_77973_b() == Items.field_151097_aZ ? true : super.func_92089_a(var1);
   }
}
