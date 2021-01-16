package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.SafeMath;
import java.util.Collection;
import java.util.Objects;
import java.util.function.DoublePredicate;
import java.util.function.Predicate;

public interface FloatCollection extends Collection<Float>, FloatIterable {
   FloatIterator iterator();

   boolean add(float var1);

   boolean contains(float var1);

   boolean rem(float var1);

   /** @deprecated */
   @Deprecated
   default boolean add(Float var1) {
      return this.add(var1);
   }

   /** @deprecated */
   @Deprecated
   default boolean contains(Object var1) {
      return var1 == null ? false : this.contains((Float)var1);
   }

   /** @deprecated */
   @Deprecated
   default boolean remove(Object var1) {
      return var1 == null ? false : this.rem((Float)var1);
   }

   float[] toFloatArray();

   /** @deprecated */
   @Deprecated
   float[] toFloatArray(float[] var1);

   float[] toArray(float[] var1);

   boolean addAll(FloatCollection var1);

   boolean containsAll(FloatCollection var1);

   boolean removeAll(FloatCollection var1);

   /** @deprecated */
   @Deprecated
   default boolean removeIf(Predicate<? super Float> var1) {
      return this.removeIf((var1x) -> {
         return var1.test(SafeMath.safeDoubleToFloat(var1x));
      });
   }

   default boolean removeIf(DoublePredicate var1) {
      Objects.requireNonNull(var1);
      boolean var2 = false;
      FloatIterator var3 = this.iterator();

      while(var3.hasNext()) {
         if (var1.test((double)var3.nextFloat())) {
            var3.remove();
            var2 = true;
         }
      }

      return var2;
   }

   boolean retainAll(FloatCollection var1);
}
