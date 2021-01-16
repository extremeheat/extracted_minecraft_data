package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

@GwtCompatible
final class WellBehavedMap<K, V> extends ForwardingMap<K, V> {
   private final Map<K, V> delegate;
   private Set<Entry<K, V>> entrySet;

   private WellBehavedMap(Map<K, V> var1) {
      super();
      this.delegate = var1;
   }

   static <K, V> WellBehavedMap<K, V> wrap(Map<K, V> var0) {
      return new WellBehavedMap(var0);
   }

   protected Map<K, V> delegate() {
      return this.delegate;
   }

   public Set<Entry<K, V>> entrySet() {
      Set var1 = this.entrySet;
      return var1 != null ? var1 : (this.entrySet = new WellBehavedMap.EntrySet());
   }

   private final class EntrySet extends Maps.EntrySet<K, V> {
      private EntrySet() {
         super();
      }

      Map<K, V> map() {
         return WellBehavedMap.this;
      }

      public Iterator<Entry<K, V>> iterator() {
         return new TransformedIterator<K, Entry<K, V>>(WellBehavedMap.this.keySet().iterator()) {
            Entry<K, V> transform(final K var1) {
               return new AbstractMapEntry<K, V>() {
                  public K getKey() {
                     return var1;
                  }

                  public V getValue() {
                     return WellBehavedMap.this.get(var1);
                  }

                  public V setValue(V var1x) {
                     return WellBehavedMap.this.put(var1, var1x);
                  }
               };
            }
         };
      }

      // $FF: synthetic method
      EntrySet(Object var2) {
         this();
      }
   }
}
