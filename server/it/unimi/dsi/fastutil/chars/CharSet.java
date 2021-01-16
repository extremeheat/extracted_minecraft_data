package it.unimi.dsi.fastutil.chars;

import java.util.Set;

public interface CharSet extends CharCollection, Set<Character> {
   CharIterator iterator();

   boolean remove(char var1);

   /** @deprecated */
   @Deprecated
   default boolean remove(Object var1) {
      return CharCollection.super.remove(var1);
   }

   /** @deprecated */
   @Deprecated
   default boolean add(Character var1) {
      return CharCollection.super.add(var1);
   }

   /** @deprecated */
   @Deprecated
   default boolean contains(Object var1) {
      return CharCollection.super.contains(var1);
   }

   /** @deprecated */
   @Deprecated
   default boolean rem(char var1) {
      return this.remove(var1);
   }
}
