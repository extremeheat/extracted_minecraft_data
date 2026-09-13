package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.util.IIcon;

public class ItemColored extends ItemBlock {
   private final Block field_150944_b;
   private String[] field_150945_c;

   public ItemColored(Block var1, boolean var2) {
      super(var1);
      this.field_150944_b = var1;
      if (var2) {
         this.func_77656_e(0);
         this.func_77627_a(true);
      }
   }

   @Override
   public int func_82790_a(ItemStack var1, int var2) {
      return this.field_150944_b.func_149741_i(var1.func_77960_j());
   }

   @Override
   public IIcon func_77617_a(int var1) {
      return this.field_150944_b.func_149691_a(0, var1);
   }

   @Override
   public int func_77647_b(int var1) {
      return var1;
   }

   public ItemColored func_150943_a(String[] var1) {
      this.field_150945_c = var1;
      return this;
   }

   @Override
   public String func_77667_c(ItemStack var1) {
      if (this.field_150945_c == null) {
         return super.func_77667_c(var1);
      } else {
         int var2 = var1.func_77960_j();
         return var2 >= 0 && var2 < this.field_150945_c.length ? super.func_77667_c(var1) + "." + this.field_150945_c[var2] : super.func_77667_c(var1);
      }
   }
}
