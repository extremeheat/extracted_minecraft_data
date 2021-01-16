package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.BigListIterator;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class AbstractCharBigList extends AbstractCharCollection implements CharBigList, CharStack {
   protected AbstractCharBigList() {
      super();
   }

   protected void ensureIndex(long var1) {
      if (var1 < 0L) {
         throw new IndexOutOfBoundsException("Index (" + var1 + ") is negative");
      } else if (var1 > this.size64()) {
         throw new IndexOutOfBoundsException("Index (" + var1 + ") is greater than list size (" + this.size64() + ")");
      }
   }

   protected void ensureRestrictedIndex(long var1) {
      if (var1 < 0L) {
         throw new IndexOutOfBoundsException("Index (" + var1 + ") is negative");
      } else if (var1 >= this.size64()) {
         throw new IndexOutOfBoundsException("Index (" + var1 + ") is greater than or equal to list size (" + this.size64() + ")");
      }
   }

   public void add(long var1, char var3) {
      throw new UnsupportedOperationException();
   }

   public boolean add(char var1) {
      this.add(this.size64(), var1);
      return true;
   }

   public char removeChar(long var1) {
      throw new UnsupportedOperationException();
   }

   public char set(long var1, char var3) {
      throw new UnsupportedOperationException();
   }

   public boolean addAll(long var1, Collection<? extends Character> var3) {
      this.ensureIndex(var1);
      Iterator var4 = var3.iterator();
      boolean var5 = var4.hasNext();

      while(var4.hasNext()) {
         this.add(var1++, (Character)var4.next());
      }

      return var5;
   }

   public boolean addAll(Collection<? extends Character> var1) {
      return this.addAll(this.size64(), var1);
   }

   public CharBigListIterator iterator() {
      return this.listIterator();
   }

   public CharBigListIterator listIterator() {
      return this.listIterator(0L);
   }

   public CharBigListIterator listIterator(final long var1) {
      this.ensureIndex(var1);
      return new CharBigListIterator() {
         long pos = var1;
         long last = -1L;

         public boolean hasNext() {
            return this.pos < AbstractCharBigList.this.size64();
         }

         public boolean hasPrevious() {
            return this.pos > 0L;
         }

         public char nextChar() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return AbstractCharBigList.this.getChar(this.last = (long)(this.pos++));
            }
         }

         public char previousChar() {
            if (!this.hasPrevious()) {
               throw new NoSuchElementException();
            } else {
               return AbstractCharBigList.this.getChar(this.last = --this.pos);
            }
         }

         public long nextIndex() {
            return this.pos;
         }

         public long previousIndex() {
            return this.pos - 1L;
         }

         public void add(char var1x) {
            AbstractCharBigList.this.add((long)(this.pos++), var1x);
            this.last = -1L;
         }

         public void set(char var1x) {
            if (this.last == -1L) {
               throw new IllegalStateException();
            } else {
               AbstractCharBigList.this.set(this.last, var1x);
            }
         }

         public void remove() {
            if (this.last == -1L) {
               throw new IllegalStateException();
            } else {
               AbstractCharBigList.this.removeChar(this.last);
               if (this.last < this.pos) {
                  --this.pos;
               }

               this.last = -1L;
            }
         }
      };
   }

   public boolean contains(char var1) {
      return this.indexOf(var1) >= 0L;
   }

   public long indexOf(char var1) {
      CharBigListIterator var2 = this.listIterator();

      char var3;
      do {
         if (!var2.hasNext()) {
            return -1L;
         }

         var3 = var2.nextChar();
      } while(var1 != var3);

      return var2.previousIndex();
   }

   public long lastIndexOf(char var1) {
      CharBigListIterator var2 = this.listIterator(this.size64());

      char var3;
      do {
         if (!var2.hasPrevious()) {
            return -1L;
         }

         var3 = var2.previousChar();
      } while(var1 != var3);

      return var2.nextIndex();
   }

   public void size(long var1) {
      long var3 = this.size64();
      if (var1 > var3) {
         while(var3++ < var1) {
            this.add('\u0000');
         }
      } else {
         while(var3-- != var1) {
            this.remove(var3);
         }
      }

   }

   public CharBigList subList(long var1, long var3) {
      this.ensureIndex(var1);
      this.ensureIndex(var3);
      if (var1 > var3) {
         throw new IndexOutOfBoundsException("Start index (" + var1 + ") is greater than end index (" + var3 + ")");
      } else {
         return new AbstractCharBigList.CharSubList(this, var1, var3);
      }
   }

   public void removeElements(long var1, long var3) {
      this.ensureIndex(var3);
      CharBigListIterator var5 = this.listIterator(var1);
      long var6 = var3 - var1;
      if (var6 < 0L) {
         throw new IllegalArgumentException("Start index (" + var1 + ") is greater than end index (" + var3 + ")");
      } else {
         while(var6-- != 0L) {
            var5.nextChar();
            var5.remove();
         }

      }
   }

   public void addElements(long var1, char[][] var3, long var4, long var6) {
      this.ensureIndex(var1);
      CharBigArrays.ensureOffsetLength(var3, var4, var6);

      while(var6-- != 0L) {
         this.add(var1++, CharBigArrays.get(var3, var4++));
      }

   }

   public void addElements(long var1, char[][] var3) {
      this.addElements(var1, var3, 0L, CharBigArrays.length(var3));
   }

   public void getElements(long var1, char[][] var3, long var4, long var6) {
      CharBigListIterator var8 = this.listIterator(var1);
      CharBigArrays.ensureOffsetLength(var3, var4, var6);
      if (var1 + var6 > this.size64()) {
         throw new IndexOutOfBoundsException("End index (" + (var1 + var6) + ") is greater than list size (" + this.size64() + ")");
      } else {
         while(var6-- != 0L) {
            CharBigArrays.set(var3, var4++, var8.nextChar());
         }

      }
   }

   public void clear() {
      this.removeElements(0L, this.size64());
   }

   /** @deprecated */
   @Deprecated
   public int size() {
      return (int)Math.min(2147483647L, this.size64());
   }

   private boolean valEquals(Object var1, Object var2) {
      return var1 == null ? var2 == null : var1.equals(var2);
   }

   public int hashCode() {
      CharBigListIterator var1 = this.iterator();
      int var2 = 1;

      char var5;
      for(long var3 = this.size64(); var3-- != 0L; var2 = 31 * var2 + var5) {
         var5 = var1.nextChar();
      }

      return var2;
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof BigList)) {
         return false;
      } else {
         BigList var2 = (BigList)var1;
         long var3 = this.size64();
         if (var3 != var2.size64()) {
            return false;
         } else {
            CharBigListIterator var5;
            if (var2 instanceof CharBigList) {
               var5 = this.listIterator();
               CharBigListIterator var7 = ((CharBigList)var2).listIterator();

               do {
                  if (var3-- == 0L) {
                     return true;
                  }
               } while(var5.nextChar() == var7.nextChar());

               return false;
            } else {
               var5 = this.listIterator();
               BigListIterator var6 = var2.listIterator();

               do {
                  if (var3-- == 0L) {
                     return true;
                  }
               } while(this.valEquals(var5.next(), var6.next()));

               return false;
            }
         }
      }
   }

   public int compareTo(BigList<? extends Character> var1) {
      if (var1 == this) {
         return 0;
      } else {
         CharBigListIterator var2;
         int var4;
         if (var1 instanceof CharBigList) {
            var2 = this.listIterator();
            CharBigListIterator var7 = ((CharBigList)var1).listIterator();

            while(var2.hasNext() && var7.hasNext()) {
               char var5 = var2.nextChar();
               char var6 = var7.nextChar();
               if ((var4 = Character.compare(var5, var6)) != 0) {
                  return var4;
               }
            }

            return var7.hasNext() ? -1 : (var2.hasNext() ? 1 : 0);
         } else {
            var2 = this.listIterator();
            BigListIterator var3 = var1.listIterator();

            while(var2.hasNext() && var3.hasNext()) {
               if ((var4 = ((Comparable)var2.next()).compareTo(var3.next())) != 0) {
                  return var4;
               }
            }

            return var3.hasNext() ? -1 : (var2.hasNext() ? 1 : 0);
         }
      }
   }

   public void push(char var1) {
      this.add(var1);
   }

   public char popChar() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.removeChar(this.size64() - 1L);
      }
   }

   public char topChar() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.getChar(this.size64() - 1L);
      }
   }

   public char peekChar(int var1) {
      return this.getChar(this.size64() - 1L - (long)var1);
   }

   public boolean rem(char var1) {
      long var2 = this.indexOf(var1);
      if (var2 == -1L) {
         return false;
      } else {
         this.removeChar(var2);
         return true;
      }
   }

   public boolean addAll(long var1, CharCollection var3) {
      return this.addAll(var1, (Collection)var3);
   }

   public boolean addAll(long var1, CharBigList var3) {
      return this.addAll(var1, (CharCollection)var3);
   }

   public boolean addAll(CharCollection var1) {
      return this.addAll(this.size64(), var1);
   }

   public boolean addAll(CharBigList var1) {
      return this.addAll(this.size64(), var1);
   }

   /** @deprecated */
   @Deprecated
   public void add(long var1, Character var3) {
      this.add(var1, var3);
   }

   /** @deprecated */
   @Deprecated
   public Character set(long var1, Character var3) {
      return this.set(var1, var3);
   }

   /** @deprecated */
   @Deprecated
   public Character get(long var1) {
      return this.getChar(var1);
   }

   /** @deprecated */
   @Deprecated
   public long indexOf(Object var1) {
      return this.indexOf((Character)var1);
   }

   /** @deprecated */
   @Deprecated
   public long lastIndexOf(Object var1) {
      return this.lastIndexOf((Character)var1);
   }

   /** @deprecated */
   @Deprecated
   public Character remove(long var1) {
      return this.removeChar(var1);
   }

   /** @deprecated */
   @Deprecated
   public void push(Character var1) {
      this.push(var1);
   }

   /** @deprecated */
   @Deprecated
   public Character pop() {
      return this.popChar();
   }

   /** @deprecated */
   @Deprecated
   public Character top() {
      return this.topChar();
   }

   /** @deprecated */
   @Deprecated
   public Character peek(int var1) {
      return this.peekChar(var1);
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      CharBigListIterator var2 = this.iterator();
      long var3 = this.size64();
      boolean var6 = true;
      var1.append("[");

      while(var3-- != 0L) {
         if (var6) {
            var6 = false;
         } else {
            var1.append(", ");
         }

         char var5 = var2.nextChar();
         var1.append(String.valueOf(var5));
      }

      var1.append("]");
      return var1.toString();
   }

   public static class CharSubList extends AbstractCharBigList implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final CharBigList l;
      protected final long from;
      protected long to;

      public CharSubList(CharBigList var1, long var2, long var4) {
         super();
         this.l = var1;
         this.from = var2;
         this.to = var4;
      }

      private boolean assertRange() {
         assert this.from <= this.l.size64();

         assert this.to <= this.l.size64();

         assert this.to >= this.from;

         return true;
      }

      public boolean add(char var1) {
         this.l.add(this.to, var1);
         ++this.to;

         assert this.assertRange();

         return true;
      }

      public void add(long var1, char var3) {
         this.ensureIndex(var1);
         this.l.add(this.from + var1, var3);
         ++this.to;

         assert this.assertRange();

      }

      public boolean addAll(long var1, Collection<? extends Character> var3) {
         this.ensureIndex(var1);
         this.to += (long)var3.size();
         return this.l.addAll(this.from + var1, (Collection)var3);
      }

      public char getChar(long var1) {
         this.ensureRestrictedIndex(var1);
         return this.l.getChar(this.from + var1);
      }

      public char removeChar(long var1) {
         this.ensureRestrictedIndex(var1);
         --this.to;
         return this.l.removeChar(this.from + var1);
      }

      public char set(long var1, char var3) {
         this.ensureRestrictedIndex(var1);
         return this.l.set(this.from + var1, var3);
      }

      public long size64() {
         return this.to - this.from;
      }

      public void getElements(long var1, char[][] var3, long var4, long var6) {
         this.ensureIndex(var1);
         if (var1 + var6 > this.size64()) {
            throw new IndexOutOfBoundsException("End index (" + var1 + var6 + ") is greater than list size (" + this.size64() + ")");
         } else {
            this.l.getElements(this.from + var1, var3, var4, var6);
         }
      }

      public void removeElements(long var1, long var3) {
         this.ensureIndex(var1);
         this.ensureIndex(var3);
         this.l.removeElements(this.from + var1, this.from + var3);
         this.to -= var3 - var1;

         assert this.assertRange();

      }

      public void addElements(long var1, char[][] var3, long var4, long var6) {
         this.ensureIndex(var1);
         this.l.addElements(this.from + var1, var3, var4, var6);
         this.to += var6;

         assert this.assertRange();

      }

      public CharBigListIterator listIterator(final long var1) {
         this.ensureIndex(var1);
         return new CharBigListIterator() {
            long pos = var1;
            long last = -1L;

            public boolean hasNext() {
               return this.pos < CharSubList.this.size64();
            }

            public boolean hasPrevious() {
               return this.pos > 0L;
            }

            public char nextChar() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return CharSubList.this.l.getChar(CharSubList.this.from + (this.last = (long)(this.pos++)));
               }
            }

            public char previousChar() {
               if (!this.hasPrevious()) {
                  throw new NoSuchElementException();
               } else {
                  return CharSubList.this.l.getChar(CharSubList.this.from + (this.last = --this.pos));
               }
            }

            public long nextIndex() {
               return this.pos;
            }

            public long previousIndex() {
               return this.pos - 1L;
            }

            public void add(char var1x) {
               if (this.last == -1L) {
                  throw new IllegalStateException();
               } else {
                  CharSubList.this.add((long)(this.pos++), var1x);
                  this.last = -1L;

                  assert CharSubList.this.assertRange();

               }
            }

            public void set(char var1x) {
               if (this.last == -1L) {
                  throw new IllegalStateException();
               } else {
                  CharSubList.this.set(this.last, var1x);
               }
            }

            public void remove() {
               if (this.last == -1L) {
                  throw new IllegalStateException();
               } else {
                  CharSubList.this.removeChar(this.last);
                  if (this.last < this.pos) {
                     --this.pos;
                  }

                  this.last = -1L;

                  assert CharSubList.this.assertRange();

               }
            }
         };
      }

      public CharBigList subList(long var1, long var3) {
         this.ensureIndex(var1);
         this.ensureIndex(var3);
         if (var1 > var3) {
            throw new IllegalArgumentException("Start index (" + var1 + ") is greater than end index (" + var3 + ")");
         } else {
            return new AbstractCharBigList.CharSubList(this, var1, var3);
         }
      }

      public boolean rem(char var1) {
         long var2 = this.indexOf(var1);
         if (var2 == -1L) {
            return false;
         } else {
            --this.to;
            this.l.removeChar(this.from + var2);

            assert this.assertRange();

            return true;
         }
      }

      public boolean addAll(long var1, CharCollection var3) {
         this.ensureIndex(var1);
         return super.addAll(var1, var3);
      }

      public boolean addAll(long var1, CharBigList var3) {
         this.ensureIndex(var1);
         return super.addAll(var1, var3);
      }
   }
}
