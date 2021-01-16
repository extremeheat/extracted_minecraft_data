package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.j2objc.annotations.Weak;
import java.io.Serializable;
import java.util.Spliterator;
import java.util.Map.Entry;
import java.util.function.Consumer;
import javax.annotation.Nullable;

@GwtCompatible(
   emulated = true
)
final class ImmutableMapValues<K, V> extends ImmutableCollection<V> {
   @Weak
   private final ImmutableMap<K, V> map;

   ImmutableMapValues(ImmutableMap<K, V> var1) {
      super();
      this.map = var1;
   }

   public int size() {
      return this.map.size();
   }

   public UnmodifiableIterator<V> iterator() {
      return new UnmodifiableIterator<V>() {
         final UnmodifiableIterator<Entry<K, V>> entryItr;

         {
            this.entryItr = ImmutableMapValues.this.map.entrySet().iterator();
         }

         public boolean hasNext() {
            return this.entryItr.hasNext();
         }

         public V next() {
            return ((Entry)this.entryItr.next()).getValue();
         }
      };
   }

   public Spliterator<V> spliterator() {
      return CollectSpliterators.map(this.map.entrySet().spliterator(), Entry::getValue);
   }

   public boolean contains(@Nullable Object var1) {
      return var1 != null && Iterators.contains(this.iterator(), var1);
   }

   boolean isPartialView() {
      return true;
   }

   public ImmutableList<V> asList() {
      final ImmutableList var1 = this.map.entrySet().asList();
      return new ImmutableAsList<V>() {
         public V get(int var1x) {
            return ((Entry)var1.get(var1x)).getValue();
         }

         ImmutableCollection<V> delegateCollection() {
            return ImmutableMapValues.this;
         }
      };
   }

   @GwtIncompatible
   public void forEach(Consumer<? super V> var1) {
      Preconditions.checkNotNull(var1);
      this.map.forEach((var1x, var2) -> {
         var1.accept(var2);
      });
   }

   @GwtIncompatible
   Object writeReplace() {
      return new ImmutableMapValues.SerializedForm(this.map);
   }

   @GwtIncompatible
   private static class SerializedForm<V> implements Serializable {
      final ImmutableMap<?, V> map;
      private static final long serialVersionUID = 0L;

      SerializedForm(ImmutableMap<?, V> var1) {
         super();
         this.map = var1;
      }

      Object readResolve() {
         return this.map.values();
      }
   }
}
