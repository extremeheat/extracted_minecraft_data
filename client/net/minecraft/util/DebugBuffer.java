package net.minecraft.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class DebugBuffer<T> {
   private final AtomicReferenceArray<T> data;
   private final AtomicInteger index;

   public DebugBuffer(int var1) {
      super();
      this.data = new AtomicReferenceArray(var1);
      this.index = new AtomicInteger(0);
   }

   public void push(T var1) {
      int var2 = this.data.length();

      int var3;
      int var4;
      do {
         var3 = this.index.get();
         var4 = (var3 + 1) % var2;
      } while(!this.index.compareAndSet(var3, var4));

      this.data.set(var4, var1);
   }

   public List<T> dump() {
      int var1 = this.index.get();
      Builder var2 = ImmutableList.builder();

      for(int var3 = 0; var3 < this.data.length(); ++var3) {
         int var4 = Math.floorMod(var1 - var3, this.data.length());
         Object var5 = this.data.get(var4);
         if (var5 != null) {
            var2.add(var5);
         }
      }

      return var2.build();
   }
}
