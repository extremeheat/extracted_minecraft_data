package net.minecraft.util.profiling.metrics.storage;

import com.mojang.logging.LogUtils;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CsvOutput;
import net.minecraft.util.profiling.ProfileResults;
import net.minecraft.util.profiling.metrics.MetricCategory;
import net.minecraft.util.profiling.metrics.MetricSampler;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class MetricsPersister {
   public static final Path PROFILING_RESULTS_DIR = Paths.get("debug/profiling");
   public static final String METRICS_DIR_NAME = "metrics";
   public static final String DEVIATIONS_DIR_NAME = "deviations";
   public static final String PROFILING_RESULT_FILENAME = "profiling.txt";
   private static final Logger LOGGER = LogUtils.getLogger();
   private final String rootFolderName;

   public MetricsPersister(String var1) {
      super();
      this.rootFolderName = var1;
   }

   public Path saveReports(Set<MetricSampler> var1, Map<MetricSampler, List<RecordedDeviation>> var2, ProfileResults var3) {
      try {
         Files.createDirectories(PROFILING_RESULTS_DIR);
      } catch (IOException var8) {
         throw new UncheckedIOException(var8);
      }

      try {
         Path var4 = Files.createTempDirectory("minecraft-profiling");
         var4.toFile().deleteOnExit();
         Files.createDirectories(PROFILING_RESULTS_DIR);
         Path var5 = var4.resolve(this.rootFolderName);
         Path var6 = var5.resolve("metrics");
         this.saveMetrics(var1, var6);
         if (!var2.isEmpty()) {
            this.saveDeviations(var2, var5.resolve("deviations"));
         }

         this.saveProfilingTaskExecutionResult(var3, var5);
         return var4;
      } catch (IOException var7) {
         throw new UncheckedIOException(var7);
      }
   }

   private void saveMetrics(Set<MetricSampler> var1, Path var2) {
      if (var1.isEmpty()) {
         throw new IllegalArgumentException("Expected at least one sampler to persist");
      } else {
         Map var3 = (Map)var1.stream().collect(Collectors.groupingBy(MetricSampler::getCategory));
         var3.forEach((var2x, var3x) -> {
            this.saveCategory(var2x, var3x, var2);
         });
      }
   }

   private void saveCategory(MetricCategory var1, List<MetricSampler> var2, Path var3) {
      String var10001 = var1.getDescription();
      Path var4 = var3.resolve(Util.sanitizeName(var10001, ResourceLocation::validPathChar) + ".csv");
      BufferedWriter var5 = null;

      try {
         Files.createDirectories(var4.getParent());
         var5 = Files.newBufferedWriter(var4, StandardCharsets.UTF_8);
         CsvOutput.Builder var6 = CsvOutput.builder();
         var6.addColumn("@tick");
         Iterator var7 = var2.iterator();

         while(var7.hasNext()) {
            MetricSampler var8 = (MetricSampler)var7.next();
            var6.addColumn(var8.getName());
         }

         CsvOutput var20 = var6.build(var5);
         List var21 = (List)var2.stream().map(MetricSampler::result).collect(Collectors.toList());
         int var9 = var21.stream().mapToInt(MetricSampler.SamplerResult::getFirstTick).summaryStatistics().getMin();
         int var10 = var21.stream().mapToInt(MetricSampler.SamplerResult::getLastTick).summaryStatistics().getMax();

         for(int var11 = var9; var11 <= var10; ++var11) {
            Stream var13 = var21.stream().map((var1x) -> {
               return String.valueOf(var1x.valueAtTick(var11));
            });
            Object[] var14 = Stream.concat(Stream.of(String.valueOf(var11)), var13).toArray((var0) -> {
               return new String[var0];
            });
            var20.writeRow(var14);
         }

         LOGGER.info("Flushed metrics to {}", var4);
      } catch (Exception var18) {
         LOGGER.error("Could not save profiler results to {}", var4, var18);
      } finally {
         IOUtils.closeQuietly(var5);
      }

   }

   private void saveDeviations(Map<MetricSampler, List<RecordedDeviation>> var1, Path var2) {
      DateTimeFormatter var3 = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss.SSS", Locale.UK).withZone(ZoneId.systemDefault());
      var1.forEach((var2x, var3x) -> {
         var3x.forEach((var3xx) -> {
            String var4 = var3.format(var3xx.timestamp);
            Path var5 = var2.resolve(Util.sanitizeName(var2x.getName(), ResourceLocation::validPathChar)).resolve(String.format(Locale.ROOT, "%d@%s.txt", var3xx.tick, var4));
            var3xx.profilerResultAtTick.saveResults(var5);
         });
      });
   }

   private void saveProfilingTaskExecutionResult(ProfileResults var1, Path var2) {
      var1.saveResults(var2.resolve("profiling.txt"));
   }
}
