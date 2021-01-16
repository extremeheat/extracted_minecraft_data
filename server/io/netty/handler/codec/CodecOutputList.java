package io.netty.handler.codec;

import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.MathUtil;
import io.netty.util.internal.ObjectUtil;
import java.util.AbstractList;
import java.util.RandomAccess;

final class CodecOutputList extends AbstractList<Object> implements RandomAccess {
   private static final CodecOutputList.CodecOutputListRecycler NOOP_RECYCLER = new CodecOutputList.CodecOutputListRecycler() {
      public void recycle(CodecOutputList var1) {
      }
   };
   private static final FastThreadLocal<CodecOutputList.CodecOutputLists> CODEC_OUTPUT_LISTS_POOL = new FastThreadLocal<CodecOutputList.CodecOutputLists>() {
      protected CodecOutputList.CodecOutputLists initialValue() throws Exception {
         return new CodecOutputList.CodecOutputLists(16);
      }
   };
   private final CodecOutputList.CodecOutputListRecycler recycler;
   private int size;
   private Object[] array;
   private boolean insertSinceRecycled;

   static CodecOutputList newInstance() {
      return ((CodecOutputList.CodecOutputLists)CODEC_OUTPUT_LISTS_POOL.get()).getOrCreate();
   }

   private CodecOutputList(CodecOutputList.CodecOutputListRecycler var1, int var2) {
      super();
      this.recycler = var1;
      this.array = new Object[var2];
   }

   public Object get(int var1) {
      this.checkIndex(var1);
      return this.array[var1];
   }

   public int size() {
      return this.size;
   }

   public boolean add(Object var1) {
      ObjectUtil.checkNotNull(var1, "element");

      try {
         this.insert(this.size, var1);
      } catch (IndexOutOfBoundsException var3) {
         this.expandArray();
         this.insert(this.size, var1);
      }

      ++this.size;
      return true;
   }

   public Object set(int var1, Object var2) {
      ObjectUtil.checkNotNull(var2, "element");
      this.checkIndex(var1);
      Object var3 = this.array[var1];
      this.insert(var1, var2);
      return var3;
   }

   public void add(int var1, Object var2) {
      ObjectUtil.checkNotNull(var2, "element");
      this.checkIndex(var1);
      if (this.size == this.array.length) {
         this.expandArray();
      }

      if (var1 != this.size - 1) {
         System.arraycopy(this.array, var1, this.array, var1 + 1, this.size - var1);
      }

      this.insert(var1, var2);
      ++this.size;
   }

   public Object remove(int var1) {
      this.checkIndex(var1);
      Object var2 = this.array[var1];
      int var3 = this.size - var1 - 1;
      if (var3 > 0) {
         System.arraycopy(this.array, var1 + 1, this.array, var1, var3);
      }

      this.array[--this.size] = null;
      return var2;
   }

   public void clear() {
      this.size = 0;
   }

   boolean insertSinceRecycled() {
      return this.insertSinceRecycled;
   }

   void recycle() {
      for(int var1 = 0; var1 < this.size; ++var1) {
         this.array[var1] = null;
      }

      this.size = 0;
      this.insertSinceRecycled = false;
      this.recycler.recycle(this);
   }

   Object getUnsafe(int var1) {
      return this.array[var1];
   }

   private void checkIndex(int var1) {
      if (var1 >= this.size) {
         throw new IndexOutOfBoundsException();
      }
   }

   private void insert(int var1, Object var2) {
      this.array[var1] = var2;
      this.insertSinceRecycled = true;
   }

   private void expandArray() {
      int var1 = this.array.length << 1;
      if (var1 < 0) {
         throw new OutOfMemoryError();
      } else {
         Object[] var2 = new Object[var1];
         System.arraycopy(this.array, 0, var2, 0, this.array.length);
         this.array = var2;
      }
   }

   // $FF: synthetic method
   CodecOutputList(CodecOutputList.CodecOutputListRecycler var1, int var2, Object var3) {
      this(var1, var2);
   }

   private static final class CodecOutputLists implements CodecOutputList.CodecOutputListRecycler {
      private final CodecOutputList[] elements;
      private final int mask;
      private int currentIdx;
      private int count;

      CodecOutputLists(int var1) {
         super();
         this.elements = new CodecOutputList[MathUtil.safeFindNextPositivePowerOfTwo(var1)];

         for(int var2 = 0; var2 < this.elements.length; ++var2) {
            this.elements[var2] = new CodecOutputList(this, 16);
         }

         this.count = this.elements.length;
         this.currentIdx = this.elements.length;
         this.mask = this.elements.length - 1;
      }

      public CodecOutputList getOrCreate() {
         if (this.count == 0) {
            return new CodecOutputList(CodecOutputList.NOOP_RECYCLER, 4);
         } else {
            --this.count;
            int var1 = this.currentIdx - 1 & this.mask;
            CodecOutputList var2 = this.elements[var1];
            this.currentIdx = var1;
            return var2;
         }
      }

      public void recycle(CodecOutputList var1) {
         int var2 = this.currentIdx;
         this.elements[var2] = var1;
         this.currentIdx = var2 + 1 & this.mask;
         ++this.count;

         assert this.count <= this.elements.length;

      }
   }

   private interface CodecOutputListRecycler {
      void recycle(CodecOutputList var1);
   }
}
