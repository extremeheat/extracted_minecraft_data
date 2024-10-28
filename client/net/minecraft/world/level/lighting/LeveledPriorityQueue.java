package net.minecraft.world.level.lighting;

import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;

public class LeveledPriorityQueue {
   private final int levelCount;
   private final LongLinkedOpenHashSet[] queues;
   private int firstQueuedLevel;

   public LeveledPriorityQueue(int var1, final int var2) {
      super();
      this.levelCount = var1;
      this.queues = new LongLinkedOpenHashSet[var1];

      for(int var3 = 0; var3 < var1; ++var3) {
         this.queues[var3] = new LongLinkedOpenHashSet(var2, 0.5F) {
            protected void rehash(int var1) {
               if (var1 > var2) {
                  super.rehash(var1);
               }

            }
         };
      }

      this.firstQueuedLevel = var1;
   }

   public long removeFirstLong() {
      LongLinkedOpenHashSet var1 = this.queues[this.firstQueuedLevel];
      long var2 = var1.removeFirstLong();
      if (var1.isEmpty()) {
         this.checkFirstQueuedLevel(this.levelCount);
      }

      return var2;
   }

   public boolean isEmpty() {
      return this.firstQueuedLevel >= this.levelCount;
   }

   public void dequeue(long var1, int var3, int var4) {
      LongLinkedOpenHashSet var5 = this.queues[var3];
      var5.remove(var1);
      if (var5.isEmpty() && this.firstQueuedLevel == var3) {
         this.checkFirstQueuedLevel(var4);
      }

   }

   public void enqueue(long var1, int var3) {
      this.queues[var3].add(var1);
      if (this.firstQueuedLevel > var3) {
         this.firstQueuedLevel = var3;
      }

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
}
