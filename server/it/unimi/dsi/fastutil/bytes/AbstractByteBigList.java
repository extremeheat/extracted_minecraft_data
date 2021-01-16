package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.BigListIterator;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class AbstractByteBigList extends AbstractByteCollection implements ByteBigList, ByteStack {
   protected AbstractByteBigList() {
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

   public void add(long var1, byte var3) {
      throw new UnsupportedOperationException();
   }

   public boolean add(byte var1) {
      this.add(this.size64(), var1);
      return true;
   }

   public byte removeByte(long var1) {
      throw new UnsupportedOperationException();
   }

   public byte set(long var1, byte var3) {
      throw new UnsupportedOperationException();
   }

   public boolean addAll(long var1, Collection<? extends Byte> var3) {
      this.ensureIndex(var1);
      Iterator var4 = var3.iterator();
      boolean var5 = var4.hasNext();

      while(var4.hasNext()) {
         this.add(var1++, (Byte)var4.next());
      }

      return var5;
   }

   public boolean addAll(Collection<? extends Byte> var1) {
      return this.addAll(this.size64(), var1);
   }

   public ByteBigListIterator iterator() {
      return this.listIterator();
   }

   public ByteBigListIterator listIterator() {
      return this.listIterator(0L);
   }

   public ByteBigListIterator listIterator(final long var1) {
      this.ensureIndex(var1);
      return new ByteBigListIterator() {
         long pos = var1;
         long last = -1L;

         public boolean hasNext() {
            return this.pos < AbstractByteBigList.this.size64();
         }

         public boolean hasPrevious() {
            return this.pos > 0L;
         }

         public byte nextByte() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return AbstractByteBigList.this.getByte(this.last = (long)(this.pos++));
            }
         }

         public byte previousByte() {
            if (!this.hasPrevious()) {
               throw new NoSuchElementException();
            } else {
               return AbstractByteBigList.this.getByte(this.last = --this.pos);
            }
         }

         public long nextIndex() {
            return this.pos;
         }

         public long previousIndex() {
            return this.pos - 1L;
         }

         public void add(byte var1x) {
            AbstractByteBigList.this.add((long)(this.pos++), var1x);
            this.last = -1L;
         }

         public void set(byte var1x) {
            if (this.last == -1L) {
               throw new IllegalStateException();
            } else {
               AbstractByteBigList.this.set(this.last, var1x);
            }
         }

         public void remove() {
            if (this.last == -1L) {
               throw new IllegalStateException();
            } else {
               AbstractByteBigList.this.removeByte(this.last);
               if (this.last < this.pos) {
                  --this.pos;
               }

               this.last = -1L;
            }
         }
      };
   }

   public boolean contains(byte var1) {
      return this.indexOf(var1) >= 0L;
   }

   public long indexOf(byte var1) {
      ByteBigListIterator var2 = this.listIterator();

      byte var3;
      do {
         if (!var2.hasNext()) {
            return -1L;
         }

         var3 = var2.nextByte();
      } while(var1 != var3);

      return var2.previousIndex();
   }

   public long lastIndexOf(byte var1) {
      ByteBigListIterator var2 = this.listIterator(this.size64());

      byte var3;
      do {
         if (!var2.hasPrevious()) {
            return -1L;
         }

         var3 = var2.previousByte();
      } while(var1 != var3);

      return var2.nextIndex();
   }

   public void size(long var1) {
      long var3 = this.size64();
      if (var1 > var3) {
         while(var3++ < var1) {
            this.add((byte)0);
         }
      } else {
         while(var3-- != var1) {
            this.remove(var3);
         }
      }

   }

   public ByteBigList subList(long var1, long var3) {
      this.ensureIndex(var1);
      this.ensureIndex(var3);
      if (var1 > var3) {
         throw new IndexOutOfBoundsException("Start index (" + var1 + ") is greater than end index (" + var3 + ")");
      } else {
         return new AbstractByteBigList.ByteSubList(this, var1, var3);
      }
   }

   public void removeElements(long var1, long var3) {
      this.ensureIndex(var3);
      ByteBigListIterator var5 = this.listIterator(var1);
      long var6 = var3 - var1;
      if (var6 < 0L) {
         throw new IllegalArgumentException("Start index (" + var1 + ") is greater than end index (" + var3 + ")");
      } else {
         while(var6-- != 0L) {
            var5.nextByte();
            var5.remove();
         }

      }
   }

   public void addElements(long var1, byte[][] var3, long var4, long var6) {
      this.ensureIndex(var1);
      ByteBigArrays.ensureOffsetLength(var3, var4, var6);

      while(var6-- != 0L) {
         this.add(var1++, ByteBigArrays.get(var3, var4++));
      }

   }

   public void addElements(long var1, byte[][] var3) {
      this.addElements(var1, var3, 0L, ByteBigArrays.length(var3));
   }

   public void getElements(long var1, byte[][] var3, long var4, long var6) {
      ByteBigListIterator var8 = this.listIterator(var1);
      ByteBigArrays.ensureOffsetLength(var3, var4, var6);
      if (var1 + var6 > this.size64()) {
         throw new IndexOutOfBoundsException("End index (" + (var1 + var6) + ") is greater than list size (" + this.size64() + ")");
      } else {
         while(var6-- != 0L) {
            ByteBigArrays.set(var3, var4++, var8.nextByte());
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
      ByteBigListIterator var1 = this.iterator();
      int var2 = 1;

      byte var5;
      for(long var3 = this.size64(); var3-- != 0L; var2 = 31 * var2 + var5) {
         var5 = var1.nextByte();
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
            ByteBigListIterator var5;
            if (var2 instanceof ByteBigList) {
               var5 = this.listIterator();
               ByteBigListIterator var7 = ((ByteBigList)var2).listIterator();

               do {
                  if (var3-- == 0L) {
                     return true;
                  }
               } while(var5.nextByte() == var7.nextByte());

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

   public int compareTo(BigList<? extends Byte> var1) {
      if (var1 == this) {
         return 0;
      } else {
         ByteBigListIterator var2;
         int var4;
         if (var1 instanceof ByteBigList) {
            var2 = this.listIterator();
            ByteBigListIterator var7 = ((ByteBigList)var1).listIterator();

            while(var2.hasNext() && var7.hasNext()) {
               byte var5 = var2.nextByte();
               byte var6 = var7.nextByte();
               if ((var4 = Byte.compare(var5, var6)) != 0) {
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

   public void push(byte var1) {
      this.add(var1);
   }

   public byte popByte() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.removeByte(this.size64() - 1L);
      }
   }

   public byte topByte() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.getByte(this.size64() - 1L);
      }
   }

   public byte peekByte(int var1) {
      return this.getByte(this.size64() - 1L - (long)var1);
   }

   public boolean rem(byte var1) {
      long var2 = this.indexOf(var1);
      if (var2 == -1L) {
         return false;
      } else {
         this.removeByte(var2);
         return true;
      }
   }

   public boolean addAll(long var1, ByteCollection var3) {
      return this.addAll(var1, (Collection)var3);
   }

   public boolean addAll(long var1, ByteBigList var3) {
      return this.addAll(var1, (ByteCollection)var3);
   }

   public boolean addAll(ByteCollection var1) {
      return this.addAll(this.size64(), var1);
   }

   public boolean addAll(ByteBigList var1) {
      return this.addAll(this.size64(), var1);
   }

   /** @deprecated */
   @Deprecated
   public void add(long var1, Byte var3) {
      this.add(var1, var3);
   }

   /** @deprecated */
   @Deprecated
   public Byte set(long var1, Byte var3) {
      return this.set(var1, var3);
   }

   /** @deprecated */
   @Deprecated
   public Byte get(long var1) {
      return this.getByte(var1);
   }

   /** @deprecated */
   @Deprecated
   public long indexOf(Object var1) {
      return this.indexOf((Byte)var1);
   }

   /** @deprecated */
   @Deprecated
   public long lastIndexOf(Object var1) {
      return this.lastIndexOf((Byte)var1);
   }

   /** @deprecated */
   @Deprecated
   public Byte remove(long var1) {
      return this.removeByte(var1);
   }

   /** @deprecated */
   @Deprecated
   public void push(Byte var1) {
      this.push(var1);
   }

   /** @deprecated */
   @Deprecated
   public Byte pop() {
      return this.popByte();
   }

   /** @deprecated */
   @Deprecated
   public Byte top() {
      return this.topByte();
   }

   /** @deprecated */
   @Deprecated
   public Byte peek(int var1) {
      return this.peekByte(var1);
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      ByteBigListIterator var2 = this.iterator();
      long var3 = this.size64();
      boolean var6 = true;
      var1.append("[");

      while(var3-- != 0L) {
         if (var6) {
            var6 = false;
         } else {
            var1.append(", ");
         }

         byte var5 = var2.nextByte();
         var1.append(String.valueOf(var5));
      }

      var1.append("]");
      return var1.toString();
   }

   public static class ByteSubList extends AbstractByteBigList implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ByteBigList l;
      protected final long from;
      protected long to;

      public ByteSubList(ByteBigList var1, long var2, long var4) {
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

      public boolean add(byte var1) {
         this.l.add(this.to, var1);
         ++this.to;

         assert this.assertRange();

         return true;
      }

      public void add(long var1, byte var3) {
         this.ensureIndex(var1);
         this.l.add(this.from + var1, var3);
         ++this.to;

         assert this.assertRange();

      }

      public boolean addAll(long var1, Collection<? extends Byte> var3) {
         this.ensureIndex(var1);
         this.to += (long)var3.size();
         return this.l.addAll(this.from + var1, (Collection)var3);
      }

      public byte getByte(long var1) {
         this.ensureRestrictedIndex(var1);
         return this.l.getByte(this.from + var1);
      }

      public byte removeByte(long var1) {
         this.ensureRestrictedIndex(var1);
         --this.to;
         return this.l.removeByte(this.from + var1);
      }

      public byte set(long var1, byte var3) {
         this.ensureRestrictedIndex(var1);
         return this.l.set(this.from + var1, var3);
      }

      public long size64() {
         return this.to - this.from;
      }

      public void getElements(long var1, byte[][] var3, long var4, long var6) {
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

      public void addElements(long var1, byte[][] var3, long var4, long var6) {
         this.ensureIndex(var1);
         this.l.addElements(this.from + var1, var3, var4, var6);
         this.to += var6;

         assert this.assertRange();

      }

      public ByteBigListIterator listIterator(final long var1) {
         this.ensureIndex(var1);
         return new ByteBigListIterator() {
            long pos = var1;
            long last = -1L;

            public boolean hasNext() {
               return this.pos < ByteSubList.this.size64();
            }

            public boolean hasPrevious() {
               return this.pos > 0L;
            }

            public byte nextByte() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return ByteSubList.this.l.getByte(ByteSubList.this.from + (this.last = (long)(this.pos++)));
               }
            }

            public byte previousByte() {
               if (!this.hasPrevious()) {
                  throw new NoSuchElementException();
               } else {
                  return ByteSubList.this.l.getByte(ByteSubList.this.from + (this.last = --this.pos));
               }
            }

            public long nextIndex() {
               return this.pos;
            }

            public long previousIndex() {
               return this.pos - 1L;
            }

            public void add(byte var1x) {
               if (this.last == -1L) {
                  throw new IllegalStateException();
               } else {
                  ByteSubList.this.add((long)(this.pos++), var1x);
                  this.last = -1L;

                  assert ByteSubList.this.assertRange();

               }
            }

            public void set(byte var1x) {
               if (this.last == -1L) {
                  throw new IllegalStateException();
               } else {
                  ByteSubList.this.set(this.last, var1x);
               }
            }

            public void remove() {
               if (this.last == -1L) {
                  throw new IllegalStateException();
               } else {
                  ByteSubList.this.removeByte(this.last);
                  if (this.last < this.pos) {
                     --this.pos;
                  }

                  this.last = -1L;

                  assert ByteSubList.this.assertRange();

               }
            }
         };
      }

      public ByteBigList subList(long var1, long var3) {
         this.ensureIndex(var1);
         this.ensureIndex(var3);
         if (var1 > var3) {
            throw new IllegalArgumentException("Start index (" + var1 + ") is greater than end index (" + var3 + ")");
         } else {
            return new AbstractByteBigList.ByteSubList(this, var1, var3);
         }
      }

      public boolean rem(byte var1) {
         long var2 = this.indexOf(var1);
         if (var2 == -1L) {
            return false;
         } else {
            --this.to;
            this.l.removeByte(this.from + var2);

            assert this.assertRange();

            return true;
         }
      }

      public boolean addAll(long var1, ByteCollection var3) {
         this.ensureIndex(var1);
         return super.addAll(var1, var3);
      }

      public boolean addAll(long var1, ByteBigList var3) {
         this.ensureIndex(var1);
         return super.addAll(var1, var3);
      }
   }
}
