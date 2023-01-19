package net.minecraft.world.level.chunk;

import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.util.VisibleForDebug;

public final class DataLayer {
   public static final int LAYER_COUNT = 16;
   public static final int LAYER_SIZE = 128;
   public static final int SIZE = 2048;
   private static final int NIBBLE_SIZE = 4;
   @Nullable
   protected byte[] data;

   public DataLayer() {
      super();
   }

   public DataLayer(byte[] var1) {
      super();
      this.data = var1;
      if (var1.length != 2048) {
         throw (IllegalArgumentException)Util.pauseInIde(new IllegalArgumentException("DataLayer should be 2048 bytes not: " + var1.length));
      }
   }

   protected DataLayer(int var1) {
      super();
      this.data = new byte[var1];
   }

   public int get(int var1, int var2, int var3) {
      return this.get(getIndex(var1, var2, var3));
   }

   public void set(int var1, int var2, int var3, int var4) {
      this.set(getIndex(var1, var2, var3), var4);
   }

   private static int getIndex(int var0, int var1, int var2) {
      return var1 << 8 | var2 << 4 | var0;
   }

   private int get(int var1) {
      if (this.data == null) {
         return 0;
      } else {
         int var2 = getByteIndex(var1);
         int var3 = getNibbleIndex(var1);
         return this.data[var2] >> 4 * var3 & 15;
      }
   }

   private void set(int var1, int var2) {
      if (this.data == null) {
         this.data = new byte[2048];
      }

      int var3 = getByteIndex(var1);
      int var4 = getNibbleIndex(var1);
      int var5 = ~(15 << 4 * var4);
      int var6 = (var2 & 15) << 4 * var4;
      this.data[var3] = (byte)(this.data[var3] & var5 | var6);
   }

   private static int getNibbleIndex(int var0) {
      return var0 & 1;
   }

   private static int getByteIndex(int var0) {
      return var0 >> 1;
   }

   public byte[] getData() {
      if (this.data == null) {
         this.data = new byte[2048];
      }

      return this.data;
   }

   public DataLayer copy() {
      return this.data == null ? new DataLayer() : new DataLayer((byte[])this.data.clone());
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();

      for(int var2 = 0; var2 < 4096; ++var2) {
         var1.append(Integer.toHexString(this.get(var2)));
         if ((var2 & 15) == 15) {
            var1.append("\n");
         }

         if ((var2 & 0xFF) == 255) {
            var1.append("\n");
         }
      }

      return var1.toString();
   }

   @VisibleForDebug
   public String layerToString(int var1) {
      StringBuilder var2 = new StringBuilder();

      for(int var3 = 0; var3 < 256; ++var3) {
         var2.append(Integer.toHexString(this.get(var3)));
         if ((var3 & 15) == 15) {
            var2.append("\n");
         }
      }

      return var2.toString();
   }

   public boolean isEmpty() {
      return this.data == null;
   }
}
