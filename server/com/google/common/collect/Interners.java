package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Equivalence;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import java.util.concurrent.ConcurrentMap;

@Beta
@GwtIncompatible
public final class Interners {
   private Interners() {
      super();
   }

   public static Interners.InternerBuilder newBuilder() {
      return new Interners.InternerBuilder();
   }

   public static <E> Interner<E> newStrongInterner() {
      return newBuilder().strong().build();
   }

   @GwtIncompatible("java.lang.ref.WeakReference")
   public static <E> Interner<E> newWeakInterner() {
      return newBuilder().weak().build();
   }

   public static <E> Function<E, E> asFunction(Interner<E> var0) {
      return new Interners.InternerFunction((Interner)Preconditions.checkNotNull(var0));
   }

   private static class InternerFunction<E> implements Function<E, E> {
      private final Interner<E> interner;

      public InternerFunction(Interner<E> var1) {
         super();
         this.interner = var1;
      }

      public E apply(E var1) {
         return this.interner.intern(var1);
      }

      public int hashCode() {
         return this.interner.hashCode();
      }

      public boolean equals(Object var1) {
         if (var1 instanceof Interners.InternerFunction) {
            Interners.InternerFunction var2 = (Interners.InternerFunction)var1;
            return this.interner.equals(var2.interner);
         } else {
            return false;
         }
      }
   }

   @VisibleForTesting
   static final class WeakInterner<E> implements Interner<E> {
      @VisibleForTesting
      final MapMakerInternalMap<E, Interners.WeakInterner.Dummy, ?, ?> map;

      private WeakInterner(MapMaker var1) {
         super();
         this.map = var1.weakKeys().keyEquivalence(Equivalence.equals()).makeCustomMap();
      }

      public E intern(E var1) {
         Interners.WeakInterner.Dummy var4;
         do {
            MapMakerInternalMap.InternalEntry var2 = this.map.getEntry(var1);
            if (var2 != null) {
               Object var3 = var2.getKey();
               if (var3 != null) {
                  return var3;
               }
            }

            var4 = (Interners.WeakInterner.Dummy)this.map.putIfAbsent(var1, Interners.WeakInterner.Dummy.VALUE);
         } while(var4 != null);

         return var1;
      }

      // $FF: synthetic method
      WeakInterner(MapMaker var1, Object var2) {
         this(var1);
      }

      private static enum Dummy {
         VALUE;

         private Dummy() {
         }
      }
   }

   @VisibleForTesting
   static final class StrongInterner<E> implements Interner<E> {
      @VisibleForTesting
      final ConcurrentMap<E, E> map;

      private StrongInterner(MapMaker var1) {
         super();
         this.map = var1.makeMap();
      }

      public E intern(E var1) {
         Object var2 = this.map.putIfAbsent(Preconditions.checkNotNull(var1), var1);
         return var2 == null ? var1 : var2;
      }

      // $FF: synthetic method
      StrongInterner(MapMaker var1, Object var2) {
         this(var1);
      }
   }

   public static class InternerBuilder {
      private final MapMaker mapMaker;
      private boolean strong;

      private InternerBuilder() {
         super();
         this.mapMaker = new MapMaker();
         this.strong = true;
      }

      public Interners.InternerBuilder strong() {
         this.strong = true;
         return this;
      }

      @GwtIncompatible("java.lang.ref.WeakReference")
      public Interners.InternerBuilder weak() {
         this.strong = false;
         return this;
      }

      public Interners.InternerBuilder concurrencyLevel(int var1) {
         this.mapMaker.concurrencyLevel(var1);
         return this;
      }

      public <E> Interner<E> build() {
         return (Interner)(this.strong ? new Interners.StrongInterner(this.mapMaker) : new Interners.WeakInterner(this.mapMaker));
      }

      // $FF: synthetic method
      InternerBuilder(Object var1) {
         this();
      }
   }
}
