package it.unimi.dsi.fastutil.floats;

import java.util.AbstractCollection;

public abstract class AbstractFloatCollection extends AbstractCollection<Float> implements FloatCollection {
   protected AbstractFloatCollection() {
      super();
   }

   public abstract FloatIterator iterator();

   public boolean add(float var1) {
      throw new UnsupportedOperationException();
   }

   public boolean contains(float var1) {
      FloatIterator var2 = this.iterator();

      do {
         if (!var2.hasNext()) {
            return false;
         }
      } while(var1 != var2.nextFloat());

      return true;
   }

   public boolean rem(float var1) {
      FloatIterator var2 = this.iterator();

      do {
         if (!var2.hasNext()) {
            return false;
         }
      } while(var1 != var2.nextFloat());

      var2.remove();
      return true;
   }

   /** @deprecated */
   @Deprecated
   public boolean add(Float var1) {
      return FloatCollection.super.add(var1);
   }

   /** @deprecated */
   @Deprecated
   public boolean contains(Object var1) {
      return FloatCollection.super.contains(var1);
   }

   /** @deprecated */
   @Deprecated
   public boolean remove(Object var1) {
      return FloatCollection.super.remove(var1);
   }

   public float[] toArray(float[] var1) {
      if (var1 == null || var1.length < this.size()) {
         var1 = new float[this.size()];
      }

      FloatIterators.unwrap(this.iterator(), var1);
      return var1;
   }

   public float[] toFloatArray() {
      return this.toArray((float[])null);
   }

   /** @deprecated */
   @Deprecated
   public float[] toFloatArray(float[] var1) {
      return this.toArray(var1);
   }

   public boolean addAll(FloatCollection var1) {
      boolean var2 = false;
      FloatIterator var3 = var1.iterator();

      while(var3.hasNext()) {
         if (this.add(var3.nextFloat())) {
            var2 = true;
         }
      }

      return var2;
   }

   public boolean containsAll(FloatCollection var1) {
      FloatIterator var2 = var1.iterator();

      do {
         if (!var2.hasNext()) {
            return true;
         }
      } while(this.contains(var2.nextFloat()));

      return false;
   }

   public boolean removeAll(FloatCollection var1) {
      boolean var2 = false;
      FloatIterator var3 = var1.iterator();

      while(var3.hasNext()) {
         if (this.rem(var3.nextFloat())) {
            var2 = true;
         }
      }

      return var2;
   }

   public boolean retainAll(FloatCollection var1) {
      boolean var2 = false;
      FloatIterator var3 = this.iterator();

      while(var3.hasNext()) {
         if (!var1.contains(var3.nextFloat())) {
            var3.remove();
            var2 = true;
         }
      }

      return var2;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      FloatIterator var2 = this.iterator();
      int var3 = this.size();
      boolean var5 = true;
      var1.append("{");

      while(var3-- != 0) {
         if (var5) {
            var5 = false;
         } else {
            var1.append(", ");
         }

         float var4 = var2.nextFloat();
         var1.append(String.valueOf(var4));
      }

      var1.append("}");
      return var1.toString();
   }
}
