package net.minecraft.util.thread;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

public class ParallelMapTransform {
   private static final int DEFAULT_TASKS_PER_THREAD = 16;

   public ParallelMapTransform() {
      super();
   }

   public static <K, U, V> CompletableFuture<Map<K, V>> schedule(Map<K, U> var0, BiFunction<K, U, @Nullable V> var1, int var2, Executor var3) {
      int var4 = var0.size();
      if (var4 == 0) {
         return CompletableFuture.completedFuture(Map.of());
      } else if (var4 == 1) {
         Map.Entry var8 = (Map.Entry)var0.entrySet().iterator().next();
         Object var6 = var8.getKey();
         Object var7 = var8.getValue();
         return CompletableFuture.supplyAsync(() -> {
            Object var3 = var1.apply(var6, var7);
            return var3 != null ? Map.of(var6, var3) : Map.of();
         }, var3);
      } else {
         Object var5 = var4 <= var2 ? new SingleTaskSplitter(var1, var4) : new BatchedTaskSplitter(var1, var4, var2);
         return ((SplitterBase)var5).scheduleTasks(var0, var3);
      }
   }

   public static <K, U, V> CompletableFuture<Map<K, V>> schedule(Map<K, U> var0, BiFunction<K, U, @Nullable V> var1, Executor var2) {
      int var3 = Util.maxAllowedExecutorThreads() * 16;
      return schedule(var0, var1, var3, var2);
   }

   static record Container<K, U, V>(BiFunction<K, U, V> operation, @Nullable Object[] keys, @Nullable Object[] values) {
      public Container(BiFunction<K, U, V> var1, int var2) {
         this(var1, new Object[var2], new Object[var2]);
      }

      private Container(BiFunction<K, U, V> var1, @Nullable Object[] var2, @Nullable Object[] var3) {
         super();
         this.operation = var1;
         this.keys = var2;
         this.values = var3;
      }

      public void put(int var1, K var2, U var3) {
         this.keys[var1] = var2;
         this.values[var1] = var3;
      }

      private @Nullable K key(int var1) {
         return (K)this.keys[var1];
      }

      private @Nullable V output(int var1) {
         return (V)this.values[var1];
      }

      private @Nullable U input(int var1) {
         return (U)this.values[var1];
      }

      public void applyOperation(int var1) {
         this.values[var1] = this.operation.apply(this.key(var1), this.input(var1));
      }

      public void copyOut(int var1, Map<K, V> var2) {
         Object var3 = this.output(var1);
         if (var3 != null) {
            Object var4 = this.key(var1);
            var2.put(var4, var3);
         }

      }

      public int size() {
         return this.keys.length;
      }
   }

   abstract static class SplitterBase<K, U, V> {
      private int lastScheduledIndex;
      private int currentIndex;
      private final CompletableFuture<?>[] tasks;
      private int batchIndex;
      private final Container<K, U, V> container;

      SplitterBase(BiFunction<K, U, V> var1, int var2, int var3) {
         super();
         this.container = new Container<K, U, V>(var1, var2);
         this.tasks = new CompletableFuture[var3];
      }

      private int pendingBatchSize() {
         return this.currentIndex - this.lastScheduledIndex;
      }

      public CompletableFuture<Map<K, V>> scheduleTasks(Map<K, U> var1, Executor var2) {
         var1.forEach((var2x, var3) -> {
            this.container.put(this.currentIndex++, var2x, var3);
            if (this.pendingBatchSize() == this.batchSize(this.batchIndex)) {
               this.tasks[this.batchIndex++] = this.scheduleBatch(this.container, this.lastScheduledIndex, this.currentIndex, var2);
               this.lastScheduledIndex = this.currentIndex;
            }

         });

         assert this.currentIndex == this.container.size();

         assert this.lastScheduledIndex == this.currentIndex;

         assert this.batchIndex == this.tasks.length;

         return this.scheduleFinalOperation(CompletableFuture.allOf(this.tasks), this.container);
      }

      protected abstract int batchSize(int var1);

      protected abstract CompletableFuture<?> scheduleBatch(Container<K, U, V> var1, int var2, int var3, Executor var4);

      protected abstract CompletableFuture<Map<K, V>> scheduleFinalOperation(CompletableFuture<?> var1, Container<K, U, V> var2);
   }

   static class SingleTaskSplitter<K, U, V> extends SplitterBase<K, U, V> {
      SingleTaskSplitter(BiFunction<K, U, V> var1, int var2) {
         super(var1, var2, var2);
      }

      protected int batchSize(int var1) {
         return 1;
      }

      protected CompletableFuture<?> scheduleBatch(Container<K, U, V> var1, int var2, int var3, Executor var4) {
         assert var2 + 1 == var3;

         return CompletableFuture.runAsync(() -> var1.applyOperation(var2), var4);
      }

      protected CompletableFuture<Map<K, V>> scheduleFinalOperation(CompletableFuture<?> var1, Container<K, U, V> var2) {
         return var1.thenApply((var1x) -> {
            HashMap var2x = new HashMap(var2.size());

            for(int var3 = 0; var3 < var2.size(); ++var3) {
               var2.copyOut(var3, var2x);
            }

            return var2x;
         });
      }
   }

   static class BatchedTaskSplitter<K, U, V> extends SplitterBase<K, U, V> {
      private final Map<K, V> result;
      private final int batchSize;
      private final int firstUndersizedBatchIndex;

      BatchedTaskSplitter(BiFunction<K, U, V> var1, int var2, int var3) {
         super(var1, var2, var3);
         this.result = new HashMap(var2);
         this.batchSize = Mth.positiveCeilDiv(var2, var3);
         int var4 = this.batchSize * var3;
         int var5 = var4 - var2;
         this.firstUndersizedBatchIndex = var3 - var5;

         assert this.firstUndersizedBatchIndex > 0 && this.firstUndersizedBatchIndex <= var3;
      }

      protected CompletableFuture<?> scheduleBatch(Container<K, U, V> var1, int var2, int var3, Executor var4) {
         int var5 = var3 - var2;

         assert var5 == this.batchSize || var5 == this.batchSize - 1;

         return CompletableFuture.runAsync(createTask(this.result, var2, var3, var1), var4);
      }

      protected int batchSize(int var1) {
         return var1 < this.firstUndersizedBatchIndex ? this.batchSize : this.batchSize - 1;
      }

      private static <K, U, V> Runnable createTask(Map<K, V> var0, int var1, int var2, Container<K, U, V> var3) {
         return () -> {
            for(int var4 = var1; var4 < var2; ++var4) {
               var3.applyOperation(var4);
            }

            synchronized(var0) {
               for(int var5 = var1; var5 < var2; ++var5) {
                  var3.copyOut(var5, var0);
               }

            }
         };
      }

      protected CompletableFuture<Map<K, V>> scheduleFinalOperation(CompletableFuture<?> var1, Container<K, U, V> var2) {
         Map var3 = this.result;
         return var1.thenApply((var1x) -> var3);
      }
   }
}
