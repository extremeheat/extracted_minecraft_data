package it.unimi.dsi.fastutil.chars;

import java.util.List;

public interface CharList extends List<Character>, Comparable<List<? extends Character>>, CharCollection {
   CharListIterator iterator();

   CharListIterator listIterator();

   CharListIterator listIterator(int var1);

   CharList subList(int var1, int var2);

   void size(int var1);

   void getElements(int var1, char[] var2, int var3, int var4);

   void removeElements(int var1, int var2);

   void addElements(int var1, char[] var2);

   void addElements(int var1, char[] var2, int var3, int var4);

   boolean add(char var1);

   void add(int var1, char var2);

   /** @deprecated */
   @Deprecated
   default void add(int var1, Character var2) {
      this.add(var1, var2);
   }

   boolean addAll(int var1, CharCollection var2);

   boolean addAll(int var1, CharList var2);

   boolean addAll(CharList var1);

   char set(int var1, char var2);

   char getChar(int var1);

   int indexOf(char var1);

   int lastIndexOf(char var1);

   /** @deprecated */
   @Deprecated
   default boolean contains(Object var1) {
      return CharCollection.super.contains(var1);
   }

   /** @deprecated */
   @Deprecated
   default Character get(int var1) {
      return this.getChar(var1);
   }

   /** @deprecated */
   @Deprecated
   default int indexOf(Object var1) {
      return this.indexOf((Character)var1);
   }

   /** @deprecated */
   @Deprecated
   default int lastIndexOf(Object var1) {
      return this.lastIndexOf((Character)var1);
   }

   /** @deprecated */
   @Deprecated
   default boolean add(Character var1) {
      return this.add(var1);
   }

   char removeChar(int var1);

   /** @deprecated */
   @Deprecated
   default boolean remove(Object var1) {
      return CharCollection.super.remove(var1);
   }

   /** @deprecated */
   @Deprecated
   default Character remove(int var1) {
      return this.removeChar(var1);
   }

   /** @deprecated */
   @Deprecated
   default Character set(int var1, Character var2) {
      return this.set(var1, var2);
   }
}
