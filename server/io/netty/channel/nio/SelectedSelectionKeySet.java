package io.netty.channel.nio;

import java.nio.channels.SelectionKey;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Iterator;

final class SelectedSelectionKeySet extends AbstractSet<SelectionKey> {
   SelectionKey[] keys = new SelectionKey[1024];
   int size;

   SelectedSelectionKeySet() {
      super();
   }

   public boolean add(SelectionKey var1) {
      if (var1 == null) {
         return false;
      } else {
         this.keys[this.size++] = var1;
         if (this.size == this.keys.length) {
            this.increaseCapacity();
         }

         return true;
      }
   }

   public int size() {
      return this.size;
   }

   public boolean remove(Object var1) {
      return false;
   }

   public boolean contains(Object var1) {
      return false;
   }

   public Iterator<SelectionKey> iterator() {
      throw new UnsupportedOperationException();
   }

   void reset() {
      this.reset(0);
   }

   void reset(int var1) {
      Arrays.fill(this.keys, var1, this.size, (Object)null);
      this.size = 0;
   }

   private void increaseCapacity() {
      SelectionKey[] var1 = new SelectionKey[this.keys.length << 1];
      System.arraycopy(this.keys, 0, var1, 0, this.size);
      this.keys = var1;
   }
}
