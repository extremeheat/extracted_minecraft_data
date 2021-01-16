package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.Size64;

public interface CharBigList extends BigList<Character>, CharCollection, Size64, Comparable<BigList<? extends Character>> {
   CharBigListIterator iterator();

   CharBigListIterator listIterator();

   CharBigListIterator listIterator(long var1);

   CharBigList subList(long var1, long var3);

   void getElements(long var1, char[][] var3, long var4, long var6);

   void removeElements(long var1, long var3);

   void addElements(long var1, char[][] var3);

   void addElements(long var1, char[][] var3, long var4, long var6);

   void add(long var1, char var3);

   boolean addAll(long var1, CharCollection var3);

   boolean addAll(long var1, CharBigList var3);

   boolean addAll(CharBigList var1);

   char getChar(long var1);

   char removeChar(long var1);

   char set(long var1, char var3);

   long indexOf(char var1);

   long lastIndexOf(char var1);

   /** @deprecated */
   @Deprecated
   void add(long var1, Character var3);

   /** @deprecated */
   @Deprecated
   Character get(long var1);

   /** @deprecated */
   @Deprecated
   long indexOf(Object var1);

   /** @deprecated */
   @Deprecated
   long lastIndexOf(Object var1);

   /** @deprecated */
   @Deprecated
   Character remove(long var1);

   /** @deprecated */
   @Deprecated
   Character set(long var1, Character var3);
}
