package com.google.common.cache;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

@GwtCompatible
public interface LoadingCache<K, V> extends Cache<K, V>, Function<K, V> {
   V get(K var1) throws ExecutionException;

   V getUnchecked(K var1);

   ImmutableMap<K, V> getAll(Iterable<? extends K> var1) throws ExecutionException;

   /** @deprecated */
   @Deprecated
   V apply(K var1);

   void refresh(K var1);

   ConcurrentMap<K, V> asMap();
}
