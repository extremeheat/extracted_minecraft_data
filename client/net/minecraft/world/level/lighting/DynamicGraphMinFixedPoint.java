package net.minecraft.world.level.lighting;

import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import java.util.function.LongPredicate;
import net.minecraft.util.Mth;

public abstract class DynamicGraphMinFixedPoint {
   public static final long SOURCE = 9223372036854775807L;
   private static final int NO_COMPUTED_LEVEL = 255;
   protected final int levelCount;
   private final LeveledPriorityQueue priorityQueue;
   private final Long2ByteMap computedLevels;
   private volatile boolean hasWork;

   protected DynamicGraphMinFixedPoint(int var1, int var2, final int var3) {
      super();
      if (var1 >= 254) {
         throw new IllegalArgumentException("Level count must be < 254.");
      } else {
         this.levelCount = var1;
         this.priorityQueue = new LeveledPriorityQueue(var1, var2);
         this.computedLevels = new Long2ByteOpenHashMap(var3, 0.5F) {
            protected void rehash(int var1) {
               if (var1 > var3) {
                  super.rehash(var1);
               }

            }
         };
         this.computedLevels.defaultReturnValue((byte)-1);
      }
   }

   protected void removeFromQueue(long var1) {
      int var3 = this.computedLevels.remove(var1) & 255;
      if (var3 != 255) {
         int var4 = this.getLevel(var1);
         int var5 = this.calculatePriority(var4, var3);
         this.priorityQueue.dequeue(var1, var5, this.levelCount);
         this.hasWork = !this.priorityQueue.isEmpty();
      }
   }

   public void removeIf(LongPredicate var1) {
      LongArrayList var2 = new LongArrayList();
      this.computedLevels.keySet().forEach((var2x) -> {
         if (var1.test(var2x)) {
            var2.add(var2x);
         }

      });
      var2.forEach(this::removeFromQueue);
   }

   private int calculatePriority(int var1, int var2) {
      return Math.min(Math.min(var1, var2), this.levelCount - 1);
   }

   protected void checkNode(long var1) {
      this.checkEdge(var1, var1, this.levelCount - 1, false);
   }

   protected void checkEdge(long var1, long var3, int var5, boolean var6) {
      this.checkEdge(var1, var3, var5, this.getLevel(var3), this.computedLevels.get(var3) & 255, var6);
      this.hasWork = !this.priorityQueue.isEmpty();
   }

   private void checkEdge(long var1, long var3, int var5, int var6, int var7, boolean var8) {
      if (!this.isSource(var3)) {
         var5 = Mth.clamp(var5, 0, this.levelCount - 1);
         var6 = Mth.clamp(var6, 0, this.levelCount - 1);
         boolean var9 = var7 == 255;
         if (var9) {
            var7 = var6;
         }

         int var10;
         if (var8) {
            var10 = Math.min(var7, var5);
         } else {
            var10 = Mth.clamp(this.getComputedLevel(var3, var1, var5), 0, this.levelCount - 1);
         }

         int var11 = this.calculatePriority(var6, var7);
         if (var6 != var10) {
            int var12 = this.calculatePriority(var6, var10);
            if (var11 != var12 && !var9) {
               this.priorityQueue.dequeue(var3, var11, var12);
            }

            this.priorityQueue.enqueue(var3, var12);
            this.computedLevels.put(var3, (byte)var10);
         } else if (!var9) {
            this.priorityQueue.dequeue(var3, var11, this.levelCount);
            this.computedLevels.remove(var3);
         }

      }
   }

   protected final void checkNeighbor(long var1, long var3, int var5, boolean var6) {
      int var7 = this.computedLevels.get(var3) & 255;
      int var8 = Mth.clamp(this.computeLevelFromNeighbor(var1, var3, var5), 0, this.levelCount - 1);
      if (var6) {
         this.checkEdge(var1, var3, var8, this.getLevel(var3), var7, var6);
      } else {
         boolean var10 = var7 == 255;
         int var9;
         if (var10) {
            var9 = Mth.clamp(this.getLevel(var3), 0, this.levelCount - 1);
         } else {
            var9 = var7;
         }

         if (var8 == var9) {
            this.checkEdge(var1, var3, this.levelCount - 1, var10 ? var9 : this.getLevel(var3), var7, var6);
         }
      }

   }

   protected final boolean hasWork() {
      return this.hasWork;
   }

   protected final int runUpdates(int var1) {
      if (this.priorityQueue.isEmpty()) {
         return var1;
      } else {
         while(!this.priorityQueue.isEmpty() && var1 > 0) {
            --var1;
            long var2 = this.priorityQueue.removeFirstLong();
            int var4 = Mth.clamp(this.getLevel(var2), 0, this.levelCount - 1);
            int var5 = this.computedLevels.remove(var2) & 255;
            if (var5 < var4) {
               this.setLevel(var2, var5);
               this.checkNeighborsAfterUpdate(var2, var5, true);
            } else if (var5 > var4) {
               this.setLevel(var2, this.levelCount - 1);
               if (var5 != this.levelCount - 1) {
                  this.priorityQueue.enqueue(var2, this.calculatePriority(this.levelCount - 1, var5));
                  this.computedLevels.put(var2, (byte)var5);
               }

               this.checkNeighborsAfterUpdate(var2, var4, false);
            }
         }

         this.hasWork = !this.priorityQueue.isEmpty();
         return var1;
      }
   }

   public int getQueueSize() {
      return this.computedLevels.size();
   }

   protected boolean isSource(long var1) {
      return var1 == 9223372036854775807L;
   }

   protected abstract int getComputedLevel(long var1, long var3, int var5);

   protected abstract void checkNeighborsAfterUpdate(long var1, int var3, boolean var4);

   protected abstract int getLevel(long var1);

   protected abstract void setLevel(long var1, int var3);

   protected abstract int computeLevelFromNeighbor(long var1, long var3, int var5);
}
