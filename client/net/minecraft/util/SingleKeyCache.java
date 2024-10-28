package net.minecraft.util;

import java.util.Objects;
import java.util.function.Function;
import javax.annotation.Nullable;

public class SingleKeyCache<K, V> {
   private final Function<K, V> computeValue;
   @Nullable
   private K cacheKey = null;
   @Nullable
   private V cachedValue;

   public SingleKeyCache(Function<K, V> var1) {
      super();
      this.computeValue = var1;
   }

   public V getValue(K var1) {
      if (this.cachedValue == null || !Objects.equals(this.cacheKey, var1)) {
         this.cachedValue = this.computeValue.apply(var1);
         this.cacheKey = var1;
      }

      return this.cachedValue;
   }
}
