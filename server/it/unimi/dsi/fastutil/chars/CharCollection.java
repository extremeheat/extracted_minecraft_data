package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.SafeMath;
import java.util.Collection;
import java.util.Objects;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

public interface CharCollection extends Collection<Character>, CharIterable {
   CharIterator iterator();

   boolean add(char var1);

   boolean contains(char var1);

   boolean rem(char var1);

   /** @deprecated */
   @Deprecated
   default boolean add(Character var1) {
      return this.add(var1);
   }

   /** @deprecated */
   @Deprecated
   default boolean contains(Object var1) {
      return var1 == null ? false : this.contains((Character)var1);
   }

   /** @deprecated */
   @Deprecated
   default boolean remove(Object var1) {
      return var1 == null ? false : this.rem((Character)var1);
   }

   char[] toCharArray();

   /** @deprecated */
   @Deprecated
   char[] toCharArray(char[] var1);

   char[] toArray(char[] var1);

   boolean addAll(CharCollection var1);

   boolean containsAll(CharCollection var1);

   boolean removeAll(CharCollection var1);

   /** @deprecated */
   @Deprecated
   default boolean removeIf(Predicate<? super Character> var1) {
      return this.removeIf((var1x) -> {
         return var1.test(SafeMath.safeIntToChar(var1x));
      });
   }

   default boolean removeIf(IntPredicate var1) {
      Objects.requireNonNull(var1);
      boolean var2 = false;
      CharIterator var3 = this.iterator();

      while(var3.hasNext()) {
         if (var1.test(var3.nextChar())) {
            var3.remove();
            var2 = true;
         }
      }

      return var2;
   }

   boolean retainAll(CharCollection var1);
}
