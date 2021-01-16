package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class ForwardingSortedMap<K, V> extends ForwardingMap<K, V> implements SortedMap<K, V> {
   protected ForwardingSortedMap() {
      super();
   }

   protected abstract SortedMap<K, V> delegate();

   public Comparator<? super K> comparator() {
      return this.delegate().comparator();
   }

   public K firstKey() {
      return this.delegate().firstKey();
   }

   public SortedMap<K, V> headMap(K var1) {
      return this.delegate().headMap(var1);
   }

   public K lastKey() {
      return this.delegate().lastKey();
   }

   public SortedMap<K, V> subMap(K var1, K var2) {
      return this.delegate().subMap(var1, var2);
   }

   public SortedMap<K, V> tailMap(K var1) {
      return this.delegate().tailMap(var1);
   }

   private int unsafeCompare(Object var1, Object var2) {
      Comparator var3 = this.comparator();
      return var3 == null ? ((Comparable)var1).compareTo(var2) : var3.compare(var1, var2);
   }

   @Beta
   protected boolean standardContainsKey(@Nullable Object var1) {
      try {
         Object var3 = this.tailMap(var1).firstKey();
         return this.unsafeCompare(var3, var1) == 0;
      } catch (ClassCastException var4) {
         return false;
      } catch (NoSuchElementException var5) {
         return false;
      } catch (NullPointerException var6) {
         return false;
      }
   }

   @Beta
   protected SortedMap<K, V> standardSubMap(K var1, K var2) {
      Preconditions.checkArgument(this.unsafeCompare(var1, var2) <= 0, "fromKey must be <= toKey");
      return this.tailMap(var1).headMap(var2);
   }

   @Beta
   protected class StandardKeySet extends Maps.SortedKeySet<K, V> {
      public StandardKeySet() {
         super(ForwardingSortedMap.this);
      }
   }
}
