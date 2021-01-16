package com.mojang.serialization;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.Map;

public abstract class CompressorHolder implements Compressable {
   private final Map<DynamicOps<?>, KeyCompressor<?>> compressors = new Object2ObjectArrayMap();

   public CompressorHolder() {
      super();
   }

   public <T> KeyCompressor<T> compressor(DynamicOps<T> var1) {
      return (KeyCompressor)this.compressors.computeIfAbsent(var1, (var2) -> {
         return new KeyCompressor(var1, this.keys(var1));
      });
   }
}
