package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public abstract class AbstractDoubleList extends AbstractDoubleCollection implements DoubleList, DoubleStack {
   protected AbstractDoubleList() {
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

   public void add(int var1, double var2) {
      throw new UnsupportedOperationException();
   }

   public boolean add(double var1) {
      this.add(this.size(), var1);
      return true;
   }

   public double removeDouble(int var1) {
      throw new UnsupportedOperationException();
   }

   public double set(int var1, double var2) {
      throw new UnsupportedOperationException();
   }

   public boolean addAll(int var1, Collection<? extends Double> var2) {
      this.ensureIndex(var1);
      Iterator var3 = var2.iterator();
      boolean var4 = var3.hasNext();

      while(var3.hasNext()) {
         this.add(var1++, (Double)var3.next());
      }

      return var4;
   }

   public boolean addAll(Collection<? extends Double> var1) {
      return this.addAll(this.size(), var1);
   }

   public DoubleListIterator iterator() {
      return this.listIterator();
   }

   public DoubleListIterator listIterator() {
      return this.listIterator(0);
   }

   public DoubleListIterator listIterator(final int var1) {
      this.ensureIndex(var1);
      return new DoubleListIterator() {
         int pos = var1;
         int last = -1;

         public boolean hasNext() {
            return this.pos < AbstractDoubleList.this.size();
         }

         public boolean hasPrevious() {
            return this.pos > 0;
         }

         public double nextDouble() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return AbstractDoubleList.this.getDouble(this.last = this.pos++);
            }
         }

         public double previousDouble() {
            if (!this.hasPrevious()) {
               throw new NoSuchElementException();
            } else {
               return AbstractDoubleList.this.getDouble(this.last = --this.pos);
            }
         }

         public int nextIndex() {
            return this.pos;
         }

         public int previousIndex() {
            return this.pos - 1;
         }

         public void add(double var1x) {
            AbstractDoubleList.this.add(this.pos++, var1x);
            this.last = -1;
         }

         public void set(double var1x) {
            if (this.last == -1) {
               throw new IllegalStateException();
            } else {
               AbstractDoubleList.this.set(this.last, var1x);
            }
         }

         public void remove() {
            if (this.last == -1) {
               throw new IllegalStateException();
            } else {
               AbstractDoubleList.this.removeDouble(this.last);
               if (this.last < this.pos) {
                  --this.pos;
               }

               this.last = -1;
            }
         }
      };
   }

   public boolean contains(double var1) {
      return this.indexOf(var1) >= 0;
   }

   public int indexOf(double var1) {
      DoubleListIterator var3 = this.listIterator();

      double var4;
      do {
         if (!var3.hasNext()) {
            return -1;
         }

         var4 = var3.nextDouble();
      } while(Double.doubleToLongBits(var1) != Double.doubleToLongBits(var4));

      return var3.previousIndex();
   }

   public int lastIndexOf(double var1) {
      DoubleListIterator var3 = this.listIterator(this.size());

      double var4;
      do {
         if (!var3.hasPrevious()) {
            return -1;
         }

         var4 = var3.previousDouble();
      } while(Double.doubleToLongBits(var1) != Double.doubleToLongBits(var4));

      return var3.nextIndex();
   }

   public void size(int var1) {
      int var2 = this.size();
      if (var1 > var2) {
         while(var2++ < var1) {
            this.add(0.0D);
         }
      } else {
         while(var2-- != var1) {
            this.removeDouble(var2);
         }
      }

   }

   public DoubleList subList(int var1, int var2) {
      this.ensureIndex(var1);
      this.ensureIndex(var2);
      if (var1 > var2) {
         throw new IndexOutOfBoundsException("Start index (" + var1 + ") is greater than end index (" + var2 + ")");
      } else {
         return new AbstractDoubleList.DoubleSubList(this, var1, var2);
      }
   }

   public void removeElements(int var1, int var2) {
      this.ensureIndex(var2);
      DoubleListIterator var3 = this.listIterator(var1);
      int var4 = var2 - var1;
      if (var4 < 0) {
         throw new IllegalArgumentException("Start index (" + var1 + ") is greater than end index (" + var2 + ")");
      } else {
         while(var4-- != 0) {
            var3.nextDouble();
            var3.remove();
         }

      }
   }

   public void addElements(int var1, double[] var2, int var3, int var4) {
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

   public void addElements(int var1, double[] var2) {
      this.addElements(var1, var2, 0, var2.length);
   }

   public void getElements(int var1, double[] var2, int var3, int var4) {
      DoubleListIterator var5 = this.listIterator(var1);
      if (var3 < 0) {
         throw new ArrayIndexOutOfBoundsException("Offset (" + var3 + ") is negative");
      } else if (var3 + var4 > var2.length) {
         throw new ArrayIndexOutOfBoundsException("End index (" + (var3 + var4) + ") is greater than array length (" + var2.length + ")");
      } else if (var1 + var4 > this.size()) {
         throw new IndexOutOfBoundsException("End index (" + (var1 + var4) + ") is greater than list size (" + this.size() + ")");
      } else {
         while(var4-- != 0) {
            var2[var3++] = var5.nextDouble();
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
      DoubleListIterator var1 = this.iterator();
      int var2 = 1;

      double var4;
      for(int var3 = this.size(); var3-- != 0; var2 = 31 * var2 + HashCommon.double2int(var4)) {
         var4 = var1.nextDouble();
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
            DoubleListIterator var4;
            if (var2 instanceof DoubleList) {
               var4 = this.listIterator();
               DoubleListIterator var6 = ((DoubleList)var2).listIterator();

               do {
                  if (var3-- == 0) {
                     return true;
                  }
               } while(var4.nextDouble() == var6.nextDouble());

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

   public int compareTo(List<? extends Double> var1) {
      if (var1 == this) {
         return 0;
      } else {
         DoubleListIterator var2;
         int var4;
         if (var1 instanceof DoubleList) {
            var2 = this.listIterator();
            DoubleListIterator var9 = ((DoubleList)var1).listIterator();

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

   public void push(double var1) {
      this.add(var1);
   }

   public double popDouble() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.removeDouble(this.size() - 1);
      }
   }

   public double topDouble() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.getDouble(this.size() - 1);
      }
   }

   public double peekDouble(int var1) {
      return this.getDouble(this.size() - 1 - var1);
   }

   public boolean rem(double var1) {
      int var3 = this.indexOf(var1);
      if (var3 == -1) {
         return false;
      } else {
         this.removeDouble(var3);
         return true;
      }
   }

   public boolean addAll(int var1, DoubleCollection var2) {
      this.ensureIndex(var1);
      DoubleIterator var3 = var2.iterator();
      boolean var4 = var3.hasNext();

      while(var3.hasNext()) {
         this.add(var1++, var3.nextDouble());
      }

      return var4;
   }

   public boolean addAll(int var1, DoubleList var2) {
      return this.addAll(var1, (DoubleCollection)var2);
   }

   public boolean addAll(DoubleCollection var1) {
      return this.addAll(this.size(), var1);
   }

   public boolean addAll(DoubleList var1) {
      return this.addAll(this.size(), var1);
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      DoubleListIterator var2 = this.iterator();
      int var3 = this.size();
      boolean var6 = true;
      var1.append("[");

      while(var3-- != 0) {
         if (var6) {
            var6 = false;
         } else {
            var1.append(", ");
         }

         double var4 = var2.nextDouble();
         var1.append(String.valueOf(var4));
      }

      var1.append("]");
      return var1.toString();
   }

   public static class DoubleSubList extends AbstractDoubleList implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final DoubleList l;
      protected final int from;
      protected int to;

      public DoubleSubList(DoubleList var1, int var2, int var3) {
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

      public boolean add(double var1) {
         this.l.add(this.to, var1);
         ++this.to;

         assert this.assertRange();

         return true;
      }

      public void add(int var1, double var2) {
         this.ensureIndex(var1);
         this.l.add(this.from + var1, var2);
         ++this.to;

         assert this.assertRange();

      }

      public boolean addAll(int var1, Collection<? extends Double> var2) {
         this.ensureIndex(var1);
         this.to += var2.size();
         return this.l.addAll(this.from + var1, (Collection)var2);
      }

      public double getDouble(int var1) {
         this.ensureRestrictedIndex(var1);
         return this.l.getDouble(this.from + var1);
      }

      public double removeDouble(int var1) {
         this.ensureRestrictedIndex(var1);
         --this.to;
         return this.l.removeDouble(this.from + var1);
      }

      public double set(int var1, double var2) {
         this.ensureRestrictedIndex(var1);
         return this.l.set(this.from + var1, var2);
      }

      public int size() {
         return this.to - this.from;
      }

      public void getElements(int var1, double[] var2, int var3, int var4) {
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

      public void addElements(int var1, double[] var2, int var3, int var4) {
         this.ensureIndex(var1);
         this.l.addElements(this.from + var1, var2, var3, var4);
         this.to += var4;

         assert this.assertRange();

      }

      public DoubleListIterator listIterator(final int var1) {
         this.ensureIndex(var1);
         return new DoubleListIterator() {
            int pos = var1;
            int last = -1;

            public boolean hasNext() {
               return this.pos < DoubleSubList.this.size();
            }

            public boolean hasPrevious() {
               return this.pos > 0;
            }

            public double nextDouble() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return DoubleSubList.this.l.getDouble(DoubleSubList.this.from + (this.last = this.pos++));
               }
            }

            public double previousDouble() {
               if (!this.hasPrevious()) {
                  throw new NoSuchElementException();
               } else {
                  return DoubleSubList.this.l.getDouble(DoubleSubList.this.from + (this.last = --this.pos));
               }
            }

            public int nextIndex() {
               return this.pos;
            }

            public int previousIndex() {
               return this.pos - 1;
            }

            public void add(double var1x) {
               if (this.last == -1) {
                  throw new IllegalStateException();
               } else {
                  DoubleSubList.this.add(this.pos++, var1x);
                  this.last = -1;

                  assert DoubleSubList.this.assertRange();

               }
            }

            public void set(double var1x) {
               if (this.last == -1) {
                  throw new IllegalStateException();
               } else {
                  DoubleSubList.this.set(this.last, var1x);
               }
            }

            public void remove() {
               if (this.last == -1) {
                  throw new IllegalStateException();
               } else {
                  DoubleSubList.this.removeDouble(this.last);
                  if (this.last < this.pos) {
                     --this.pos;
                  }

                  this.last = -1;

                  assert DoubleSubList.this.assertRange();

               }
            }
         };
      }

      public DoubleList subList(int var1, int var2) {
         this.ensureIndex(var1);
         this.ensureIndex(var2);
         if (var1 > var2) {
            throw new IllegalArgumentException("Start index (" + var1 + ") is greater than end index (" + var2 + ")");
         } else {
            return new AbstractDoubleList.DoubleSubList(this, var1, var2);
         }
      }

      public boolean rem(double var1) {
         int var3 = this.indexOf(var1);
         if (var3 == -1) {
            return false;
         } else {
            --this.to;
            this.l.removeDouble(this.from + var3);

            assert this.assertRange();

            return true;
         }
      }

      public boolean addAll(int var1, DoubleCollection var2) {
         this.ensureIndex(var1);
         return super.addAll(var1, var2);
      }

      public boolean addAll(int var1, DoubleList var2) {
         this.ensureIndex(var1);
         return super.addAll(var1, var2);
      }
   }
}
