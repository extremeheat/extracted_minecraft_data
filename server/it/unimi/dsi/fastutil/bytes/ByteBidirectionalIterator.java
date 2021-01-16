package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public interface ByteBidirectionalIterator extends ByteIterator, ObjectBidirectionalIterator<Byte> {
   byte previousByte();

   /** @deprecated */
   @Deprecated
   default Byte previous() {
      return this.previousByte();
   }

   default int back(int var1) {
      int var2 = var1;

      while(var2-- != 0 && this.hasPrevious()) {
         this.previousByte();
      }

      return var1 - var2 - 1;
   }

   default int skip(int var1) {
      return ByteIterator.super.skip(var1);
   }
}
