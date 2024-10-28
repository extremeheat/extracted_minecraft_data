package net.minecraft.util;

import java.util.Locale;
import java.util.function.Consumer;

public class StaticCache2D<T> {
   private final int minX;
   private final int minZ;
   private final int sizeX;
   private final int sizeZ;
   private final Object[] cache;

   public static <T> StaticCache2D<T> create(int var0, int var1, int var2, Initializer<T> var3) {
      int var4 = var0 - var2;
      int var5 = var1 - var2;
      int var6 = 2 * var2 + 1;
      return new StaticCache2D(var4, var5, var6, var6, var3);
   }

   private StaticCache2D(int var1, int var2, int var3, int var4, Initializer<T> var5) {
      super();
      this.minX = var1;
      this.minZ = var2;
      this.sizeX = var3;
      this.sizeZ = var4;
      this.cache = new Object[this.sizeX * this.sizeZ];

      for(int var6 = var1; var6 < var1 + var3; ++var6) {
         for(int var7 = var2; var7 < var2 + var4; ++var7) {
            this.cache[this.getIndex(var6, var7)] = var5.get(var6, var7);
         }
      }

   }

   public void forEach(Consumer<T> var1) {
      Object[] var2 = this.cache;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Object var5 = var2[var4];
         var1.accept(var5);
      }

   }

   public T get(int var1, int var2) {
      if (!this.contains(var1, var2)) {
         throw new IllegalArgumentException("Requested out of range value (" + var1 + "," + var2 + ") from " + String.valueOf(this));
      } else {
         return this.cache[this.getIndex(var1, var2)];
      }
   }

   public boolean contains(int var1, int var2) {
      int var3 = var1 - this.minX;
      int var4 = var2 - this.minZ;
      return var3 >= 0 && var3 < this.sizeX && var4 >= 0 && var4 < this.sizeZ;
   }

   public String toString() {
      return String.format(Locale.ROOT, "StaticCache2D[%d, %d, %d, %d]", this.minX, this.minZ, this.minX + this.sizeX, this.minZ + this.sizeZ);
   }

   private int getIndex(int var1, int var2) {
      int var3 = var1 - this.minX;
      int var4 = var2 - this.minZ;
      return var3 * this.sizeZ + var4;
   }

   @FunctionalInterface
   public interface Initializer<T> {
      T get(int var1, int var2);
   }
}
