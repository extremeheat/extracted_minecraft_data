package net.minecraft.world.level.levelgen.feature;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.apache.commons.lang3.mutable.MutableInt;
import org.slf4j.Logger;

public class FeatureCountTracker {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final LoadingCache<ServerLevel, LevelData> data;

   public FeatureCountTracker() {
      super();
   }

   public static void chunkDecorated(final ServerLevel level) {
      try {
         ((LevelData)data.get(level)).chunksWithFeatures().increment();
      } catch (Exception e) {
         LOGGER.error("Failed to increment chunk count", e);
      }

   }

   public static void featurePlaced(final ServerLevel level, final Feature feature, final Optional<PlacedFeature> topFeature) {
      try {
         ((LevelData)data.get(level)).featureData().computeInt(new FeatureData(feature, topFeature), (f, old) -> old == null ? 1 : old + 1);
      } catch (Exception e) {
         LOGGER.error("Failed to increment feature count", e);
      }

   }

   public static void clearCounts() {
      data.invalidateAll();
      LOGGER.debug("Cleared feature counts");
   }

   public static void logCounts() {
      LOGGER.debug("Logging feature counts:");
      data.asMap().forEach((level, featureCounts) -> {
         String name = level.dimension().identifier().toString();
         boolean running = level.getServer().isRunning();
         Registry<PlacedFeature> featureRegistry = level.registryAccess().lookupOrThrow(Registries.PLACED_FEATURE);
         String prefix = (running ? "running" : "dead") + " " + name;
         int chunks = featureCounts.chunksWithFeatures().intValue();
         LOGGER.debug("{} total_chunks: {}", prefix, chunks);
         featureCounts.featureData().forEach((data, count) -> {
            Logger var10000 = LOGGER;
            Object[] var10002 = new Object[]{prefix, String.format(Locale.ROOT, "%10d", count), String.format(Locale.ROOT, "%10f", (double)count / (double)chunks), null, null};
            Optional var10005 = data.topFeature();
            Objects.requireNonNull(featureRegistry);
            var10002[3] = var10005.flatMap(featureRegistry::getResourceKey).map(ResourceKey::identifier);
            var10002[4] = data.feature();
            var10000.debug("{} {} {} {} {}", var10002);
         });
      });
   }

   static {
      data = CacheBuilder.newBuilder().weakKeys().expireAfterAccess(5L, TimeUnit.MINUTES).build(new CacheLoader<ServerLevel, LevelData>() {
         public LevelData load(final ServerLevel level) {
            return new LevelData(Object2IntMaps.synchronize(new Object2IntOpenHashMap()), new MutableInt(0));
         }
      });
   }

   private static record FeatureData(Feature feature, Optional<PlacedFeature> topFeature) {
      private FeatureData {
         super();
      }
   }

   private static record LevelData(Object2IntMap<FeatureData> featureData, MutableInt chunksWithFeatures) {
      private LevelData {
         super();
      }
   }
}
