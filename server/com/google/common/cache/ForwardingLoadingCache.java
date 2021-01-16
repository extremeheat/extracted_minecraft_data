package com.google.common.cache;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.util.concurrent.ExecutionException;

@GwtIncompatible
public abstract class ForwardingLoadingCache<K, V> extends ForwardingCache<K, V> implements LoadingCache<K, V> {
   protected ForwardingLoadingCache() {
      super();
   }

   protected abstract LoadingCache<K, V> delegate();

   public V get(K var1) throws ExecutionException {
      return this.delegate().get(var1);
   }

   public V getUnchecked(K var1) {
      return this.delegate().getUnchecked(var1);
   }

   public ImmutableMap<K, V> getAll(Iterable<? extends K> var1) throws ExecutionException {
      return this.delegate().getAll(var1);
   }

   public V apply(K var1) {
      return this.delegate().apply(var1);
   }

   public void refresh(K var1) {
      this.delegate().refresh(var1);
   }

   public abstract static class SimpleForwardingLoadingCache<K, V> extends ForwardingLoadingCache<K, V> {
      private final LoadingCache<K, V> delegate;

      protected SimpleForwardingLoadingCache(LoadingCache<K, V> var1) {
         super();
         this.delegate = (LoadingCache)Preconditions.checkNotNull(var1);
      }

      protected final LoadingCache<K, V> delegate() {
         return this.delegate;
      }
   }
}
