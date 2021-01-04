package net.minecraft.world.level.chunk;

import javax.annotation.Nullable;

public class DataLayer {
   @Nullable
   protected byte[] data;

   public DataLayer() {
      super();
   }

   public DataLayer(byte[] var1) {
      super();
      this.data = var1;
      if (var1.length != 2048) {
         throw new IllegalArgumentException("ChunkNibbleArrays should be 2048 bytes not: " + var1.length);
      }
   }

   protected DataLayer(int var1) {
      super();
      this.data = new byte[var1];
   }

   public int get(int var1, int var2, int var3) {
      return this.get(this.getIndex(var1, var2, var3));
   }

   public void set(int var1, int var2, int var3, int var4) {
      this.set(this.getIndex(var1, var2, var3), var4);
   }

   protected int getIndex(int var1, int var2, int var3) {
      return var2 << 8 | var3 << 4 | var1;
   }

   private int get(int var1) {
      if (this.data == null) {
         return 0;
      } else {
         int var2 = this.getPosition(var1);
         return this.isFirst(var1) ? this.data[var2] & 15 : this.data[var2] >> 4 & 15;
      }
   }

   private void set(int var1, int var2) {
      if (this.data == null) {
         this.data = new byte[2048];
      }

      int var3 = this.getPosition(var1);
      if (this.isFirst(var1)) {
         this.data[var3] = (byte)(this.data[var3] & 240 | var2 & 15);
      } else {
         this.data[var3] = (byte)(this.data[var3] & 15 | (var2 & 15) << 4);
      }

   }

   private boolean isFirst(int var1) {
      return (var1 & 1) == 0;
   }

   private int getPosition(int var1) {
      return var1 >> 1;
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

   public String toString() {
      StringBuilder var1 = new StringBuilder();

      for(int var2 = 0; var2 < 4096; ++var2) {
         var1.append(Integer.toHexString(this.get(var2)));
         if ((var2 & 15) == 15) {
            var1.append("\n");
         }

         if ((var2 & 255) == 255) {
            var1.append("\n");
         }
      }

      return var1.toString();
   }

   public boolean isEmpty() {
      return this.data == null;
   }
}
