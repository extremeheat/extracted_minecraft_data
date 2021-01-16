package it.unimi.dsi.fastutil.chars;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public abstract class AbstractCharList extends AbstractCharCollection implements CharList, CharStack {
   protected AbstractCharList() {
      super();
   }

   protected void ensureIndex(int var1) {
      if (var1 < 0) {
         throw new IndexOutOfBoundsException("Index (" + var1 + ") is negative");
      } else if (var1 > this.size()) {
         throw new IndexOutOfBoundsException("Index (" + var1 + ") is greater than list size (" + this.size() + ")");
      }
   }

   protected void ensureRestrictedIndex(int var1) {
      if (var1 < 0) {
         throw new IndexOutOfBoundsException("Index (" + var1 + ") is negative");
      } else if (var1 >= this.size()) {
         throw new IndexOutOfBoundsException("Index (" + var1 + ") is greater than or equal to list size (" + this.size() + ")");
      }
   }

   public void add(int var1, char var2) {
      throw new UnsupportedOperationException();
   }

   public boolean add(char var1) {
      this.add(this.size(), var1);
      return true;
   }

   public char removeChar(int var1) {
      throw new UnsupportedOperationException();
   }

   public char set(int var1, char var2) {
      throw new UnsupportedOperationException();
   }

   public boolean addAll(int var1, Collection<? extends Character> var2) {
      this.ensureIndex(var1);
      Iterator var3 = var2.iterator();
      boolean var4 = var3.hasNext();

      while(var3.hasNext()) {
         this.add(var1++, (Character)var3.next());
      }

      return var4;
   }

   public boolean addAll(Collection<? extends Character> var1) {
      return this.addAll(this.size(), var1);
   }

   public CharListIterator iterator() {
      return this.listIterator();
   }

   public CharListIterator listIterator() {
      return this.listIterator(0);
   }

   public CharListIterator listIterator(final int var1) {
      this.ensureIndex(var1);
      return new CharListIterator() {
         int pos = var1;
         int last = -1;

         public boolean hasNext() {
            return this.pos < AbstractCharList.this.size();
         }

         public boolean hasPrevious() {
            return this.pos > 0;
         }

         public char nextChar() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return AbstractCharList.this.getChar(this.last = this.pos++);
            }
         }

         public char previousChar() {
            if (!this.hasPrevious()) {
               throw new NoSuchElementException();
            } else {
               return AbstractCharList.this.getChar(this.last = --this.pos);
            }
         }

         public int nextIndex() {
            return this.pos;
         }

         public int previousIndex() {
            return this.pos - 1;
         }

         public void add(char var1x) {
            AbstractCharList.this.add(this.pos++, var1x);
            this.last = -1;
         }

         public void set(char var1x) {
            if (this.last == -1) {
               throw new IllegalStateException();
            } else {
               AbstractCharList.this.set(this.last, var1x);
            }
         }

         public void remove() {
            if (this.last == -1) {
               throw new IllegalStateException();
            } else {
               AbstractCharList.this.removeChar(this.last);
               if (this.last < this.pos) {
                  --this.pos;
               }

               this.last = -1;
            }
         }
      };
   }

   public boolean contains(char var1) {
      return this.indexOf(var1) >= 0;
   }

   public int indexOf(char var1) {
      CharListIterator var2 = this.listIterator();

      char var3;
      do {
         if (!var2.hasNext()) {
            return -1;
         }

         var3 = var2.nextChar();
      } while(var1 != var3);

      return var2.previousIndex();
   }

   public int lastIndexOf(char var1) {
      CharListIterator var2 = this.listIterator(this.size());

      char var3;
      do {
         if (!var2.hasPrevious()) {
            return -1;
         }

         var3 = var2.previousChar();
      } while(var1 != var3);

      return var2.nextIndex();
   }

   public void size(int var1) {
      int var2 = this.size();
      if (var1 > var2) {
         while(var2++ < var1) {
            this.add('\u0000');
         }
      } else {
         while(var2-- != var1) {
            this.removeChar(var2);
         }
      }

   }

   public CharList subList(int var1, int var2) {
      this.ensureIndex(var1);
      this.ensureIndex(var2);
      if (var1 > var2) {
         throw new IndexOutOfBoundsException("Start index (" + var1 + ") is greater than end index (" + var2 + ")");
      } else {
         return new AbstractCharList.CharSubList(this, var1, var2);
      }
   }

   public void removeElements(int var1, int var2) {
      this.ensureIndex(var2);
      CharListIterator var3 = this.listIterator(var1);
      int var4 = var2 - var1;
      if (var4 < 0) {
         throw new IllegalArgumentException("Start index (" + var1 + ") is greater than end index (" + var2 + ")");
      } else {
         while(var4-- != 0) {
            var3.nextChar();
            var3.remove();
         }

      }
   }

   public void addElements(int var1, char[] var2, int var3, int var4) {
      this.ensureIndex(var1);
      if (var3 < 0) {
         throw new ArrayIndexOutOfBoundsException("Offset (" + var3 + ") is negative");
      } else if (var3 + var4 > var2.length) {
         throw new ArrayIndexOutOfBoundsException("End index (" + (var3 + var4) + ") is greater than array length (" + var2.length + ")");
      } else {
         while(var4-- != 0) {
            this.add(var1++, var2[var3++]);
         }

      }
   }

   public void addElements(int var1, char[] var2) {
      this.addElements(var1, var2, 0, var2.length);
   }

   public void getElements(int var1, char[] var2, int var3, int var4) {
      CharListIterator var5 = this.listIterator(var1);
      if (var3 < 0) {
         throw new ArrayIndexOutOfBoundsException("Offset (" + var3 + ") is negative");
      } else if (var3 + var4 > var2.length) {
         throw new ArrayIndexOutOfBoundsException("End index (" + (var3 + var4) + ") is greater than array length (" + var2.length + ")");
      } else if (var1 + var4 > this.size()) {
         throw new IndexOutOfBoundsException("End index (" + (var1 + var4) + ") is greater than list size (" + this.size() + ")");
      } else {
         while(var4-- != 0) {
            var2[var3++] = var5.nextChar();
         }

      }
   }

   public void clear() {
      this.removeElements(0, this.size());
   }

   private boolean valEquals(Object var1, Object var2) {
      return var1 == null ? var2 == null : var1.equals(var2);
   }

   public int hashCode() {
      CharListIterator var1 = this.iterator();
      int var2 = 1;

      char var4;
      for(int var3 = this.size(); var3-- != 0; var2 = 31 * var2 + var4) {
         var4 = var1.nextChar();
      }

      return var2;
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof List)) {
         return false;
      } else {
         List var2 = (List)var1;
         int var3 = this.size();
         if (var3 != var2.size()) {
            return false;
         } else {
            CharListIterator var4;
            if (var2 instanceof CharList) {
               var4 = this.listIterator();
               CharListIterator var6 = ((CharList)var2).listIterator();

               do {
                  if (var3-- == 0) {
                     return true;
                  }
               } while(var4.nextChar() == var6.nextChar());

               return false;
            } else {
               var4 = this.listIterator();
               ListIterator var5 = var2.listIterator();

               do {
                  if (var3-- == 0) {
                     return true;
                  }
               } while(this.valEquals(var4.next(), var5.next()));

               return false;
            }
         }
      }
   }

   public int compareTo(List<? extends Character> var1) {
      if (var1 == this) {
         return 0;
      } else {
         CharListIterator var2;
         int var4;
         if (var1 instanceof CharList) {
            var2 = this.listIterator();
            CharListIterator var7 = ((CharList)var1).listIterator();

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
            ListIterator var3 = var1.listIterator();

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
         return this.removeChar(this.size() - 1);
      }
   }

   public char topChar() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.getChar(this.size() - 1);
      }
   }

   public char peekChar(int var1) {
      return this.getChar(this.size() - 1 - var1);
   }

   public boolean rem(char var1) {
      int var2 = this.indexOf(var1);
      if (var2 == -1) {
         return false;
      } else {
         this.removeChar(var2);
         return true;
      }
   }

   public boolean addAll(int var1, CharCollection var2) {
      this.ensureIndex(var1);
      CharIterator var3 = var2.iterator();
      boolean var4 = var3.hasNext();

      while(var3.hasNext()) {
         this.add(var1++, var3.nextChar());
      }

      return var4;
   }

   public boolean addAll(int var1, CharList var2) {
      return this.addAll(var1, (CharCollection)var2);
   }

   public boolean addAll(CharCollection var1) {
      return this.addAll(this.size(), var1);
   }

   public boolean addAll(CharList var1) {
      return this.addAll(this.size(), var1);
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      CharListIterator var2 = this.iterator();
      int var3 = this.size();
      boolean var5 = true;
      var1.append("[");

      while(var3-- != 0) {
         if (var5) {
            var5 = false;
         } else {
            var1.append(", ");
         }

         char var4 = var2.nextChar();
         var1.append(String.valueOf(var4));
      }

      var1.append("]");
      return var1.toString();
   }

   public static class CharSubList extends AbstractCharList implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final CharList l;
      protected final int from;
      protected int to;

      public CharSubList(CharList var1, int var2, int var3) {
         super();
         this.l = var1;
         this.from = var2;
         this.to = var3;
      }

      private boolean assertRange() {
         assert this.from <= this.l.size();

         assert this.to <= this.l.size();

         assert this.to >= this.from;

         return true;
      }

      public boolean add(char var1) {
         this.l.add(this.to, var1);
         ++this.to;

         assert this.assertRange();

         return true;
      }

      public void add(int var1, char var2) {
         this.ensureIndex(var1);
         this.l.add(this.from + var1, var2);
         ++this.to;

         assert this.assertRange();

      }

      public boolean addAll(int var1, Collection<? extends Character> var2) {
         this.ensureIndex(var1);
         this.to += var2.size();
         return this.l.addAll(this.from + var1, (Collection)var2);
      }

      public char getChar(int var1) {
         this.ensureRestrictedIndex(var1);
         return this.l.getChar(this.from + var1);
      }

      public char removeChar(int var1) {
         this.ensureRestrictedIndex(var1);
         --this.to;
         return this.l.removeChar(this.from + var1);
      }

      public char set(int var1, char var2) {
         this.ensureRestrictedIndex(var1);
         return this.l.set(this.from + var1, var2);
      }

      public int size() {
         return this.to - this.from;
      }

      public void getElements(int var1, char[] var2, int var3, int var4) {
         this.ensureIndex(var1);
         if (var1 + var4 > this.size()) {
            throw new IndexOutOfBoundsException("End index (" + var1 + var4 + ") is greater than list size (" + this.size() + ")");
         } else {
            this.l.getElements(this.from + var1, var2, var3, var4);
         }
      }

      public void removeElements(int var1, int var2) {
         this.ensureIndex(var1);
         this.ensureIndex(var2);
         this.l.removeElements(this.from + var1, this.from + var2);
         this.to -= var2 - var1;

         assert this.assertRange();

      }

      public void addElements(int var1, char[] var2, int var3, int var4) {
         this.ensureIndex(var1);
         this.l.addElements(this.from + var1, var2, var3, var4);
         this.to += var4;

         assert this.assertRange();

      }

      public CharListIterator listIterator(final int var1) {
         this.ensureIndex(var1);
         return new CharListIterator() {
            int pos = var1;
            int last = -1;

            public boolean hasNext() {
               return this.pos < CharSubList.this.size();
            }

            public boolean hasPrevious() {
               return this.pos > 0;
            }

            public char nextChar() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return CharSubList.this.l.getChar(CharSubList.this.from + (this.last = this.pos++));
               }
            }

            public char previousChar() {
               if (!this.hasPrevious()) {
                  throw new NoSuchElementException();
               } else {
                  return CharSubList.this.l.getChar(CharSubList.this.from + (this.last = --this.pos));
               }
            }

            public int nextIndex() {
               return this.pos;
            }

            public int previousIndex() {
               return this.pos - 1;
            }

            public void add(char var1x) {
               if (this.last == -1) {
                  throw new IllegalStateException();
               } else {
                  CharSubList.this.add(this.pos++, var1x);
                  this.last = -1;

                  assert CharSubList.this.assertRange();

               }
            }

            public void set(char var1x) {
               if (this.last == -1) {
                  throw new IllegalStateException();
               } else {
                  CharSubList.this.set(this.last, var1x);
               }
            }

            public void remove() {
               if (this.last == -1) {
                  throw new IllegalStateException();
               } else {
                  CharSubList.this.removeChar(this.last);
                  if (this.last < this.pos) {
                     --this.pos;
                  }

                  this.last = -1;

                  assert CharSubList.this.assertRange();

               }
            }
         };
      }

      public CharList subList(int var1, int var2) {
         this.ensureIndex(var1);
         this.ensureIndex(var2);
         if (var1 > var2) {
            throw new IllegalArgumentException("Start index (" + var1 + ") is greater than end index (" + var2 + ")");
         } else {
            return new AbstractCharList.CharSubList(this, var1, var2);
         }
      }

      public boolean rem(char var1) {
         int var2 = this.indexOf(var1);
         if (var2 == -1) {
            return false;
         } else {
            --this.to;
            this.l.removeChar(this.from + var2);

            assert this.assertRange();

            return true;
         }
      }

      public boolean addAll(int var1, CharCollection var2) {
         this.ensureIndex(var1);
         return super.addAll(var1, var2);
      }

      public boolean addAll(int var1, CharList var2) {
         this.ensureIndex(var1);
         return super.addAll(var1, var2);
      }
   }
}
