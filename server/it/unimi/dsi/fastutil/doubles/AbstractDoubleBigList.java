package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.HashCommon;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class AbstractDoubleBigList extends AbstractDoubleCollection implements DoubleBigList, DoubleStack {
   protected AbstractDoubleBigList() {
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

   public void add(long var1, double var3) {
      throw new UnsupportedOperationException();
   }

   public boolean add(double var1) {
      this.add(this.size64(), var1);
      return true;
   }

   public double removeDouble(long var1) {
      throw new UnsupportedOperationException();
   }

   public double set(long var1, double var3) {
      throw new UnsupportedOperationException();
   }

   public boolean addAll(long var1, Collection<? extends Double> var3) {
      this.ensureIndex(var1);
      Iterator var4 = var3.iterator();
      boolean var5 = var4.hasNext();

      while(var4.hasNext()) {
         this.add(var1++, (Double)var4.next());
      }

      return var5;
   }

   public boolean addAll(Collection<? extends Double> var1) {
      return this.addAll(this.size64(), var1);
   }

   public DoubleBigListIterator iterator() {
      return this.listIterator();
   }

   public DoubleBigListIterator listIterator() {
      return this.listIterator(0L);
   }

   public DoubleBigListIterator listIterator(final long var1) {
      this.ensureIndex(var1);
      return new DoubleBigListIterator() {
         long pos = var1;
         long last = -1L;

         public boolean hasNext() {
            return this.pos < AbstractDoubleBigList.this.size64();
         }

         public boolean hasPrevious() {
            return this.pos > 0L;
         }

         public double nextDouble() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return AbstractDoubleBigList.this.getDouble(this.last = (long)(this.pos++));
            }
         }

         public double previousDouble() {
            if (!this.hasPrevious()) {
               throw new NoSuchElementException();
            } else {
               return AbstractDoubleBigList.this.getDouble(this.last = --this.pos);
            }
         }

         public long nextIndex() {
            return this.pos;
         }

         public long previousIndex() {
            return this.pos - 1L;
         }

         public void add(double var1x) {
            AbstractDoubleBigList.this.add((long)(this.pos++), var1x);
            this.last = -1L;
         }

         public void set(double var1x) {
            if (this.last == -1L) {
               throw new IllegalStateException();
            } else {
               AbstractDoubleBigList.this.set(this.last, var1x);
            }
         }

         public void remove() {
            if (this.last == -1L) {
               throw new IllegalStateException();
            } else {
               AbstractDoubleBigList.this.removeDouble(this.last);
               if (this.last < this.pos) {
                  --this.pos;
               }

               this.last = -1L;
            }
         }
      };
   }

   public boolean contains(double var1) {
      return this.indexOf(var1) >= 0L;
   }

   public long indexOf(double var1) {
      DoubleBigListIterator var3 = this.listIterator();

      double var4;
      do {
         if (!var3.hasNext()) {
            return -1L;
         }

         var4 = var3.nextDouble();
      } while(Double.doubleToLongBits(var1) != Double.doubleToLongBits(var4));

      return var3.previousIndex();
   }

   public long lastIndexOf(double var1) {
      DoubleBigListIterator var3 = this.listIterator(this.size64());

      double var4;
      do {
         if (!var3.hasPrevious()) {
            return -1L;
         }

         var4 = var3.previousDouble();
      } while(Double.doubleToLongBits(var1) != Double.doubleToLongBits(var4));

      return var3.nextIndex();
   }

   public void size(long var1) {
      long var3 = this.size64();
      if (var1 > var3) {
         while(var3++ < var1) {
            this.add(0.0D);
         }
      } else {
         while(var3-- != var1) {
            this.remove(var3);
         }
      }

   }

   public DoubleBigList subList(long var1, long var3) {
      this.ensureIndex(var1);
      this.ensureIndex(var3);
      if (var1 > var3) {
         throw new IndexOutOfBoundsException("Start index (" + var1 + ") is greater than end index (" + var3 + ")");
      } else {
         return new AbstractDoubleBigList.DoubleSubList(this, var1, var3);
      }
   }

   public void removeElements(long var1, long var3) {
      this.ensureIndex(var3);
      DoubleBigListIterator var5 = this.listIterator(var1);
      long var6 = var3 - var1;
      if (var6 < 0L) {
         throw new IllegalArgumentException("Start index (" + var1 + ") is greater than end index (" + var3 + ")");
      } else {
         while(var6-- != 0L) {
            var5.nextDouble();
            var5.remove();
         }

      }
   }

   public void addElements(long var1, double[][] var3, long var4, long var6) {
      this.ensureIndex(var1);
      DoubleBigArrays.ensureOffsetLength(var3, var4, var6);

      while(var6-- != 0L) {
         this.add(var1++, DoubleBigArrays.get(var3, var4++));
      }

   }

   public void addElements(long var1, double[][] var3) {
      this.addElements(var1, var3, 0L, DoubleBigArrays.length(var3));
   }

   public void getElements(long var1, double[][] var3, long var4, long var6) {
      DoubleBigListIterator var8 = this.listIterator(var1);
      DoubleBigArrays.ensureOffsetLength(var3, var4, var6);
      if (var1 + var6 > this.size64()) {
         throw new IndexOutOfBoundsException("End index (" + (var1 + var6) + ") is greater than list size (" + this.size64() + ")");
      } else {
         while(var6-- != 0L) {
            DoubleBigArrays.set(var3, var4++, var8.nextDouble());
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
      DoubleBigListIterator var1 = this.iterator();
      int var2 = 1;

      double var5;
      for(long var3 = this.size64(); var3-- != 0L; var2 = 31 * var2 + HashCommon.double2int(var5)) {
         var5 = var1.nextDouble();
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
            DoubleBigListIterator var5;
            if (var2 instanceof DoubleBigList) {
               var5 = this.listIterator();
               DoubleBigListIterator var7 = ((DoubleBigList)var2).listIterator();

               do {
                  if (var3-- == 0L) {
                     return true;
                  }
               } while(var5.nextDouble() == var7.nextDouble());

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

   public int compareTo(BigList<? extends Double> var1) {
      if (var1 == this) {
         return 0;
      } else {
         DoubleBigListIterator var2;
         int var4;
         if (var1 instanceof DoubleBigList) {
            var2 = this.listIterator();
            DoubleBigListIterator var9 = ((DoubleBigList)var1).listIterator();

            while(var2.hasNext() && var9.hasNext()) {
               double var5 = var2.nextDouble();
               double var7 = var9.nextDouble();
               if ((var4 = Double.compare(var5, var7)) != 0) {
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

   public void push(double var1) {
      this.add(var1);
   }

   public double popDouble() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.removeDouble(this.size64() - 1L);
      }
   }

   public double topDouble() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.getDouble(this.size64() - 1L);
      }
   }

   public double peekDouble(int var1) {
      return this.getDouble(this.size64() - 1L - (long)var1);
   }

   public boolean rem(double var1) {
      long var3 = this.indexOf(var1);
      if (var3 == -1L) {
         return false;
      } else {
         this.removeDouble(var3);
         return true;
      }
   }

   public boolean addAll(long var1, DoubleCollection var3) {
      return this.addAll(var1, (Collection)var3);
   }

   public boolean addAll(long var1, DoubleBigList var3) {
      return this.addAll(var1, (DoubleCollection)var3);
   }

   public boolean addAll(DoubleCollection var1) {
      return this.addAll(this.size64(), var1);
   }

   public boolean addAll(DoubleBigList var1) {
      return this.addAll(this.size64(), var1);
   }

   /** @deprecated */
   @Deprecated
   public void add(long var1, Double var3) {
      this.add(var1, var3);
   }

   /** @deprecated */
   @Deprecated
   public Double set(long var1, Double var3) {
      return this.set(var1, var3);
   }

   /** @deprecated */
   @Deprecated
   public Double get(long var1) {
      return this.getDouble(var1);
   }

   /** @deprecated */
   @Deprecated
   public long indexOf(Object var1) {
      return this.indexOf((Double)var1);
   }

   /** @deprecated */
   @Deprecated
   public long lastIndexOf(Object var1) {
      return this.lastIndexOf((Double)var1);
   }

   /** @deprecated */
   @Deprecated
   public Double remove(long var1) {
      return this.removeDouble(var1);
   }

   /** @deprecated */
   @Deprecated
   public void push(Double var1) {
      this.push(var1);
   }

   /** @deprecated */
   @Deprecated
   public Double pop() {
      return this.popDouble();
   }

   /** @deprecated */
   @Deprecated
   public Double top() {
      return this.topDouble();
   }

   /** @deprecated */
   @Deprecated
   public Double peek(int var1) {
      return this.peekDouble(var1);
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      DoubleBigListIterator var2 = this.iterator();
      long var3 = this.size64();
      boolean var7 = true;
      var1.append("[");

      while(var3-- != 0L) {
         if (var7) {
            var7 = false;
         } else {
            var1.append(", ");
         }

         double var5 = var2.nextDouble();
         var1.append(String.valueOf(var5));
      }

      var1.append("]");
      return var1.toString();
   }

   public static class DoubleSubList extends AbstractDoubleBigList implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final DoubleBigList l;
      protected final long from;
      protected long to;

      public DoubleSubList(DoubleBigList var1, long var2, long var4) {
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

      public boolean add(double var1) {
         this.l.add(this.to, var1);
         ++this.to;

         assert this.assertRange();

         return true;
      }

      public void add(long var1, double var3) {
         this.ensureIndex(var1);
         this.l.add(this.from + var1, var3);
         ++this.to;

         assert this.assertRange();

      }

      public boolean addAll(long var1, Collection<? extends Double> var3) {
         this.ensureIndex(var1);
         this.to += (long)var3.size();
         return this.l.addAll(this.from + var1, (Collection)var3);
      }

      public double getDouble(long var1) {
         this.ensureRestrictedIndex(var1);
         return this.l.getDouble(this.from + var1);
      }

      public double removeDouble(long var1) {
         this.ensureRestrictedIndex(var1);
         --this.to;
         return this.l.removeDouble(this.from + var1);
      }

      public double set(long var1, double var3) {
         this.ensureRestrictedIndex(var1);
         return this.l.set(this.from + var1, var3);
      }

      public long size64() {
         return this.to - this.from;
      }

      public void getElements(long var1, double[][] var3, long var4, long var6) {
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

      public void addElements(long var1, double[][] var3, long var4, long var6) {
         this.ensureIndex(var1);
         this.l.addElements(this.from + var1, var3, var4, var6);
         this.to += var6;

         assert this.assertRange();

      }

      public DoubleBigListIterator listIterator(final long var1) {
         this.ensureIndex(var1);
         return new DoubleBigListIterator() {
            long pos = var1;
            long last = -1L;

            public boolean hasNext() {
               return this.pos < DoubleSubList.this.size64();
            }

            public boolean hasPrevious() {
               return this.pos > 0L;
            }

            public double nextDouble() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return DoubleSubList.this.l.getDouble(DoubleSubList.this.from + (this.last = (long)(this.pos++)));
               }
            }

            public double previousDouble() {
               if (!this.hasPrevious()) {
                  throw new NoSuchElementException();
               } else {
                  return DoubleSubList.this.l.getDouble(DoubleSubList.this.from + (this.last = --this.pos));
               }
            }

            public long nextIndex() {
               return this.pos;
            }

            public long previousIndex() {
               return this.pos - 1L;
            }

            public void add(double var1x) {
               if (this.last == -1L) {
                  throw new IllegalStateException();
               } else {
                  DoubleSubList.this.add((long)(this.pos++), var1x);
                  this.last = -1L;

                  assert DoubleSubList.this.assertRange();

               }
            }

            public void set(double var1x) {
               if (this.last == -1L) {
                  throw new IllegalStateException();
               } else {
                  DoubleSubList.this.set(this.last, var1x);
               }
            }

            public void remove() {
               if (this.last == -1L) {
                  throw new IllegalStateException();
               } else {
                  DoubleSubList.this.removeDouble(this.last);
                  if (this.last < this.pos) {
                     --this.pos;
                  }

                  this.last = -1L;

                  assert DoubleSubList.this.assertRange();

               }
            }
         };
      }

      public DoubleBigList subList(long var1, long var3) {
         this.ensureIndex(var1);
         this.ensureIndex(var3);
         if (var1 > var3) {
            throw new IllegalArgumentException("Start index (" + var1 + ") is greater than end index (" + var3 + ")");
         } else {
            return new AbstractDoubleBigList.DoubleSubList(this, var1, var3);
         }
      }

      public boolean rem(double var1) {
         long var3 = this.indexOf(var1);
         if (var3 == -1L) {
            return false;
         } else {
            --this.to;
            this.l.removeDouble(this.from + var3);

            assert this.assertRange();

            return true;
         }
      }

      public boolean addAll(long var1, DoubleCollection var3) {
         this.ensureIndex(var1);
         return super.addAll(var1, var3);
      }

      public boolean addAll(long var1, DoubleBigList var3) {
         this.ensureIndex(var1);
         return super.addAll(var1, var3);
      }
   }
}
