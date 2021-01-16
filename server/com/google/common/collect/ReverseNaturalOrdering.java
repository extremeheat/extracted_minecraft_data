package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.Iterator;

@GwtCompatible(
   serializable = true
)
final class ReverseNaturalOrdering extends Ordering<Comparable> implements Serializable {
   static final ReverseNaturalOrdering INSTANCE = new ReverseNaturalOrdering();
   private static final long serialVersionUID = 0L;

   public int compare(Comparable var1, Comparable var2) {
      Preconditions.checkNotNull(var1);
      return var1 == var2 ? 0 : var2.compareTo(var1);
   }

   public <S extends Comparable> Ordering<S> reverse() {
      return Ordering.natural();
   }

   public <E extends Comparable> E min(E var1, E var2) {
      return (Comparable)NaturalOrdering.INSTANCE.max(var1, var2);
   }

   public <E extends Comparable> E min(E var1, E var2, E var3, E... var4) {
      return (Comparable)NaturalOrdering.INSTANCE.max(var1, var2, var3, var4);
   }

   public <E extends Comparable> E min(Iterator<E> var1) {
      return (Comparable)NaturalOrdering.INSTANCE.max(var1);
   }

   public <E extends Comparable> E min(Iterable<E> var1) {
      return (Comparable)NaturalOrdering.INSTANCE.max(var1);
   }

   public <E extends Comparable> E max(E var1, E var2) {
      return (Comparable)NaturalOrdering.INSTANCE.min(var1, var2);
   }

   public <E extends Comparable> E max(E var1, E var2, E var3, E... var4) {
      return (Comparable)NaturalOrdering.INSTANCE.min(var1, var2, var3, var4);
   }

   public <E extends Comparable> E max(Iterator<E> var1) {
      return (Comparable)NaturalOrdering.INSTANCE.min(var1);
   }

   public <E extends Comparable> E max(Iterable<E> var1) {
      return (Comparable)NaturalOrdering.INSTANCE.min(var1);
   }

   private Object readResolve() {
      return INSTANCE;
   }

   public String toString() {
      return "Ordering.natural().reverse()";
   }

   private ReverseNaturalOrdering() {
      super();
   }
}
