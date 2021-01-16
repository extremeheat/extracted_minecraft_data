package it.unimi.dsi.fastutil.bytes;

import java.util.SortedSet;

public interface ByteSortedSet extends ByteSet, SortedSet<Byte>, ByteBidirectionalIterable {
   ByteBidirectionalIterator iterator(byte var1);

   ByteBidirectionalIterator iterator();

   ByteSortedSet subSet(byte var1, byte var2);

   ByteSortedSet headSet(byte var1);

   ByteSortedSet tailSet(byte var1);

   ByteComparator comparator();

   byte firstByte();

   byte lastByte();

   /** @deprecated */
   @Deprecated
   default ByteSortedSet subSet(Byte var1, Byte var2) {
      return this.subSet(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default ByteSortedSet headSet(Byte var1) {
      return this.headSet(var1);
   }

   /** @deprecated */
   @Deprecated
   default ByteSortedSet tailSet(Byte var1) {
      return this.tailSet(var1);
   }

   /** @deprecated */
   @Deprecated
   default Byte first() {
      return this.firstByte();
   }

   /** @deprecated */
   @Deprecated
   default Byte last() {
      return this.lastByte();
   }
}
