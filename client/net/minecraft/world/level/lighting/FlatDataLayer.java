package net.minecraft.world.level.lighting;

import net.minecraft.world.level.chunk.DataLayer;

public class FlatDataLayer extends DataLayer {
   public FlatDataLayer() {
      super(128);
   }

   public FlatDataLayer(DataLayer var1, int var2) {
      super(128);
      System.arraycopy(var1.getData(), var2 * 128, this.data, 0, 128);
   }

   protected int getIndex(int var1, int var2, int var3) {
      return var3 << 4 | var1;
   }

   public byte[] getData() {
      byte[] var1 = new byte[2048];

      for(int var2 = 0; var2 < 16; ++var2) {
         System.arraycopy(this.data, 0, var1, var2 * 128, 128);
      }

      return var1;
   }
}
