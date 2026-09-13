package net.minecraft.item;

import net.minecraft.block.BlockLeaves;
import net.minecraft.util.IIcon;

public class ItemLeaves extends ItemBlock {
   private final BlockLeaves field_150940_b;

   public ItemLeaves(BlockLeaves var1) {
      super(var1);
      this.field_150940_b = var1;
      this.func_77656_e(0);
      this.func_77627_a(true);
   }

   @Override
   public int func_77647_b(int var1) {
      return var1 | 4;
   }

   @Override
   public IIcon func_77617_a(int var1) {
      return this.field_150940_b.func_149691_a(0, var1);
   }

   @Override
   public int func_82790_a(ItemStack var1, int var2) {
      return this.field_150940_b.func_149741_i(var1.func_77960_j());
   }

   @Override
   public String func_77667_c(ItemStack var1) {
      int var2 = var1.func_77960_j();
      if (var2 < 0 || var2 >= this.field_150940_b.func_150125_e().length) {
         var2 = 0;
      }

      return super.func_77658_a() + "." + this.field_150940_b.func_150125_e()[var2];
   }
}
