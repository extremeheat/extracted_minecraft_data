package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.BigListIterator;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class AbstractBooleanBigList extends AbstractBooleanCollection implements BooleanBigList, BooleanStack {
   protected AbstractBooleanBigList() {
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

   public void add(long var1, boolean var3) {
      throw new UnsupportedOperationException();
   }

   public boolean add(boolean var1) {
      this.add(this.size64(), var1);
      return true;
   }

   public boolean removeBoolean(long var1) {
      throw new UnsupportedOperationException();
   }

   public boolean set(long var1, boolean var3) {
      throw new UnsupportedOperationException();
   }

   public boolean addAll(long var1, Collection<? extends Boolean> var3) {
      this.ensureIndex(var1);
      Iterator var4 = var3.iterator();
      boolean var5 = var4.hasNext();

      while(var4.hasNext()) {
         this.add(var1++, (Boolean)var4.next());
      }

      return var5;
   }

   public boolean addAll(Collection<? extends Boolean> var1) {
      return this.addAll(this.size64(), var1);
   }

   public BooleanBigListIterator iterator() {
      return this.listIterator();
   }

   public BooleanBigListIterator listIterator() {
      return this.listIterator(0L);
   }

   public BooleanBigListIterator listIterator(final long var1) {
      this.ensureIndex(var1);
      return new BooleanBigListIterator() {
         long pos = var1;
         long last = -1L;

         public boolean hasNext() {
            return this.pos < AbstractBooleanBigList.this.size64();
         }

         public boolean hasPrevious() {
            return this.pos > 0L;
         }

         public boolean nextBoolean() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return AbstractBooleanBigList.this.getBoolean(this.last = (long)(this.pos++));
            }
         }

         public boolean previousBoolean() {
            if (!this.hasPrevious()) {
               throw new NoSuchElementException();
            } else {
               return AbstractBooleanBigList.this.getBoolean(this.last = --this.pos);
            }
         }

         public long nextIndex() {
            return this.pos;
         }

         public long previousIndex() {
            return this.pos - 1L;
         }

         public void add(boolean var1x) {
            AbstractBooleanBigList.this.add((long)(this.pos++), var1x);
            this.last = -1L;
         }

         public void set(boolean var1x) {
            if (this.last == -1L) {
               throw new IllegalStateException();
            } else {
               AbstractBooleanBigList.this.set(this.last, var1x);
            }
         }

         public void remove() {
            if (this.last == -1L) {
               throw new IllegalStateException();
            } else {
               AbstractBooleanBigList.this.removeBoolean(this.last);
               if (this.last < this.pos) {
                  --this.pos;
               }

               this.last = -1L;
            }
         }
      };
   }

   public boolean contains(boolean var1) {
      return this.indexOf(var1) >= 0L;
   }

   public long indexOf(boolean var1) {
      BooleanBigListIterator var2 = this.listIterator();

      boolean var3;
      do {
         if (!var2.hasNext()) {
            return -1L;
         }

         var3 = var2.nextBoolean();
      } while(var1 != var3);

      return var2.previousIndex();
   }

   public long lastIndexOf(boolean var1) {
      BooleanBigListIterator var2 = this.listIterator(this.size64());

      boolean var3;
      do {
         if (!var2.hasPrevious()) {
            return -1L;
         }

         var3 = var2.previousBoolean();
      } while(var1 != var3);

      return var2.nextIndex();
   }

   public void size(long var1) {
      long var3 = this.size64();
      if (var1 > var3) {
         while(var3++ < var1) {
            this.add(false);
         }
      } else {
         while(var3-- != var1) {
            this.remove(var3);
         }
      }

   }

   public BooleanBigList subList(long var1, long var3) {
      this.ensureIndex(var1);
      this.ensureIndex(var3);
      if (var1 > var3) {
         throw new IndexOutOfBoundsException("Start index (" + var1 + ") is greater than end index (" + var3 + ")");
      } else {
         return new AbstractBooleanBigList.BooleanSubList(this, var1, var3);
      }
   }

   public void removeElements(long var1, long var3) {
      this.ensureIndex(var3);
      BooleanBigListIterator var5 = this.listIterator(var1);
      long var6 = var3 - var1;
      if (var6 < 0L) {
         throw new IllegalArgumentException("Start index (" + var1 + ") is greater than end index (" + var3 + ")");
      } else {
         while(var6-- != 0L) {
            var5.nextBoolean();
            var5.remove();
         }

      }
   }

   public void addElements(long var1, boolean[][] var3, long var4, long var6) {
      this.ensureIndex(var1);
      BooleanBigArrays.ensureOffsetLength(var3, var4, var6);

      while(var6-- != 0L) {
         this.add(var1++, BooleanBigArrays.get(var3, var4++));
      }

   }

   public void addElements(long var1, boolean[][] var3) {
      this.addElements(var1, var3, 0L, BooleanBigArrays.length(var3));
   }

   public void getElements(long var1, boolean[][] var3, long var4, long var6) {
      BooleanBigListIterator var8 = this.listIterator(var1);
      BooleanBigArrays.ensureOffsetLength(var3, var4, var6);
      if (var1 + var6 > this.size64()) {
         throw new IndexOutOfBoundsException("End index (" + (var1 + var6) + ") is greater than list size (" + this.size64() + ")");
      } else {
         while(var6-- != 0L) {
            BooleanBigArrays.set(var3, var4++, var8.nextBoolean());
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
      BooleanBigListIterator var1 = this.iterator();
      int var2 = 1;

      boolean var5;
      for(long var3 = this.size64(); var3-- != 0L; var2 = 31 * var2 + (var5 ? 1231 : 1237)) {
         var5 = var1.nextBoolean();
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
            BooleanBigListIterator var5;
            if (var2 instanceof BooleanBigList) {
               var5 = this.listIterator();
               BooleanBigListIterator var7 = ((BooleanBigList)var2).listIterator();

               do {
                  if (var3-- == 0L) {
                     return true;
                  }
               } while(var5.nextBoolean() == var7.nextBoolean());

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

   public int compareTo(BigList<? extends Boolean> var1) {
      if (var1 == this) {
         return 0;
      } else {
         BooleanBigListIterator var2;
         int var4;
         if (var1 instanceof BooleanBigList) {
            var2 = this.listIterator();
            BooleanBigListIterator var7 = ((BooleanBigList)var1).listIterator();

            while(var2.hasNext() && var7.hasNext()) {
               boolean var5 = var2.nextBoolean();
               boolean var6 = var7.nextBoolean();
               if ((var4 = Boolean.compare(var5, var6)) != 0) {
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

   public void push(boolean var1) {
      this.add(var1);
   }

   public boolean popBoolean() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.removeBoolean(this.size64() - 1L);
      }
   }

   public boolean topBoolean() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.getBoolean(this.size64() - 1L);
      }
   }

   public boolean peekBoolean(int var1) {
      return this.getBoolean(this.size64() - 1L - (long)var1);
   }

   public boolean rem(boolean var1) {
      long var2 = this.indexOf(var1);
      if (var2 == -1L) {
         return false;
      } else {
         this.removeBoolean(var2);
         return true;
      }
   }

   public boolean addAll(long var1, BooleanCollection var3) {
      return this.addAll(var1, (Collection)var3);
   }

   public boolean addAll(long var1, BooleanBigList var3) {
      return this.addAll(var1, (BooleanCollection)var3);
   }

   public boolean addAll(BooleanCollection var1) {
      return this.addAll(this.size64(), var1);
   }

   public boolean addAll(BooleanBigList var1) {
      return this.addAll(this.size64(), var1);
   }

   /** @deprecated */
   @Deprecated
   public void add(long var1, Boolean var3) {
      this.add(var1, var3);
   }

   /** @deprecated */
   @Deprecated
   public Boolean set(long var1, Boolean var3) {
      return this.set(var1, var3);
   }

   /** @deprecated */
   @Deprecated
   public Boolean get(long var1) {
      return this.getBoolean(var1);
   }

   /** @deprecated */
   @Deprecated
   public long indexOf(Object var1) {
      return this.indexOf((Boolean)var1);
   }

   /** @deprecated */
   @Deprecated
   public long lastIndexOf(Object var1) {
      return this.lastIndexOf((Boolean)var1);
   }

   /** @deprecated */
   @Deprecated
   public Boolean remove(long var1) {
      return this.removeBoolean(var1);
   }

   /** @deprecated */
   @Deprecated
   public void push(Boolean var1) {
      this.push(var1);
   }

   /** @deprecated */
   @Deprecated
   public Boolean pop() {
      return this.popBoolean();
   }

   /** @deprecated */
   @Deprecated
   public Boolean top() {
      return this.topBoolean();
   }

   /** @deprecated */
   @Deprecated
   public Boolean peek(int var1) {
      return this.peekBoolean(var1);
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      BooleanBigListIterator var2 = this.iterator();
      long var3 = this.size64();
      boolean var6 = true;
      var1.append("[");

      while(var3-- != 0L) {
         if (var6) {
            var6 = false;
         } else {
            var1.append(", ");
         }

         boolean var5 = var2.nextBoolean();
         var1.append(String.valueOf(var5));
      }

      var1.append("]");
      return var1.toString();
   }

   public static class BooleanSubList extends AbstractBooleanBigList implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final BooleanBigList l;
      protected final long from;
      protected long to;

      public BooleanSubList(BooleanBigList var1, long var2, long var4) {
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

      public boolean add(boolean var1) {
         this.l.add(this.to, var1);
         ++this.to;

         assert this.assertRange();

         return true;
      }

      public void add(long var1, boolean var3) {
         this.ensureIndex(var1);
         this.l.add(this.from + var1, var3);
         ++this.to;

         assert this.assertRange();

      }

      public boolean addAll(long var1, Collection<? extends Boolean> var3) {
         this.ensureIndex(var1);
         this.to += (long)var3.size();
         return this.l.addAll(this.from + var1, (Collection)var3);
      }

      public boolean getBoolean(long var1) {
         this.ensureRestrictedIndex(var1);
         return this.l.getBoolean(this.from + var1);
      }

      public boolean removeBoolean(long var1) {
         this.ensureRestrictedIndex(var1);
         --this.to;
         return this.l.removeBoolean(this.from + var1);
      }

      public boolean set(long var1, boolean var3) {
         this.ensureRestrictedIndex(var1);
         return this.l.set(this.from + var1, var3);
      }

      public long size64() {
         return this.to - this.from;
      }

      public void getElements(long var1, boolean[][] var3, long var4, long var6) {
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

      public void addElements(long var1, boolean[][] var3, long var4, long var6) {
         this.ensureIndex(var1);
         this.l.addElements(this.from + var1, var3, var4, var6);
         this.to += var6;

         assert this.assertRange();

      }

      public BooleanBigListIterator listIterator(final long var1) {
         this.ensureIndex(var1);
         return new BooleanBigListIterator() {
            long pos = var1;
            long last = -1L;

            public boolean hasNext() {
               return this.pos < BooleanSubList.this.size64();
            }

            public boolean hasPrevious() {
               return this.pos > 0L;
            }

            public boolean nextBoolean() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return BooleanSubList.this.l.getBoolean(BooleanSubList.this.from + (this.last = (long)(this.pos++)));
               }
            }

            public boolean previousBoolean() {
               if (!this.hasPrevious()) {
                  throw new NoSuchElementException();
               } else {
                  return BooleanSubList.this.l.getBoolean(BooleanSubList.this.from + (this.last = --this.pos));
               }
            }

            public long nextIndex() {
               return this.pos;
            }

            public long previousIndex() {
               return this.pos - 1L;
            }

            public void add(boolean var1x) {
               if (this.last == -1L) {
                  throw new IllegalStateException();
               } else {
                  BooleanSubList.this.add((long)(this.pos++), var1x);
                  this.last = -1L;

                  assert BooleanSubList.this.assertRange();

               }
            }

            public void set(boolean var1x) {
               if (this.last == -1L) {
                  throw new IllegalStateException();
               } else {
                  BooleanSubList.this.set(this.last, var1x);
               }
            }

            public void remove() {
               if (this.last == -1L) {
                  throw new IllegalStateException();
               } else {
                  BooleanSubList.this.removeBoolean(this.last);
                  if (this.last < this.pos) {
                     --this.pos;
                  }

                  this.last = -1L;

                  assert BooleanSubList.this.assertRange();

               }
            }
         };
      }

      public BooleanBigList subList(long var1, long var3) {
         this.ensureIndex(var1);
         this.ensureIndex(var3);
         if (var1 > var3) {
            throw new IllegalArgumentException("Start index (" + var1 + ") is greater than end index (" + var3 + ")");
         } else {
            return new AbstractBooleanBigList.BooleanSubList(this, var1, var3);
         }
      }

      public boolean rem(boolean var1) {
         long var2 = this.indexOf(var1);
         if (var2 == -1L) {
            return false;
         } else {
            --this.to;
            this.l.removeBoolean(this.from + var2);

            assert this.assertRange();

            return true;
         }
      }

      public boolean addAll(long var1, BooleanCollection var3) {
         this.ensureIndex(var1);
         return super.addAll(var1, var3);
      }

      public boolean addAll(long var1, BooleanBigList var3) {
         this.ensureIndex(var1);
         return super.addAll(var1, var3);
      }
   }
}
