package net.minecraft.tileentity;

import java.util.concurrent.Callable;

class TileEntity$1 implements Callable {
   TileEntity$1(TileEntity var1) {
      super();
      this.field_150830_a = var1;
   }

   public String call() {
      return (String)TileEntity.access$000().get(this.field_150830_a.getClass()) + " // " + this.field_150830_a.getClass().getCanonicalName();
   }
}
