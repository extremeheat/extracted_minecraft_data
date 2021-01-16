package it.unimi.dsi.fastutil.shorts;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public abstract class AbstractShortList extends AbstractShortCollection implements ShortList, ShortStack {
   protected AbstractShortList() {
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

   public void add(int var1, short var2) {
      throw new UnsupportedOperationException();
   }

   public boolean add(short var1) {
      this.add(this.size(), var1);
      return true;
   }

   public short removeShort(int var1) {
      throw new UnsupportedOperationException();
   }

   public short set(int var1, short var2) {
      throw new UnsupportedOperationException();
   }

   public boolean addAll(int var1, Collection<? extends Short> var2) {
      this.ensureIndex(var1);
      Iterator var3 = var2.iterator();
      boolean var4 = var3.hasNext();

      while(var3.hasNext()) {
         this.add(var1++, (Short)var3.next());
      }

      return var4;
   }

   public boolean addAll(Collection<? extends Short> var1) {
      return this.addAll(this.size(), var1);
   }

   public ShortListIterator iterator() {
      return this.listIterator();
   }

   public ShortListIterator listIterator() {
      return this.listIterator(0);
   }

   public ShortListIterator listIterator(final int var1) {
      this.ensureIndex(var1);
      return new ShortListIterator() {
         int pos = var1;
         int last = -1;

         public boolean hasNext() {
            return this.pos < AbstractShortList.this.size();
         }

         public boolean hasPrevious() {
            return this.pos > 0;
         }

         public short nextShort() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return AbstractShortList.this.getShort(this.last = this.pos++);
            }
         }

         public short previousShort() {
            if (!this.hasPrevious()) {
               throw new NoSuchElementException();
            } else {
               return AbstractShortList.this.getShort(this.last = --this.pos);
            }
         }

         public int nextIndex() {
            return this.pos;
         }

         public int previousIndex() {
            return this.pos - 1;
         }

         public void add(short var1x) {
            AbstractShortList.this.add(this.pos++, var1x);
            this.last = -1;
         }

         public void set(short var1x) {
            if (this.last == -1) {
               throw new IllegalStateException();
            } else {
               AbstractShortList.this.set(this.last, var1x);
            }
         }

         public void remove() {
            if (this.last == -1) {
               throw new IllegalStateException();
            } else {
               AbstractShortList.this.removeShort(this.last);
               if (this.last < this.pos) {
                  --this.pos;
               }

               this.last = -1;
            }
         }
      };
   }

   public boolean contains(short var1) {
      return this.indexOf(var1) >= 0;
   }

   public int indexOf(short var1) {
      ShortListIterator var2 = this.listIterator();

      short var3;
      do {
         if (!var2.hasNext()) {
            return -1;
         }

         var3 = var2.nextShort();
      } while(var1 != var3);

      return var2.previousIndex();
   }

   public int lastIndexOf(short var1) {
      ShortListIterator var2 = this.listIterator(this.size());

      short var3;
      do {
         if (!var2.hasPrevious()) {
            return -1;
         }

         var3 = var2.previousShort();
      } while(var1 != var3);

      return var2.nextIndex();
   }

   public void size(int var1) {
      int var2 = this.size();
      if (var1 > var2) {
         while(var2++ < var1) {
            this.add((short)0);
         }
      } else {
         while(var2-- != var1) {
            this.removeShort(var2);
         }
      }

   }

   public ShortList subList(int var1, int var2) {
      this.ensureIndex(var1);
      this.ensureIndex(var2);
      if (var1 > var2) {
         throw new IndexOutOfBoundsException("Start index (" + var1 + ") is greater than end index (" + var2 + ")");
      } else {
         return new AbstractShortList.ShortSubList(this, var1, var2);
      }
   }

   public void removeElements(int var1, int var2) {
      this.ensureIndex(var2);
      ShortListIterator var3 = this.listIterator(var1);
      int var4 = var2 - var1;
      if (var4 < 0) {
         throw new IllegalArgumentException("Start index (" + var1 + ") is greater than end index (" + var2 + ")");
      } else {
         while(var4-- != 0) {
            var3.nextShort();
            var3.remove();
         }

      }
   }

   public void addElements(int var1, short[] var2, int var3, int var4) {
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

   public void addElements(int var1, short[] var2) {
      this.addElements(var1, var2, 0, var2.length);
   }

   public void getElements(int var1, short[] var2, int var3, int var4) {
      ShortListIterator var5 = this.listIterator(var1);
      if (var3 < 0) {
         throw new ArrayIndexOutOfBoundsException("Offset (" + var3 + ") is negative");
      } else if (var3 + var4 > var2.length) {
         throw new ArrayIndexOutOfBoundsException("End index (" + (var3 + var4) + ") is greater than array length (" + var2.length + ")");
      } else if (var1 + var4 > this.size()) {
         throw new IndexOutOfBoundsException("End index (" + (var1 + var4) + ") is greater than list size (" + this.size() + ")");
      } else {
         while(var4-- != 0) {
            var2[var3++] = var5.nextShort();
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
      ShortListIterator var1 = this.iterator();
      int var2 = 1;

      short var4;
      for(int var3 = this.size(); var3-- != 0; var2 = 31 * var2 + var4) {
         var4 = var1.nextShort();
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
            ShortListIterator var4;
            if (var2 instanceof ShortList) {
               var4 = this.listIterator();
               ShortListIterator var6 = ((ShortList)var2).listIterator();

               do {
                  if (var3-- == 0) {
                     return true;
                  }
               } while(var4.nextShort() == var6.nextShort());

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

   public int compareTo(List<? extends Short> var1) {
      if (var1 == this) {
         return 0;
      } else {
         ShortListIterator var2;
         int var4;
         if (var1 instanceof ShortList) {
            var2 = this.listIterator();
            ShortListIterator var7 = ((ShortList)var1).listIterator();

            while(var2.hasNext() && var7.hasNext()) {
               short var5 = var2.nextShort();
               short var6 = var7.nextShort();
               if ((var4 = Short.compare(var5, var6)) != 0) {
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

   public void push(short var1) {
      this.add(var1);
   }

   public short popShort() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.removeShort(this.size() - 1);
      }
   }

   public short topShort() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.getShort(this.size() - 1);
      }
   }

   public short peekShort(int var1) {
      return this.getShort(this.size() - 1 - var1);
   }

   public boolean rem(short var1) {
      int var2 = this.indexOf(var1);
      if (var2 == -1) {
         return false;
      } else {
         this.removeShort(var2);
         return true;
      }
   }

   public boolean addAll(int var1, ShortCollection var2) {
      this.ensureIndex(var1);
      ShortIterator var3 = var2.iterator();
      boolean var4 = var3.hasNext();

      while(var3.hasNext()) {
         this.add(var1++, var3.nextShort());
      }

      return var4;
   }

   public boolean addAll(int var1, ShortList var2) {
      return this.addAll(var1, (ShortCollection)var2);
   }

   public boolean addAll(ShortCollection var1) {
      return this.addAll(this.size(), var1);
   }

   public boolean addAll(ShortList var1) {
      return this.addAll(this.size(), var1);
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      ShortListIterator var2 = this.iterator();
      int var3 = this.size();
      boolean var5 = true;
      var1.append("[");

      while(var3-- != 0) {
         if (var5) {
            var5 = false;
         } else {
            var1.append(", ");
         }

         short var4 = var2.nextShort();
         var1.append(String.valueOf(var4));
      }

      var1.append("]");
      return var1.toString();
   }

   public static class ShortSubList extends AbstractShortList implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ShortList l;
      protected final int from;
      protected int to;

      public ShortSubList(ShortList var1, int var2, int var3) {
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

      public boolean add(short var1) {
         this.l.add(this.to, var1);
         ++this.to;

         assert this.assertRange();

         return true;
      }

      public void add(int var1, short var2) {
         this.ensureIndex(var1);
         this.l.add(this.from + var1, var2);
         ++this.to;

         assert this.assertRange();

      }

      public boolean addAll(int var1, Collection<? extends Short> var2) {
         this.ensureIndex(var1);
         this.to += var2.size();
         return this.l.addAll(this.from + var1, (Collection)var2);
      }

      public short getShort(int var1) {
         this.ensureRestrictedIndex(var1);
         return this.l.getShort(this.from + var1);
      }

      public short removeShort(int var1) {
         this.ensureRestrictedIndex(var1);
         --this.to;
         return this.l.removeShort(this.from + var1);
      }

      public short set(int var1, short var2) {
         this.ensureRestrictedIndex(var1);
         return this.l.set(this.from + var1, var2);
      }

      public int size() {
         return this.to - this.from;
      }

      public void getElements(int var1, short[] var2, int var3, int var4) {
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

      public void addElements(int var1, short[] var2, int var3, int var4) {
         this.ensureIndex(var1);
         this.l.addElements(this.from + var1, var2, var3, var4);
         this.to += var4;

         assert this.assertRange();

      }

      public ShortListIterator listIterator(final int var1) {
         this.ensureIndex(var1);
         return new ShortListIterator() {
            int pos = var1;
            int last = -1;

            public boolean hasNext() {
               return this.pos < ShortSubList.this.size();
            }

            public boolean hasPrevious() {
               return this.pos > 0;
            }

            public short nextShort() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return ShortSubList.this.l.getShort(ShortSubList.this.from + (this.last = this.pos++));
               }
            }

            public short previousShort() {
               if (!this.hasPrevious()) {
                  throw new NoSuchElementException();
               } else {
                  return ShortSubList.this.l.getShort(ShortSubList.this.from + (this.last = --this.pos));
               }
            }

            public int nextIndex() {
               return this.pos;
            }

            public int previousIndex() {
               return this.pos - 1;
            }

            public void add(short var1x) {
               if (this.last == -1) {
                  throw new IllegalStateException();
               } else {
                  ShortSubList.this.add(this.pos++, var1x);
                  this.last = -1;

                  assert ShortSubList.this.assertRange();

               }
            }

            public void set(short var1x) {
               if (this.last == -1) {
                  throw new IllegalStateException();
               } else {
                  ShortSubList.this.set(this.last, var1x);
               }
            }

            public void remove() {
               if (this.last == -1) {
                  throw new IllegalStateException();
               } else {
                  ShortSubList.this.removeShort(this.last);
                  if (this.last < this.pos) {
                     --this.pos;
                  }

                  this.last = -1;

                  assert ShortSubList.this.assertRange();

               }
            }
         };
      }

      public ShortList subList(int var1, int var2) {
         this.ensureIndex(var1);
         this.ensureIndex(var2);
         if (var1 > var2) {
            throw new IllegalArgumentException("Start index (" + var1 + ") is greater than end index (" + var2 + ")");
         } else {
            return new AbstractShortList.ShortSubList(this, var1, var2);
         }
      }

      public boolean rem(short var1) {
         int var2 = this.indexOf(var1);
         if (var2 == -1) {
            return false;
         } else {
            --this.to;
            this.l.removeShort(this.from + var2);

            assert this.assertRange();

            return true;
         }
      }

      public boolean addAll(int var1, ShortCollection var2) {
         this.ensureIndex(var1);
         return super.addAll(var1, var2);
      }

      public boolean addAll(int var1, ShortList var2) {
         this.ensureIndex(var1);
         return super.addAll(var1, var2);
      }
   }
}
