package it.unimi.dsi.fastutil.bytes;

import java.util.AbstractCollection;

public abstract class AbstractByteCollection extends AbstractCollection<Byte> implements ByteCollection {
   protected AbstractByteCollection() {
      super();
   }

   public abstract ByteIterator iterator();

   public boolean add(byte var1) {
      throw new UnsupportedOperationException();
   }

   public boolean contains(byte var1) {
      ByteIterator var2 = this.iterator();

      do {
         if (!var2.hasNext()) {
            return false;
         }
      } while(var1 != var2.nextByte());

      return true;
   }

   public boolean rem(byte var1) {
      ByteIterator var2 = this.iterator();

      do {
         if (!var2.hasNext()) {
            return false;
         }
      } while(var1 != var2.nextByte());

      var2.remove();
      return true;
   }

   /** @deprecated */
   @Deprecated
   public boolean add(Byte var1) {
      return ByteCollection.super.add(var1);
   }

   /** @deprecated */
   @Deprecated
   public boolean contains(Object var1) {
      return ByteCollection.super.contains(var1);
   }

   /** @deprecated */
   @Deprecated
   public boolean remove(Object var1) {
      return ByteCollection.super.remove(var1);
   }

   public byte[] toArray(byte[] var1) {
      if (var1 == null || var1.length < this.size()) {
         var1 = new byte[this.size()];
      }

      ByteIterators.unwrap(this.iterator(), var1);
      return var1;
   }

   public byte[] toByteArray() {
      return this.toArray((byte[])null);
   }

   /** @deprecated */
   @Deprecated
   public byte[] toByteArray(byte[] var1) {
      return this.toArray(var1);
   }

   public boolean addAll(ByteCollection var1) {
      boolean var2 = false;
      ByteIterator var3 = var1.iterator();

      while(var3.hasNext()) {
         if (this.add(var3.nextByte())) {
            var2 = true;
         }
      }

      return var2;
   }

   public boolean containsAll(ByteCollection var1) {
      ByteIterator var2 = var1.iterator();

      do {
         if (!var2.hasNext()) {
            return true;
         }
      } while(this.contains(var2.nextByte()));

      return false;
   }

   public boolean removeAll(ByteCollection var1) {
      boolean var2 = false;
      ByteIterator var3 = var1.iterator();

      while(var3.hasNext()) {
         if (this.rem(var3.nextByte())) {
            var2 = true;
         }
      }

      return var2;
   }

   public boolean retainAll(ByteCollection var1) {
      boolean var2 = false;
      ByteIterator var3 = this.iterator();

      while(var3.hasNext()) {
         if (!var1.contains(var3.nextByte())) {
            var3.remove();
            var2 = true;
         }
      }

      return var2;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      ByteIterator var2 = this.iterator();
      int var3 = this.size();
      boolean var5 = true;
      var1.append("{");

      while(var3-- != 0) {
         if (var5) {
            var5 = false;
         } else {
            var1.append(", ");
         }

         byte var4 = var2.nextByte();
         var1.append(String.valueOf(var4));
      }

      var1.append("}");
      return var1.toString();
   }
}
