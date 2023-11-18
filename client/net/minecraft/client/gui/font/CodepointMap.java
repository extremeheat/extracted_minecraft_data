package net.minecraft.client.gui.font;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.Arrays;
import java.util.function.IntFunction;
import javax.annotation.Nullable;

public class CodepointMap<T> {
   private static final int BLOCK_BITS = 8;
   private static final int BLOCK_SIZE = 256;
   private static final int IN_BLOCK_MASK = 255;
   private static final int MAX_BLOCK = 4351;
   private static final int BLOCK_COUNT = 4352;
   private final T[] empty;
   private final T[][] blockMap;
   private final IntFunction<T[]> blockConstructor;

   public CodepointMap(IntFunction<T[]> var1, IntFunction<T[][]> var2) {
      super();
      this.empty = (T[])((Object[])var1.apply(256));
      this.blockMap = (T[][])((Object[][])var2.apply(4352));
      Arrays.fill(this.blockMap, this.empty);
      this.blockConstructor = var1;
   }

   public void clear() {
      Arrays.fill(this.blockMap, this.empty);
   }

   @Nullable
   public T get(int var1) {
      int var2 = var1 >> 8;
      int var3 = var1 & 0xFF;
      return this.blockMap[var2][var3];
   }

   @Nullable
   public T put(int var1, T var2) {
      int var3 = var1 >> 8;
      int var4 = var1 & 0xFF;
      Object[] var5 = this.blockMap[var3];
      if (var5 == this.empty) {
         var5 = (Object[])this.blockConstructor.apply(256);
         this.blockMap[var3] = (T[])var5;
         var5[var4] = var2;
         return null;
      } else {
         Object var6 = var5[var4];
         var5[var4] = var2;
         return (T)var6;
      }
   }

   public T computeIfAbsent(int var1, IntFunction<T> var2) {
      int var3 = var1 >> 8;
      int var4 = var1 & 0xFF;
      Object[] var5 = this.blockMap[var3];
      Object var6 = var5[var4];
      if (var6 != null) {
         return (T)var6;
      } else {
         if (var5 == this.empty) {
            var5 = (Object[])this.blockConstructor.apply(256);
            this.blockMap[var3] = (T[])var5;
         }

         Object var7 = var2.apply(var1);
         var5[var4] = var7;
         return (T)var7;
      }
   }

   @Nullable
   public T remove(int var1) {
      int var2 = var1 >> 8;
      int var3 = var1 & 0xFF;
      Object[] var4 = this.blockMap[var2];
      if (var4 == this.empty) {
         return null;
      } else {
         Object var5 = var4[var3];
         var4[var3] = null;
         return (T)var5;
      }
   }

   public void forEach(CodepointMap.Output<T> var1) {
      for(int var2 = 0; var2 < this.blockMap.length; ++var2) {
         Object[] var3 = this.blockMap[var2];
         if (var3 != this.empty) {
            for(int var4 = 0; var4 < var3.length; ++var4) {
               Object var5 = var3[var4];
               if (var5 != null) {
                  int var6 = var2 << 8 | var4;
                  var1.accept(var6, (T)var5);
               }
            }
         }
      }
   }

   public IntSet keySet() {
      IntOpenHashSet var1 = new IntOpenHashSet();
      this.forEach((var1x, var2) -> var1.add(var1x));
      return var1;
   }

   @FunctionalInterface
   public interface Output<T> {
      void accept(int var1, T var2);
   }
}
