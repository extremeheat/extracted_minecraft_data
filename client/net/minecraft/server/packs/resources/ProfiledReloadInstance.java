package net.minecraft.server.packs.resources;

import com.google.common.base.Stopwatch;
import com.mojang.logging.LogUtils;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.Util;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ActiveProfiler;
import net.minecraft.util.profiling.ProfileResults;
import org.slf4j.Logger;

public class ProfiledReloadInstance extends SimpleReloadInstance<State> {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Stopwatch total = Stopwatch.createUnstarted();

   public ProfiledReloadInstance(ResourceManager var1, List<PreparableReloadListener> var2, Executor var3, Executor var4, CompletableFuture<Unit> var5) {
      super(var3, var4, var1, var2, (var1x, var2x, var3x, var4x, var5x) -> {
         AtomicLong var6 = new AtomicLong();
         AtomicLong var7 = new AtomicLong();
         ActiveProfiler var8 = new ActiveProfiler(Util.timeSource, () -> {
            return 0;
         }, false);
         ActiveProfiler var9 = new ActiveProfiler(Util.timeSource, () -> {
            return 0;
         }, false);
         CompletableFuture var10 = var3x.reload(var1x, var2x, var8, var9, (var2) -> {
            var4x.execute(() -> {
               long var2x = Util.getNanos();
               var2.run();
               var6.addAndGet(Util.getNanos() - var2x);
            });
         }, (var2) -> {
            var5x.execute(() -> {
               long var2x = Util.getNanos();
               var2.run();
               var7.addAndGet(Util.getNanos() - var2x);
            });
         });
         return var10.thenApplyAsync((var5) -> {
            LOGGER.debug("Finished reloading " + var3x.getName());
            return new State(var3x.getName(), var8.getResults(), var9.getResults(), var6, var7);
         }, var4);
      }, var5);
      this.total.start();
      this.allDone = this.allDone.thenApplyAsync(this::finish, var4);
   }

   private List<State> finish(List<State> var1) {
      this.total.stop();
      int var2 = 0;
      LOGGER.info("Resource reload finished after {} ms", this.total.elapsed(TimeUnit.MILLISECONDS));

      int var8;
      for(Iterator var3 = var1.iterator(); var3.hasNext(); var2 += var8) {
         State var4 = (State)var3.next();
         ProfileResults var5 = var4.preparationResult;
         ProfileResults var6 = var4.reloadResult;
         int var7 = (int)((double)var4.preparationNanos.get() / 1000000.0);
         var8 = (int)((double)var4.reloadNanos.get() / 1000000.0);
         int var9 = var7 + var8;
         String var10 = var4.name;
         LOGGER.info("{} took approximately {} ms ({} ms preparing, {} ms applying)", new Object[]{var10, var9, var7, var8});
      }

      LOGGER.info("Total blocking time: {} ms", var2);
      return var1;
   }

   public static class State {
      final String name;
      final ProfileResults preparationResult;
      final ProfileResults reloadResult;
      final AtomicLong preparationNanos;
      final AtomicLong reloadNanos;

      State(String var1, ProfileResults var2, ProfileResults var3, AtomicLong var4, AtomicLong var5) {
         super();
         this.name = var1;
         this.preparationResult = var2;
         this.reloadResult = var3;
         this.preparationNanos = var4;
         this.reloadNanos = var5;
      }
   }
}
