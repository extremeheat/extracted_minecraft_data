package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import javax.annotation.Nullable;

@GwtIncompatible
abstract class AbstractRangeSet<C extends Comparable> implements RangeSet<C> {
   AbstractRangeSet() {
      super();
   }

   public boolean contains(C var1) {
      return this.rangeContaining(var1) != null;
   }

   public abstract Range<C> rangeContaining(C var1);

   public boolean isEmpty() {
      return this.asRanges().isEmpty();
   }

   public void add(Range<C> var1) {
      throw new UnsupportedOperationException();
   }

   public void remove(Range<C> var1) {
      throw new UnsupportedOperationException();
   }

   public void clear() {
      this.remove(Range.all());
   }

   public boolean enclosesAll(RangeSet<C> var1) {
      return this.enclosesAll(var1.asRanges());
   }

   public void addAll(RangeSet<C> var1) {
      this.addAll(var1.asRanges());
   }

   public void removeAll(RangeSet<C> var1) {
      this.removeAll(var1.asRanges());
   }

   public boolean intersects(Range<C> var1) {
      return !this.subRangeSet(var1).isEmpty();
   }

   public abstract boolean encloses(Range<C> var1);

   public boolean equals(@Nullable Object var1) {
      if (var1 == this) {
         return true;
      } else if (var1 instanceof RangeSet) {
         RangeSet var2 = (RangeSet)var1;
         return this.asRanges().equals(var2.asRanges());
      } else {
         return false;
      }
   }

   public final int hashCode() {
      return this.asRanges().hashCode();
   }

   public final String toString() {
      return this.asRanges().toString();
   }
}
