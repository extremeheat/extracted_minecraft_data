package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.HashCommon;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class AbstractLongBigList extends AbstractLongCollection implements LongBigList, LongStack {
   protected AbstractLongBigList() {
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

   public void add(long var1, long var3) {
      throw new UnsupportedOperationException();
   }

   public boolean add(long var1) {
      this.add(this.size64(), var1);
      return true;
   }

   public long removeLong(long var1) {
      throw new UnsupportedOperationException();
   }

   public long set(long var1, long var3) {
      throw new UnsupportedOperationException();
   }

   public boolean addAll(long var1, Collection<? extends Long> var3) {
      this.ensureIndex(var1);
      Iterator var4 = var3.iterator();
      boolean var5 = var4.hasNext();

      while(var4.hasNext()) {
         this.add(var1++, (Long)var4.next());
      }

      return var5;
   }

   public boolean addAll(Collection<? extends Long> var1) {
      return this.addAll(this.size64(), var1);
   }

   public LongBigListIterator iterator() {
      return this.listIterator();
   }

   public LongBigListIterator listIterator() {
      return this.listIterator(0L);
   }

   public LongBigListIterator listIterator(final long var1) {
      this.ensureIndex(var1);
      return new LongBigListIterator() {
         long pos = var1;
         long last = -1L;

         public boolean hasNext() {
            return this.pos < AbstractLongBigList.this.size64();
         }

         public boolean hasPrevious() {
            return this.pos > 0L;
         }

         public long nextLong() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return AbstractLongBigList.this.getLong(this.last = (long)(this.pos++));
            }
         }

         public long previousLong() {
            if (!this.hasPrevious()) {
               throw new NoSuchElementException();
            } else {
               return AbstractLongBigList.this.getLong(this.last = --this.pos);
            }
         }

         public long nextIndex() {
            return this.pos;
         }

         public long previousIndex() {
            return this.pos - 1L;
         }

         public void add(long var1x) {
            AbstractLongBigList.this.add((long)(this.pos++), var1x);
            this.last = -1L;
         }

         public void set(long var1x) {
            if (this.last == -1L) {
               throw new IllegalStateException();
            } else {
               AbstractLongBigList.this.set(this.last, var1x);
            }
         }

         public void remove() {
            if (this.last == -1L) {
               throw new IllegalStateException();
            } else {
               AbstractLongBigList.this.removeLong(this.last);
               if (this.last < this.pos) {
                  --this.pos;
               }

               this.last = -1L;
            }
         }
      };
   }

   public boolean contains(long var1) {
      return this.indexOf(var1) >= 0L;
   }

   public long indexOf(long var1) {
      LongBigListIterator var3 = this.listIterator();

      long var4;
      do {
         if (!var3.hasNext()) {
            return -1L;
         }

         var4 = var3.nextLong();
      } while(var1 != var4);

      return var3.previousIndex();
   }

   public long lastIndexOf(long var1) {
      LongBigListIterator var3 = this.listIterator(this.size64());

      long var4;
      do {
         if (!var3.hasPrevious()) {
            return -1L;
         }

         var4 = var3.previousLong();
      } while(var1 != var4);

      return var3.nextIndex();
   }

   public void size(long var1) {
      long var3 = this.size64();
      if (var1 > var3) {
         while(var3++ < var1) {
            this.add(0L);
         }
      } else {
         while(var3-- != var1) {
            this.remove(var3);
         }
      }

   }

   public LongBigList subList(long var1, long var3) {
      this.ensureIndex(var1);
      this.ensureIndex(var3);
      if (var1 > var3) {
         throw new IndexOutOfBoundsException("Start index (" + var1 + ") is greater than end index (" + var3 + ")");
      } else {
         return new AbstractLongBigList.LongSubList(this, var1, var3);
      }
   }

   public void removeElements(long var1, long var3) {
      this.ensureIndex(var3);
      LongBigListIterator var5 = this.listIterator(var1);
      long var6 = var3 - var1;
      if (var6 < 0L) {
         throw new IllegalArgumentException("Start index (" + var1 + ") is greater than end index (" + var3 + ")");
      } else {
         while(var6-- != 0L) {
            var5.nextLong();
            var5.remove();
         }

      }
   }

   public void addElements(long var1, long[][] var3, long var4, long var6) {
      this.ensureIndex(var1);
      LongBigArrays.ensureOffsetLength(var3, var4, var6);

      while(var6-- != 0L) {
         this.add(var1++, LongBigArrays.get(var3, var4++));
      }

   }

   public void addElements(long var1, long[][] var3) {
      this.addElements(var1, var3, 0L, LongBigArrays.length(var3));
   }

   public void getElements(long var1, long[][] var3, long var4, long var6) {
      LongBigListIterator var8 = this.listIterator(var1);
      LongBigArrays.ensureOffsetLength(var3, var4, var6);
      if (var1 + var6 > this.size64()) {
         throw new IndexOutOfBoundsException("End index (" + (var1 + var6) + ") is greater than list size (" + this.size64() + ")");
      } else {
         while(var6-- != 0L) {
            LongBigArrays.set(var3, var4++, var8.nextLong());
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
      LongBigListIterator var1 = this.iterator();
      int var2 = 1;

      long var5;
      for(long var3 = this.size64(); var3-- != 0L; var2 = 31 * var2 + HashCommon.long2int(var5)) {
         var5 = var1.nextLong();
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
            LongBigListIterator var5;
            if (var2 instanceof LongBigList) {
               var5 = this.listIterator();
               LongBigListIterator var7 = ((LongBigList)var2).listIterator();

               do {
                  if (var3-- == 0L) {
                     return true;
                  }
               } while(var5.nextLong() == var7.nextLong());

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

   public int compareTo(BigList<? extends Long> var1) {
      if (var1 == this) {
         return 0;
      } else {
         LongBigListIterator var2;
         int var4;
         if (var1 instanceof LongBigList) {
            var2 = this.listIterator();
            LongBigListIterator var9 = ((LongBigList)var1).listIterator();

            while(var2.hasNext() && var9.hasNext()) {
               long var5 = var2.nextLong();
               long var7 = var9.nextLong();
               if ((var4 = Long.compare(var5, var7)) != 0) {
                  return var4;
               }
            }

            return var9.hasNext() ? -1 : (var2.hasNext() ? 1 : 0);
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

   public void push(long var1) {
      this.add(var1);
   }

   public long popLong() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.removeLong(this.size64() - 1L);
      }
   }

   public long topLong() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.getLong(this.size64() - 1L);
      }
   }

   public long peekLong(int var1) {
      return this.getLong(this.size64() - 1L - (long)var1);
   }

   public boolean rem(long var1) {
      long var3 = this.indexOf(var1);
      if (var3 == -1L) {
         return false;
      } else {
         this.removeLong(var3);
         return true;
      }
   }

   public boolean addAll(long var1, LongCollection var3) {
      return this.addAll(var1, (Collection)var3);
   }

   public boolean addAll(long var1, LongBigList var3) {
      return this.addAll(var1, (LongCollection)var3);
   }

   public boolean addAll(LongCollection var1) {
      return this.addAll(this.size64(), var1);
   }

   public boolean addAll(LongBigList var1) {
      return this.addAll(this.size64(), var1);
   }

   /** @deprecated */
   @Deprecated
   public void add(long var1, Long var3) {
      this.add(var1, var3);
   }

   /** @deprecated */
   @Deprecated
   public Long set(long var1, Long var3) {
      return this.set(var1, var3);
   }

   /** @deprecated */
   @Deprecated
   public Long get(long var1) {
      return this.getLong(var1);
   }

   /** @deprecated */
   @Deprecated
   public long indexOf(Object var1) {
      return this.indexOf((Long)var1);
   }

   /** @deprecated */
   @Deprecated
   public long lastIndexOf(Object var1) {
      return this.lastIndexOf((Long)var1);
   }

   /** @deprecated */
   @Deprecated
   public Long remove(long var1) {
      return this.removeLong(var1);
   }

   /** @deprecated */
   @Deprecated
   public void push(Long var1) {
      this.push(var1);
   }

   /** @deprecated */
   @Deprecated
   public Long pop() {
      return this.popLong();
   }

   /** @deprecated */
   @Deprecated
   public Long top() {
      return this.topLong();
   }

   /** @deprecated */
   @Deprecated
   public Long peek(int var1) {
      return this.peekLong(var1);
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      LongBigListIterator var2 = this.iterator();
      long var3 = this.size64();
      boolean var7 = true;
      var1.append("[");

      while(var3-- != 0L) {
         if (var7) {
            var7 = false;
         } else {
            var1.append(", ");
         }

         long var5 = var2.nextLong();
         var1.append(String.valueOf(var5));
      }

      var1.append("]");
      return var1.toString();
   }

   public static class LongSubList extends AbstractLongBigList implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final LongBigList l;
      protected final long from;
      protected long to;

      public LongSubList(LongBigList var1, long var2, long var4) {
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

      public boolean add(long var1) {
         this.l.add(this.to, var1);
         ++this.to;

         assert this.assertRange();

         return true;
      }

      public void add(long var1, long var3) {
         this.ensureIndex(var1);
         this.l.add(this.from + var1, var3);
         ++this.to;

         assert this.assertRange();

      }

      public boolean addAll(long var1, Collection<? extends Long> var3) {
         this.ensureIndex(var1);
         this.to += (long)var3.size();
         return this.l.addAll(this.from + var1, (Collection)var3);
      }

      public long getLong(long var1) {
         this.ensureRestrictedIndex(var1);
         return this.l.getLong(this.from + var1);
      }

      public long removeLong(long var1) {
         this.ensureRestrictedIndex(var1);
         --this.to;
         return this.l.removeLong(this.from + var1);
      }

      public long set(long var1, long var3) {
         this.ensureRestrictedIndex(var1);
         return this.l.set(this.from + var1, var3);
      }

      public long size64() {
         return this.to - this.from;
      }

      public void getElements(long var1, long[][] var3, long var4, long var6) {
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

      public void addElements(long var1, long[][] var3, long var4, long var6) {
         this.ensureIndex(var1);
         this.l.addElements(this.from + var1, var3, var4, var6);
         this.to += var6;

         assert this.assertRange();

      }

      public LongBigListIterator listIterator(final long var1) {
         this.ensureIndex(var1);
         return new LongBigListIterator() {
            long pos = var1;
            long last = -1L;

            public boolean hasNext() {
               return this.pos < LongSubList.this.size64();
            }

            public boolean hasPrevious() {
               return this.pos > 0L;
            }

            public long nextLong() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return LongSubList.this.l.getLong(LongSubList.this.from + (this.last = (long)(this.pos++)));
               }
            }

            public long previousLong() {
               if (!this.hasPrevious()) {
                  throw new NoSuchElementException();
               } else {
                  return LongSubList.this.l.getLong(LongSubList.this.from + (this.last = --this.pos));
               }
            }

            public long nextIndex() {
               return this.pos;
            }

            public long previousIndex() {
               return this.pos - 1L;
            }

            public void add(long var1x) {
               if (this.last == -1L) {
                  throw new IllegalStateException();
               } else {
                  LongSubList.this.add((long)(this.pos++), var1x);
                  this.last = -1L;

                  assert LongSubList.this.assertRange();

               }
            }

            public void set(long var1x) {
               if (this.last == -1L) {
                  throw new IllegalStateException();
               } else {
                  LongSubList.this.set(this.last, var1x);
               }
            }

            public void remove() {
               if (this.last == -1L) {
                  throw new IllegalStateException();
               } else {
                  LongSubList.this.removeLong(this.last);
                  if (this.last < this.pos) {
                     --this.pos;
                  }

                  this.last = -1L;

                  assert LongSubList.this.assertRange();

               }
            }
         };
      }

      public LongBigList subList(long var1, long var3) {
         this.ensureIndex(var1);
         this.ensureIndex(var3);
         if (var1 > var3) {
            throw new IllegalArgumentException("Start index (" + var1 + ") is greater than end index (" + var3 + ")");
         } else {
            return new AbstractLongBigList.LongSubList(this, var1, var3);
         }
      }

      public boolean rem(long var1) {
         long var3 = this.indexOf(var1);
         if (var3 == -1L) {
            return false;
         } else {
            --this.to;
            this.l.removeLong(this.from + var3);

            assert this.assertRange();

            return true;
         }
      }

      public boolean addAll(long var1, LongCollection var3) {
         this.ensureIndex(var1);
         return super.addAll(var1, var3);
      }

      public boolean addAll(long var1, LongBigList var3) {
         this.ensureIndex(var1);
         return super.addAll(var1, var3);
      }
   }
}
