package it.unimi.dsi.fastutil.bytes;

import java.util.List;

public interface ByteList extends List<Byte>, Comparable<List<? extends Byte>>, ByteCollection {
   ByteListIterator iterator();

   ByteListIterator listIterator();

   ByteListIterator listIterator(int var1);

   ByteList subList(int var1, int var2);

   void size(int var1);

   void getElements(int var1, byte[] var2, int var3, int var4);

   void removeElements(int var1, int var2);

   void addElements(int var1, byte[] var2);

   void addElements(int var1, byte[] var2, int var3, int var4);

   boolean add(byte var1);

   void add(int var1, byte var2);

   /** @deprecated */
   @Deprecated
   default void add(int var1, Byte var2) {
      this.add(var1, var2);
   }

   boolean addAll(int var1, ByteCollection var2);

   boolean addAll(int var1, ByteList var2);

   boolean addAll(ByteList var1);

   byte set(int var1, byte var2);

   byte getByte(int var1);

   int indexOf(byte var1);

   int lastIndexOf(byte var1);

   /** @deprecated */
   @Deprecated
   default boolean contains(Object var1) {
      return ByteCollection.super.contains(var1);
   }

   /** @deprecated */
   @Deprecated
   default Byte get(int var1) {
      return this.getByte(var1);
   }

   /** @deprecated */
   @Deprecated
   default int indexOf(Object var1) {
      return this.indexOf((Byte)var1);
   }

   /** @deprecated */
   @Deprecated
   default int lastIndexOf(Object var1) {
      return this.lastIndexOf((Byte)var1);
   }

   /** @deprecated */
   @Deprecated
   default boolean add(Byte var1) {
      return this.add(var1);
   }

   byte removeByte(int var1);

   /** @deprecated */
   @Deprecated
   default boolean remove(Object var1) {
      return ByteCollection.super.remove(var1);
   }

   /** @deprecated */
   @Deprecated
   default Byte remove(int var1) {
      return this.removeByte(var1);
   }

   /** @deprecated */
   @Deprecated
   default Byte set(int var1, Byte var2) {
      return this.set(var1, var2);
   }
}
