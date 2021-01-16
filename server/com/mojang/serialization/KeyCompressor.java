package com.mojang.serialization;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.stream.Stream;

public final class KeyCompressor<T> {
   private final Int2ObjectMap<T> decompress = new Int2ObjectArrayMap();
   private final Object2IntMap<T> compress = new Object2IntArrayMap();
   private final Object2IntMap<String> compressString = new Object2IntArrayMap();
   private final int size;
   private final DynamicOps<T> ops;

   public KeyCompressor(DynamicOps<T> var1, Stream<T> var2) {
      super();
      this.ops = var1;
      this.compressString.defaultReturnValue(-1);
      var2.forEach((var2x) -> {
         if (!this.compress.containsKey(var2x)) {
            int var3 = this.compress.size();
            this.compress.put(var2x, var3);
            var1.getStringValue(var2x).result().ifPresent((var2) -> {
               this.compressString.put(var2, var3);
            });
            this.decompress.put(var3, var2x);
         }
      });
      this.size = this.compress.size();
   }

   public T decompress(int var1) {
      return this.decompress.get(var1);
   }

   public int compress(String var1) {
      int var2 = this.compressString.getInt(var1);
      return var2 == -1 ? this.compress(this.ops.createString(var1)) : var2;
   }

   public int compress(T var1) {
      return (Integer)this.compress.get(var1);
   }

   public int size() {
      return this.size;
   }
}
