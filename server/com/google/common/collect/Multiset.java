package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CompatibleWith;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;
import javax.annotation.Nullable;

@GwtCompatible
public interface Multiset<E> extends Collection<E> {
   int size();

   int count(@Nullable @CompatibleWith("E") Object var1);

   @CanIgnoreReturnValue
   int add(@Nullable E var1, int var2);

   @CanIgnoreReturnValue
   int remove(@Nullable @CompatibleWith("E") Object var1, int var2);

   @CanIgnoreReturnValue
   int setCount(E var1, int var2);

   @CanIgnoreReturnValue
   boolean setCount(E var1, int var2, int var3);

   Set<E> elementSet();

   Set<Multiset.Entry<E>> entrySet();

   @Beta
   default void forEachEntry(ObjIntConsumer<? super E> var1) {
      Preconditions.checkNotNull(var1);
      this.entrySet().forEach((var1x) -> {
         var1.accept(var1x.getElement(), var1x.getCount());
      });
   }

   boolean equals(@Nullable Object var1);

   int hashCode();

   String toString();

   Iterator<E> iterator();

   boolean contains(@Nullable Object var1);

   boolean containsAll(Collection<?> var1);

   @CanIgnoreReturnValue
   boolean add(E var1);

   @CanIgnoreReturnValue
   boolean remove(@Nullable Object var1);

   @CanIgnoreReturnValue
   boolean removeAll(Collection<?> var1);

   @CanIgnoreReturnValue
   boolean retainAll(Collection<?> var1);

   default void forEach(Consumer<? super E> var1) {
      Preconditions.checkNotNull(var1);
      this.entrySet().forEach((var1x) -> {
         Object var2 = var1x.getElement();
         int var3 = var1x.getCount();

         for(int var4 = 0; var4 < var3; ++var4) {
            var1.accept(var2);
         }

      });
   }

   default Spliterator<E> spliterator() {
      return Multisets.spliteratorImpl(this);
   }

   public interface Entry<E> {
      E getElement();

      int getCount();

      boolean equals(Object var1);

      int hashCode();

      String toString();
   }
}
