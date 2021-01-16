package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.HashCommon;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class AbstractFloatBigList extends AbstractFloatCollection implements FloatBigList, FloatStack {
   protected AbstractFloatBigList() {
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

   public void add(long var1, float var3) {
      throw new UnsupportedOperationException();
   }

   public boolean add(float var1) {
      this.add(this.size64(), var1);
      return true;
   }

   public float removeFloat(long var1) {
      throw new UnsupportedOperationException();
   }

   public float set(long var1, float var3) {
      throw new UnsupportedOperationException();
   }

   public boolean addAll(long var1, Collection<? extends Float> var3) {
      this.ensureIndex(var1);
      Iterator var4 = var3.iterator();
      boolean var5 = var4.hasNext();

      while(var4.hasNext()) {
         this.add(var1++, (Float)var4.next());
      }

      return var5;
   }

   public boolean addAll(Collection<? extends Float> var1) {
      return this.addAll(this.size64(), var1);
   }

   public FloatBigListIterator iterator() {
      return this.listIterator();
   }

   public FloatBigListIterator listIterator() {
      return this.listIterator(0L);
   }

   public FloatBigListIterator listIterator(final long var1) {
      this.ensureIndex(var1);
      return new FloatBigListIterator() {
         long pos = var1;
         long last = -1L;

         public boolean hasNext() {
            return this.pos < AbstractFloatBigList.this.size64();
         }

         public boolean hasPrevious() {
            return this.pos > 0L;
         }

         public float nextFloat() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return AbstractFloatBigList.this.getFloat(this.last = (long)(this.pos++));
            }
         }

         public float previousFloat() {
            if (!this.hasPrevious()) {
               throw new NoSuchElementException();
            } else {
               return AbstractFloatBigList.this.getFloat(this.last = --this.pos);
            }
         }

         public long nextIndex() {
            return this.pos;
         }

         public long previousIndex() {
            return this.pos - 1L;
         }

         public void add(float var1x) {
            AbstractFloatBigList.this.add((long)(this.pos++), var1x);
            this.last = -1L;
         }

         public void set(float var1x) {
            if (this.last == -1L) {
               throw new IllegalStateException();
            } else {
               AbstractFloatBigList.this.set(this.last, var1x);
            }
         }

         public void remove() {
            if (this.last == -1L) {
               throw new IllegalStateException();
            } else {
               AbstractFloatBigList.this.removeFloat(this.last);
               if (this.last < this.pos) {
                  --this.pos;
               }

               this.last = -1L;
            }
         }
      };
   }

   public boolean contains(float var1) {
      return this.indexOf(var1) >= 0L;
   }

   public long indexOf(float var1) {
      FloatBigListIterator var2 = this.listIterator();

      float var3;
      do {
         if (!var2.hasNext()) {
            return -1L;
         }

         var3 = var2.nextFloat();
      } while(Float.floatToIntBits(var1) != Float.floatToIntBits(var3));

      return var2.previousIndex();
   }

   public long lastIndexOf(float var1) {
      FloatBigListIterator var2 = this.listIterator(this.size64());

      float var3;
      do {
         if (!var2.hasPrevious()) {
            return -1L;
         }

         var3 = var2.previousFloat();
      } while(Float.floatToIntBits(var1) != Float.floatToIntBits(var3));

      return var2.nextIndex();
   }

   public void size(long var1) {
      long var3 = this.size64();
      if (var1 > var3) {
         while(var3++ < var1) {
            this.add(0.0F);
         }
      } else {
         while(var3-- != var1) {
            this.remove(var3);
         }
      }

   }

   public FloatBigList subList(long var1, long var3) {
      this.ensureIndex(var1);
      this.ensureIndex(var3);
      if (var1 > var3) {
         throw new IndexOutOfBoundsException("Start index (" + var1 + ") is greater than end index (" + var3 + ")");
      } else {
         return new AbstractFloatBigList.FloatSubList(this, var1, var3);
      }
   }

   public void removeElements(long var1, long var3) {
      this.ensureIndex(var3);
      FloatBigListIterator var5 = this.listIterator(var1);
      long var6 = var3 - var1;
      if (var6 < 0L) {
         throw new IllegalArgumentException("Start index (" + var1 + ") is greater than end index (" + var3 + ")");
      } else {
         while(var6-- != 0L) {
            var5.nextFloat();
            var5.remove();
         }

      }
   }

   public void addElements(long var1, float[][] var3, long var4, long var6) {
      this.ensureIndex(var1);
      FloatBigArrays.ensureOffsetLength(var3, var4, var6);

      while(var6-- != 0L) {
         this.add(var1++, FloatBigArrays.get(var3, var4++));
      }

   }

   public void addElements(long var1, float[][] var3) {
      this.addElements(var1, var3, 0L, FloatBigArrays.length(var3));
   }

   public void getElements(long var1, float[][] var3, long var4, long var6) {
      FloatBigListIterator var8 = this.listIterator(var1);
      FloatBigArrays.ensureOffsetLength(var3, var4, var6);
      if (var1 + var6 > this.size64()) {
         throw new IndexOutOfBoundsException("End index (" + (var1 + var6) + ") is greater than list size (" + this.size64() + ")");
      } else {
         while(var6-- != 0L) {
            FloatBigArrays.set(var3, var4++, var8.nextFloat());
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
      FloatBigListIterator var1 = this.iterator();
      int var2 = 1;

      float var5;
      for(long var3 = this.size64(); var3-- != 0L; var2 = 31 * var2 + HashCommon.float2int(var5)) {
         var5 = var1.nextFloat();
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
            FloatBigListIterator var5;
            if (var2 instanceof FloatBigList) {
               var5 = this.listIterator();
               FloatBigListIterator var7 = ((FloatBigList)var2).listIterator();

               do {
                  if (var3-- == 0L) {
                     return true;
                  }
               } while(var5.nextFloat() == var7.nextFloat());

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

   public int compareTo(BigList<? extends Float> var1) {
      if (var1 == this) {
         return 0;
      } else {
         FloatBigListIterator var2;
         int var4;
         if (var1 instanceof FloatBigList) {
            var2 = this.listIterator();
            FloatBigListIterator var7 = ((FloatBigList)var1).listIterator();

            while(var2.hasNext() && var7.hasNext()) {
               float var5 = var2.nextFloat();
               float var6 = var7.nextFloat();
               if ((var4 = Float.compare(var5, var6)) != 0) {
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

   public void push(float var1) {
      this.add(var1);
   }

   public float popFloat() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.removeFloat(this.size64() - 1L);
      }
   }

   public float topFloat() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.getFloat(this.size64() - 1L);
      }
   }

   public float peekFloat(int var1) {
      return this.getFloat(this.size64() - 1L - (long)var1);
   }

   public boolean rem(float var1) {
      long var2 = this.indexOf(var1);
      if (var2 == -1L) {
         return false;
      } else {
         this.removeFloat(var2);
         return true;
      }
   }

   public boolean addAll(long var1, FloatCollection var3) {
      return this.addAll(var1, (Collection)var3);
   }

   public boolean addAll(long var1, FloatBigList var3) {
      return this.addAll(var1, (FloatCollection)var3);
   }

   public boolean addAll(FloatCollection var1) {
      return this.addAll(this.size64(), var1);
   }

   public boolean addAll(FloatBigList var1) {
      return this.addAll(this.size64(), var1);
   }

   /** @deprecated */
   @Deprecated
   public void add(long var1, Float var3) {
      this.add(var1, var3);
   }

   /** @deprecated */
   @Deprecated
   public Float set(long var1, Float var3) {
      return this.set(var1, var3);
   }

   /** @deprecated */
   @Deprecated
   public Float get(long var1) {
      return this.getFloat(var1);
   }

   /** @deprecated */
   @Deprecated
   public long indexOf(Object var1) {
      return this.indexOf((Float)var1);
   }

   /** @deprecated */
   @Deprecated
   public long lastIndexOf(Object var1) {
      return this.lastIndexOf((Float)var1);
   }

   /** @deprecated */
   @Deprecated
   public Float remove(long var1) {
      return this.removeFloat(var1);
   }

   /** @deprecated */
   @Deprecated
   public void push(Float var1) {
      this.push(var1);
   }

   /** @deprecated */
   @Deprecated
   public Float pop() {
      return this.popFloat();
   }

   /** @deprecated */
   @Deprecated
   public Float top() {
      return this.topFloat();
   }

   /** @deprecated */
   @Deprecated
   public Float peek(int var1) {
      return this.peekFloat(var1);
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      FloatBigListIterator var2 = this.iterator();
      long var3 = this.size64();
      boolean var6 = true;
      var1.append("[");

      while(var3-- != 0L) {
         if (var6) {
            var6 = false;
         } else {
            var1.append(", ");
         }

         float var5 = var2.nextFloat();
         var1.append(String.valueOf(var5));
      }

      var1.append("]");
      return var1.toString();
   }

   public static class FloatSubList extends AbstractFloatBigList implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final FloatBigList l;
      protected final long from;
      protected long to;

      public FloatSubList(FloatBigList var1, long var2, long var4) {
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

      public boolean add(float var1) {
         this.l.add(this.to, var1);
         ++this.to;

         assert this.assertRange();

         return true;
      }

      public void add(long var1, float var3) {
         this.ensureIndex(var1);
         this.l.add(this.from + var1, var3);
         ++this.to;

         assert this.assertRange();

      }

      public boolean addAll(long var1, Collection<? extends Float> var3) {
         this.ensureIndex(var1);
         this.to += (long)var3.size();
         return this.l.addAll(this.from + var1, (Collection)var3);
      }

      public float getFloat(long var1) {
         this.ensureRestrictedIndex(var1);
         return this.l.getFloat(this.from + var1);
      }

      public float removeFloat(long var1) {
         this.ensureRestrictedIndex(var1);
         --this.to;
         return this.l.removeFloat(this.from + var1);
      }

      public float set(long var1, float var3) {
         this.ensureRestrictedIndex(var1);
         return this.l.set(this.from + var1, var3);
      }

      public long size64() {
         return this.to - this.from;
      }

      public void getElements(long var1, float[][] var3, long var4, long var6) {
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

      public void addElements(long var1, float[][] var3, long var4, long var6) {
         this.ensureIndex(var1);
         this.l.addElements(this.from + var1, var3, var4, var6);
         this.to += var6;

         assert this.assertRange();

      }

      public FloatBigListIterator listIterator(final long var1) {
         this.ensureIndex(var1);
         return new FloatBigListIterator() {
            long pos = var1;
            long last = -1L;

            public boolean hasNext() {
               return this.pos < FloatSubList.this.size64();
            }

            public boolean hasPrevious() {
               return this.pos > 0L;
            }

            public float nextFloat() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return FloatSubList.this.l.getFloat(FloatSubList.this.from + (this.last = (long)(this.pos++)));
               }
            }

            public float previousFloat() {
               if (!this.hasPrevious()) {
                  throw new NoSuchElementException();
               } else {
                  return FloatSubList.this.l.getFloat(FloatSubList.this.from + (this.last = --this.pos));
               }
            }

            public long nextIndex() {
               return this.pos;
            }

            public long previousIndex() {
               return this.pos - 1L;
            }

            public void add(float var1x) {
               if (this.last == -1L) {
                  throw new IllegalStateException();
               } else {
                  FloatSubList.this.add((long)(this.pos++), var1x);
                  this.last = -1L;

                  assert FloatSubList.this.assertRange();

               }
            }

            public void set(float var1x) {
               if (this.last == -1L) {
                  throw new IllegalStateException();
               } else {
                  FloatSubList.this.set(this.last, var1x);
               }
            }

            public void remove() {
               if (this.last == -1L) {
                  throw new IllegalStateException();
               } else {
                  FloatSubList.this.removeFloat(this.last);
                  if (this.last < this.pos) {
                     --this.pos;
                  }

                  this.last = -1L;

                  assert FloatSubList.this.assertRange();

               }
            }
         };
      }

      public FloatBigList subList(long var1, long var3) {
         this.ensureIndex(var1);
         this.ensureIndex(var3);
         if (var1 > var3) {
            throw new IllegalArgumentException("Start index (" + var1 + ") is greater than end index (" + var3 + ")");
         } else {
            return new AbstractFloatBigList.FloatSubList(this, var1, var3);
         }
      }

      public boolean rem(float var1) {
         long var2 = this.indexOf(var1);
         if (var2 == -1L) {
            return false;
         } else {
            --this.to;
            this.l.removeFloat(this.from + var2);

            assert this.assertRange();

            return true;
         }
      }

      public boolean addAll(long var1, FloatCollection var3) {
         this.ensureIndex(var1);
         return super.addAll(var1, var3);
      }

      public boolean addAll(long var1, FloatBigList var3) {
         this.ensureIndex(var1);
         return super.addAll(var1, var3);
      }
   }
}
