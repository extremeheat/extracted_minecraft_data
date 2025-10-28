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

   public static void chunkDecorated(ServerLevel var0) {
      try {
         ((LevelData)data.get(var0)).chunksWithFeatures().increment();
      } catch (Exception var2) {
         LOGGER.error("Failed to increment chunk count", var2);
      }

   }

   public static void featurePlaced(ServerLevel var0, ConfiguredFeature<?, ?> var1, Optional<PlacedFeature> var2) {
      try {
         ((LevelData)data.get(var0)).featureData().computeInt(new FeatureData(var1, var2), (var0x, var1x) -> var1x == null ? 1 : var1x + 1);
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
      data.asMap().forEach((var0, var1) -> {
         String var2 = var0.dimension().location().toString();
         boolean var3 = var0.getServer().isRunning();
         Registry var4 = var0.registryAccess().lookupOrThrow(Registries.PLACED_FEATURE);
         String var5 = (var3 ? "running" : "dead") + " " + var2;
         int var6 = var1.chunksWithFeatures().intValue();
         LOGGER.debug("{} total_chunks: {}", var5, var6);
         var1.featureData().forEach((var3x, var4x) -> {
            Logger var10000 = LOGGER;
            Object[] var10002 = new Object[]{var5, String.format(Locale.ROOT, "%10d", var4x), String.format(Locale.ROOT, "%10f", (double)var4x / (double)var6), null, null, null};
            Optional var10005 = var3x.topFeature();
            Objects.requireNonNull(var4);
            var10002[3] = var10005.flatMap(var4::getResourceKey).map(ResourceKey::location);
            var10002[4] = var3x.feature().feature();
            var10002[5] = var3x.feature();
            var10000.debug("{} {} {} {} {} {}", var10002);
         });
      });
   }

   static {
      data = CacheBuilder.newBuilder().weakKeys().expireAfterAccess(5L, TimeUnit.MINUTES).build(new CacheLoader<ServerLevel, LevelData>() {
         public LevelData load(ServerLevel var1) {
            return new LevelData(Object2IntMaps.synchronize(new Object2IntOpenHashMap()), new MutableInt(0));
         }

         // $FF: synthetic method
         public Object load(final Object var1) throws Exception {
            return this.load((ServerLevel)var1);
         }
      });
   }

   static record FeatureData(ConfiguredFeature<?, ?> feature, Optional<PlacedFeature> topFeature) {
      FeatureData(ConfiguredFeature<?, ?> var1, Optional<PlacedFeature> var2) {
         super();
         this.feature = var1;
         this.topFeature = var2;
      }
   }

   static record LevelData(Object2IntMap<FeatureData> featureData, MutableInt chunksWithFeatures) {
      LevelData(Object2IntMap<FeatureData> var1, MutableInt var2) {
         super();
         this.featureData = var1;
         this.chunksWithFeatures = var2;
      }
   }
}
