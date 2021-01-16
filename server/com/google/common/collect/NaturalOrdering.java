package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.io.Serializable;

@GwtCompatible(
   serializable = true
)
final class NaturalOrdering extends Ordering<Comparable> implements Serializable {
   static final NaturalOrdering INSTANCE = new NaturalOrdering();
   private transient Ordering<Comparable> nullsFirst;
   private transient Ordering<Comparable> nullsLast;
   private static final long serialVersionUID = 0L;

   public int compare(Comparable var1, Comparable var2) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      return var1.compareTo(var2);
   }

   public <S extends Comparable> Ordering<S> nullsFirst() {
      Ordering var1 = this.nullsFirst;
      if (var1 == null) {
         var1 = this.nullsFirst = super.nullsFirst();
      }

      return var1;
   }

   public <S extends Comparable> Ordering<S> nullsLast() {
      Ordering var1 = this.nullsLast;
      if (var1 == null) {
         var1 = this.nullsLast = super.nullsLast();
      }

      return var1;
   }

   public <S extends Comparable> Ordering<S> reverse() {
      return ReverseNaturalOrdering.INSTANCE;
   }

   private Object readResolve() {
      return INSTANCE;
   }

   public String toString() {
      return "Ordering.natural()";
   }

   private NaturalOrdering() {
      super();
   }
}
