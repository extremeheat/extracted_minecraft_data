package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.util.IIcon;

public class ItemBlockWithMetadata extends ItemBlock {
   private Block field_150950_b;

   public ItemBlockWithMetadata(Block var1, Block var2) {
      super(var1);
      this.field_150950_b = var2;
      this.func_77656_e(0);
      this.func_77627_a(true);
   }

   @Override
   public IIcon func_77617_a(int var1) {
      return this.field_150950_b.func_149691_a(2, var1);
   }

   @Override
   public int func_77647_b(int var1) {
      return var1;
   }
}
