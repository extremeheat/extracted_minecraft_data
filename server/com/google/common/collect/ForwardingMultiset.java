package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class ForwardingMultiset<E> extends ForwardingCollection<E> implements Multiset<E> {
   protected ForwardingMultiset() {
      super();
   }

   protected abstract Multiset<E> delegate();

   public int count(Object var1) {
      return this.delegate().count(var1);
   }

   @CanIgnoreReturnValue
   public int add(E var1, int var2) {
      return this.delegate().add(var1, var2);
   }

   @CanIgnoreReturnValue
   public int remove(Object var1, int var2) {
      return this.delegate().remove(var1, var2);
   }

   public Set<E> elementSet() {
      return this.delegate().elementSet();
   }

   public Set<Multiset.Entry<E>> entrySet() {
      return this.delegate().entrySet();
   }

   public boolean equals(@Nullable Object var1) {
      return var1 == this || this.delegate().equals(var1);
   }

   public int hashCode() {
      return this.delegate().hashCode();
   }

   @CanIgnoreReturnValue
   public int setCount(E var1, int var2) {
      return this.delegate().setCount(var1, var2);
   }

   @CanIgnoreReturnValue
   public boolean setCount(E var1, int var2, int var3) {
      return this.delegate().setCount(var1, var2, var3);
   }

   protected boolean standardContains(@Nullable Object var1) {
      return this.count(var1) > 0;
   }

   protected void standardClear() {
      Iterators.clear(this.entrySet().iterator());
   }

   @Beta
   protected int standardCount(@Nullable Object var1) {
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

   protected boolean standardAdd(E var1) {
      this.add(var1, 1);
      return true;
   }

   @Beta
   protected boolean standardAddAll(Collection<? extends E> var1) {
      return Multisets.addAllImpl(this, var1);
   }

   protected boolean standardRemove(Object var1) {
      return this.remove(var1, 1) > 0;
   }

   protected boolean standardRemoveAll(Collection<?> var1) {
      return Multisets.removeAllImpl(this, var1);
   }

   protected boolean standardRetainAll(Collection<?> var1) {
      return Multisets.retainAllImpl(this, var1);
   }

   protected int standardSetCount(E var1, int var2) {
      return Multisets.setCountImpl(this, var1, var2);
   }

   protected boolean standardSetCount(E var1, int var2, int var3) {
      return Multisets.setCountImpl(this, var1, var2, var3);
   }

   protected Iterator<E> standardIterator() {
      return Multisets.iteratorImpl(this);
   }

   protected int standardSize() {
      return Multisets.sizeImpl(this);
   }

   protected boolean standardEquals(@Nullable Object var1) {
      return Multisets.equalsImpl(this, var1);
   }

   protected int standardHashCode() {
      return this.entrySet().hashCode();
   }

   protected String standardToString() {
      return this.entrySet().toString();
   }

   @Beta
   protected class StandardElementSet extends Multisets.ElementSet<E> {
      public StandardElementSet() {
         super();
      }

      Multiset<E> multiset() {
         return ForwardingMultiset.this;
      }
   }
}
