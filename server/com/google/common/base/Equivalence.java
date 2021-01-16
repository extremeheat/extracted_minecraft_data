package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;
import java.util.function.BiPredicate;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class Equivalence<T> implements BiPredicate<T, T> {
   protected Equivalence() {
      super();
   }

   public final boolean equivalent(@Nullable T var1, @Nullable T var2) {
      if (var1 == var2) {
         return true;
      } else {
         return var1 != null && var2 != null ? this.doEquivalent(var1, var2) : false;
      }
   }

   /** @deprecated */
   @Deprecated
   public final boolean test(@Nullable T var1, @Nullable T var2) {
      return this.equivalent(var1, var2);
   }

   protected abstract boolean doEquivalent(T var1, T var2);

   public final int hash(@Nullable T var1) {
      return var1 == null ? 0 : this.doHash(var1);
   }

   protected abstract int doHash(T var1);

   public final <F> Equivalence<F> onResultOf(Function<F, ? extends T> var1) {
      return new FunctionalEquivalence(var1, this);
   }

   public final <S extends T> Equivalence.Wrapper<S> wrap(@Nullable S var1) {
      return new Equivalence.Wrapper(this, var1);
   }

   @GwtCompatible(
      serializable = true
   )
   public final <S extends T> Equivalence<Iterable<S>> pairwise() {
      return new PairwiseEquivalence(this);
   }

   public final Predicate<T> equivalentTo(@Nullable T var1) {
      return new Equivalence.EquivalentToPredicate(this, var1);
   }

   public static Equivalence<Object> equals() {
      return Equivalence.Equals.INSTANCE;
   }

   public static Equivalence<Object> identity() {
      return Equivalence.Identity.INSTANCE;
   }

   static final class Identity extends Equivalence<Object> implements Serializable {
      static final Equivalence.Identity INSTANCE = new Equivalence.Identity();
      private static final long serialVersionUID = 1L;

      Identity() {
         super();
      }

      protected boolean doEquivalent(Object var1, Object var2) {
         return false;
      }

      protected int doHash(Object var1) {
         return System.identityHashCode(var1);
      }

      private Object readResolve() {
         return INSTANCE;
      }
   }

   static final class Equals extends Equivalence<Object> implements Serializable {
      static final Equivalence.Equals INSTANCE = new Equivalence.Equals();
      private static final long serialVersionUID = 1L;

      Equals() {
         super();
      }

      protected boolean doEquivalent(Object var1, Object var2) {
         return var1.equals(var2);
      }

      protected int doHash(Object var1) {
         return var1.hashCode();
      }

      private Object readResolve() {
         return INSTANCE;
      }
   }

   private static final class EquivalentToPredicate<T> implements Predicate<T>, Serializable {
      private final Equivalence<T> equivalence;
      @Nullable
      private final T target;
      private static final long serialVersionUID = 0L;

      EquivalentToPredicate(Equivalence<T> var1, @Nullable T var2) {
         super();
         this.equivalence = (Equivalence)Preconditions.checkNotNull(var1);
         this.target = var2;
      }

      public boolean apply(@Nullable T var1) {
         return this.equivalence.equivalent(var1, this.target);
      }

      public boolean equals(@Nullable Object var1) {
         if (this == var1) {
            return true;
         } else if (!(var1 instanceof Equivalence.EquivalentToPredicate)) {
            return false;
         } else {
            Equivalence.EquivalentToPredicate var2 = (Equivalence.EquivalentToPredicate)var1;
            return this.equivalence.equals(var2.equivalence) && Objects.equal(this.target, var2.target);
         }
      }

      public int hashCode() {
         return Objects.hashCode(this.equivalence, this.target);
      }

      public String toString() {
         return this.equivalence + ".equivalentTo(" + this.target + ")";
      }
   }

   public static final class Wrapper<T> implements Serializable {
      private final Equivalence<? super T> equivalence;
      @Nullable
      private final T reference;
      private static final long serialVersionUID = 0L;

      private Wrapper(Equivalence<? super T> var1, @Nullable T var2) {
         super();
         this.equivalence = (Equivalence)Preconditions.checkNotNull(var1);
         this.reference = var2;
      }

      @Nullable
      public T get() {
         return this.reference;
      }

      public boolean equals(@Nullable Object var1) {
         if (var1 == this) {
            return true;
         } else {
            if (var1 instanceof Equivalence.Wrapper) {
               Equivalence.Wrapper var2 = (Equivalence.Wrapper)var1;
               if (this.equivalence.equals(var2.equivalence)) {
                  Equivalence var3 = this.equivalence;
                  return var3.equivalent(this.reference, var2.reference);
               }
            }

            return false;
         }
      }

      public int hashCode() {
         return this.equivalence.hash(this.reference);
      }

      public String toString() {
         return this.equivalence + ".wrap(" + this.reference + ")";
      }

      // $FF: synthetic method
      Wrapper(Equivalence var1, Object var2, Object var3) {
         this(var1, var2);
      }
   }
}
