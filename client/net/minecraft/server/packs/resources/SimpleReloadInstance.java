package net.minecraft.server.packs.resources;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.util.Unit;

public class SimpleReloadInstance<S> implements ReloadInstance {
   private static final int PREPARATION_PROGRESS_WEIGHT = 2;
   private static final int EXTRA_RELOAD_PROGRESS_WEIGHT = 2;
   private static final int LISTENER_PROGRESS_WEIGHT = 1;
   final CompletableFuture<Unit> allPreparations = new CompletableFuture();
   @Nullable
   private CompletableFuture<List<S>> allDone;
   final Set<PreparableReloadListener> preparingListeners;
   private final int listenerCount;
   private final AtomicInteger startedTasks = new AtomicInteger();
   private final AtomicInteger finishedTasks = new AtomicInteger();
   private final AtomicInteger startedReloads = new AtomicInteger();
   private final AtomicInteger finishedReloads = new AtomicInteger();

   public static ReloadInstance of(ResourceManager var0, List<PreparableReloadListener> var1, Executor var2, Executor var3, CompletableFuture<Unit> var4) {
      SimpleReloadInstance var5 = new SimpleReloadInstance(var1);
      var5.startTasks(var2, var3, var0, var1, SimpleReloadInstance.StateFactory.SIMPLE, var4);
      return var5;
   }

   protected SimpleReloadInstance(List<PreparableReloadListener> var1) {
      super();
      this.listenerCount = var1.size();
      this.preparingListeners = new HashSet(var1);
   }

   protected void startTasks(Executor var1, Executor var2, ResourceManager var3, List<PreparableReloadListener> var4, StateFactory<S> var5, CompletableFuture<?> var6) {
      this.allDone = this.prepareTasks(var1, var2, var3, var4, var5, var6);
   }

   protected CompletableFuture<List<S>> prepareTasks(Executor var1, Executor var2, ResourceManager var3, List<PreparableReloadListener> var4, StateFactory<S> var5, CompletableFuture<?> var6) {
      Executor var7 = (var2x) -> {
         this.startedTasks.incrementAndGet();
         var1.execute(() -> {
            var2x.run();
            this.finishedTasks.incrementAndGet();
         });
      };
      Executor var8 = (var2x) -> {
         this.startedReloads.incrementAndGet();
         var2.execute(() -> {
            var2x.run();
            this.finishedReloads.incrementAndGet();
         });
      };
      this.startedTasks.incrementAndGet();
      AtomicInteger var10001 = this.finishedTasks;
      Objects.requireNonNull(var10001);
      var6.thenRun(var10001::incrementAndGet);
      CompletableFuture var9 = var6;
      ArrayList var10 = new ArrayList();

      for(PreparableReloadListener var12 : var4) {
         PreparableReloadListener.PreparationBarrier var13 = this.createBarrierForListener(var12, var9, var2);
         CompletableFuture var14 = var5.create(var13, var3, var12, var7, var8);
         var10.add(var14);
         var9 = var14;
      }

      return Util.sequenceFailFast(var10);
   }

   private PreparableReloadListener.PreparationBarrier createBarrierForListener(final PreparableReloadListener var1, final CompletableFuture<?> var2, final Executor var3) {
      return new PreparableReloadListener.PreparationBarrier() {
         public <T> CompletableFuture<T> wait(T var1x) {
            var3.execute(() -> {
               SimpleReloadInstance.this.preparingListeners.remove(var1);
               if (SimpleReloadInstance.this.preparingListeners.isEmpty()) {
                  SimpleReloadInstance.this.allPreparations.complete(Unit.INSTANCE);
               }

            });
            return SimpleReloadInstance.this.allPreparations.thenCombine(var2, (var1xx, var2x) -> var1x);
         }
      };
   }

   public CompletableFuture<?> done() {
      return (CompletableFuture)Objects.requireNonNull(this.allDone, "not started");
   }

   public float getActualProgress() {
      int var1 = this.listenerCount - this.preparingListeners.size();
      float var2 = (float)weightProgress(this.finishedTasks.get(), this.finishedReloads.get(), var1);
      float var3 = (float)weightProgress(this.startedTasks.get(), this.startedReloads.get(), this.listenerCount);
      return var2 / var3;
   }

   private static int weightProgress(int var0, int var1, int var2) {
      return var0 * 2 + var1 * 2 + var2 * 1;
   }

   public static ReloadInstance create(ResourceManager var0, List<PreparableReloadListener> var1, Executor var2, Executor var3, CompletableFuture<Unit> var4, boolean var5) {
      return var5 ? ProfiledReloadInstance.of(var0, var1, var2, var3, var4) : of(var0, var1, var2, var3, var4);
   }

   @FunctionalInterface
   protected interface StateFactory<S> {
      StateFactory<Void> SIMPLE = (var0, var1, var2, var3, var4) -> var2.reload(var0, var1, var3, var4);

      CompletableFuture<S> create(PreparableReloadListener.PreparationBarrier var1, ResourceManager var2, PreparableReloadListener var3, Executor var4, Executor var5);
   }
}
