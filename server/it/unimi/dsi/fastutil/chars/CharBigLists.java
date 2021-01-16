package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.BigList;
import java.io.Serializable;
import java.util.Collection;
import java.util.Random;

public final class CharBigLists {
   public static final CharBigLists.EmptyBigList EMPTY_BIG_LIST = new CharBigLists.EmptyBigList();

   private CharBigLists() {
      super();
   }

   public static CharBigList shuffle(CharBigList var0, Random var1) {
      long var2 = var0.size64();

      while(var2-- != 0L) {
         long var4 = (var1.nextLong() & 9223372036854775807L) % (var2 + 1L);
         char var6 = var0.getChar(var2);
         var0.set(var2, var0.getChar(var4));
         var0.set(var4, var6);
      }

      return var0;
   }

   public static CharBigList singleton(char var0) {
      return new CharBigLists.Singleton(var0);
   }

   public static CharBigList singleton(Object var0) {
      return new CharBigLists.Singleton((Character)var0);
   }

   public static CharBigList synchronize(CharBigList var0) {
      return new CharBigLists.SynchronizedBigList(var0);
   }

   public static CharBigList synchronize(CharBigList var0, Object var1) {
      return new CharBigLists.SynchronizedBigList(var0, var1);
   }

   public static CharBigList unmodifiable(CharBigList var0) {
      return new CharBigLists.UnmodifiableBigList(var0);
   }

   public static CharBigList asBigList(CharList var0) {
      return new CharBigLists.ListBigList(var0);
   }

   public static class ListBigList extends AbstractCharBigList implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      private final CharList list;

      protected ListBigList(CharList var1) {
         super();
         this.list = var1;
      }

      private int intIndex(long var1) {
         if (var1 >= 2147483647L) {
            throw new IndexOutOfBoundsException("This big list is restricted to 32-bit indices");
         } else {
            return (int)var1;
         }
      }

      public long size64() {
         return (long)this.list.size();
      }

      public void size(long var1) {
         this.list.size(this.intIndex(var1));
      }

      public CharBigListIterator iterator() {
         return CharBigListIterators.asBigListIterator(this.list.iterator());
      }

      public CharBigListIterator listIterator() {
         return CharBigListIterators.asBigListIterator(this.list.listIterator());
      }

      public CharBigListIterator listIterator(long var1) {
         return CharBigListIterators.asBigListIterator(this.list.listIterator(this.intIndex(var1)));
      }

      public boolean addAll(long var1, Collection<? extends Character> var3) {
         return this.list.addAll(this.intIndex(var1), (Collection)var3);
      }

      public CharBigList subList(long var1, long var3) {
         return new CharBigLists.ListBigList(this.list.subList(this.intIndex(var1), this.intIndex(var3)));
      }

      public boolean contains(char var1) {
         return this.list.contains(var1);
      }

      public char[] toCharArray() {
         return this.list.toCharArray();
      }

      public void removeElements(long var1, long var3) {
         this.list.removeElements(this.intIndex(var1), this.intIndex(var3));
      }

      /** @deprecated */
      @Deprecated
      public char[] toCharArray(char[] var1) {
         return this.list.toArray(var1);
      }

      public boolean addAll(long var1, CharCollection var3) {
         return this.list.addAll(this.intIndex(var1), var3);
      }

      public boolean addAll(CharCollection var1) {
         return this.list.addAll(var1);
      }

      public boolean addAll(long var1, CharBigList var3) {
         return this.list.addAll(this.intIndex(var1), (CharCollection)var3);
      }

      public boolean addAll(CharBigList var1) {
         return this.list.addAll(var1);
      }

      public boolean containsAll(CharCollection var1) {
         return this.list.containsAll(var1);
      }

      public boolean removeAll(CharCollection var1) {
         return this.list.removeAll(var1);
      }

      public boolean retainAll(CharCollection var1) {
         return this.list.retainAll(var1);
      }

      public void add(long var1, char var3) {
         this.list.add(this.intIndex(var1), var3);
      }

      public boolean add(char var1) {
         return this.list.add(var1);
      }

      public char getChar(long var1) {
         return this.list.getChar(this.intIndex(var1));
      }

      public long indexOf(char var1) {
         return (long)this.list.indexOf(var1);
      }

      public long lastIndexOf(char var1) {
         return (long)this.list.lastIndexOf(var1);
      }

      public char removeChar(long var1) {
         return this.list.removeChar(this.intIndex(var1));
      }

      public char set(long var1, char var3) {
         return this.list.set(this.intIndex(var1), var3);
      }

      public boolean isEmpty() {
         return this.list.isEmpty();
      }

      public <T> T[] toArray(T[] var1) {
         return this.list.toArray(var1);
      }

      public boolean containsAll(Collection<?> var1) {
         return this.list.containsAll(var1);
      }

      public boolean addAll(Collection<? extends Character> var1) {
         return this.list.addAll(var1);
      }

      public boolean removeAll(Collection<?> var1) {
         return this.list.removeAll(var1);
      }

      public boolean retainAll(Collection<?> var1) {
         return this.list.retainAll(var1);
      }

      public void clear() {
         this.list.clear();
      }

      public int hashCode() {
         return this.list.hashCode();
      }
   }

   public static class UnmodifiableBigList extends CharCollections.UnmodifiableCollection implements CharBigList, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final CharBigList list;

      protected UnmodifiableBigList(CharBigList var1) {
         super(var1);
         this.list = var1;
      }

      public char getChar(long var1) {
         return this.list.getChar(var1);
      }

      public char set(long var1, char var3) {
         throw new UnsupportedOperationException();
      }

      public void add(long var1, char var3) {
         throw new UnsupportedOperationException();
      }

      public char removeChar(long var1) {
         throw new UnsupportedOperationException();
      }

      public long indexOf(char var1) {
         return this.list.indexOf(var1);
      }

      public long lastIndexOf(char var1) {
         return this.list.lastIndexOf(var1);
      }

      public boolean addAll(long var1, Collection<? extends Character> var3) {
         throw new UnsupportedOperationException();
      }

      public void getElements(long var1, char[][] var3, long var4, long var6) {
         this.list.getElements(var1, var3, var4, var6);
      }

      public void removeElements(long var1, long var3) {
         throw new UnsupportedOperationException();
      }

      public void addElements(long var1, char[][] var3, long var4, long var6) {
         throw new UnsupportedOperationException();
      }

      public void addElements(long var1, char[][] var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public void size(long var1) {
         this.list.size(var1);
      }

      public long size64() {
         return this.list.size64();
      }

      public CharBigListIterator iterator() {
         return this.listIterator();
      }

      public CharBigListIterator listIterator() {
         return CharBigListIterators.unmodifiable(this.list.listIterator());
      }

      public CharBigListIterator listIterator(long var1) {
         return CharBigListIterators.unmodifiable(this.list.listIterator(var1));
      }

      public CharBigList subList(long var1, long var3) {
         return CharBigLists.unmodifiable(this.list.subList(var1, var3));
      }

      public boolean equals(Object var1) {
         return var1 == this ? true : this.list.equals(var1);
      }

      public int hashCode() {
         return this.list.hashCode();
      }

      public int compareTo(BigList<? extends Character> var1) {
         return this.list.compareTo(var1);
      }

      public boolean addAll(long var1, CharCollection var3) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(CharBigList var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(long var1, CharBigList var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Character get(long var1) {
         return this.list.get(var1);
      }

      /** @deprecated */
      @Deprecated
      public void add(long var1, Character var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Character set(long var1, Character var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Character remove(long var1) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public long indexOf(Object var1) {
         return this.list.indexOf(var1);
      }

      /** @deprecated */
      @Deprecated
      public long lastIndexOf(Object var1) {
         return this.list.lastIndexOf(var1);
      }
   }

   public static class SynchronizedBigList extends CharCollections.SynchronizedCollection implements CharBigList, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final CharBigList list;

      protected SynchronizedBigList(CharBigList var1, Object var2) {
         super(var1, var2);
         this.list = var1;
      }

      protected SynchronizedBigList(CharBigList var1) {
         super(var1);
         this.list = var1;
      }

      public char getChar(long var1) {
         synchronized(this.sync) {
            return this.list.getChar(var1);
         }
      }

      public char set(long var1, char var3) {
         synchronized(this.sync) {
            return this.list.set(var1, var3);
         }
      }

      public void add(long var1, char var3) {
         synchronized(this.sync) {
            this.list.add(var1, var3);
         }
      }

      public char removeChar(long var1) {
         synchronized(this.sync) {
            return this.list.removeChar(var1);
         }
      }

      public long indexOf(char var1) {
         synchronized(this.sync) {
            return this.list.indexOf(var1);
         }
      }

      public long lastIndexOf(char var1) {
         synchronized(this.sync) {
            return this.list.lastIndexOf(var1);
         }
      }

      public boolean addAll(long var1, Collection<? extends Character> var3) {
         synchronized(this.sync) {
            return this.list.addAll(var1, (Collection)var3);
         }
      }

      public void getElements(long var1, char[][] var3, long var4, long var6) {
         synchronized(this.sync) {
            this.list.getElements(var1, var3, var4, var6);
         }
      }

      public void removeElements(long var1, long var3) {
         synchronized(this.sync) {
            this.list.removeElements(var1, var3);
         }
      }

      public void addElements(long var1, char[][] var3, long var4, long var6) {
         synchronized(this.sync) {
            this.list.addElements(var1, var3, var4, var6);
         }
      }

      public void addElements(long var1, char[][] var3) {
         synchronized(this.sync) {
            this.list.addElements(var1, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public void size(long var1) {
         synchronized(this.sync) {
            this.list.size(var1);
         }
      }

      public long size64() {
         synchronized(this.sync) {
            return this.list.size64();
         }
      }

      public CharBigListIterator iterator() {
         return this.list.listIterator();
      }

      public CharBigListIterator listIterator() {
         return this.list.listIterator();
      }

      public CharBigListIterator listIterator(long var1) {
         return this.list.listIterator(var1);
      }

      public CharBigList subList(long var1, long var3) {
         synchronized(this.sync) {
            return CharBigLists.synchronize(this.list.subList(var1, var3), this.sync);
         }
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else {
            synchronized(this.sync) {
               return this.list.equals(var1);
            }
         }
      }

      public int hashCode() {
         synchronized(this.sync) {
            return this.list.hashCode();
         }
      }

      public int compareTo(BigList<? extends Character> var1) {
         synchronized(this.sync) {
            return this.list.compareTo(var1);
         }
      }

      public boolean addAll(long var1, CharCollection var3) {
         synchronized(this.sync) {
            return this.list.addAll(var1, var3);
         }
      }

      public boolean addAll(long var1, CharBigList var3) {
         synchronized(this.sync) {
            return this.list.addAll(var1, var3);
         }
      }

      public boolean addAll(CharBigList var1) {
         synchronized(this.sync) {
            return this.list.addAll(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public void add(long var1, Character var3) {
         synchronized(this.sync) {
            this.list.add(var1, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Character get(long var1) {
         synchronized(this.sync) {
            return this.list.get(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public Character set(long var1, Character var3) {
         synchronized(this.sync) {
            return this.list.set(var1, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Character remove(long var1) {
         synchronized(this.sync) {
            return this.list.remove(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public long indexOf(Object var1) {
         synchronized(this.sync) {
            return this.list.indexOf(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public long lastIndexOf(Object var1) {
         synchronized(this.sync) {
            return this.list.lastIndexOf(var1);
         }
      }
   }

   public static class Singleton extends AbstractCharBigList implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      private final char element;

      protected Singleton(char var1) {
         super();
         this.element = var1;
      }

      public char getChar(long var1) {
         if (var1 == 0L) {
            return this.element;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public boolean rem(char var1) {
         throw new UnsupportedOperationException();
      }

      public char removeChar(long var1) {
         throw new UnsupportedOperationException();
      }

      public boolean contains(char var1) {
         return var1 == this.element;
      }

      public char[] toCharArray() {
         char[] var1 = new char[]{this.element};
         return var1;
      }

      public CharBigListIterator listIterator() {
         return CharBigListIterators.singleton(this.element);
      }

      public CharBigListIterator listIterator(long var1) {
         if (var1 <= 1L && var1 >= 0L) {
            CharBigListIterator var3 = this.listIterator();
            if (var1 == 1L) {
               var3.nextChar();
            }

            return var3;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public CharBigList subList(long var1, long var3) {
         this.ensureIndex(var1);
         this.ensureIndex(var3);
         if (var1 > var3) {
            throw new IndexOutOfBoundsException("Start index (" + var1 + ") is greater than end index (" + var3 + ")");
         } else {
            return (CharBigList)(var1 == 0L && var3 == 1L ? this : CharBigLists.EMPTY_BIG_LIST);
         }
      }

      public boolean addAll(long var1, Collection<? extends Character> var3) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(Collection<? extends Character> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(CharBigList var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(long var1, CharBigList var3) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(long var1, CharCollection var3) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(CharCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(CharCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(CharCollection var1) {
         throw new UnsupportedOperationException();
      }

      public void clear() {
         throw new UnsupportedOperationException();
      }

      public long size64() {
         return 1L;
      }

      public Object clone() {
         return this;
      }
   }

   public static class EmptyBigList extends CharCollections.EmptyCollection implements CharBigList, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyBigList() {
         super();
      }

      public char getChar(long var1) {
         throw new IndexOutOfBoundsException();
      }

      public boolean rem(char var1) {
         throw new UnsupportedOperationException();
      }

      public char removeChar(long var1) {
         throw new UnsupportedOperationException();
      }

      public void add(long var1, char var3) {
         throw new UnsupportedOperationException();
      }

      public char set(long var1, char var3) {
         throw new UnsupportedOperationException();
      }

      public long indexOf(char var1) {
         return -1L;
      }

      public long lastIndexOf(char var1) {
         return -1L;
      }

      public boolean addAll(long var1, Collection<? extends Character> var3) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(CharCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(CharBigList var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(long var1, CharCollection var3) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(long var1, CharBigList var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public void add(long var1, Character var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean add(Character var1) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Character get(long var1) {
         throw new IndexOutOfBoundsException();
      }

      /** @deprecated */
      @Deprecated
      public Character set(long var1, Character var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Character remove(long var1) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public long indexOf(Object var1) {
         return -1L;
      }

      /** @deprecated */
      @Deprecated
      public long lastIndexOf(Object var1) {
         return -1L;
      }

      public CharBigListIterator listIterator() {
         return CharBigListIterators.EMPTY_BIG_LIST_ITERATOR;
      }

      public CharBigListIterator iterator() {
         return CharBigListIterators.EMPTY_BIG_LIST_ITERATOR;
      }

      public CharBigListIterator listIterator(long var1) {
         if (var1 == 0L) {
            return CharBigListIterators.EMPTY_BIG_LIST_ITERATOR;
         } else {
            throw new IndexOutOfBoundsException(String.valueOf(var1));
         }
      }

      public CharBigList subList(long var1, long var3) {
         if (var1 == 0L && var3 == 0L) {
            return this;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public void getElements(long var1, char[][] var3, long var4, long var6) {
         CharBigArrays.ensureOffsetLength(var3, var4, var6);
         if (var1 != 0L) {
            throw new IndexOutOfBoundsException();
         }
      }

      public void removeElements(long var1, long var3) {
         throw new UnsupportedOperationException();
      }

      public void addElements(long var1, char[][] var3, long var4, long var6) {
         throw new UnsupportedOperationException();
      }

      public void addElements(long var1, char[][] var3) {
         throw new UnsupportedOperationException();
      }

      public void size(long var1) {
         throw new UnsupportedOperationException();
      }

      public long size64() {
         return 0L;
      }

      public int compareTo(BigList<? extends Character> var1) {
         if (var1 == this) {
            return 0;
         } else {
            return var1.isEmpty() ? 0 : -1;
         }
      }

      public Object clone() {
         return CharBigLists.EMPTY_BIG_LIST;
      }

      public int hashCode() {
         return 1;
      }

      public boolean equals(Object var1) {
         return var1 instanceof BigList && ((BigList)var1).isEmpty();
      }

      public String toString() {
         return "[]";
      }

      private Object readResolve() {
         return CharBigLists.EMPTY_BIG_LIST;
      }
   }
}
