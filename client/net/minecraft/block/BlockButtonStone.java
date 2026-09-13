package net.minecraft.block;

import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;

public class BlockButtonStone extends BlockButton {
   protected BlockButtonStone() {
      super(false);
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      return Blocks.field_150348_b.func_149733_h(1);
   }
}
