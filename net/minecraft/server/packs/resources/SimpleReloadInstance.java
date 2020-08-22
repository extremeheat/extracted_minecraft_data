package net.minecraft.server.packs.resources;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.Util;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.InactiveProfiler;

public class SimpleReloadInstance implements ReloadInstance {
   protected final ResourceManager resourceManager;
   protected final CompletableFuture allPreparations = new CompletableFuture();
   protected final CompletableFuture allDone;
   private final Set preparingListeners;
   private final int listenerCount;
   private int startedReloads;
   private int finishedReloads;
   private final AtomicInteger startedTaskCounter = new AtomicInteger();
   private final AtomicInteger doneTaskCounter = new AtomicInteger();

   public static SimpleReloadInstance of(ResourceManager var0, List var1, Executor var2, Executor var3, CompletableFuture var4) {
      return new SimpleReloadInstance(var2, var3, var0, var1, (var1x, var2x, var3x, var4x, var5) -> {
         return var3x.reload(var1x, var2x, InactiveProfiler.INACTIVE, InactiveProfiler.INACTIVE, var2, var5);
      }, var4);
   }

   protected SimpleReloadInstance(Executor var1, final Executor var2, ResourceManager var3, List var4, SimpleReloadInstance.StateFactory var5, CompletableFuture var6) {
      this.resourceManager = var3;
      this.listenerCount = var4.size();
      this.startedTaskCounter.incrementAndGet();
      AtomicInteger var10001 = this.doneTaskCounter;
      var6.thenRun(var10001::incrementAndGet);
      ArrayList var7 = Lists.newArrayList();
      final CompletableFuture var8 = var6;
      this.preparingListeners = Sets.newHashSet(var4);

      CompletableFuture var12;
      for(Iterator var9 = var4.iterator(); var9.hasNext(); var8 = var12) {
         final PreparableReloadListener var10 = (PreparableReloadListener)var9.next();
         var12 = var5.create(new PreparableReloadListener.PreparationBarrier() {
            public CompletableFuture wait(Object var1) {
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

      this.allDone = Util.sequence(var7);
   }

   public CompletableFuture done() {
      return this.allDone.thenApply((var0) -> {
         return Unit.INSTANCE;
      });
   }

   public float getActualProgress() {
      int var1 = this.listenerCount - this.preparingListeners.size();
      float var2 = (float)(this.doneTaskCounter.get() * 2 + this.finishedReloads * 2 + var1 * 1);
      float var3 = (float)(this.startedTaskCounter.get() * 2 + this.startedReloads * 2 + this.listenerCount * 1);
      return var2 / var3;
   }

   public boolean isApplying() {
      return this.allPreparations.isDone();
   }

   public boolean isDone() {
      return this.allDone.isDone();
   }

   public void checkExceptions() {
      if (this.allDone.isCompletedExceptionally()) {
         this.allDone.join();
      }

   }

   public interface StateFactory {
      CompletableFuture create(PreparableReloadListener.PreparationBarrier var1, ResourceManager var2, PreparableReloadListener var3, Executor var4, Executor var5);
   }
}
