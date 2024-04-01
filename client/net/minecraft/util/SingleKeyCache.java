package net.minecraft.util;

import java.util.Objects;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.Maybe;

public class SingleKeyCache<K, V> {
   private final Function<K, V> computeValue;
   @Nullable
   private K cacheKey = (K)null;
   private Maybe<V> cachedValue = Maybe.no();

   public SingleKeyCache(Function<K, V> var1) {
      super();
      this.computeValue = var1;
   }

   public V getValue(K var1) {
      if (this.cachedValue.isEmpty() || !Objects.equals(this.cacheKey, var1)) {
         this.cachedValue = Maybe.yes(this.computeValue.apply((K)var1));
         this.cacheKey = (K)var1;
      }

      return this.cachedValue.getValue();
   }
}
