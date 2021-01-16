package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Booleans;
import java.io.Serializable;
import java.util.NoSuchElementException;
import javax.annotation.Nullable;

@GwtCompatible
abstract class Cut<C extends Comparable> implements Comparable<Cut<C>>, Serializable {
   final C endpoint;
   private static final long serialVersionUID = 0L;

   Cut(@Nullable C var1) {
      super();
      this.endpoint = var1;
   }

   abstract boolean isLessThan(C var1);

   abstract BoundType typeAsLowerBound();

   abstract BoundType typeAsUpperBound();

   abstract Cut<C> withLowerBoundType(BoundType var1, DiscreteDomain<C> var2);

   abstract Cut<C> withUpperBoundType(BoundType var1, DiscreteDomain<C> var2);

   abstract void describeAsLowerBound(StringBuilder var1);

   abstract void describeAsUpperBound(StringBuilder var1);

   abstract C leastValueAbove(DiscreteDomain<C> var1);

   abstract C greatestValueBelow(DiscreteDomain<C> var1);

   Cut<C> canonical(DiscreteDomain<C> var1) {
      return this;
   }

   public int compareTo(Cut<C> var1) {
      if (var1 == belowAll()) {
         return 1;
      } else if (var1 == aboveAll()) {
         return -1;
      } else {
         int var2 = Range.compareOrThrow(this.endpoint, var1.endpoint);
         return var2 != 0 ? var2 : Booleans.compare(this instanceof Cut.AboveValue, var1 instanceof Cut.AboveValue);
      }
   }

   C endpoint() {
      return this.endpoint;
   }

   public boolean equals(Object var1) {
      if (var1 instanceof Cut) {
         Cut var2 = (Cut)var1;

         try {
            int var3 = this.compareTo(var2);
            return var3 == 0;
         } catch (ClassCastException var4) {
         }
      }

      return false;
   }

   static <C extends Comparable> Cut<C> belowAll() {
      return Cut.BelowAll.INSTANCE;
   }

   static <C extends Comparable> Cut<C> aboveAll() {
      return Cut.AboveAll.INSTANCE;
   }

   static <C extends Comparable> Cut<C> belowValue(C var0) {
      return new Cut.BelowValue(var0);
   }

   static <C extends Comparable> Cut<C> aboveValue(C var0) {
      return new Cut.AboveValue(var0);
   }

   private static final class AboveValue<C extends Comparable> extends Cut<C> {
      private static final long serialVersionUID = 0L;

      AboveValue(C var1) {
         super((Comparable)Preconditions.checkNotNull(var1));
      }

      boolean isLessThan(C var1) {
         return Range.compareOrThrow(this.endpoint, var1) < 0;
      }

      BoundType typeAsLowerBound() {
         return BoundType.OPEN;
      }

      BoundType typeAsUpperBound() {
         return BoundType.CLOSED;
      }

      Cut<C> withLowerBoundType(BoundType var1, DiscreteDomain<C> var2) {
         switch(var1) {
         case CLOSED:
            Comparable var3 = var2.next(this.endpoint);
            return var3 == null ? Cut.belowAll() : belowValue(var3);
         case OPEN:
            return this;
         default:
            throw new AssertionError();
         }
      }

      Cut<C> withUpperBoundType(BoundType var1, DiscreteDomain<C> var2) {
         switch(var1) {
         case CLOSED:
            return this;
         case OPEN:
            Comparable var3 = var2.next(this.endpoint);
            return var3 == null ? Cut.aboveAll() : belowValue(var3);
         default:
            throw new AssertionError();
         }
      }

      void describeAsLowerBound(StringBuilder var1) {
         var1.append('(').append(this.endpoint);
      }

      void describeAsUpperBound(StringBuilder var1) {
         var1.append(this.endpoint).append(']');
      }

      C leastValueAbove(DiscreteDomain<C> var1) {
         return var1.next(this.endpoint);
      }

      C greatestValueBelow(DiscreteDomain<C> var1) {
         return this.endpoint;
      }

      Cut<C> canonical(DiscreteDomain<C> var1) {
         Comparable var2 = this.leastValueAbove(var1);
         return var2 != null ? belowValue(var2) : Cut.aboveAll();
      }

      public int hashCode() {
         return ~this.endpoint.hashCode();
      }

      public String toString() {
         return "/" + this.endpoint + "\\";
      }
   }

   private static final class BelowValue<C extends Comparable> extends Cut<C> {
      private static final long serialVersionUID = 0L;

      BelowValue(C var1) {
         super((Comparable)Preconditions.checkNotNull(var1));
      }

      boolean isLessThan(C var1) {
         return Range.compareOrThrow(this.endpoint, var1) <= 0;
      }

      BoundType typeAsLowerBound() {
         return BoundType.CLOSED;
      }

      BoundType typeAsUpperBound() {
         return BoundType.OPEN;
      }

      Cut<C> withLowerBoundType(BoundType var1, DiscreteDomain<C> var2) {
         switch(var1) {
         case CLOSED:
            return this;
         case OPEN:
            Comparable var3 = var2.previous(this.endpoint);
            return (Cut)(var3 == null ? Cut.belowAll() : new Cut.AboveValue(var3));
         default:
            throw new AssertionError();
         }
      }

      Cut<C> withUpperBoundType(BoundType var1, DiscreteDomain<C> var2) {
         switch(var1) {
         case CLOSED:
            Comparable var3 = var2.previous(this.endpoint);
            return (Cut)(var3 == null ? Cut.aboveAll() : new Cut.AboveValue(var3));
         case OPEN:
            return this;
         default:
            throw new AssertionError();
         }
      }

      void describeAsLowerBound(StringBuilder var1) {
         var1.append('[').append(this.endpoint);
      }

      void describeAsUpperBound(StringBuilder var1) {
         var1.append(this.endpoint).append(')');
      }

      C leastValueAbove(DiscreteDomain<C> var1) {
         return this.endpoint;
      }

      C greatestValueBelow(DiscreteDomain<C> var1) {
         return var1.previous(this.endpoint);
      }

      public int hashCode() {
         return this.endpoint.hashCode();
      }

      public String toString() {
         return "\\" + this.endpoint + "/";
      }
   }

   private static final class AboveAll extends Cut<Comparable<?>> {
      private static final Cut.AboveAll INSTANCE = new Cut.AboveAll();
      private static final long serialVersionUID = 0L;

      private AboveAll() {
         super((Comparable)null);
      }

      Comparable<?> endpoint() {
         throw new IllegalStateException("range unbounded on this side");
      }

      boolean isLessThan(Comparable<?> var1) {
         return false;
      }

      BoundType typeAsLowerBound() {
         throw new AssertionError("this statement should be unreachable");
      }

      BoundType typeAsUpperBound() {
         throw new IllegalStateException();
      }

      Cut<Comparable<?>> withLowerBoundType(BoundType var1, DiscreteDomain<Comparable<?>> var2) {
         throw new AssertionError("this statement should be unreachable");
      }

      Cut<Comparable<?>> withUpperBoundType(BoundType var1, DiscreteDomain<Comparable<?>> var2) {
         throw new IllegalStateException();
      }

      void describeAsLowerBound(StringBuilder var1) {
         throw new AssertionError();
      }

      void describeAsUpperBound(StringBuilder var1) {
         var1.append("+\u221e)");
      }

      Comparable<?> leastValueAbove(DiscreteDomain<Comparable<?>> var1) {
         throw new AssertionError();
      }

      Comparable<?> greatestValueBelow(DiscreteDomain<Comparable<?>> var1) {
         return var1.maxValue();
      }

      public int compareTo(Cut<Comparable<?>> var1) {
         return var1 == this ? 0 : 1;
      }

      public String toString() {
         return "+\u221e";
      }

      private Object readResolve() {
         return INSTANCE;
      }
   }

   private static final class BelowAll extends Cut<Comparable<?>> {
      private static final Cut.BelowAll INSTANCE = new Cut.BelowAll();
      private static final long serialVersionUID = 0L;

      private BelowAll() {
         super((Comparable)null);
      }

      Comparable<?> endpoint() {
         throw new IllegalStateException("range unbounded on this side");
      }

      boolean isLessThan(Comparable<?> var1) {
         return true;
      }

      BoundType typeAsLowerBound() {
         throw new IllegalStateException();
      }

      BoundType typeAsUpperBound() {
         throw new AssertionError("this statement should be unreachable");
      }

      Cut<Comparable<?>> withLowerBoundType(BoundType var1, DiscreteDomain<Comparable<?>> var2) {
         throw new IllegalStateException();
      }

      Cut<Comparable<?>> withUpperBoundType(BoundType var1, DiscreteDomain<Comparable<?>> var2) {
         throw new AssertionError("this statement should be unreachable");
      }

      void describeAsLowerBound(StringBuilder var1) {
         var1.append("(-\u221e");
      }

      void describeAsUpperBound(StringBuilder var1) {
         throw new AssertionError();
      }

      Comparable<?> leastValueAbove(DiscreteDomain<Comparable<?>> var1) {
         return var1.minValue();
      }

      Comparable<?> greatestValueBelow(DiscreteDomain<Comparable<?>> var1) {
         throw new AssertionError();
      }

      Cut<Comparable<?>> canonical(DiscreteDomain<Comparable<?>> var1) {
         try {
            return Cut.belowValue(var1.minValue());
         } catch (NoSuchElementException var3) {
            return this;
         }
      }

      public int compareTo(Cut<Comparable<?>> var1) {
         return var1 == this ? 0 : -1;
      }

      public String toString() {
         return "-\u221e";
      }

      private Object readResolve() {
         return INSTANCE;
      }
   }
}
