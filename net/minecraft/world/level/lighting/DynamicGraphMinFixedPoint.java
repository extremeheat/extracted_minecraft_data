package net.minecraft.world.level.lighting;

import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import java.util.function.LongPredicate;
import net.minecraft.util.Mth;

public abstract class DynamicGraphMinFixedPoint {
   private final int levelCount;
   private final LongLinkedOpenHashSet[] queues;
   private final Long2ByteMap computedLevels;
   private int firstQueuedLevel;
   private volatile boolean hasWork;

   protected DynamicGraphMinFixedPoint(int var1, final int var2, final int var3) {
      if (var1 >= 254) {
         throw new IllegalArgumentException("Level count must be < 254.");
      } else {
         this.levelCount = var1;
         this.queues = new LongLinkedOpenHashSet[var1];

         for(int var4 = 0; var4 < var1; ++var4) {
            this.queues[var4] = new LongLinkedOpenHashSet(var2, 0.5F) {
               protected void rehash(int var1) {
                  if (var1 > var2) {
                     super.rehash(var1);
                  }

               }
            };
         }

         this.computedLevels = new Long2ByteOpenHashMap(var3, 0.5F) {
            protected void rehash(int var1) {
               if (var1 > var3) {
                  super.rehash(var1);
               }

            }
         };
         this.computedLevels.defaultReturnValue((byte)-1);
         this.firstQueuedLevel = var1;
      }
   }

   private int getKey(int var1, int var2) {
      int var3 = var1;
      if (var1 > var2) {
         var3 = var2;
      }

      if (var3 > this.levelCount - 1) {
         var3 = this.levelCount - 1;
      }

      return var3;
   }

   private void checkFirstQueuedLevel(int var1) {
      int var2 = this.firstQueuedLevel;
      this.firstQueuedLevel = var1;

      for(int var3 = var2 + 1; var3 < var1; ++var3) {
         if (!this.queues[var3].isEmpty()) {
            this.firstQueuedLevel = var3;
            break;
         }
      }

   }

   protected void removeFromQueue(long var1) {
      int var3 = this.computedLevels.get(var1) & 255;
      if (var3 != 255) {
         int var4 = this.getLevel(var1);
         int var5 = this.getKey(var4, var3);
         this.dequeue(var1, var5, this.levelCount, true);
         this.hasWork = this.firstQueuedLevel < this.levelCount;
      }
   }

   public void removeIf(LongPredicate var1) {
      LongArrayList var2 = new LongArrayList(0);
      this.computedLevels.keySet().forEach((var2x) -> {
         if (var1.test(var2x)) {
            var2.add(var2x);
         }

      });
      var2.forEach(this::removeFromQueue);
   }

   private void dequeue(long var1, int var3, int var4, boolean var5) {
      if (var5) {
         this.computedLevels.remove(var1);
      }

      this.queues[var3].remove(var1);
      if (this.queues[var3].isEmpty() && this.firstQueuedLevel == var3) {
         this.checkFirstQueuedLevel(var4);
      }

   }

   private void enqueue(long var1, int var3, int var4) {
      this.computedLevels.put(var1, (byte)var3);
      this.queues[var4].add(var1);
      if (this.firstQueuedLevel > var4) {
         this.firstQueuedLevel = var4;
      }

   }

   protected void checkNode(long var1) {
      this.checkEdge(var1, var1, this.levelCount - 1, false);
   }

   protected void checkEdge(long var1, long var3, int var5, boolean var6) {
      this.checkEdge(var1, var3, var5, this.getLevel(var3), this.computedLevels.get(var3) & 255, var6);
      this.hasWork = this.firstQueuedLevel < this.levelCount;
   }

   private void checkEdge(long var1, long var3, int var5, int var6, int var7, boolean var8) {
      if (!this.isSource(var3)) {
         var5 = Mth.clamp(var5, 0, this.levelCount - 1);
         var6 = Mth.clamp(var6, 0, this.levelCount - 1);
         boolean var9;
         if (var7 == 255) {
            var9 = true;
            var7 = var6;
         } else {
            var9 = false;
         }

         int var10;
         if (var8) {
            var10 = Math.min(var7, var5);
         } else {
            var10 = Mth.clamp(this.getComputedLevel(var3, var1, var5), 0, this.levelCount - 1);
         }

         int var11 = this.getKey(var6, var7);
         if (var6 != var10) {
            int var12 = this.getKey(var6, var10);
            if (var11 != var12 && !var9) {
               this.dequeue(var3, var11, var12, false);
            }

            this.enqueue(var3, var10, var12);
         } else if (!var9) {
            this.dequeue(var3, var11, this.levelCount, true);
         }

      }
   }

   protected final void checkNeighbor(long var1, long var3, int var5, boolean var6) {
      int var7 = this.computedLevels.get(var3) & 255;
      int var8 = Mth.clamp(this.computeLevelFromNeighbor(var1, var3, var5), 0, this.levelCount - 1);
      if (var6) {
         this.checkEdge(var1, var3, var8, this.getLevel(var3), var7, true);
      } else {
         int var9;
         boolean var10;
         if (var7 == 255) {
            var10 = true;
            var9 = Mth.clamp(this.getLevel(var3), 0, this.levelCount - 1);
         } else {
            var9 = var7;
            var10 = false;
         }

         if (var8 == var9) {
            this.checkEdge(var1, var3, this.levelCount - 1, var10 ? var9 : this.getLevel(var3), var7, false);
         }
      }

   }

   protected final boolean hasWork() {
      return this.hasWork;
   }

   protected final int runUpdates(int var1) {
      if (this.firstQueuedLevel >= this.levelCount) {
         return var1;
      } else {
         while(this.firstQueuedLevel < this.levelCount && var1 > 0) {
            --var1;
            LongLinkedOpenHashSet var2 = this.queues[this.firstQueuedLevel];
            long var3 = var2.removeFirstLong();
            int var5 = Mth.clamp(this.getLevel(var3), 0, this.levelCount - 1);
            if (var2.isEmpty()) {
               this.checkFirstQueuedLevel(this.levelCount);
            }

            int var6 = this.computedLevels.remove(var3) & 255;
            if (var6 < var5) {
               this.setLevel(var3, var6);
               this.checkNeighborsAfterUpdate(var3, var6, true);
            } else if (var6 > var5) {
               this.enqueue(var3, var6, this.getKey(this.levelCount - 1, var6));
               this.setLevel(var3, this.levelCount - 1);
               this.checkNeighborsAfterUpdate(var3, var5, false);
            }
         }

         this.hasWork = this.firstQueuedLevel < this.levelCount;
         return var1;
      }
   }

   public int getQueueSize() {
      return this.computedLevels.size();
   }

   protected abstract boolean isSource(long var1);

   protected abstract int getComputedLevel(long var1, long var3, int var5);

   protected abstract void checkNeighborsAfterUpdate(long var1, int var3, boolean var4);

   protected abstract int getLevel(long var1);

   protected abstract void setLevel(long var1, int var3);

   protected abstract int computeLevelFromNeighbor(long var1, long var3, int var5);
}
