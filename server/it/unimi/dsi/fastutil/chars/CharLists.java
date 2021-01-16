package it.unimi.dsi.fastutil.chars;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.RandomAccess;

public final class CharLists {
   public static final CharLists.EmptyList EMPTY_LIST = new CharLists.EmptyList();

   private CharLists() {
      super();
   }

   public static CharList shuffle(CharList var0, Random var1) {
      int var2 = var0.size();

      while(var2-- != 0) {
         int var3 = var1.nextInt(var2 + 1);
         char var4 = var0.getChar(var2);
         var0.set(var2, var0.getChar(var3));
         var0.set(var3, var4);
      }

      return var0;
   }

   public static CharList singleton(char var0) {
      return new CharLists.Singleton(var0);
   }

   public static CharList singleton(Object var0) {
      return new CharLists.Singleton((Character)var0);
   }

   public static CharList synchronize(CharList var0) {
      return (CharList)(var0 instanceof RandomAccess ? new CharLists.SynchronizedRandomAccessList(var0) : new CharLists.SynchronizedList(var0));
   }

   public static CharList synchronize(CharList var0, Object var1) {
      return (CharList)(var0 instanceof RandomAccess ? new CharLists.SynchronizedRandomAccessList(var0, var1) : new CharLists.SynchronizedList(var0, var1));
   }

   public static CharList unmodifiable(CharList var0) {
      return (CharList)(var0 instanceof RandomAccess ? new CharLists.UnmodifiableRandomAccessList(var0) : new CharLists.UnmodifiableList(var0));
   }

   public static class UnmodifiableRandomAccessList extends CharLists.UnmodifiableList implements RandomAccess, Serializable {
      private static final long serialVersionUID = 0L;

      protected UnmodifiableRandomAccessList(CharList var1) {
         super(var1);
      }

      public CharList subList(int var1, int var2) {
         return new CharLists.UnmodifiableRandomAccessList(this.list.subList(var1, var2));
      }
   }

   public static class UnmodifiableList extends CharCollections.UnmodifiableCollection implements CharList, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final CharList list;

      protected UnmodifiableList(CharList var1) {
         super(var1);
         this.list = var1;
      }

      public char getChar(int var1) {
         return this.list.getChar(var1);
      }

      public char set(int var1, char var2) {
         throw new UnsupportedOperationException();
      }

      public void add(int var1, char var2) {
         throw new UnsupportedOperationException();
      }

      public char removeChar(int var1) {
         throw new UnsupportedOperationException();
      }

      public int indexOf(char var1) {
         return this.list.indexOf(var1);
      }

      public int lastIndexOf(char var1) {
         return this.list.lastIndexOf(var1);
      }

      public boolean addAll(int var1, Collection<? extends Character> var2) {
         throw new UnsupportedOperationException();
      }

      public void getElements(int var1, char[] var2, int var3, int var4) {
         this.list.getElements(var1, var2, var3, var4);
      }

      public void removeElements(int var1, int var2) {
         throw new UnsupportedOperationException();
      }

      public void addElements(int var1, char[] var2, int var3, int var4) {
         throw new UnsupportedOperationException();
      }

      public void addElements(int var1, char[] var2) {
         throw new UnsupportedOperationException();
      }

      public void size(int var1) {
         this.list.size(var1);
      }

      public CharListIterator listIterator() {
         return CharIterators.unmodifiable(this.list.listIterator());
      }

      public CharListIterator iterator() {
         return this.listIterator();
      }

      public CharListIterator listIterator(int var1) {
         return CharIterators.unmodifiable(this.list.listIterator(var1));
      }

      public CharList subList(int var1, int var2) {
         return new CharLists.UnmodifiableList(this.list.subList(var1, var2));
      }

      public boolean equals(Object var1) {
         return var1 == this ? true : this.collection.equals(var1);
      }

      public int hashCode() {
         return this.collection.hashCode();
      }

      public int compareTo(List<? extends Character> var1) {
         return this.list.compareTo(var1);
      }

      public boolean addAll(int var1, CharCollection var2) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(CharList var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(int var1, CharList var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Character get(int var1) {
         return this.list.get(var1);
      }

      /** @deprecated */
      @Deprecated
      public void add(int var1, Character var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Character set(int var1, Character var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Character remove(int var1) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public int indexOf(Object var1) {
         return this.list.indexOf(var1);
      }

      /** @deprecated */
      @Deprecated
      public int lastIndexOf(Object var1) {
         return this.list.lastIndexOf(var1);
      }
   }

   public static class SynchronizedRandomAccessList extends CharLists.SynchronizedList implements RandomAccess, Serializable {
      private static final long serialVersionUID = 0L;

      protected SynchronizedRandomAccessList(CharList var1, Object var2) {
         super(var1, var2);
      }

      protected SynchronizedRandomAccessList(CharList var1) {
         super(var1);
      }

      public CharList subList(int var1, int var2) {
         synchronized(this.sync) {
            return new CharLists.SynchronizedRandomAccessList(this.list.subList(var1, var2), this.sync);
         }
      }
   }

   public static class SynchronizedList extends CharCollections.SynchronizedCollection implements CharList, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final CharList list;

      protected SynchronizedList(CharList var1, Object var2) {
         super(var1, var2);
         this.list = var1;
      }

      protected SynchronizedList(CharList var1) {
         super(var1);
         this.list = var1;
      }

      public char getChar(int var1) {
         synchronized(this.sync) {
            return this.list.getChar(var1);
         }
      }

      public char set(int var1, char var2) {
         synchronized(this.sync) {
            return this.list.set(var1, var2);
         }
      }

      public void add(int var1, char var2) {
         synchronized(this.sync) {
            this.list.add(var1, var2);
         }
      }

      public char removeChar(int var1) {
         synchronized(this.sync) {
            return this.list.removeChar(var1);
         }
      }

      public int indexOf(char var1) {
         synchronized(this.sync) {
            return this.list.indexOf(var1);
         }
      }

      public int lastIndexOf(char var1) {
         synchronized(this.sync) {
            return this.list.lastIndexOf(var1);
         }
      }

      public boolean addAll(int var1, Collection<? extends Character> var2) {
         synchronized(this.sync) {
            return this.list.addAll(var1, (Collection)var2);
         }
      }

      public void getElements(int var1, char[] var2, int var3, int var4) {
         synchronized(this.sync) {
            this.list.getElements(var1, var2, var3, var4);
         }
      }

      public void removeElements(int var1, int var2) {
         synchronized(this.sync) {
            this.list.removeElements(var1, var2);
         }
      }

      public void addElements(int var1, char[] var2, int var3, int var4) {
         synchronized(this.sync) {
            this.list.addElements(var1, var2, var3, var4);
         }
      }

      public void addElements(int var1, char[] var2) {
         synchronized(this.sync) {
            this.list.addElements(var1, var2);
         }
      }

      public void size(int var1) {
         synchronized(this.sync) {
            this.list.size(var1);
         }
      }

      public CharListIterator listIterator() {
         return this.list.listIterator();
      }

      public CharListIterator iterator() {
         return this.listIterator();
      }

      public CharListIterator listIterator(int var1) {
         return this.list.listIterator(var1);
      }

      public CharList subList(int var1, int var2) {
         synchronized(this.sync) {
            return new CharLists.SynchronizedList(this.list.subList(var1, var2), this.sync);
         }
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else {
            synchronized(this.sync) {
               return this.collection.equals(var1);
            }
         }
      }

      public int hashCode() {
         synchronized(this.sync) {
            return this.collection.hashCode();
         }
      }

      public int compareTo(List<? extends Character> var1) {
         synchronized(this.sync) {
            return this.list.compareTo(var1);
         }
      }

      public boolean addAll(int var1, CharCollection var2) {
         synchronized(this.sync) {
            return this.list.addAll(var1, var2);
         }
      }

      public boolean addAll(int var1, CharList var2) {
         synchronized(this.sync) {
            return this.list.addAll(var1, var2);
         }
      }

      public boolean addAll(CharList var1) {
         synchronized(this.sync) {
            return this.list.addAll(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public Character get(int var1) {
         synchronized(this.sync) {
            return this.list.get(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public void add(int var1, Character var2) {
         synchronized(this.sync) {
            this.list.add(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Character set(int var1, Character var2) {
         synchronized(this.sync) {
            return this.list.set(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Character remove(int var1) {
         synchronized(this.sync) {
            return this.list.remove(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public int indexOf(Object var1) {
         synchronized(this.sync) {
            return this.list.indexOf(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public int lastIndexOf(Object var1) {
         synchronized(this.sync) {
            return this.list.lastIndexOf(var1);
         }
      }

      private void writeObject(ObjectOutputStream var1) throws IOException {
         synchronized(this.sync) {
            var1.defaultWriteObject();
         }
      }
   }

   public static class Singleton extends AbstractCharList implements RandomAccess, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      private final char element;

      protected Singleton(char var1) {
         super();
         this.element = var1;
      }

      public char getChar(int var1) {
         if (var1 == 0) {
            return this.element;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public boolean rem(char var1) {
         throw new UnsupportedOperationException();
      }

      public char removeChar(int var1) {
         throw new UnsupportedOperationException();
      }

      public boolean contains(char var1) {
         return var1 == this.element;
      }

      public char[] toCharArray() {
         char[] var1 = new char[]{this.element};
         return var1;
      }

      public CharListIterator listIterator() {
         return CharIterators.singleton(this.element);
      }

      public CharListIterator iterator() {
         return this.listIterator();
      }

      public CharListIterator listIterator(int var1) {
         if (var1 <= 1 && var1 >= 0) {
            CharListIterator var2 = this.listIterator();
            if (var1 == 1) {
               var2.nextChar();
            }

            return var2;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public CharList subList(int var1, int var2) {
         this.ensureIndex(var1);
         this.ensureIndex(var2);
         if (var1 > var2) {
            throw new IndexOutOfBoundsException("Start index (" + var1 + ") is greater than end index (" + var2 + ")");
         } else {
            return (CharList)(var1 == 0 && var2 == 1 ? this : CharLists.EMPTY_LIST);
         }
      }

      public boolean addAll(int var1, Collection<? extends Character> var2) {
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

      public boolean addAll(CharList var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(int var1, CharList var2) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(int var1, CharCollection var2) {
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

      public int size() {
         return 1;
      }

      public void size(int var1) {
         throw new UnsupportedOperationException();
      }

      public void clear() {
         throw new UnsupportedOperationException();
      }

      public Object clone() {
         return this;
      }
   }

   public static class EmptyList extends CharCollections.EmptyCollection implements CharList, RandomAccess, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyList() {
         super();
      }

      public char getChar(int var1) {
         throw new IndexOutOfBoundsException();
      }

      public boolean rem(char var1) {
         throw new UnsupportedOperationException();
      }

      public char removeChar(int var1) {
         throw new UnsupportedOperationException();
      }

      public void add(int var1, char var2) {
         throw new UnsupportedOperationException();
      }

      public char set(int var1, char var2) {
         throw new UnsupportedOperationException();
      }

      public int indexOf(char var1) {
         return -1;
      }

      public int lastIndexOf(char var1) {
         return -1;
      }

      public boolean addAll(int var1, Collection<? extends Character> var2) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(CharList var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(int var1, CharCollection var2) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(int var1, CharList var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public void add(int var1, Character var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Character get(int var1) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean add(Character var1) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Character set(int var1, Character var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Character remove(int var1) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public int indexOf(Object var1) {
         return -1;
      }

      /** @deprecated */
      @Deprecated
      public int lastIndexOf(Object var1) {
         return -1;
      }

      public CharListIterator listIterator() {
         return CharIterators.EMPTY_ITERATOR;
      }

      public CharListIterator iterator() {
         return CharIterators.EMPTY_ITERATOR;
      }

      public CharListIterator listIterator(int var1) {
         if (var1 == 0) {
            return CharIterators.EMPTY_ITERATOR;
         } else {
            throw new IndexOutOfBoundsException(String.valueOf(var1));
         }
      }

      public CharList subList(int var1, int var2) {
         if (var1 == 0 && var2 == 0) {
            return this;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public void getElements(int var1, char[] var2, int var3, int var4) {
         if (var1 != 0 || var4 != 0 || var3 < 0 || var3 > var2.length) {
            throw new IndexOutOfBoundsException();
         }
      }

      public void removeElements(int var1, int var2) {
         throw new UnsupportedOperationException();
      }

      public void addElements(int var1, char[] var2, int var3, int var4) {
         throw new UnsupportedOperationException();
      }

      public void addElements(int var1, char[] var2) {
         throw new UnsupportedOperationException();
      }

      public void size(int var1) {
         throw new UnsupportedOperationException();
      }

      public int compareTo(List<? extends Character> var1) {
         if (var1 == this) {
            return 0;
         } else {
            return var1.isEmpty() ? 0 : -1;
         }
      }

      public Object clone() {
         return CharLists.EMPTY_LIST;
      }

      public int hashCode() {
         return 1;
      }

      public boolean equals(Object var1) {
         return var1 instanceof List && ((List)var1).isEmpty();
      }

      public String toString() {
         return "[]";
      }

      private Object readResolve() {
         return CharLists.EMPTY_LIST;
      }
   }
}
