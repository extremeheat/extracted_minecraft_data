package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.Size64;

public interface ByteBigList extends BigList<Byte>, ByteCollection, Size64, Comparable<BigList<? extends Byte>> {
   ByteBigListIterator iterator();

   ByteBigListIterator listIterator();

   ByteBigListIterator listIterator(long var1);

   ByteBigList subList(long var1, long var3);

   void getElements(long var1, byte[][] var3, long var4, long var6);

   void removeElements(long var1, long var3);

   void addElements(long var1, byte[][] var3);

   void addElements(long var1, byte[][] var3, long var4, long var6);

   void add(long var1, byte var3);

   boolean addAll(long var1, ByteCollection var3);

   boolean addAll(long var1, ByteBigList var3);

   boolean addAll(ByteBigList var1);

   byte getByte(long var1);

   byte removeByte(long var1);

   byte set(long var1, byte var3);

   long indexOf(byte var1);

   long lastIndexOf(byte var1);

   /** @deprecated */
   @Deprecated
   void add(long var1, Byte var3);

   /** @deprecated */
   @Deprecated
   Byte get(long var1);

   /** @deprecated */
   @Deprecated
   long indexOf(Object var1);

   /** @deprecated */
   @Deprecated
   long lastIndexOf(Object var1);

   /** @deprecated */
   @Deprecated
   Byte remove(long var1);

   /** @deprecated */
   @Deprecated
   Byte set(long var1, Byte var3);
}
