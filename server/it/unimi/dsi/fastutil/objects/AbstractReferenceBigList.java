package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.Stack;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class AbstractReferenceBigList<K> extends AbstractReferenceCollection<K> implements ReferenceBigList<K>, Stack<K> {
   protected AbstractReferenceBigList() {
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

   public void add(long var1, K var3) {
      throw new UnsupportedOperationException();
   }

   public boolean add(K var1) {
      this.add(this.size64(), var1);
      return true;
   }

   public K remove(long var1) {
      throw new UnsupportedOperationException();
   }

   public K set(long var1, K var3) {
      throw new UnsupportedOperationException();
   }

   public boolean addAll(long var1, Collection<? extends K> var3) {
      this.ensureIndex(var1);
      Iterator var4 = var3.iterator();
      boolean var5 = var4.hasNext();

      while(var4.hasNext()) {
         this.add(var1++, var4.next());
      }

      return var5;
   }

   public boolean addAll(Collection<? extends K> var1) {
      return this.addAll(this.size64(), var1);
   }

   public ObjectBigListIterator<K> iterator() {
      return this.listIterator();
   }

   public ObjectBigListIterator<K> listIterator() {
      return this.listIterator(0L);
   }

   public ObjectBigListIterator<K> listIterator(final long var1) {
      this.ensureIndex(var1);
      return new ObjectBigListIterator<K>() {
         long pos = var1;
         long last = -1L;

         public boolean hasNext() {
            return this.pos < AbstractReferenceBigList.this.size64();
         }

         public boolean hasPrevious() {
            return this.pos > 0L;
         }

         public K next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return AbstractReferenceBigList.this.get(this.last = (long)(this.pos++));
            }
         }

         public K previous() {
            if (!this.hasPrevious()) {
               throw new NoSuchElementException();
            } else {
               return AbstractReferenceBigList.this.get(this.last = --this.pos);
            }
         }

         public long nextIndex() {
            return this.pos;
         }

         public long previousIndex() {
            return this.pos - 1L;
         }

         public void add(K var1x) {
            AbstractReferenceBigList.this.add((long)(this.pos++), var1x);
            this.last = -1L;
         }

         public void set(K var1x) {
            if (this.last == -1L) {
               throw new IllegalStateException();
            } else {
               AbstractReferenceBigList.this.set(this.last, var1x);
            }
         }

         public void remove() {
            if (this.last == -1L) {
               throw new IllegalStateException();
            } else {
               AbstractReferenceBigList.this.remove(this.last);
               if (this.last < this.pos) {
                  --this.pos;
               }

               this.last = -1L;
            }
         }
      };
   }

   public boolean contains(Object var1) {
      return this.indexOf(var1) >= 0L;
   }

   public long indexOf(Object var1) {
      ObjectBigListIterator var2 = this.listIterator();

      Object var3;
      do {
         if (!var2.hasNext()) {
            return -1L;
         }

         var3 = var2.next();
      } while(var1 != var3);

      return var2.previousIndex();
   }

   public long lastIndexOf(Object var1) {
      ObjectBigListIterator var2 = this.listIterator(this.size64());

      Object var3;
      do {
         if (!var2.hasPrevious()) {
            return -1L;
         }

         var3 = var2.previous();
      } while(var1 != var3);

      return var2.nextIndex();
   }

   public void size(long var1) {
      long var3 = this.size64();
      if (var1 > var3) {
         while(var3++ < var1) {
            this.add((Object)null);
         }
      } else {
         while(var3-- != var1) {
            this.remove(var3);
         }
      }

   }

   public ReferenceBigList<K> subList(long var1, long var3) {
      this.ensureIndex(var1);
      this.ensureIndex(var3);
      if (var1 > var3) {
         throw new IndexOutOfBoundsException("Start index (" + var1 + ") is greater than end index (" + var3 + ")");
      } else {
         return new AbstractReferenceBigList.ReferenceSubList(this, var1, var3);
      }
   }

   public void removeElements(long var1, long var3) {
      this.ensureIndex(var3);
      ObjectBigListIterator var5 = this.listIterator(var1);
      long var6 = var3 - var1;
      if (var6 < 0L) {
         throw new IllegalArgumentException("Start index (" + var1 + ") is greater than end index (" + var3 + ")");
      } else {
         while(var6-- != 0L) {
            var5.next();
            var5.remove();
         }

      }
   }

   public void addElements(long var1, K[][] var3, long var4, long var6) {
      this.ensureIndex(var1);
      ObjectBigArrays.ensureOffsetLength(var3, var4, var6);

      while(var6-- != 0L) {
         this.add(var1++, ObjectBigArrays.get(var3, var4++));
      }

   }

   public void addElements(long var1, K[][] var3) {
      this.addElements(var1, var3, 0L, ObjectBigArrays.length(var3));
   }

   public void getElements(long var1, Object[][] var3, long var4, long var6) {
      ObjectBigListIterator var8 = this.listIterator(var1);
      ObjectBigArrays.ensureOffsetLength(var3, var4, var6);
      if (var1 + var6 > this.size64()) {
         throw new IndexOutOfBoundsException("End index (" + (var1 + var6) + ") is greater than list size (" + this.size64() + ")");
      } else {
         while(var6-- != 0L) {
            ObjectBigArrays.set(var3, var4++, var8.next());
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

   public int hashCode() {
      ObjectBigListIterator var1 = this.iterator();
      int var2 = 1;

      Object var5;
      for(long var3 = this.size64(); var3-- != 0L; var2 = 31 * var2 + System.identityHashCode(var5)) {
         var5 = var1.next();
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
            ObjectBigListIterator var5 = this.listIterator();
            BigListIterator var6 = var2.listIterator();

            do {
               if (var3-- == 0L) {
                  return true;
               }
            } while(var5.next() == var6.next());

            return false;
         }
      }
   }

   public void push(K var1) {
      this.add(var1);
   }

   public K pop() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.remove(this.size64() - 1L);
      }
   }

   public K top() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.get(this.size64() - 1L);
      }
   }

   public K peek(int var1) {
      return this.get(this.size64() - 1L - (long)var1);
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      ObjectBigListIterator var2 = this.iterator();
      long var3 = this.size64();
      boolean var6 = true;
      var1.append("[");

      while(var3-- != 0L) {
         if (var6) {
            var6 = false;
         } else {
            var1.append(", ");
         }

         Object var5 = var2.next();
         if (this == var5) {
            var1.append("(this big list)");
         } else {
            var1.append(String.valueOf(var5));
         }
      }

      var1.append("]");
      return var1.toString();
   }

   public static class ReferenceSubList<K> extends AbstractReferenceBigList<K> implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ReferenceBigList<K> l;
      protected final long from;
      protected long to;

      public ReferenceSubList(ReferenceBigList<K> var1, long var2, long var4) {
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

      public boolean add(K var1) {
         this.l.add(this.to, var1);
         ++this.to;

         assert this.assertRange();

         return true;
      }

      public void add(long var1, K var3) {
         this.ensureIndex(var1);
         this.l.add(this.from + var1, var3);
         ++this.to;

         assert this.assertRange();

      }

      public boolean addAll(long var1, Collection<? extends K> var3) {
         this.ensureIndex(var1);
         this.to += (long)var3.size();
         return this.l.addAll(this.from + var1, var3);
      }

      public K get(long var1) {
         this.ensureRestrictedIndex(var1);
         return this.l.get(this.from + var1);
      }

      public K remove(long var1) {
         this.ensureRestrictedIndex(var1);
         --this.to;
         return this.l.remove(this.from + var1);
      }

      public K set(long var1, K var3) {
         this.ensureRestrictedIndex(var1);
         return this.l.set(this.from + var1, var3);
      }

      public long size64() {
         return this.to - this.from;
      }

      public void getElements(long var1, Object[][] var3, long var4, long var6) {
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

      public void addElements(long var1, K[][] var3, long var4, long var6) {
         this.ensureIndex(var1);
         this.l.addElements(this.from + var1, var3, var4, var6);
         this.to += var6;

         assert this.assertRange();

      }

      public ObjectBigListIterator<K> listIterator(final long var1) {
         this.ensureIndex(var1);
         return new ObjectBigListIterator<K>() {
            long pos = var1;
            long last = -1L;

            public boolean hasNext() {
               return this.pos < ReferenceSubList.this.size64();
            }

            public boolean hasPrevious() {
               return this.pos > 0L;
            }

            public K next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return ReferenceSubList.this.l.get(ReferenceSubList.this.from + (this.last = (long)(this.pos++)));
               }
            }

            public K previous() {
               if (!this.hasPrevious()) {
                  throw new NoSuchElementException();
               } else {
                  return ReferenceSubList.this.l.get(ReferenceSubList.this.from + (this.last = --this.pos));
               }
            }

            public long nextIndex() {
               return this.pos;
            }

            public long previousIndex() {
               return this.pos - 1L;
            }

            public void add(K var1x) {
               if (this.last == -1L) {
                  throw new IllegalStateException();
               } else {
                  ReferenceSubList.this.add((long)(this.pos++), var1x);
                  this.last = -1L;

                  assert ReferenceSubList.this.assertRange();

               }
            }

            public void set(K var1x) {
               if (this.last == -1L) {
                  throw new IllegalStateException();
               } else {
                  ReferenceSubList.this.set(this.last, var1x);
               }
            }

            public void remove() {
               if (this.last == -1L) {
                  throw new IllegalStateException();
               } else {
                  ReferenceSubList.this.remove(this.last);
                  if (this.last < this.pos) {
                     --this.pos;
                  }

                  this.last = -1L;

                  assert ReferenceSubList.this.assertRange();

               }
            }
         };
      }

      public ReferenceBigList<K> subList(long var1, long var3) {
         this.ensureIndex(var1);
         this.ensureIndex(var3);
         if (var1 > var3) {
            throw new IllegalArgumentException("Start index (" + var1 + ") is greater than end index (" + var3 + ")");
         } else {
            return new AbstractReferenceBigList.ReferenceSubList(this, var1, var3);
         }
      }
   }
}
