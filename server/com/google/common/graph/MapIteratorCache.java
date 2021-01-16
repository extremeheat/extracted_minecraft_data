package com.google.common.graph;

import com.google.common.base.Preconditions;
import com.google.common.collect.UnmodifiableIterator;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;

class MapIteratorCache<K, V> {
   private final Map<K, V> backingMap;
   @Nullable
   private transient Entry<K, V> entrySetCache;

   MapIteratorCache(Map<K, V> var1) {
      super();
      this.backingMap = (Map)Preconditions.checkNotNull(var1);
   }

   @CanIgnoreReturnValue
   public V put(@Nullable K var1, @Nullable V var2) {
      this.clearCache();
      return this.backingMap.put(var1, var2);
   }

   @CanIgnoreReturnValue
   public V remove(@Nullable Object var1) {
      this.clearCache();
      return this.backingMap.remove(var1);
   }

   public void clear() {
      this.clearCache();
      this.backingMap.clear();
   }

   public V get(@Nullable Object var1) {
      Object var2 = this.getIfCached(var1);
      return var2 != null ? var2 : this.getWithoutCaching(var1);
   }

   public final V getWithoutCaching(@Nullable Object var1) {
      return this.backingMap.get(var1);
   }

   public final boolean containsKey(@Nullable Object var1) {
      return this.getIfCached(var1) != null || this.backingMap.containsKey(var1);
   }

   public final Set<K> unmodifiableKeySet() {
      return new AbstractSet<K>() {
         public UnmodifiableIterator<K> iterator() {
            final Iterator var1 = MapIteratorCache.this.backingMap.entrySet().iterator();
            return new UnmodifiableIterator<K>() {
               public boolean hasNext() {
                  return var1.hasNext();
               }

               public K next() {
                  Entry var1x = (Entry)var1.next();
                  MapIteratorCache.this.entrySetCache = var1x;
                  return var1x.getKey();
               }
            };
         }

         public int size() {
            return MapIteratorCache.this.backingMap.size();
         }

         public boolean contains(@Nullable Object var1) {
            return MapIteratorCache.this.containsKey(var1);
         }
      };
   }

   protected V getIfCached(@Nullable Object var1) {
      Entry var2 = this.entrySetCache;
      return var2 != null && var2.getKey() == var1 ? var2.getValue() : null;
   }

   protected void clearCache() {
      this.entrySetCache = null;
   }
}
