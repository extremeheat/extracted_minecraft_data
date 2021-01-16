package com.google.common.cache;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.UncheckedExecutionException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@GwtIncompatible
public abstract class AbstractLoadingCache<K, V> extends AbstractCache<K, V> implements LoadingCache<K, V> {
   protected AbstractLoadingCache() {
      super();
   }

   public V getUnchecked(K var1) {
      try {
         return this.get(var1);
      } catch (ExecutionException var3) {
         throw new UncheckedExecutionException(var3.getCause());
      }
   }

   public ImmutableMap<K, V> getAll(Iterable<? extends K> var1) throws ExecutionException {
      LinkedHashMap var2 = Maps.newLinkedHashMap();
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         Object var4 = var3.next();
         if (!var2.containsKey(var4)) {
            var2.put(var4, this.get(var4));
         }
      }

      return ImmutableMap.copyOf((Map)var2);
   }

   public final V apply(K var1) {
      return this.getUnchecked(var1);
   }

   public void refresh(K var1) {
      throw new UnsupportedOperationException();
   }
}
