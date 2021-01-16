package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;

@Beta
@GwtIncompatible
public interface RangeSet<C extends Comparable> {
   boolean contains(C var1);

   Range<C> rangeContaining(C var1);

   boolean intersects(Range<C> var1);

   boolean encloses(Range<C> var1);

   boolean enclosesAll(RangeSet<C> var1);

   default boolean enclosesAll(Iterable<Range<C>> var1) {
      Iterator var2 = var1.iterator();

      Range var3;
      do {
         if (!var2.hasNext()) {
            return true;
         }

         var3 = (Range)var2.next();
      } while(this.encloses(var3));

      return false;
   }

   boolean isEmpty();

   Range<C> span();

   Set<Range<C>> asRanges();

   Set<Range<C>> asDescendingSetOfRanges();

   RangeSet<C> complement();

   RangeSet<C> subRangeSet(Range<C> var1);

   void add(Range<C> var1);

   void remove(Range<C> var1);

   void clear();

   void addAll(RangeSet<C> var1);

   default void addAll(Iterable<Range<C>> var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         Range var3 = (Range)var2.next();
         this.add(var3);
      }

   }

   void removeAll(RangeSet<C> var1);

   default void removeAll(Iterable<Range<C>> var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         Range var3 = (Range)var2.next();
         this.remove(var3);
      }

   }

   boolean equals(@Nullable Object var1);

   int hashCode();

   String toString();
}
