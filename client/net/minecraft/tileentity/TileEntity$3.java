package net.minecraft.tileentity;

import java.util.concurrent.Callable;

class TileEntity$3 implements Callable {
   TileEntity$3(TileEntity var1) {
      super();
      this.field_150834_a = var1;
   }

   public String call() {
      int var1 = this.field_150834_a
         .field_145850_b
         .func_72805_g(this.field_150834_a.field_145851_c, this.field_150834_a.field_145848_d, this.field_150834_a.field_145849_e);
      if (var1 < 0) {
         return "Unknown? (Got " + var1 + ")";
      } else {
         String var2 = String.format("%4s", Integer.toBinaryString(var1)).replace(" ", "0");
         return String.format("%1$d / 0x%1$X / 0b%2$s", var1, var2);
      }
   }
}
