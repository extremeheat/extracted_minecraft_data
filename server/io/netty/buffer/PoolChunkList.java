package io.netty.buffer;

import io.netty.util.internal.StringUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

final class PoolChunkList<T> implements PoolChunkListMetric {
   private static final Iterator<PoolChunkMetric> EMPTY_METRICS = Collections.emptyList().iterator();
   private final PoolArena<T> arena;
   private final PoolChunkList<T> nextList;
   private final int minUsage;
   private final int maxUsage;
   private final int maxCapacity;
   private PoolChunk<T> head;
   private PoolChunkList<T> prevList;

   PoolChunkList(PoolArena<T> var1, PoolChunkList<T> var2, int var3, int var4, int var5) {
      super();

      assert var3 <= var4;

      this.arena = var1;
      this.nextList = var2;
      this.minUsage = var3;
      this.maxUsage = var4;
      this.maxCapacity = calculateMaxCapacity(var3, var5);
   }

   private static int calculateMaxCapacity(int var0, int var1) {
      var0 = minUsage0(var0);
      return var0 == 100 ? 0 : (int)((long)var1 * (100L - (long)var0) / 100L);
   }

   void prevList(PoolChunkList<T> var1) {
      assert this.prevList == null;

      this.prevList = var1;
   }

   boolean allocate(PooledByteBuf<T> var1, int var2, int var3) {
      if (this.head != null && var3 <= this.maxCapacity) {
         PoolChunk var4 = this.head;

         do {
            long var5 = var4.allocate(var3);
            if (var5 >= 0L) {
               var4.initBuf(var1, var5, var2);
               if (var4.usage() >= this.maxUsage) {
                  this.remove(var4);
                  this.nextList.add(var4);
               }

               return true;
            }

            var4 = var4.next;
         } while(var4 != null);

         return false;
      } else {
         return false;
      }
   }

   boolean free(PoolChunk<T> var1, long var2) {
      var1.free(var2);
      if (var1.usage() < this.minUsage) {
         this.remove(var1);
         return this.move0(var1);
      } else {
         return true;
      }
   }

   private boolean move(PoolChunk<T> var1) {
      assert var1.usage() < this.maxUsage;

      if (var1.usage() < this.minUsage) {
         return this.move0(var1);
      } else {
         this.add0(var1);
         return true;
      }
   }

   private boolean move0(PoolChunk<T> var1) {
      if (this.prevList == null) {
         assert var1.usage() == 0;

         return false;
      } else {
         return this.prevList.move(var1);
      }
   }

   void add(PoolChunk<T> var1) {
      if (var1.usage() >= this.maxUsage) {
         this.nextList.add(var1);
      } else {
         this.add0(var1);
      }
   }

   void add0(PoolChunk<T> var1) {
      var1.parent = this;
      if (this.head == null) {
         this.head = var1;
         var1.prev = null;
         var1.next = null;
      } else {
         var1.prev = null;
         var1.next = this.head;
         this.head.prev = var1;
         this.head = var1;
      }

   }

   private void remove(PoolChunk<T> var1) {
      if (var1 == this.head) {
         this.head = var1.next;
         if (this.head != null) {
            this.head.prev = null;
         }
      } else {
         PoolChunk var2 = var1.next;
         var1.prev.next = var2;
         if (var2 != null) {
            var2.prev = var1.prev;
         }
      }

   }

   public int minUsage() {
      return minUsage0(this.minUsage);
   }

   public int maxUsage() {
      return Math.min(this.maxUsage, 100);
   }

   private static int minUsage0(int var0) {
      return Math.max(1, var0);
   }

   public Iterator<PoolChunkMetric> iterator() {
      synchronized(this.arena) {
         if (this.head == null) {
            return EMPTY_METRICS;
         } else {
            ArrayList var2 = new ArrayList();
            PoolChunk var3 = this.head;

            do {
               var2.add(var3);
               var3 = var3.next;
            } while(var3 != null);

            return var2.iterator();
         }
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      synchronized(this.arena) {
         if (this.head == null) {
            return "none";
         } else {
            PoolChunk var3 = this.head;

            while(true) {
               var1.append(var3);
               var3 = var3.next;
               if (var3 == null) {
                  return var1.toString();
               }

               var1.append(StringUtil.NEWLINE);
            }
         }
      }
   }

   void destroy(PoolArena<T> var1) {
      for(PoolChunk var2 = this.head; var2 != null; var2 = var2.next) {
         var1.destroyChunk(var2);
      }

      this.head = null;
   }
}
