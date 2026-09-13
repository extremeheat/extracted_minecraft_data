package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.util.IIcon;

public class ItemMultiTexture extends ItemBlock {
   protected final Block field_150941_b;
   protected final String[] field_150942_c;

   public ItemMultiTexture(Block var1, Block var2, String[] var3) {
      super(var1);
      this.field_150941_b = var2;
      this.field_150942_c = var3;
      this.func_77656_e(0);
      this.func_77627_a(true);
   }

   @Override
   public IIcon func_77617_a(int var1) {
      return this.field_150941_b.func_149691_a(2, var1);
   }

   @Override
   public int func_77647_b(int var1) {
      return var1;
   }

   @Override
   public String func_77667_c(ItemStack var1) {
      int var2 = var1.func_77960_j();
      if (var2 < 0 || var2 >= this.field_150942_c.length) {
         var2 = 0;
      }

      return super.func_77658_a() + "." + this.field_150942_c[var2];
   }
}
