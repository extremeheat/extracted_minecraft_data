package com.google.common.graph;

import java.util.Map;
import javax.annotation.Nullable;

class MapRetrievalCache<K, V> extends MapIteratorCache<K, V> {
   @Nullable
   private transient MapRetrievalCache.CacheEntry<K, V> cacheEntry1;
   @Nullable
   private transient MapRetrievalCache.CacheEntry<K, V> cacheEntry2;

   MapRetrievalCache(Map<K, V> var1) {
      super(var1);
   }

   public V get(@Nullable Object var1) {
      Object var2 = this.getIfCached(var1);
      if (var2 != null) {
         return var2;
      } else {
         var2 = this.getWithoutCaching(var1);
         if (var2 != null) {
            this.addToCache(var1, var2);
         }

         return var2;
      }
   }

   protected V getIfCached(@Nullable Object var1) {
      Object var2 = super.getIfCached(var1);
      if (var2 != null) {
         return var2;
      } else {
         MapRetrievalCache.CacheEntry var3 = this.cacheEntry1;
         if (var3 != null && var3.key == var1) {
            return var3.value;
         } else {
            var3 = this.cacheEntry2;
            if (var3 != null && var3.key == var1) {
               this.addToCache(var3);
               return var3.value;
            } else {
               return null;
            }
         }
      }
   }

   protected void clearCache() {
      super.clearCache();
      this.cacheEntry1 = null;
      this.cacheEntry2 = null;
   }

   private void addToCache(K var1, V var2) {
      this.addToCache(new MapRetrievalCache.CacheEntry(var1, var2));
   }

   private void addToCache(MapRetrievalCache.CacheEntry<K, V> var1) {
      this.cacheEntry2 = this.cacheEntry1;
      this.cacheEntry1 = var1;
   }

   private static final class CacheEntry<K, V> {
      final K key;
      final V value;

      CacheEntry(K var1, V var2) {
         super();
         this.key = var1;
         this.value = var2;
      }
   }
}
