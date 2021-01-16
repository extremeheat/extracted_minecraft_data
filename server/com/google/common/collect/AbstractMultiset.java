package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
abstract class AbstractMultiset<E> extends AbstractCollection<E> implements Multiset<E> {
   private transient Set<E> elementSet;
   private transient Set<Multiset.Entry<E>> entrySet;

   AbstractMultiset() {
      super();
   }

   public int size() {
      return Multisets.sizeImpl(this);
   }

   public boolean isEmpty() {
      return this.entrySet().isEmpty();
   }

   public boolean contains(@Nullable Object var1) {
      return this.count(var1) > 0;
   }

   public Iterator<E> iterator() {
      return Multisets.iteratorImpl(this);
   }

   public int count(@Nullable Object var1) {
      Iterator var2 = this.entrySet().iterator();

      Multiset.Entry var3;
      do {
         if (!var2.hasNext()) {
            return 0;
         }

         var3 = (Multiset.Entry)var2.next();
      } while(!Objects.equal(var3.getElement(), var1));

      return var3.getCount();
   }

   @CanIgnoreReturnValue
   public boolean add(@Nullable E var1) {
      this.add(var1, 1);
      return true;
   }

   @CanIgnoreReturnValue
   public int add(@Nullable E var1, int var2) {
      throw new UnsupportedOperationException();
   }

   @CanIgnoreReturnValue
   public boolean remove(@Nullable Object var1) {
      return this.remove(var1, 1) > 0;
   }

   @CanIgnoreReturnValue
   public int remove(@Nullable Object var1, int var2) {
      throw new UnsupportedOperationException();
   }

   @CanIgnoreReturnValue
   public int setCount(@Nullable E var1, int var2) {
      return Multisets.setCountImpl(this, var1, var2);
   }

   @CanIgnoreReturnValue
   public boolean setCount(@Nullable E var1, int var2, int var3) {
      return Multisets.setCountImpl(this, var1, var2, var3);
   }

   @CanIgnoreReturnValue
   public boolean addAll(Collection<? extends E> var1) {
      return Multisets.addAllImpl(this, var1);
   }

   @CanIgnoreReturnValue
   public boolean removeAll(Collection<?> var1) {
      return Multisets.removeAllImpl(this, var1);
   }

   @CanIgnoreReturnValue
   public boolean retainAll(Collection<?> var1) {
      return Multisets.retainAllImpl(this, var1);
   }

   public void clear() {
      Iterators.clear(this.entryIterator());
   }

   public Set<E> elementSet() {
      Set var1 = this.elementSet;
      if (var1 == null) {
         this.elementSet = var1 = this.createElementSet();
      }

      return var1;
   }

   Set<E> createElementSet() {
      return new AbstractMultiset.ElementSet();
   }

   abstract Iterator<Multiset.Entry<E>> entryIterator();

   abstract int distinctElements();

   public Set<Multiset.Entry<E>> entrySet() {
      Set var1 = this.entrySet;
      if (var1 == null) {
         this.entrySet = var1 = this.createEntrySet();
      }

      return var1;
   }

   Set<Multiset.Entry<E>> createEntrySet() {
      return new AbstractMultiset.EntrySet();
   }

   public boolean equals(@Nullable Object var1) {
      return Multisets.equalsImpl(this, var1);
   }

   public int hashCode() {
      return this.entrySet().hashCode();
   }

   public String toString() {
      return this.entrySet().toString();
   }

   class EntrySet extends Multisets.EntrySet<E> {
      EntrySet() {
         super();
      }

      Multiset<E> multiset() {
         return AbstractMultiset.this;
      }

      public Iterator<Multiset.Entry<E>> iterator() {
         return AbstractMultiset.this.entryIterator();
      }

      public int size() {
         return AbstractMultiset.this.distinctElements();
      }
   }

   class ElementSet extends Multisets.ElementSet<E> {
      ElementSet() {
         super();
      }

      Multiset<E> multiset() {
         return AbstractMultiset.this;
      }
   }
}
