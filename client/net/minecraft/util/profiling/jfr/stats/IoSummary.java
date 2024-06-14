package net.minecraft.util.profiling.jfr.stats;

import com.mojang.datafixers.util.Pair;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;

public final class IoSummary<T> {
   private final IoSummary.CountAndSize totalCountAndSize;
   private final List<Pair<T, IoSummary.CountAndSize>> largestSizeContributors;
   private final Duration recordingDuration;

   public IoSummary(Duration var1, List<Pair<T, IoSummary.CountAndSize>> var2) {
      super();
      this.recordingDuration = var1;
      this.totalCountAndSize = var2.stream()
         .<IoSummary.CountAndSize>map(Pair::getSecond)
         .reduce(new IoSummary.CountAndSize(0L, 0L), IoSummary.CountAndSize::add);
      this.largestSizeContributors = var2.stream().sorted(Comparator.comparing(Pair::getSecond, IoSummary.CountAndSize.SIZE_THEN_COUNT)).limit(10L).toList();
   }

   public double getCountsPerSecond() {
      return (double)this.totalCountAndSize.totalCount / (double)this.recordingDuration.getSeconds();
   }

   public double getSizePerSecond() {
      return (double)this.totalCountAndSize.totalSize / (double)this.recordingDuration.getSeconds();
   }

   public long getTotalCount() {
      return this.totalCountAndSize.totalCount;
   }

   public long getTotalSize() {
      return this.totalCountAndSize.totalSize;
   }

   public List<Pair<T, IoSummary.CountAndSize>> largestSizeContributors() {
      return this.largestSizeContributors;
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
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
