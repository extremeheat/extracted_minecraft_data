package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.SafeMath;
import java.util.Collection;
import java.util.Objects;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

public interface ByteCollection extends Collection<Byte>, ByteIterable {
   ByteIterator iterator();

   boolean add(byte var1);

   boolean contains(byte var1);

   boolean rem(byte var1);

   /** @deprecated */
   @Deprecated
   default boolean add(Byte var1) {
      return this.add(var1);
   }

   /** @deprecated */
   @Deprecated
   default boolean contains(Object var1) {
      return var1 == null ? false : this.contains((Byte)var1);
   }

   /** @deprecated */
   @Deprecated
   default boolean remove(Object var1) {
      return var1 == null ? false : this.rem((Byte)var1);
   }

   byte[] toByteArray();

   /** @deprecated */
   @Deprecated
   byte[] toByteArray(byte[] var1);

   byte[] toArray(byte[] var1);

   boolean addAll(ByteCollection var1);

   boolean containsAll(ByteCollection var1);

   boolean removeAll(ByteCollection var1);

   /** @deprecated */
   @Deprecated
   default boolean removeIf(Predicate<? super Byte> var1) {
      return this.removeIf((var1x) -> {
         return var1.test(SafeMath.safeIntToByte(var1x));
      });
   }

   default boolean removeIf(IntPredicate var1) {
      Objects.requireNonNull(var1);
      boolean var2 = false;
      ByteIterator var3 = this.iterator();

      while(var3.hasNext()) {
         if (var1.test(var3.nextByte())) {
            var3.remove();
            var2 = true;
         }
      }

      return var2;
   }

   boolean retainAll(ByteCollection var1);
}
