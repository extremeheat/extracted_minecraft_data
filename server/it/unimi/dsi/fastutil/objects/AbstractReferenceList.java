package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Stack;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public abstract class AbstractReferenceList<K> extends AbstractReferenceCollection<K> implements ReferenceList<K>, Stack<K> {
   protected AbstractReferenceList() {
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

   public void add(int var1, K var2) {
      throw new UnsupportedOperationException();
   }

   public boolean add(K var1) {
      this.add(this.size(), var1);
      return true;
   }

   public K remove(int var1) {
      throw new UnsupportedOperationException();
   }

   public K set(int var1, K var2) {
      throw new UnsupportedOperationException();
   }

   public boolean addAll(int var1, Collection<? extends K> var2) {
      this.ensureIndex(var1);
      Iterator var3 = var2.iterator();
      boolean var4 = var3.hasNext();

      while(var3.hasNext()) {
         this.add(var1++, var3.next());
      }

      return var4;
   }

   public boolean addAll(Collection<? extends K> var1) {
      return this.addAll(this.size(), var1);
   }

   public ObjectListIterator<K> iterator() {
      return this.listIterator();
   }

   public ObjectListIterator<K> listIterator() {
      return this.listIterator(0);
   }

   public ObjectListIterator<K> listIterator(final int var1) {
      this.ensureIndex(var1);
      return new ObjectListIterator<K>() {
         int pos = var1;
         int last = -1;

         public boolean hasNext() {
            return this.pos < AbstractReferenceList.this.size();
         }

         public boolean hasPrevious() {
            return this.pos > 0;
         }

         public K next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return AbstractReferenceList.this.get(this.last = this.pos++);
            }
         }

         public K previous() {
            if (!this.hasPrevious()) {
               throw new NoSuchElementException();
            } else {
               return AbstractReferenceList.this.get(this.last = --this.pos);
            }
         }

         public int nextIndex() {
            return this.pos;
         }

         public int previousIndex() {
            return this.pos - 1;
         }

         public void add(K var1x) {
            AbstractReferenceList.this.add(this.pos++, var1x);
            this.last = -1;
         }

         public void set(K var1x) {
            if (this.last == -1) {
               throw new IllegalStateException();
            } else {
               AbstractReferenceList.this.set(this.last, var1x);
            }
         }

         public void remove() {
            if (this.last == -1) {
               throw new IllegalStateException();
            } else {
               AbstractReferenceList.this.remove(this.last);
               if (this.last < this.pos) {
                  --this.pos;
               }

               this.last = -1;
            }
         }
      };
   }

   public boolean contains(Object var1) {
      return this.indexOf(var1) >= 0;
   }

   public int indexOf(Object var1) {
      ObjectListIterator var2 = this.listIterator();

      Object var3;
      do {
         if (!var2.hasNext()) {
            return -1;
         }

         var3 = var2.next();
      } while(var1 != var3);

      return var2.previousIndex();
   }

   public int lastIndexOf(Object var1) {
      ObjectListIterator var2 = this.listIterator(this.size());

      Object var3;
      do {
         if (!var2.hasPrevious()) {
            return -1;
         }

         var3 = var2.previous();
      } while(var1 != var3);

      return var2.nextIndex();
   }

   public void size(int var1) {
      int var2 = this.size();
      if (var1 > var2) {
         while(var2++ < var1) {
            this.add((Object)null);
         }
      } else {
         while(var2-- != var1) {
            this.remove(var2);
         }
      }

   }

   public ReferenceList<K> subList(int var1, int var2) {
      this.ensureIndex(var1);
      this.ensureIndex(var2);
      if (var1 > var2) {
         throw new IndexOutOfBoundsException("Start index (" + var1 + ") is greater than end index (" + var2 + ")");
      } else {
         return new AbstractReferenceList.ReferenceSubList(this, var1, var2);
      }
   }

   public void removeElements(int var1, int var2) {
      this.ensureIndex(var2);
      ObjectListIterator var3 = this.listIterator(var1);
      int var4 = var2 - var1;
      if (var4 < 0) {
         throw new IllegalArgumentException("Start index (" + var1 + ") is greater than end index (" + var2 + ")");
      } else {
         while(var4-- != 0) {
            var3.next();
            var3.remove();
         }

      }
   }

   public void addElements(int var1, K[] var2, int var3, int var4) {
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

   public void addElements(int var1, K[] var2) {
      this.addElements(var1, var2, 0, var2.length);
   }

   public void getElements(int var1, Object[] var2, int var3, int var4) {
      ObjectListIterator var5 = this.listIterator(var1);
      if (var3 < 0) {
         throw new ArrayIndexOutOfBoundsException("Offset (" + var3 + ") is negative");
      } else if (var3 + var4 > var2.length) {
         throw new ArrayIndexOutOfBoundsException("End index (" + (var3 + var4) + ") is greater than array length (" + var2.length + ")");
      } else if (var1 + var4 > this.size()) {
         throw new IndexOutOfBoundsException("End index (" + (var1 + var4) + ") is greater than list size (" + this.size() + ")");
      } else {
         while(var4-- != 0) {
            var2[var3++] = var5.next();
         }

      }
   }

   public void clear() {
      this.removeElements(0, this.size());
   }

   public int hashCode() {
      ObjectListIterator var1 = this.iterator();
      int var2 = 1;

      Object var4;
      for(int var3 = this.size(); var3-- != 0; var2 = 31 * var2 + System.identityHashCode(var4)) {
         var4 = var1.next();
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
            ObjectListIterator var4 = this.listIterator();
            ListIterator var5 = var2.listIterator();

            do {
               if (var3-- == 0) {
                  return true;
               }
            } while(var4.next() == var5.next());

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
         return this.remove(this.size() - 1);
      }
   }

   public K top() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.get(this.size() - 1);
      }
   }

   public K peek(int var1) {
      return this.get(this.size() - 1 - var1);
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      ObjectListIterator var2 = this.iterator();
      int var3 = this.size();
      boolean var5 = true;
      var1.append("[");

      while(var3-- != 0) {
         if (var5) {
            var5 = false;
         } else {
            var1.append(", ");
         }

         Object var4 = var2.next();
         if (this == var4) {
            var1.append("(this list)");
         } else {
            var1.append(String.valueOf(var4));
         }
      }

      var1.append("]");
      return var1.toString();
   }

   public static class ReferenceSubList<K> extends AbstractReferenceList<K> implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ReferenceList<K> l;
      protected final int from;
      protected int to;

      public ReferenceSubList(ReferenceList<K> var1, int var2, int var3) {
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

      public boolean add(K var1) {
         this.l.add(this.to, var1);
         ++this.to;

         assert this.assertRange();

         return true;
      }

      public void add(int var1, K var2) {
         this.ensureIndex(var1);
         this.l.add(this.from + var1, var2);
         ++this.to;

         assert this.assertRange();

      }

      public boolean addAll(int var1, Collection<? extends K> var2) {
         this.ensureIndex(var1);
         this.to += var2.size();
         return this.l.addAll(this.from + var1, var2);
      }

      public K get(int var1) {
         this.ensureRestrictedIndex(var1);
         return this.l.get(this.from + var1);
      }

      public K remove(int var1) {
         this.ensureRestrictedIndex(var1);
         --this.to;
         return this.l.remove(this.from + var1);
      }

      public K set(int var1, K var2) {
         this.ensureRestrictedIndex(var1);
         return this.l.set(this.from + var1, var2);
      }

      public int size() {
         return this.to - this.from;
      }

      public void getElements(int var1, Object[] var2, int var3, int var4) {
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

      public void addElements(int var1, K[] var2, int var3, int var4) {
         this.ensureIndex(var1);
         this.l.addElements(this.from + var1, var2, var3, var4);
         this.to += var4;

         assert this.assertRange();

      }

      public ObjectListIterator<K> listIterator(final int var1) {
         this.ensureIndex(var1);
         return new ObjectListIterator<K>() {
            int pos = var1;
            int last = -1;

            public boolean hasNext() {
               return this.pos < ReferenceSubList.this.size();
            }

            public boolean hasPrevious() {
               return this.pos > 0;
            }

            public K next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return ReferenceSubList.this.l.get(ReferenceSubList.this.from + (this.last = this.pos++));
               }
            }

            public K previous() {
               if (!this.hasPrevious()) {
                  throw new NoSuchElementException();
               } else {
                  return ReferenceSubList.this.l.get(ReferenceSubList.this.from + (this.last = --this.pos));
               }
            }

            public int nextIndex() {
               return this.pos;
            }

            public int previousIndex() {
               return this.pos - 1;
            }

            public void add(K var1x) {
               if (this.last == -1) {
                  throw new IllegalStateException();
               } else {
                  ReferenceSubList.this.add(this.pos++, var1x);
                  this.last = -1;

                  assert ReferenceSubList.this.assertRange();

               }
            }

            public void set(K var1x) {
               if (this.last == -1) {
                  throw new IllegalStateException();
               } else {
                  ReferenceSubList.this.set(this.last, var1x);
               }
            }

            public void remove() {
               if (this.last == -1) {
                  throw new IllegalStateException();
               } else {
                  ReferenceSubList.this.remove(this.last);
                  if (this.last < this.pos) {
                     --this.pos;
                  }

                  this.last = -1;

                  assert ReferenceSubList.this.assertRange();

               }
            }
         };
      }

      public ReferenceList<K> subList(int var1, int var2) {
         this.ensureIndex(var1);
         this.ensureIndex(var2);
         if (var1 > var2) {
            throw new IllegalArgumentException("Start index (" + var1 + ") is greater than end index (" + var2 + ")");
         } else {
            return new AbstractReferenceList.ReferenceSubList(this, var1, var2);
         }
      }
   }
}
