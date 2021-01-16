package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class ForwardingSortedSet<E> extends ForwardingSet<E> implements SortedSet<E> {
   protected ForwardingSortedSet() {
      super();
   }

   protected abstract SortedSet<E> delegate();

   public Comparator<? super E> comparator() {
      return this.delegate().comparator();
   }

   public E first() {
      return this.delegate().first();
   }

   public SortedSet<E> headSet(E var1) {
      return this.delegate().headSet(var1);
   }

   public E last() {
      return this.delegate().last();
   }

   public SortedSet<E> subSet(E var1, E var2) {
      return this.delegate().subSet(var1, var2);
   }

   public SortedSet<E> tailSet(E var1) {
      return this.delegate().tailSet(var1);
   }

   private int unsafeCompare(Object var1, Object var2) {
      Comparator var3 = this.comparator();
      return var3 == null ? ((Comparable)var1).compareTo(var2) : var3.compare(var1, var2);
   }

   @Beta
   protected boolean standardContains(@Nullable Object var1) {
      try {
         Object var3 = this.tailSet(var1).first();
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
   protected boolean standardRemove(@Nullable Object var1) {
      try {
         Iterator var3 = this.tailSet(var1).iterator();
         if (var3.hasNext()) {
            Object var4 = var3.next();
            if (this.unsafeCompare(var4, var1) == 0) {
               var3.remove();
               return true;
            }
         }

         return false;
      } catch (ClassCastException var5) {
         return false;
      } catch (NullPointerException var6) {
         return false;
      }
   }

   @Beta
   protected SortedSet<E> standardSubSet(E var1, E var2) {
      return this.tailSet(var1).headSet(var2);
   }
}
