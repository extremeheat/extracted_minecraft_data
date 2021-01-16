package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.BigListIterator;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class AbstractShortBigList extends AbstractShortCollection implements ShortBigList, ShortStack {
   protected AbstractShortBigList() {
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

   public void add(long var1, short var3) {
      throw new UnsupportedOperationException();
   }

   public boolean add(short var1) {
      this.add(this.size64(), var1);
      return true;
   }

   public short removeShort(long var1) {
      throw new UnsupportedOperationException();
   }

   public short set(long var1, short var3) {
      throw new UnsupportedOperationException();
   }

   public boolean addAll(long var1, Collection<? extends Short> var3) {
      this.ensureIndex(var1);
      Iterator var4 = var3.iterator();
      boolean var5 = var4.hasNext();

      while(var4.hasNext()) {
         this.add(var1++, (Short)var4.next());
      }

      return var5;
   }

   public boolean addAll(Collection<? extends Short> var1) {
      return this.addAll(this.size64(), var1);
   }

   public ShortBigListIterator iterator() {
      return this.listIterator();
   }

   public ShortBigListIterator listIterator() {
      return this.listIterator(0L);
   }

   public ShortBigListIterator listIterator(final long var1) {
      this.ensureIndex(var1);
      return new ShortBigListIterator() {
         long pos = var1;
         long last = -1L;

         public boolean hasNext() {
            return this.pos < AbstractShortBigList.this.size64();
         }

         public boolean hasPrevious() {
            return this.pos > 0L;
         }

         public short nextShort() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return AbstractShortBigList.this.getShort(this.last = (long)(this.pos++));
            }
         }

         public short previousShort() {
            if (!this.hasPrevious()) {
               throw new NoSuchElementException();
            } else {
               return AbstractShortBigList.this.getShort(this.last = --this.pos);
            }
         }

         public long nextIndex() {
            return this.pos;
         }

         public long previousIndex() {
            return this.pos - 1L;
         }

         public void add(short var1x) {
            AbstractShortBigList.this.add((long)(this.pos++), var1x);
            this.last = -1L;
         }

         public void set(short var1x) {
            if (this.last == -1L) {
               throw new IllegalStateException();
            } else {
               AbstractShortBigList.this.set(this.last, var1x);
            }
         }

         public void remove() {
            if (this.last == -1L) {
               throw new IllegalStateException();
            } else {
               AbstractShortBigList.this.removeShort(this.last);
               if (this.last < this.pos) {
                  --this.pos;
               }

               this.last = -1L;
            }
         }
      };
   }

   public boolean contains(short var1) {
      return this.indexOf(var1) >= 0L;
   }

   public long indexOf(short var1) {
      ShortBigListIterator var2 = this.listIterator();

      short var3;
      do {
         if (!var2.hasNext()) {
            return -1L;
         }

         var3 = var2.nextShort();
      } while(var1 != var3);

      return var2.previousIndex();
   }

   public long lastIndexOf(short var1) {
      ShortBigListIterator var2 = this.listIterator(this.size64());

      short var3;
      do {
         if (!var2.hasPrevious()) {
            return -1L;
         }

         var3 = var2.previousShort();
      } while(var1 != var3);

      return var2.nextIndex();
   }

   public void size(long var1) {
      long var3 = this.size64();
      if (var1 > var3) {
         while(var3++ < var1) {
            this.add((short)0);
         }
      } else {
         while(var3-- != var1) {
            this.remove(var3);
         }
      }

   }

   public ShortBigList subList(long var1, long var3) {
      this.ensureIndex(var1);
      this.ensureIndex(var3);
      if (var1 > var3) {
         throw new IndexOutOfBoundsException("Start index (" + var1 + ") is greater than end index (" + var3 + ")");
      } else {
         return new AbstractShortBigList.ShortSubList(this, var1, var3);
      }
   }

   public void removeElements(long var1, long var3) {
      this.ensureIndex(var3);
      ShortBigListIterator var5 = this.listIterator(var1);
      long var6 = var3 - var1;
      if (var6 < 0L) {
         throw new IllegalArgumentException("Start index (" + var1 + ") is greater than end index (" + var3 + ")");
      } else {
         while(var6-- != 0L) {
            var5.nextShort();
            var5.remove();
         }

      }
   }

   public void addElements(long var1, short[][] var3, long var4, long var6) {
      this.ensureIndex(var1);
      ShortBigArrays.ensureOffsetLength(var3, var4, var6);

      while(var6-- != 0L) {
         this.add(var1++, ShortBigArrays.get(var3, var4++));
      }

   }

   public void addElements(long var1, short[][] var3) {
      this.addElements(var1, var3, 0L, ShortBigArrays.length(var3));
   }

   public void getElements(long var1, short[][] var3, long var4, long var6) {
      ShortBigListIterator var8 = this.listIterator(var1);
      ShortBigArrays.ensureOffsetLength(var3, var4, var6);
      if (var1 + var6 > this.size64()) {
         throw new IndexOutOfBoundsException("End index (" + (var1 + var6) + ") is greater than list size (" + this.size64() + ")");
      } else {
         while(var6-- != 0L) {
            ShortBigArrays.set(var3, var4++, var8.nextShort());
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
      ShortBigListIterator var1 = this.iterator();
      int var2 = 1;

      short var5;
      for(long var3 = this.size64(); var3-- != 0L; var2 = 31 * var2 + var5) {
         var5 = var1.nextShort();
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
            ShortBigListIterator var5;
            if (var2 instanceof ShortBigList) {
               var5 = this.listIterator();
               ShortBigListIterator var7 = ((ShortBigList)var2).listIterator();

               do {
                  if (var3-- == 0L) {
                     return true;
                  }
               } while(var5.nextShort() == var7.nextShort());

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

   public int compareTo(BigList<? extends Short> var1) {
      if (var1 == this) {
         return 0;
      } else {
         ShortBigListIterator var2;
         int var4;
         if (var1 instanceof ShortBigList) {
            var2 = this.listIterator();
            ShortBigListIterator var7 = ((ShortBigList)var1).listIterator();

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

   public void push(short var1) {
      this.add(var1);
   }

   public short popShort() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.removeShort(this.size64() - 1L);
      }
   }

   public short topShort() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.getShort(this.size64() - 1L);
      }
   }

   public short peekShort(int var1) {
      return this.getShort(this.size64() - 1L - (long)var1);
   }

   public boolean rem(short var1) {
      long var2 = this.indexOf(var1);
      if (var2 == -1L) {
         return false;
      } else {
         this.removeShort(var2);
         return true;
      }
   }

   public boolean addAll(long var1, ShortCollection var3) {
      return this.addAll(var1, (Collection)var3);
   }

   public boolean addAll(long var1, ShortBigList var3) {
      return this.addAll(var1, (ShortCollection)var3);
   }

   public boolean addAll(ShortCollection var1) {
      return this.addAll(this.size64(), var1);
   }

   public boolean addAll(ShortBigList var1) {
      return this.addAll(this.size64(), var1);
   }

   /** @deprecated */
   @Deprecated
   public void add(long var1, Short var3) {
      this.add(var1, var3);
   }

   /** @deprecated */
   @Deprecated
   public Short set(long var1, Short var3) {
      return this.set(var1, var3);
   }

   /** @deprecated */
   @Deprecated
   public Short get(long var1) {
      return this.getShort(var1);
   }

   /** @deprecated */
   @Deprecated
   public long indexOf(Object var1) {
      return this.indexOf((Short)var1);
   }

   /** @deprecated */
   @Deprecated
   public long lastIndexOf(Object var1) {
      return this.lastIndexOf((Short)var1);
   }

   /** @deprecated */
   @Deprecated
   public Short remove(long var1) {
      return this.removeShort(var1);
   }

   /** @deprecated */
   @Deprecated
   public void push(Short var1) {
      this.push(var1);
   }

   /** @deprecated */
   @Deprecated
   public Short pop() {
      return this.popShort();
   }

   /** @deprecated */
   @Deprecated
   public Short top() {
      return this.topShort();
   }

   /** @deprecated */
   @Deprecated
   public Short peek(int var1) {
      return this.peekShort(var1);
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      ShortBigListIterator var2 = this.iterator();
      long var3 = this.size64();
      boolean var6 = true;
      var1.append("[");

      while(var3-- != 0L) {
         if (var6) {
            var6 = false;
         } else {
            var1.append(", ");
         }

         short var5 = var2.nextShort();
         var1.append(String.valueOf(var5));
      }

      var1.append("]");
      return var1.toString();
   }

   public static class ShortSubList extends AbstractShortBigList implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ShortBigList l;
      protected final long from;
      protected long to;

      public ShortSubList(ShortBigList var1, long var2, long var4) {
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

      public boolean add(short var1) {
         this.l.add(this.to, var1);
         ++this.to;

         assert this.assertRange();

         return true;
      }

      public void add(long var1, short var3) {
         this.ensureIndex(var1);
         this.l.add(this.from + var1, var3);
         ++this.to;

         assert this.assertRange();

      }

      public boolean addAll(long var1, Collection<? extends Short> var3) {
         this.ensureIndex(var1);
         this.to += (long)var3.size();
         return this.l.addAll(this.from + var1, (Collection)var3);
      }

      public short getShort(long var1) {
         this.ensureRestrictedIndex(var1);
         return this.l.getShort(this.from + var1);
      }

      public short removeShort(long var1) {
         this.ensureRestrictedIndex(var1);
         --this.to;
         return this.l.removeShort(this.from + var1);
      }

      public short set(long var1, short var3) {
         this.ensureRestrictedIndex(var1);
         return this.l.set(this.from + var1, var3);
      }

      public long size64() {
         return this.to - this.from;
      }

      public void getElements(long var1, short[][] var3, long var4, long var6) {
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

      public void addElements(long var1, short[][] var3, long var4, long var6) {
         this.ensureIndex(var1);
         this.l.addElements(this.from + var1, var3, var4, var6);
         this.to += var6;

         assert this.assertRange();

      }

      public ShortBigListIterator listIterator(final long var1) {
         this.ensureIndex(var1);
         return new ShortBigListIterator() {
            long pos = var1;
            long last = -1L;

            public boolean hasNext() {
               return this.pos < ShortSubList.this.size64();
            }

            public boolean hasPrevious() {
               return this.pos > 0L;
            }

            public short nextShort() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return ShortSubList.this.l.getShort(ShortSubList.this.from + (this.last = (long)(this.pos++)));
               }
            }

            public short previousShort() {
               if (!this.hasPrevious()) {
                  throw new NoSuchElementException();
               } else {
                  return ShortSubList.this.l.getShort(ShortSubList.this.from + (this.last = --this.pos));
               }
            }

            public long nextIndex() {
               return this.pos;
            }

            public long previousIndex() {
               return this.pos - 1L;
            }

            public void add(short var1x) {
               if (this.last == -1L) {
                  throw new IllegalStateException();
               } else {
                  ShortSubList.this.add((long)(this.pos++), var1x);
                  this.last = -1L;

                  assert ShortSubList.this.assertRange();

               }
            }

            public void set(short var1x) {
               if (this.last == -1L) {
                  throw new IllegalStateException();
               } else {
                  ShortSubList.this.set(this.last, var1x);
               }
            }

            public void remove() {
               if (this.last == -1L) {
                  throw new IllegalStateException();
               } else {
                  ShortSubList.this.removeShort(this.last);
                  if (this.last < this.pos) {
                     --this.pos;
                  }

                  this.last = -1L;

                  assert ShortSubList.this.assertRange();

               }
            }
         };
      }

      public ShortBigList subList(long var1, long var3) {
         this.ensureIndex(var1);
         this.ensureIndex(var3);
         if (var1 > var3) {
            throw new IllegalArgumentException("Start index (" + var1 + ") is greater than end index (" + var3 + ")");
         } else {
            return new AbstractShortBigList.ShortSubList(this, var1, var3);
         }
      }

      public boolean rem(short var1) {
         long var2 = this.indexOf(var1);
         if (var2 == -1L) {
            return false;
         } else {
            --this.to;
            this.l.removeShort(this.from + var2);

            assert this.assertRange();

            return true;
         }
      }

      public boolean addAll(long var1, ShortCollection var3) {
         this.ensureIndex(var1);
         return super.addAll(var1, var3);
      }

      public boolean addAll(long var1, ShortBigList var3) {
         this.ensureIndex(var1);
         return super.addAll(var1, var3);
      }
   }
}
