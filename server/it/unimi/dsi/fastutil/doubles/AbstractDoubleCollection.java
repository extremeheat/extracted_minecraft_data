package it.unimi.dsi.fastutil.doubles;

import java.util.AbstractCollection;

public abstract class AbstractDoubleCollection extends AbstractCollection<Double> implements DoubleCollection {
   protected AbstractDoubleCollection() {
      super();
   }

   public abstract DoubleIterator iterator();

   public boolean add(double var1) {
      throw new UnsupportedOperationException();
   }

   public boolean contains(double var1) {
      DoubleIterator var3 = this.iterator();

      do {
         if (!var3.hasNext()) {
            return false;
         }
      } while(var1 != var3.nextDouble());

      return true;
   }

   public boolean rem(double var1) {
      DoubleIterator var3 = this.iterator();

      do {
         if (!var3.hasNext()) {
            return false;
         }
      } while(var1 != var3.nextDouble());

      var3.remove();
      return true;
   }

   /** @deprecated */
   @Deprecated
   public boolean add(Double var1) {
      return DoubleCollection.super.add(var1);
   }

   /** @deprecated */
   @Deprecated
   public boolean contains(Object var1) {
      return DoubleCollection.super.contains(var1);
   }

   /** @deprecated */
   @Deprecated
   public boolean remove(Object var1) {
      return DoubleCollection.super.remove(var1);
   }

   public double[] toArray(double[] var1) {
      if (var1 == null || var1.length < this.size()) {
         var1 = new double[this.size()];
      }

      DoubleIterators.unwrap(this.iterator(), var1);
      return var1;
   }

   public double[] toDoubleArray() {
      return this.toArray((double[])null);
   }

   /** @deprecated */
   @Deprecated
   public double[] toDoubleArray(double[] var1) {
      return this.toArray(var1);
   }

   public boolean addAll(DoubleCollection var1) {
      boolean var2 = false;
      DoubleIterator var3 = var1.iterator();

      while(var3.hasNext()) {
         if (this.add(var3.nextDouble())) {
            var2 = true;
         }
      }

      return var2;
   }

   public boolean containsAll(DoubleCollection var1) {
      DoubleIterator var2 = var1.iterator();

      do {
         if (!var2.hasNext()) {
            return true;
         }
      } while(this.contains(var2.nextDouble()));

      return false;
   }

   public boolean removeAll(DoubleCollection var1) {
      boolean var2 = false;
      DoubleIterator var3 = var1.iterator();

      while(var3.hasNext()) {
         if (this.rem(var3.nextDouble())) {
            var2 = true;
         }
      }

      return var2;
   }

   public boolean retainAll(DoubleCollection var1) {
      boolean var2 = false;
      DoubleIterator var3 = this.iterator();

      while(var3.hasNext()) {
         if (!var1.contains(var3.nextDouble())) {
            var3.remove();
            var2 = true;
         }
      }

      return var2;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      DoubleIterator var2 = this.iterator();
      int var3 = this.size();
      boolean var6 = true;
      var1.append("{");

      while(var3-- != 0) {
         if (var6) {
            var6 = false;
         } else {
            var1.append(", ");
         }

         double var4 = var2.nextDouble();
         var1.append(String.valueOf(var4));
      }

      var1.append("}");
      return var1.toString();
   }
}
