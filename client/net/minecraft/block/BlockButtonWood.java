package net.minecraft.block;

import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;

public class BlockButtonWood extends BlockButton {
   protected BlockButtonWood() {
      super(true);
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      return Blocks.field_150344_f.func_149733_h(1);
   }
}
