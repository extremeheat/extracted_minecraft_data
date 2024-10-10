package net.minecraft.server.packs.resources;

import com.google.common.base.Stopwatch;
import com.mojang.logging.LogUtils;
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

public class ProfiledReloadInstance extends SimpleReloadInstance<ProfiledReloadInstance.State> {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Stopwatch total = Stopwatch.createUnstarted();

   public ProfiledReloadInstance(ResourceManager var1, List<PreparableReloadListener> var2, Executor var3, Executor var4, CompletableFuture<Unit> var5) {
      super(var3, var4, var1, var2, (var1x, var2x, var3x, var4x, var5x) -> {
         AtomicLong var6 = new AtomicLong();
         AtomicLong var7 = new AtomicLong();
         CompletableFuture var8 = var3x.reload(var1x, var2x, profiledExecutor(var4x, var6, var3x.getName()), profiledExecutor(var5x, var7, var3x.getName()));
         return var8.thenApplyAsync(var3xx -> {
            LOGGER.debug("Finished reloading {}", var3x.getName());
            return new ProfiledReloadInstance.State(var3x.getName(), var6, var7);
         }, var4);
      }, var5);
      this.total.start();
      this.allDone = this.allDone.thenApplyAsync(this::finish, var4);
   }

   private static Executor profiledExecutor(Executor var0, AtomicLong var1, String var2) {
      return var3 -> var0.execute(() -> {
            ProfilerFiller var3x = Profiler.get();
            var3x.push(var2);
            long var4 = Util.getNanos();
            var3.run();
            var1.addAndGet(Util.getNanos() - var4);
            var3x.pop();
         });
   }

   private List<ProfiledReloadInstance.State> finish(List<ProfiledReloadInstance.State> var1) {
      this.total.stop();
      long var2 = 0L;
      LOGGER.info("Resource reload finished after {} ms", this.total.elapsed(TimeUnit.MILLISECONDS));

      for (ProfiledReloadInstance.State var5 : var1) {
         long var6 = TimeUnit.NANOSECONDS.toMillis(var5.preparationNanos.get());
         long var8 = TimeUnit.NANOSECONDS.toMillis(var5.reloadNanos.get());
         long var10 = var6 + var8;
         String var12 = var5.name;
         LOGGER.info("{} took approximately {} ms ({} ms preparing, {} ms applying)", new Object[]{var12, var10, var6, var8});
         var2 += var8;
      }

      LOGGER.info("Total blocking time: {} ms", var2);
      return var1;
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "varName" is null
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
