package net.minecraft.world.level.levelgen.feature;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.apache.commons.lang3.mutable.MutableInt;
import org.slf4j.Logger;

public class FeatureCountTracker {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final LoadingCache<ServerLevel, FeatureCountTracker.LevelData> data = CacheBuilder.newBuilder()
      .weakKeys()
      .expireAfterAccess(5L, TimeUnit.MINUTES)
      .build(new CacheLoader<ServerLevel, FeatureCountTracker.LevelData>() {
         public FeatureCountTracker.LevelData load(ServerLevel var1) {
            return new FeatureCountTracker.LevelData(Object2IntMaps.synchronize(new Object2IntOpenHashMap()), new MutableInt(0));
         }
      });

   public FeatureCountTracker() {
      super();
   }

   public static void chunkDecorated(ServerLevel var0) {
      try {
         ((FeatureCountTracker.LevelData)data.get(var0)).chunksWithFeatures().increment();
      } catch (Exception var2) {
         LOGGER.error("Failed to increment chunk count", var2);
      }
   }

   public static void featurePlaced(ServerLevel var0, ConfiguredFeature<?, ?> var1, Optional<PlacedFeature> var2) {
      try {
         ((FeatureCountTracker.LevelData)data.get(var0))
            .featureData()
            .computeInt(new FeatureCountTracker.FeatureData(var1, var2), (var0x, var1x) -> var1x == null ? 1 : var1x + 1);
      } catch (Exception var4) {
         LOGGER.error("Failed to increment feature count", var4);
      }
   }

   public static void clearCounts() {
      data.invalidateAll();
      LOGGER.debug("Cleared feature counts");
   }

   public static void logCounts() {
      LOGGER.debug("Logging feature counts:");
      data.asMap()
         .forEach(
            (var0, var1) -> {
               String var2 = var0.dimension().location().toString();
               boolean var3 = var0.getServer().isRunning();
               Registry var4 = var0.registryAccess().lookupOrThrow(Registries.PLACED_FEATURE);
               String var5 = (var3 ? "running" : "dead") + " " + var2;
               Integer var6 = var1.chunksWithFeatures().getValue();
               LOGGER.debug(var5 + " total_chunks: " + var6);
               var1.featureData()
                  .forEach(
                     (var3x, var4x) -> LOGGER.debug(
                           var5
                              + " "
                              + String.format(Locale.ROOT, "%10d ", var4x)
                              + String.format(Locale.ROOT, "%10f ", (double)var4x.intValue() / (double)var6.intValue())
                              + var3x.topFeature().flatMap(var4::getResourceKey).<ResourceLocation>map(ResourceKey::location)
                              + " "
                              + var3x.feature().feature()
                              + " "
                              + var3x.feature()
                        )
                  );
            }
         );
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
