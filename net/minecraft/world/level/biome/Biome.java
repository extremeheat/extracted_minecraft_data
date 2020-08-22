package net.minecraft.world.level.biome;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.IdMapper;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.util.WeighedRandom;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderConfiguration;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Biome {
   public static final Logger LOGGER = LogManager.getLogger();
   public static final Set EXPLORABLE_BIOMES = Sets.newHashSet();
   public static final IdMapper MUTATED_BIOMES = new IdMapper();
   protected static final PerlinSimplexNoise TEMPERATURE_NOISE = new PerlinSimplexNoise(new WorldgenRandom(1234L), 0, 0);
   public static final PerlinSimplexNoise BIOME_INFO_NOISE = new PerlinSimplexNoise(new WorldgenRandom(2345L), 0, 0);
   @Nullable
   protected String descriptionId;
   protected final float depth;
   protected final float scale;
   protected final float temperature;
   protected final float downfall;
   protected final int waterColor;
   protected final int waterFogColor;
   private final int skyColor;
   @Nullable
   protected final String parent;
   protected final ConfiguredSurfaceBuilder surfaceBuilder;
   protected final Biome.BiomeCategory biomeCategory;
   protected final Biome.Precipitation precipitation;
   protected final Map carvers = Maps.newHashMap();
   protected final Map features = Maps.newHashMap();
   protected final List flowerFeatures = Lists.newArrayList();
   protected final Map validFeatureStarts = Maps.newHashMap();
   private final Map spawners = Maps.newHashMap();
   private final ThreadLocal temperatureCache = ThreadLocal.withInitial(() -> {
      return (Long2FloatLinkedOpenHashMap)Util.make(() -> {
         Long2FloatLinkedOpenHashMap var1 = new Long2FloatLinkedOpenHashMap(1024, 0.25F) {
            protected void rehash(int var1) {
            }
         };
         var1.defaultReturnValue(Float.NaN);
         return var1;
      });
   });

   @Nullable
   public static Biome getMutatedVariant(Biome var0) {
      return (Biome)MUTATED_BIOMES.byId(Registry.BIOME.getId(var0));
   }

   public static ConfiguredWorldCarver makeCarver(WorldCarver var0, CarverConfiguration var1) {
      return new ConfiguredWorldCarver(var0, var1);
   }

   protected Biome(Biome.BiomeBuilder var1) {
      if (var1.surfaceBuilder != null && var1.precipitation != null && var1.biomeCategory != null && var1.depth != null && var1.scale != null && var1.temperature != null && var1.downfall != null && var1.waterColor != null && var1.waterFogColor != null) {
         this.surfaceBuilder = var1.surfaceBuilder;
         this.precipitation = var1.precipitation;
         this.biomeCategory = var1.biomeCategory;
         this.depth = var1.depth;
         this.scale = var1.scale;
         this.temperature = var1.temperature;
         this.downfall = var1.downfall;
         this.waterColor = var1.waterColor;
         this.waterFogColor = var1.waterFogColor;
         this.skyColor = this.calculateSkyColor();
         this.parent = var1.parent;
         GenerationStep.Decoration[] var2 = GenerationStep.Decoration.values();
         int var3 = var2.length;

         int var4;
         for(var4 = 0; var4 < var3; ++var4) {
            GenerationStep.Decoration var5 = var2[var4];
            this.features.put(var5, Lists.newArrayList());
         }

         MobCategory[] var6 = MobCategory.values();
         var3 = var6.length;

         for(var4 = 0; var4 < var3; ++var4) {
            MobCategory var7 = var6[var4];
            this.spawners.put(var7, Lists.newArrayList());
         }

      } else {
         throw new IllegalStateException("You are missing parameters to build a proper biome for " + this.getClass().getSimpleName() + "\n" + var1);
      }
   }

   public boolean isMutated() {
      return this.parent != null;
   }

   private int calculateSkyColor() {
      float var1 = this.temperature;
      var1 /= 3.0F;
      var1 = Mth.clamp(var1, -1.0F, 1.0F);
      return Mth.hsvToRgb(0.62222224F - var1 * 0.05F, 0.5F + var1 * 0.1F, 1.0F);
   }

   public int getSkyColor() {
      return this.skyColor;
   }

   protected void addSpawn(MobCategory var1, Biome.SpawnerData var2) {
      ((List)this.spawners.get(var1)).add(var2);
   }

   public List getMobs(MobCategory var1) {
      return (List)this.spawners.get(var1);
   }

   public Biome.Precipitation getPrecipitation() {
      return this.precipitation;
   }

   public boolean isHumid() {
      return this.getDownfall() > 0.85F;
   }

   public float getCreatureProbability() {
      return 0.1F;
   }

   protected float getTemperatureNoCache(BlockPos var1) {
      if (var1.getY() > 64) {
         float var2 = (float)(TEMPERATURE_NOISE.getValue((double)((float)var1.getX() / 8.0F), (double)((float)var1.getZ() / 8.0F), false) * 4.0D);
         return this.getTemperature() - (var2 + (float)var1.getY() - 64.0F) * 0.05F / 30.0F;
      } else {
         return this.getTemperature();
      }
   }

   public final float getTemperature(BlockPos var1) {
      long var2 = var1.asLong();
      Long2FloatLinkedOpenHashMap var4 = (Long2FloatLinkedOpenHashMap)this.temperatureCache.get();
      float var5 = var4.get(var2);
      if (!Float.isNaN(var5)) {
         return var5;
      } else {
         float var6 = this.getTemperatureNoCache(var1);
         if (var4.size() == 1024) {
            var4.removeFirstFloat();
         }

         var4.put(var2, var6);
         return var6;
      }
   }

   public boolean shouldFreeze(LevelReader var1, BlockPos var2) {
      return this.shouldFreeze(var1, var2, true);
   }

   public boolean shouldFreeze(LevelReader var1, BlockPos var2, boolean var3) {
      if (this.getTemperature(var2) >= 0.15F) {
         return false;
      } else {
         if (var2.getY() >= 0 && var2.getY() < 256 && var1.getBrightness(LightLayer.BLOCK, var2) < 10) {
            BlockState var4 = var1.getBlockState(var2);
            FluidState var5 = var1.getFluidState(var2);
            if (var5.getType() == Fluids.WATER && var4.getBlock() instanceof LiquidBlock) {
               if (!var3) {
                  return true;
               }

               boolean var6 = var1.isWaterAt(var2.west()) && var1.isWaterAt(var2.east()) && var1.isWaterAt(var2.north()) && var1.isWaterAt(var2.south());
               if (!var6) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public boolean shouldSnow(LevelReader var1, BlockPos var2) {
      if (this.getTemperature(var2) >= 0.15F) {
         return false;
      } else {
         if (var2.getY() >= 0 && var2.getY() < 256 && var1.getBrightness(LightLayer.BLOCK, var2) < 10) {
            BlockState var3 = var1.getBlockState(var2);
            if (var3.isAir() && Blocks.SNOW.defaultBlockState().canSurvive(var1, var2)) {
               return true;
            }
         }

         return false;
      }
   }

   public void addFeature(GenerationStep.Decoration var1, ConfiguredFeature var2) {
      if (var2.feature == Feature.DECORATED_FLOWER) {
         this.flowerFeatures.add(var2);
      }

      ((List)this.features.get(var1)).add(var2);
   }

   public void addCarver(GenerationStep.Carving var1, ConfiguredWorldCarver var2) {
      ((List)this.carvers.computeIfAbsent(var1, (var0) -> {
         return Lists.newArrayList();
      })).add(var2);
   }

   public List getCarvers(GenerationStep.Carving var1) {
      return (List)this.carvers.computeIfAbsent(var1, (var0) -> {
         return Lists.newArrayList();
      });
   }

   public void addStructureStart(ConfiguredFeature var1) {
      this.validFeatureStarts.put(var1.feature, var1.config);
   }

   public boolean isValidStart(StructureFeature var1) {
      return this.validFeatureStarts.containsKey(var1);
   }

   @Nullable
   public FeatureConfiguration getStructureConfiguration(StructureFeature var1) {
      return (FeatureConfiguration)this.validFeatureStarts.get(var1);
   }

   public List getFlowerFeatures() {
      return this.flowerFeatures;
   }

   public List getFeaturesForStep(GenerationStep.Decoration var1) {
      return (List)this.features.get(var1);
   }

   public void generate(GenerationStep.Decoration var1, ChunkGenerator var2, LevelAccessor var3, long var4, WorldgenRandom var6, BlockPos var7) {
      int var8 = 0;

      for(Iterator var9 = ((List)this.features.get(var1)).iterator(); var9.hasNext(); ++var8) {
         ConfiguredFeature var10 = (ConfiguredFeature)var9.next();
         var6.setFeatureSeed(var4, var8, var1.ordinal());

         try {
            var10.place(var3, var2, var6, var7);
         } catch (Exception var13) {
            CrashReport var12 = CrashReport.forThrowable(var13, "Feature placement");
            var12.addCategory("Feature").setDetail("Id", (Object)Registry.FEATURE.getKey(var10.feature)).setDetail("Description", () -> {
               return var10.feature.toString();
            });
            throw new ReportedException(var12);
         }
      }

   }

   public int getGrassColor(double var1, double var3) {
      double var5 = (double)Mth.clamp(this.getTemperature(), 0.0F, 1.0F);
      double var7 = (double)Mth.clamp(this.getDownfall(), 0.0F, 1.0F);
      return GrassColor.get(var5, var7);
   }

   public int getFoliageColor() {
      double var1 = (double)Mth.clamp(this.getTemperature(), 0.0F, 1.0F);
      double var3 = (double)Mth.clamp(this.getDownfall(), 0.0F, 1.0F);
      return FoliageColor.get(var1, var3);
   }

   public void buildSurfaceAt(Random var1, ChunkAccess var2, int var3, int var4, int var5, double var6, BlockState var8, BlockState var9, int var10, long var11) {
      this.surfaceBuilder.initNoise(var11);
      this.surfaceBuilder.apply(var1, var2, this, var3, var4, var5, var6, var8, var9, var10, var11);
   }

   public Biome.BiomeTempCategory getTemperatureCategory() {
      if (this.biomeCategory == Biome.BiomeCategory.OCEAN) {
         return Biome.BiomeTempCategory.OCEAN;
      } else if ((double)this.getTemperature() < 0.2D) {
         return Biome.BiomeTempCategory.COLD;
      } else {
         return (double)this.getTemperature() < 1.0D ? Biome.BiomeTempCategory.MEDIUM : Biome.BiomeTempCategory.WARM;
      }
   }

   public final float getDepth() {
      return this.depth;
   }

   public final float getDownfall() {
      return this.downfall;
   }

   public Component getName() {
      return new TranslatableComponent(this.getDescriptionId(), new Object[0]);
   }

   public String getDescriptionId() {
      if (this.descriptionId == null) {
         this.descriptionId = Util.makeDescriptionId("biome", Registry.BIOME.getKey(this));
      }

      return this.descriptionId;
   }

   public final float getScale() {
      return this.scale;
   }

   public final float getTemperature() {
      return this.temperature;
   }

   public final int getWaterColor() {
      return this.waterColor;
   }

   public final int getWaterFogColor() {
      return this.waterFogColor;
   }

   public final Biome.BiomeCategory getBiomeCategory() {
      return this.biomeCategory;
   }

   public ConfiguredSurfaceBuilder getSurfaceBuilder() {
      return this.surfaceBuilder;
   }

   public SurfaceBuilderConfiguration getSurfaceBuilderConfig() {
      return this.surfaceBuilder.getSurfaceBuilderConfiguration();
   }

   @Nullable
   public String getParent() {
      return this.parent;
   }

   public static class BiomeBuilder {
      @Nullable
      private ConfiguredSurfaceBuilder surfaceBuilder;
      @Nullable
      private Biome.Precipitation precipitation;
      @Nullable
      private Biome.BiomeCategory biomeCategory;
      @Nullable
      private Float depth;
      @Nullable
      private Float scale;
      @Nullable
      private Float temperature;
      @Nullable
      private Float downfall;
      @Nullable
      private Integer waterColor;
      @Nullable
      private Integer waterFogColor;
      @Nullable
      private String parent;

      public Biome.BiomeBuilder surfaceBuilder(SurfaceBuilder var1, SurfaceBuilderConfiguration var2) {
         this.surfaceBuilder = new ConfiguredSurfaceBuilder(var1, var2);
         return this;
      }

      public Biome.BiomeBuilder surfaceBuilder(ConfiguredSurfaceBuilder var1) {
         this.surfaceBuilder = var1;
         return this;
      }

      public Biome.BiomeBuilder precipitation(Biome.Precipitation var1) {
         this.precipitation = var1;
         return this;
      }

      public Biome.BiomeBuilder biomeCategory(Biome.BiomeCategory var1) {
         this.biomeCategory = var1;
         return this;
      }

      public Biome.BiomeBuilder depth(float var1) {
         this.depth = var1;
         return this;
      }

      public Biome.BiomeBuilder scale(float var1) {
         this.scale = var1;
         return this;
      }

      public Biome.BiomeBuilder temperature(float var1) {
         this.temperature = var1;
         return this;
      }

      public Biome.BiomeBuilder downfall(float var1) {
         this.downfall = var1;
         return this;
      }

      public Biome.BiomeBuilder waterColor(int var1) {
         this.waterColor = var1;
         return this;
      }

      public Biome.BiomeBuilder waterFogColor(int var1) {
         this.waterFogColor = var1;
         return this;
      }

      public Biome.BiomeBuilder parent(@Nullable String var1) {
         this.parent = var1;
         return this;
      }

      public String toString() {
         return "BiomeBuilder{\nsurfaceBuilder=" + this.surfaceBuilder + ",\nprecipitation=" + this.precipitation + ",\nbiomeCategory=" + this.biomeCategory + ",\ndepth=" + this.depth + ",\nscale=" + this.scale + ",\ntemperature=" + this.temperature + ",\ndownfall=" + this.downfall + ",\nwaterColor=" + this.waterColor + ",\nwaterFogColor=" + this.waterFogColor + ",\nparent='" + this.parent + '\'' + "\n" + '}';
      }
   }

   public static class SpawnerData extends WeighedRandom.WeighedRandomItem {
      public final EntityType type;
      public final int minCount;
      public final int maxCount;

      public SpawnerData(EntityType var1, int var2, int var3, int var4) {
         super(var2);
         this.type = var1;
         this.minCount = var3;
         this.maxCount = var4;
      }

      public String toString() {
         return EntityType.getKey(this.type) + "*(" + this.minCount + "-" + this.maxCount + "):" + this.weight;
      }
   }

   public static enum Precipitation {
      NONE("none"),
      RAIN("rain"),
      SNOW("snow");

      private static final Map BY_NAME = (Map)Arrays.stream(values()).collect(Collectors.toMap(Biome.Precipitation::getName, (var0) -> {
         return var0;
      }));
      private final String name;

      private Precipitation(String var3) {
         this.name = var3;
      }

      public String getName() {
         return this.name;
      }
   }

   public static enum BiomeCategory {
      NONE("none"),
      TAIGA("taiga"),
      EXTREME_HILLS("extreme_hills"),
      JUNGLE("jungle"),
      MESA("mesa"),
      PLAINS("plains"),
      SAVANNA("savanna"),
      ICY("icy"),
      THEEND("the_end"),
      BEACH("beach"),
      FOREST("forest"),
      OCEAN("ocean"),
      DESERT("desert"),
      RIVER("river"),
      SWAMP("swamp"),
      MUSHROOM("mushroom"),
      NETHER("nether");

      private static final Map BY_NAME = (Map)Arrays.stream(values()).collect(Collectors.toMap(Biome.BiomeCategory::getName, (var0) -> {
         return var0;
      }));
      private final String name;

      private BiomeCategory(String var3) {
         this.name = var3;
      }

      public String getName() {
         return this.name;
      }
   }

   public static enum BiomeTempCategory {
      OCEAN("ocean"),
      COLD("cold"),
      MEDIUM("medium"),
      WARM("warm");

      private static final Map BY_NAME = (Map)Arrays.stream(values()).collect(Collectors.toMap(Biome.BiomeTempCategory::getName, (var0) -> {
         return var0;
      }));
      private final String name;

      private BiomeTempCategory(String var3) {
         this.name = var3;
      }

      public String getName() {
         return this.name;
      }
   }
}
