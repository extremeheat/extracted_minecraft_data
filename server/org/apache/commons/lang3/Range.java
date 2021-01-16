package org.apache.commons.lang3;

import java.io.Serializable;
import java.util.Comparator;

public final class Range<T> implements Serializable {
   private static final long serialVersionUID = 1L;
   private final Comparator<T> comparator;
   private final T minimum;
   private final T maximum;
   private transient int hashCode;
   private transient String toString;

   public static <T extends Comparable<T>> Range<T> is(T var0) {
      return between(var0, var0, (Comparator)null);
   }

   public static <T> Range<T> is(T var0, Comparator<T> var1) {
      return between(var0, var0, var1);
   }

   public static <T extends Comparable<T>> Range<T> between(T var0, T var1) {
      return between(var0, var1, (Comparator)null);
   }

   public static <T> Range<T> between(T var0, T var1, Comparator<T> var2) {
      return new Range(var0, var1, var2);
   }

   private Range(T var1, T var2, Comparator<T> var3) {
      super();
      if (var1 != null && var2 != null) {
         if (var3 == null) {
            this.comparator = Range.ComparableComparator.INSTANCE;
         } else {
            this.comparator = var3;
         }

         if (this.comparator.compare(var1, var2) < 1) {
            this.minimum = var1;
            this.maximum = var2;
         } else {
            this.minimum = var2;
            this.maximum = var1;
         }

      } else {
         throw new IllegalArgumentException("Elements in a range must not be null: element1=" + var1 + ", element2=" + var2);
      }
   }

   public T getMinimum() {
      return this.minimum;
   }

   public T getMaximum() {
      return this.maximum;
   }

   public Comparator<T> getComparator() {
      return this.comparator;
   }

   public boolean isNaturalOrdering() {
      return this.comparator == Range.ComparableComparator.INSTANCE;
   }

   public boolean contains(T var1) {
      if (var1 == null) {
         return false;
      } else {
         return this.comparator.compare(var1, this.minimum) > -1 && this.comparator.compare(var1, this.maximum) < 1;
      }
   }

   public boolean isAfter(T var1) {
      if (var1 == null) {
         return false;
      } else {
         return this.comparator.compare(var1, this.minimum) < 0;
      }
   }

   public boolean isStartedBy(T var1) {
      if (var1 == null) {
         return false;
      } else {
         return this.comparator.compare(var1, this.minimum) == 0;
      }
   }

   public boolean isEndedBy(T var1) {
      if (var1 == null) {
         return false;
      } else {
         return this.comparator.compare(var1, this.maximum) == 0;
      }
   }

   public boolean isBefore(T var1) {
      if (var1 == null) {
         return false;
      } else {
         return this.comparator.compare(var1, this.maximum) > 0;
      }
   }

   public int elementCompareTo(T var1) {
      if (var1 == null) {
         throw new NullPointerException("Element is null");
      } else if (this.isAfter(var1)) {
         return -1;
      } else {
         return this.isBefore(var1) ? 1 : 0;
      }
   }

   public boolean containsRange(Range<T> var1) {
      if (var1 == null) {
         return false;
      } else {
         return this.contains(var1.minimum) && this.contains(var1.maximum);
      }
   }

   public boolean isAfterRange(Range<T> var1) {
      return var1 == null ? false : this.isAfter(var1.maximum);
   }

   public boolean isOverlappedBy(Range<T> var1) {
      if (var1 == null) {
         return false;
      } else {
         return var1.contains(this.minimum) || var1.contains(this.maximum) || this.contains(var1.minimum);
      }
   }

   public boolean isBeforeRange(Range<T> var1) {
      return var1 == null ? false : this.isBefore(var1.minimum);
   }

   public Range<T> intersectionWith(Range<T> var1) {
      if (!this.isOverlappedBy(var1)) {
         throw new IllegalArgumentException(String.format("Cannot calculate intersection with non-overlapping range %s", var1));
      } else if (this.equals(var1)) {
         return this;
      } else {
         Object var2 = this.getComparator().compare(this.minimum, var1.minimum) < 0 ? var1.minimum : this.minimum;
         Object var3 = this.getComparator().compare(this.maximum, var1.maximum) < 0 ? this.maximum : var1.maximum;
         return between(var2, var3, this.getComparator());
      }
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (var1 != null && var1.getClass() == this.getClass()) {
         Range var2 = (Range)var1;
         return this.minimum.equals(var2.minimum) && this.maximum.equals(var2.maximum);
      } else {
         return false;
      }
   }

   public int hashCode() {
      int var1 = this.hashCode;
      if (this.hashCode == 0) {
         byte var2 = 17;
         var1 = 37 * var2 + this.getClass().hashCode();
         var1 = 37 * var1 + this.minimum.hashCode();
         var1 = 37 * var1 + this.maximum.hashCode();
         this.hashCode = var1;
      }

      return var1;
   }

   public String toString() {
      if (this.toString == null) {
         this.toString = "[" + this.minimum + ".." + this.maximum + "]";
      }

      return this.toString;
   }

   public String toString(String var1) {
      return String.format(var1, this.minimum, this.maximum, this.comparator);
   }

   private static enum ComparableComparator implements Comparator {
      INSTANCE;

      private ComparableComparator() {
      }

      public int compare(Object var1, Object var2) {
         return ((Comparable)var1).compareTo(var2);
      }
   }
}
