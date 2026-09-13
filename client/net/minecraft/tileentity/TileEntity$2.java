package net.minecraft.tileentity;

import java.util.concurrent.Callable;
import net.minecraft.block.Block;

class TileEntity$2 implements Callable {
   TileEntity$2(TileEntity var1) {
      super();
      this.field_150832_a = var1;
   }

   public String call() {
      int var1 = Block.func_149682_b(
         this.field_150832_a
            .field_145850_b
            .func_147439_a(this.field_150832_a.field_145851_c, this.field_150832_a.field_145848_d, this.field_150832_a.field_145849_e)
      );

      try {
         return String.format("ID #%d (%s // %s)", var1, Block.func_149729_e(var1).func_149739_a(), Block.func_149729_e(var1).getClass().getCanonicalName());
      } catch (Throwable var3) {
         return "ID #" + var1;
      }
   }
}
