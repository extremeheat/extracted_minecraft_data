package it.unimi.dsi.fastutil.bytes;

import java.util.Set;

public interface ByteSet extends ByteCollection, Set<Byte> {
   ByteIterator iterator();

   boolean remove(byte var1);

   /** @deprecated */
   @Deprecated
   default boolean remove(Object var1) {
      return ByteCollection.super.remove(var1);
   }

   /** @deprecated */
   @Deprecated
   default boolean add(Byte var1) {
      return ByteCollection.super.add(var1);
   }

   /** @deprecated */
   @Deprecated
   default boolean contains(Object var1) {
      return ByteCollection.super.contains(var1);
   }

   /** @deprecated */
   @Deprecated
   default boolean rem(byte var1) {
      return this.remove(var1);
   }
}
