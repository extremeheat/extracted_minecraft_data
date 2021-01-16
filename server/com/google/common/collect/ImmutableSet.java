package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.concurrent.LazyInit;
import com.google.j2objc.annotations.RetainedWith;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Collector;
import javax.annotation.Nullable;

@GwtCompatible(
   serializable = true,
   emulated = true
)
public abstract class ImmutableSet<E> extends ImmutableCollection<E> implements Set<E> {
   static final int SPLITERATOR_CHARACTERISTICS = 1297;
   static final int MAX_TABLE_SIZE = 1073741824;
   private static final double DESIRED_LOAD_FACTOR = 0.7D;
   private static final int CUTOFF = 751619276;
   @LazyInit
   @RetainedWith
   private transient ImmutableList<E> asList;

   @Beta
   public static <E> Collector<E, ?, ImmutableSet<E>> toImmutableSet() {
      return CollectCollectors.toImmutableSet();
   }

   public static <E> ImmutableSet<E> of() {
      return RegularImmutableSet.EMPTY;
   }

   public static <E> ImmutableSet<E> of(E var0) {
      return new SingletonImmutableSet(var0);
   }

   public static <E> ImmutableSet<E> of(E var0, E var1) {
      return construct(2, var0, var1);
   }

   public static <E> ImmutableSet<E> of(E var0, E var1, E var2) {
      return construct(3, var0, var1, var2);
   }

   public static <E> ImmutableSet<E> of(E var0, E var1, E var2, E var3) {
      return construct(4, var0, var1, var2, var3);
   }

   public static <E> ImmutableSet<E> of(E var0, E var1, E var2, E var3, E var4) {
      return construct(5, var0, var1, var2, var3, var4);
   }

   @SafeVarargs
   public static <E> ImmutableSet<E> of(E var0, E var1, E var2, E var3, E var4, E var5, E... var6) {
      boolean var7 = true;
      Object[] var8 = new Object[6 + var6.length];
      var8[0] = var0;
      var8[1] = var1;
      var8[2] = var2;
      var8[3] = var3;
      var8[4] = var4;
      var8[5] = var5;
      System.arraycopy(var6, 0, var8, 6, var6.length);
      return construct(var8.length, var8);
   }

   private static <E> ImmutableSet<E> construct(int var0, Object... var1) {
      switch(var0) {
      case 0:
         return of();
      case 1:
         Object var2 = var1[0];
         return of(var2);
      default:
         int var13 = chooseTableSize(var0);
         Object[] var3 = new Object[var13];
         int var4 = var13 - 1;
         int var5 = 0;
         int var6 = 0;
         int var7 = 0;

         for(; var7 < var0; ++var7) {
            Object var8 = ObjectArrays.checkElementNotNull(var1[var7], var7);
            int var9 = var8.hashCode();
            int var10 = Hashing.smear(var9);

            while(true) {
               int var11 = var10 & var4;
               Object var12 = var3[var11];
               if (var12 == null) {
                  var1[var6++] = var8;
                  var3[var11] = var8;
                  var5 += var9;
                  break;
               }

               if (var12.equals(var8)) {
                  break;
               }

               ++var10;
            }
         }

         Arrays.fill(var1, var6, var0, (Object)null);
         if (var6 == 1) {
            Object var15 = var1[0];
            return new SingletonImmutableSet(var15, var5);
         } else if (var13 != chooseTableSize(var6)) {
            return construct(var6, var1);
         } else {
            Object[] var14 = var6 < var1.length ? Arrays.copyOf(var1, var6) : var1;
            return new RegularImmutableSet(var14, var5, var3, var4);
         }
      }
   }

   @VisibleForTesting
   static int chooseTableSize(int var0) {
      if (var0 >= 751619276) {
         Preconditions.checkArgument(var0 < 1073741824, "collection too large");
         return 1073741824;
      } else {
         int var1;
         for(var1 = Integer.highestOneBit(var0 - 1) << 1; (double)var1 * 0.7D < (double)var0; var1 <<= 1) {
         }

         return var1;
      }
   }

   public static <E> ImmutableSet<E> copyOf(Collection<? extends E> var0) {
      if (var0 instanceof ImmutableSet && !(var0 instanceof ImmutableSortedSet)) {
         ImmutableSet var1 = (ImmutableSet)var0;
         if (!var1.isPartialView()) {
            return var1;
         }
      } else if (var0 instanceof EnumSet) {
         return copyOfEnumSet((EnumSet)var0);
      }

      Object[] var2 = var0.toArray();
      return construct(var2.length, var2);
   }

   public static <E> ImmutableSet<E> copyOf(Iterable<? extends E> var0) {
      return var0 instanceof Collection ? copyOf((Collection)var0) : copyOf(var0.iterator());
   }

   public static <E> ImmutableSet<E> copyOf(Iterator<? extends E> var0) {
      if (!var0.hasNext()) {
         return of();
      } else {
         Object var1 = var0.next();
         return !var0.hasNext() ? of(var1) : (new ImmutableSet.Builder()).add(var1).addAll(var0).build();
      }
   }

   public static <E> ImmutableSet<E> copyOf(E[] var0) {
      switch(var0.length) {
      case 0:
         return of();
      case 1:
         return of(var0[0]);
      default:
         return construct(var0.length, (Object[])var0.clone());
      }
   }

   private static ImmutableSet copyOfEnumSet(EnumSet var0) {
      return ImmutableEnumSet.asImmutable(EnumSet.copyOf(var0));
   }

   ImmutableSet() {
      super();
   }

   boolean isHashCodeFast() {
      return false;
   }

   public boolean equals(@Nullable Object var1) {
      if (var1 == this) {
         return true;
      } else {
         return var1 instanceof ImmutableSet && this.isHashCodeFast() && ((ImmutableSet)var1).isHashCodeFast() && this.hashCode() != var1.hashCode() ? false : Sets.equalsImpl(this, var1);
      }
   }

   public int hashCode() {
      return Sets.hashCodeImpl(this);
   }

   public abstract UnmodifiableIterator<E> iterator();

   public ImmutableList<E> asList() {
      ImmutableList var1 = this.asList;
      return var1 == null ? (this.asList = this.createAsList()) : var1;
   }

   ImmutableList<E> createAsList() {
      return new RegularImmutableAsList(this, this.toArray());
   }

   Object writeReplace() {
      return new ImmutableSet.SerializedForm(this.toArray());
   }

   public static <E> ImmutableSet.Builder<E> builder() {
      return new ImmutableSet.Builder();
   }

   public static class Builder<E> extends ImmutableCollection.ArrayBasedBuilder<E> {
      public Builder() {
         this(4);
      }

      Builder(int var1) {
         super(var1);
      }

      @CanIgnoreReturnValue
      public ImmutableSet.Builder<E> add(E var1) {
         super.add(var1);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableSet.Builder<E> add(E... var1) {
         super.add(var1);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableSet.Builder<E> addAll(Iterable<? extends E> var1) {
         super.addAll(var1);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableSet.Builder<E> addAll(Iterator<? extends E> var1) {
         super.addAll(var1);
         return this;
      }

      @CanIgnoreReturnValue
      ImmutableSet.Builder<E> combine(ImmutableCollection.ArrayBasedBuilder<E> var1) {
         super.combine(var1);
         return this;
      }

      public ImmutableSet<E> build() {
         ImmutableSet var1 = ImmutableSet.construct(this.size, this.contents);
         this.size = var1.size();
         return var1;
      }
   }

   private static class SerializedForm implements Serializable {
      final Object[] elements;
      private static final long serialVersionUID = 0L;

      SerializedForm(Object[] var1) {
         super();
         this.elements = var1;
      }

      Object readResolve() {
         return ImmutableSet.copyOf(this.elements);
      }
   }

   abstract static class Indexed<E> extends ImmutableSet<E> {
      Indexed() {
         super();
      }

      abstract E get(int var1);

      public UnmodifiableIterator<E> iterator() {
         return this.asList().iterator();
      }

      public Spliterator<E> spliterator() {
         return CollectSpliterators.indexed(this.size(), 1297, this::get);
      }

      public void forEach(Consumer<? super E> var1) {
         Preconditions.checkNotNull(var1);
         int var2 = this.size();

         for(int var3 = 0; var3 < var2; ++var3) {
            var1.accept(this.get(var3));
         }

      }

      ImmutableList<E> createAsList() {
         return new ImmutableAsList<E>() {
            public E get(int var1) {
               return Indexed.this.get(var1);
            }

            ImmutableSet.Indexed<E> delegateCollection() {
               return Indexed.this;
            }
         };
      }
   }
}
