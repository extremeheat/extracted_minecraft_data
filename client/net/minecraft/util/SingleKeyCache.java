package net.minecraft.util;

import java.util.Objects;
import java.util.function.Function;
import org.jspecify.annotations.Nullable;

public class SingleKeyCache<K, V> {
   private final Function<K, V> computeValue;
   private @Nullable K cacheKey = null;
   private @Nullable V cachedValue;

   public SingleKeyCache(Function<K, V> var1) {
      super();
      this.computeValue = var1;
   }

   public V getValue(K var1) {
      if (this.cachedValue == null || !Objects.equals(this.cacheKey, var1)) {
         this.cachedValue = (V)this.computeValue.apply(var1);
         this.cacheKey = var1;
      }

      return this.cachedValue;
   }
}
