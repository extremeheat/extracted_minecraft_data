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
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

public class ProfiledReloadInstance extends SimpleReloadInstance<State> {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Stopwatch total = Stopwatch.createUnstarted();

   public ProfiledReloadInstance(ResourceManager var1, List<PreparableReloadListener> var2, Executor var3, Executor var4, CompletableFuture<Unit> var5) {
      super(var3, var4, var1, var2, (var1x, var2x, var3x, var4x, var5x) -> {
         AtomicLong var6 = new AtomicLong();
         AtomicLong var7 = new AtomicLong();
         CompletableFuture var8 = var3x.reload(var1x, var2x, profiledExecutor(var4x, var6, var3x.getName()), profiledExecutor(var5x, var7, var3x.getName()));
         return var8.thenApplyAsync((var3) -> {
            LOGGER.debug("Finished reloading {}", var3x.getName());
            return new State(var3x.getName(), var6, var7);
         }, var4);
      }, var5);
      this.total.start();
      this.allDone = this.allDone.thenApplyAsync(this::finish, var4);
   }

   private static Executor profiledExecutor(Executor var0, AtomicLong var1, String var2) {
      return (var3) -> {
         var0.execute(() -> {
            ProfilerFiller var3x = Profiler.get();
            var3x.push(var2);
            long var4 = Util.getNanos();
            var3.run();
            var1.addAndGet(Util.getNanos() - var4);
            var3x.pop();
         });
      };
   }

   private List<State> finish(List<State> var1) {
      this.total.stop();
      long var2 = 0L;
      LOGGER.info("Resource reload finished after {} ms", this.total.elapsed(TimeUnit.MILLISECONDS));

      long var8;
      for(Iterator var4 = var1.iterator(); var4.hasNext(); var2 += var8) {
         State var5 = (State)var4.next();
         long var6 = TimeUnit.NANOSECONDS.toMillis(var5.preparationNanos.get());
         var8 = TimeUnit.NANOSECONDS.toMillis(var5.reloadNanos.get());
         long var10 = var6 + var8;
         String var12 = var5.name;
         LOGGER.info("{} took approximately {} ms ({} ms preparing, {} ms applying)", new Object[]{var12, var10, var6, var8});
      }

      LOGGER.info("Total blocking time: {} ms", var2);
      return var1;
   }

   public static record State(String name, AtomicLong preparationNanos, AtomicLong reloadNanos) {
      final String name;
      final AtomicLong preparationNanos;
      final AtomicLong reloadNanos;

      public State(String var1, AtomicLong var2, AtomicLong var3) {
         super();
         this.name = var1;
         this.preparationNanos = var2;
         this.reloadNanos = var3;
      }

      public String name() {
         return this.name;
      }

      public AtomicLong preparationNanos() {
         return this.preparationNanos;
      }

      public AtomicLong reloadNanos() {
         return this.reloadNanos;
      }
   }
}
