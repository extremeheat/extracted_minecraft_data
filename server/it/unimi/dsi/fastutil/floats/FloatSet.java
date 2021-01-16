package it.unimi.dsi.fastutil.floats;

import java.util.Set;

public interface FloatSet extends FloatCollection, Set<Float> {
   FloatIterator iterator();

   boolean remove(float var1);

   /** @deprecated */
   @Deprecated
   default boolean remove(Object var1) {
      return FloatCollection.super.remove(var1);
   }

   /** @deprecated */
   @Deprecated
   default boolean add(Float var1) {
      return FloatCollection.super.add(var1);
   }

   /** @deprecated */
   @Deprecated
   default boolean contains(Object var1) {
      return FloatCollection.super.contains(var1);
   }

   /** @deprecated */
   @Deprecated
   default boolean rem(float var1) {
      return this.remove(var1);
   }
}
