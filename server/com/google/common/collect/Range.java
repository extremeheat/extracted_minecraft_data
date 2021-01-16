package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import javax.annotation.Nullable;

@GwtCompatible
public final class Range<C extends Comparable> implements Predicate<C>, Serializable {
   private static final Function<Range, Cut> LOWER_BOUND_FN = new Function<Range, Cut>() {
      public Cut apply(Range var1) {
         return var1.lowerBound;
      }
   };
   private static final Function<Range, Cut> UPPER_BOUND_FN = new Function<Range, Cut>() {
      public Cut apply(Range var1) {
         return var1.upperBound;
      }
   };
   static final Ordering<Range<?>> RANGE_LEX_ORDERING = new Range.RangeLexOrdering();
   private static final Range<Comparable> ALL = new Range(Cut.belowAll(), Cut.aboveAll());
   final Cut<C> lowerBound;
   final Cut<C> upperBound;
   private static final long serialVersionUID = 0L;

   static <C extends Comparable<?>> Function<Range<C>, Cut<C>> lowerBoundFn() {
      return LOWER_BOUND_FN;
   }

   static <C extends Comparable<?>> Function<Range<C>, Cut<C>> upperBoundFn() {
      return UPPER_BOUND_FN;
   }

   static <C extends Comparable<?>> Range<C> create(Cut<C> var0, Cut<C> var1) {
      return new Range(var0, var1);
   }

   public static <C extends Comparable<?>> Range<C> open(C var0, C var1) {
      return create(Cut.aboveValue(var0), Cut.belowValue(var1));
   }

   public static <C extends Comparable<?>> Range<C> closed(C var0, C var1) {
      return create(Cut.belowValue(var0), Cut.aboveValue(var1));
   }

   public static <C extends Comparable<?>> Range<C> closedOpen(C var0, C var1) {
      return create(Cut.belowValue(var0), Cut.belowValue(var1));
   }

   public static <C extends Comparable<?>> Range<C> openClosed(C var0, C var1) {
      return create(Cut.aboveValue(var0), Cut.aboveValue(var1));
   }

   public static <C extends Comparable<?>> Range<C> range(C var0, BoundType var1, C var2, BoundType var3) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var3);
      Cut var4 = var1 == BoundType.OPEN ? Cut.aboveValue(var0) : Cut.belowValue(var0);
      Cut var5 = var3 == BoundType.OPEN ? Cut.belowValue(var2) : Cut.aboveValue(var2);
      return create(var4, var5);
   }

   public static <C extends Comparable<?>> Range<C> lessThan(C var0) {
      return create(Cut.belowAll(), Cut.belowValue(var0));
   }

   public static <C extends Comparable<?>> Range<C> atMost(C var0) {
      return create(Cut.belowAll(), Cut.aboveValue(var0));
   }

   public static <C extends Comparable<?>> Range<C> upTo(C var0, BoundType var1) {
      switch(var1) {
      case OPEN:
         return lessThan(var0);
      case CLOSED:
         return atMost(var0);
      default:
         throw new AssertionError();
      }
   }

   public static <C extends Comparable<?>> Range<C> greaterThan(C var0) {
      return create(Cut.aboveValue(var0), Cut.aboveAll());
   }

   public static <C extends Comparable<?>> Range<C> atLeast(C var0) {
      return create(Cut.belowValue(var0), Cut.aboveAll());
   }

   public static <C extends Comparable<?>> Range<C> downTo(C var0, BoundType var1) {
      switch(var1) {
      case OPEN:
         return greaterThan(var0);
      case CLOSED:
         return atLeast(var0);
      default:
         throw new AssertionError();
      }
   }

   public static <C extends Comparable<?>> Range<C> all() {
      return ALL;
   }

   public static <C extends Comparable<?>> Range<C> singleton(C var0) {
      return closed(var0, var0);
   }

   public static <C extends Comparable<?>> Range<C> encloseAll(Iterable<C> var0) {
      Preconditions.checkNotNull(var0);
      if (var0 instanceof ContiguousSet) {
         return ((ContiguousSet)var0).range();
      } else {
         Iterator var1 = var0.iterator();
         Comparable var2 = (Comparable)Preconditions.checkNotNull(var1.next());

         Comparable var3;
         Comparable var4;
         for(var3 = var2; var1.hasNext(); var3 = (Comparable)Ordering.natural().max(var3, var4)) {
            var4 = (Comparable)Preconditions.checkNotNull(var1.next());
            var2 = (Comparable)Ordering.natural().min(var2, var4);
         }

         return closed(var2, var3);
      }
   }

   private Range(Cut<C> var1, Cut<C> var2) {
      super();
      this.lowerBound = (Cut)Preconditions.checkNotNull(var1);
      this.upperBound = (Cut)Preconditions.checkNotNull(var2);
      if (var1.compareTo(var2) > 0 || var1 == Cut.aboveAll() || var2 == Cut.belowAll()) {
         throw new IllegalArgumentException("Invalid range: " + toString(var1, var2));
      }
   }

   public boolean hasLowerBound() {
      return this.lowerBound != Cut.belowAll();
   }

   public C lowerEndpoint() {
      return this.lowerBound.endpoint();
   }

   public BoundType lowerBoundType() {
      return this.lowerBound.typeAsLowerBound();
   }

   public boolean hasUpperBound() {
      return this.upperBound != Cut.aboveAll();
   }

   public C upperEndpoint() {
      return this.upperBound.endpoint();
   }

   public BoundType upperBoundType() {
      return this.upperBound.typeAsUpperBound();
   }

   public boolean isEmpty() {
      return this.lowerBound.equals(this.upperBound);
   }

   public boolean contains(C var1) {
      Preconditions.checkNotNull(var1);
      return this.lowerBound.isLessThan(var1) && !this.upperBound.isLessThan(var1);
   }

   /** @deprecated */
   @Deprecated
   public boolean apply(C var1) {
      return this.contains(var1);
   }

   public boolean containsAll(Iterable<? extends C> var1) {
      if (Iterables.isEmpty(var1)) {
         return true;
      } else {
         if (var1 instanceof SortedSet) {
            SortedSet var2 = cast(var1);
            Comparator var3 = var2.comparator();
            if (Ordering.natural().equals(var3) || var3 == null) {
               return this.contains((Comparable)var2.first()) && this.contains((Comparable)var2.last());
            }
         }

         Iterator var4 = var1.iterator();

         Comparable var5;
         do {
            if (!var4.hasNext()) {
               return true;
            }

            var5 = (Comparable)var4.next();
         } while(this.contains(var5));

         return false;
      }
   }

   public boolean encloses(Range<C> var1) {
      return this.lowerBound.compareTo(var1.lowerBound) <= 0 && this.upperBound.compareTo(var1.upperBound) >= 0;
   }

   public boolean isConnected(Range<C> var1) {
      return this.lowerBound.compareTo(var1.upperBound) <= 0 && var1.lowerBound.compareTo(this.upperBound) <= 0;
   }

   public Range<C> intersection(Range<C> var1) {
      int var2 = this.lowerBound.compareTo(var1.lowerBound);
      int var3 = this.upperBound.compareTo(var1.upperBound);
      if (var2 >= 0 && var3 <= 0) {
         return this;
      } else if (var2 <= 0 && var3 >= 0) {
         return var1;
      } else {
         Cut var4 = var2 >= 0 ? this.lowerBound : var1.lowerBound;
         Cut var5 = var3 <= 0 ? this.upperBound : var1.upperBound;
         return create(var4, var5);
      }
   }

   public Range<C> span(Range<C> var1) {
      int var2 = this.lowerBound.compareTo(var1.lowerBound);
      int var3 = this.upperBound.compareTo(var1.upperBound);
      if (var2 <= 0 && var3 >= 0) {
         return this;
      } else if (var2 >= 0 && var3 <= 0) {
         return var1;
      } else {
         Cut var4 = var2 <= 0 ? this.lowerBound : var1.lowerBound;
         Cut var5 = var3 >= 0 ? this.upperBound : var1.upperBound;
         return create(var4, var5);
      }
   }

   public Range<C> canonical(DiscreteDomain<C> var1) {
      Preconditions.checkNotNull(var1);
      Cut var2 = this.lowerBound.canonical(var1);
      Cut var3 = this.upperBound.canonical(var1);
      return var2 == this.lowerBound && var3 == this.upperBound ? this : create(var2, var3);
   }

   public boolean equals(@Nullable Object var1) {
      if (!(var1 instanceof Range)) {
         return false;
      } else {
         Range var2 = (Range)var1;
         return this.lowerBound.equals(var2.lowerBound) && this.upperBound.equals(var2.upperBound);
      }
   }

   public int hashCode() {
      return this.lowerBound.hashCode() * 31 + this.upperBound.hashCode();
   }

   public String toString() {
      return toString(this.lowerBound, this.upperBound);
   }

   private static String toString(Cut<?> var0, Cut<?> var1) {
      StringBuilder var2 = new StringBuilder(16);
      var0.describeAsLowerBound(var2);
      var2.append("..");
      var1.describeAsUpperBound(var2);
      return var2.toString();
   }

   private static <T> SortedSet<T> cast(Iterable<T> var0) {
      return (SortedSet)var0;
   }

   Object readResolve() {
      return this.equals(ALL) ? all() : this;
   }

   static int compareOrThrow(Comparable var0, Comparable var1) {
      return var0.compareTo(var1);
   }

   private static class RangeLexOrdering extends Ordering<Range<?>> implements Serializable {
      private static final long serialVersionUID = 0L;

      private RangeLexOrdering() {
         super();
      }

      public int compare(Range<?> var1, Range<?> var2) {
         return ComparisonChain.start().compare((Comparable)var1.lowerBound, (Comparable)var2.lowerBound).compare((Comparable)var1.upperBound, (Comparable)var2.upperBound).result();
      }

      // $FF: synthetic method
      RangeLexOrdering(Object var1) {
         this();
      }
   }
}
