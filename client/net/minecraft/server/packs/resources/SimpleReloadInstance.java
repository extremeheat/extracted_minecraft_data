package net.minecraft.server.packs.resources;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.Util;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.InactiveProfiler;

public class SimpleReloadInstance<S> implements ReloadInstance {
   private static final int PREPARATION_PROGRESS_WEIGHT = 2;
   private static final int EXTRA_RELOAD_PROGRESS_WEIGHT = 2;
   private static final int LISTENER_PROGRESS_WEIGHT = 1;
   protected final CompletableFuture<Unit> allPreparations = new CompletableFuture();
   protected CompletableFuture<List<S>> allDone;
   final Set<PreparableReloadListener> preparingListeners;
   private final int listenerCount;
   private int startedReloads;
   private int finishedReloads;
   private final AtomicInteger startedTaskCounter = new AtomicInteger();
   private final AtomicInteger doneTaskCounter = new AtomicInteger();

   public static SimpleReloadInstance<Void> of(ResourceManager var0, List<PreparableReloadListener> var1, Executor var2, Executor var3, CompletableFuture<Unit> var4) {
      return new SimpleReloadInstance(var2, var3, var0, var1, (var1x, var2x, var3x, var4x, var5) -> {
         return var3x.reload(var1x, var2x, InactiveProfiler.INSTANCE, InactiveProfiler.INSTANCE, var2, var5);
      }, var4);
   }

   protected SimpleReloadInstance(Executor var1, final Executor var2, ResourceManager var3, List<PreparableReloadListener> var4, StateFactory<S> var5, CompletableFuture<Unit> var6) {
      super();
      this.listenerCount = var4.size();
      this.startedTaskCounter.incrementAndGet();
      AtomicInteger var10001 = this.doneTaskCounter;
      Objects.requireNonNull(var10001);
      var6.thenRun(var10001::incrementAndGet);
      ArrayList var7 = Lists.newArrayList();
      final CompletableFuture var8 = var6;
      this.preparingListeners = Sets.newHashSet(var4);

      CompletableFuture var12;
      for(Iterator var9 = var4.iterator(); var9.hasNext(); var8 = var12) {
         final PreparableReloadListener var10 = (PreparableReloadListener)var9.next();
         var12 = var5.create(new PreparableReloadListener.PreparationBarrier() {
            public <T> CompletableFuture<T> wait(T var1) {
               var2.execute(() -> {
                  SimpleReloadInstance.this.preparingListeners.remove(var10);
                  if (SimpleReloadInstance.this.preparingListeners.isEmpty()) {
                     SimpleReloadInstance.this.allPreparations.complete(Unit.INSTANCE);
                  }

               });
               return SimpleReloadInstance.this.allPreparations.thenCombine(var8, (var1x, var2x) -> {
                  return var1;
               });
            }
         }, var3, var10, (var2x) -> {
            this.startedTaskCounter.incrementAndGet();
            var1.execute(() -> {
               var2x.run();
               this.doneTaskCounter.incrementAndGet();
            });
         }, (var2x) -> {
            ++this.startedReloads;
            var2.execute(() -> {
               var2x.run();
               ++this.finishedReloads;
            });
         });
         var7.add(var12);
      }

      this.allDone = Util.sequenceFailFast(var7);
   }

   public CompletableFuture<?> done() {
      return this.allDone;
   }

   public float getActualProgress() {
      int var1 = this.listenerCount - this.preparingListeners.size();
      float var2 = (float)(this.doneTaskCounter.get() * 2 + this.finishedReloads * 2 + var1 * 1);
      float var3 = (float)(this.startedTaskCounter.get() * 2 + this.startedReloads * 2 + this.listenerCount * 1);
      return var2 / var3;
   }

   public static ReloadInstance create(ResourceManager var0, List<PreparableReloadListener> var1, Executor var2, Executor var3, CompletableFuture<Unit> var4, boolean var5) {
      return (ReloadInstance)(var5 ? new ProfiledReloadInstance(var0, var1, var2, var3, var4) : of(var0, var1, var2, var3, var4));
   }

   protected interface StateFactory<S> {
      CompletableFuture<S> create(PreparableReloadListener.PreparationBarrier var1, ResourceManager var2, PreparableReloadListener var3, Executor var4, Executor var5);
   }
}
