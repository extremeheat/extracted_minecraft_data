package net.minecraft.world.level.dimension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.File;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Supplier;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeZoomer;
import net.minecraft.world.level.biome.FuzzyOffsetBiomeZoomer;
import net.minecraft.world.level.biome.FuzzyOffsetConstantColumnBiomeZoomer;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.TheEndBiomeSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

public class DimensionType {
   public static final ResourceLocation OVERWORLD_EFFECTS = new ResourceLocation("overworld");
   public static final ResourceLocation NETHER_EFFECTS = new ResourceLocation("the_nether");
   public static final ResourceLocation END_EFFECTS = new ResourceLocation("the_end");
   public static final Codec<DimensionType> DIRECT_CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Codec.LONG.optionalFieldOf("fixed_time").xmap((var0x) -> {
         return (OptionalLong)var0x.map(OptionalLong::of).orElseGet(OptionalLong::empty);
      }, (var0x) -> {
         return var0x.isPresent() ? Optional.of(var0x.getAsLong()) : Optional.empty();
      }).forGetter((var0x) -> {
         return var0x.fixedTime;
      }), Codec.BOOL.fieldOf("has_skylight").forGetter(DimensionType::hasSkyLight), Codec.BOOL.fieldOf("has_ceiling").forGetter(DimensionType::hasCeiling), Codec.BOOL.fieldOf("ultrawarm").forGetter(DimensionType::ultraWarm), Codec.BOOL.fieldOf("natural").forGetter(DimensionType::natural), Codec.doubleRange(9.999999747378752E-6D, 3.0E7D).fieldOf("coordinate_scale").forGetter(DimensionType::coordinateScale), Codec.BOOL.fieldOf("piglin_safe").forGetter(DimensionType::piglinSafe), Codec.BOOL.fieldOf("bed_works").forGetter(DimensionType::bedWorks), Codec.BOOL.fieldOf("respawn_anchor_works").forGetter(DimensionType::respawnAnchorWorks), Codec.BOOL.fieldOf("has_raids").forGetter(DimensionType::hasRaids), Codec.intRange(0, 256).fieldOf("logical_height").forGetter(DimensionType::logicalHeight), ResourceLocation.CODEC.fieldOf("infiniburn").forGetter((var0x) -> {
         return var0x.infiniburn;
      }), ResourceLocation.CODEC.fieldOf("effects").orElse(OVERWORLD_EFFECTS).forGetter((var0x) -> {
         return var0x.effectsLocation;
      }), Codec.FLOAT.fieldOf("ambient_light").forGetter((var0x) -> {
         return var0x.ambientLight;
      })).apply(var0, DimensionType::new);
   });
   public static final float[] MOON_BRIGHTNESS_PER_PHASE = new float[]{1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};
   public static final ResourceKey<DimensionType> OVERWORLD_LOCATION;
   public static final ResourceKey<DimensionType> NETHER_LOCATION;
   public static final ResourceKey<DimensionType> END_LOCATION;
   protected static final DimensionType DEFAULT_OVERWORLD;
   protected static final DimensionType DEFAULT_NETHER;
   protected static final DimensionType DEFAULT_END;
   public static final ResourceKey<DimensionType> OVERWORLD_CAVES_LOCATION;
   protected static final DimensionType DEFAULT_OVERWORLD_CAVES;
   public static final Codec<Supplier<DimensionType>> CODEC;
   private final OptionalLong fixedTime;
   private final boolean hasSkylight;
   private final boolean hasCeiling;
   private final boolean ultraWarm;
   private final boolean natural;
   private final double coordinateScale;
   private final boolean createDragonFight;
   private final boolean piglinSafe;
   private final boolean bedWorks;
   private final boolean respawnAnchorWorks;
   private final boolean hasRaids;
   private final int logicalHeight;
   private final BiomeZoomer biomeZoomer;
   private final ResourceLocation infiniburn;
   private final ResourceLocation effectsLocation;
   private final float ambientLight;
   private final transient float[] brightnessRamp;

   protected DimensionType(OptionalLong var1, boolean var2, boolean var3, boolean var4, boolean var5, double var6, boolean var8, boolean var9, boolean var10, boolean var11, int var12, ResourceLocation var13, ResourceLocation var14, float var15) {
      this(var1, var2, var3, var4, var5, var6, false, var8, var9, var10, var11, var12, FuzzyOffsetBiomeZoomer.INSTANCE, var13, var14, var15);
   }

   protected DimensionType(OptionalLong var1, boolean var2, boolean var3, boolean var4, boolean var5, double var6, boolean var8, boolean var9, boolean var10, boolean var11, boolean var12, int var13, BiomeZoomer var14, ResourceLocation var15, ResourceLocation var16, float var17) {
      super();
      this.fixedTime = var1;
      this.hasSkylight = var2;
      this.hasCeiling = var3;
      this.ultraWarm = var4;
      this.natural = var5;
      this.coordinateScale = var6;
      this.createDragonFight = var8;
      this.piglinSafe = var9;
      this.bedWorks = var10;
      this.respawnAnchorWorks = var11;
      this.hasRaids = var12;
      this.logicalHeight = var13;
      this.biomeZoomer = var14;
      this.infiniburn = var15;
      this.effectsLocation = var16;
      this.ambientLight = var17;
      this.brightnessRamp = fillBrightnessRamp(var17);
   }

   private static float[] fillBrightnessRamp(float var0) {
      float[] var1 = new float[16];

      for(int var2 = 0; var2 <= 15; ++var2) {
         float var3 = (float)var2 / 15.0F;
         float var4 = var3 / (4.0F - 3.0F * var3);
         var1[var2] = Mth.lerp(var0, var4, 1.0F);
      }

      return var1;
   }

   @Deprecated
   public static DataResult<ResourceKey<Level>> parseLegacy(Dynamic<?> var0) {
      Optional var1 = var0.asNumber().result();
      if (var1.isPresent()) {
         int var2 = ((Number)var1.get()).intValue();
         if (var2 == -1) {
            return DataResult.success(Level.NETHER);
         }

         if (var2 == 0) {
            return DataResult.success(Level.OVERWORLD);
         }

         if (var2 == 1) {
            return DataResult.success(Level.END);
         }
      }

      return Level.RESOURCE_KEY_CODEC.parse(var0);
   }

   public static RegistryAccess.RegistryHolder registerBuiltin(RegistryAccess.RegistryHolder var0) {
      WritableRegistry var1 = var0.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
      var1.register(OVERWORLD_LOCATION, DEFAULT_OVERWORLD, Lifecycle.stable());
      var1.register(OVERWORLD_CAVES_LOCATION, DEFAULT_OVERWORLD_CAVES, Lifecycle.stable());
      var1.register(NETHER_LOCATION, DEFAULT_NETHER, Lifecycle.stable());
      var1.register(END_LOCATION, DEFAULT_END, Lifecycle.stable());
      return var0;
   }

   private static ChunkGenerator defaultEndGenerator(Registry<Biome> var0, Registry<NoiseGeneratorSettings> var1, long var2) {
      return new NoiseBasedChunkGenerator(new TheEndBiomeSource(var0, var2), var2, () -> {
         return (NoiseGeneratorSettings)var1.getOrThrow(NoiseGeneratorSettings.END);
      });
   }

   private static ChunkGenerator defaultNetherGenerator(Registry<Biome> var0, Registry<NoiseGeneratorSettings> var1, long var2) {
      return new NoiseBasedChunkGenerator(MultiNoiseBiomeSource.Preset.NETHER.biomeSource(var0, var2), var2, () -> {
         return (NoiseGeneratorSettings)var1.getOrThrow(NoiseGeneratorSettings.NETHER);
      });
   }

   public static MappedRegistry<LevelStem> defaultDimensions(Registry<DimensionType> var0, Registry<Biome> var1, Registry<NoiseGeneratorSettings> var2, long var3) {
      MappedRegistry var5 = new MappedRegistry(Registry.LEVEL_STEM_REGISTRY, Lifecycle.experimental());
      var5.register(LevelStem.NETHER, new LevelStem(() -> {
         return (DimensionType)var0.getOrThrow(NETHER_LOCATION);
      }, defaultNetherGenerator(var1, var2, var3)), Lifecycle.stable());
      var5.register(LevelStem.END, new LevelStem(() -> {
         return (DimensionType)var0.getOrThrow(END_LOCATION);
      }, defaultEndGenerator(var1, var2, var3)), Lifecycle.stable());
      return var5;
   }

   public static double getTeleportationScale(DimensionType var0, DimensionType var1) {
      double var2 = var0.coordinateScale();
      double var4 = var1.coordinateScale();
      return var2 / var4;
   }

   @Deprecated
   public String getFileSuffix() {
      return this.equalTo(DEFAULT_END) ? "_end" : "";
   }

   public static File getStorageFolder(ResourceKey<Level> var0, File var1) {
      if (var0 == Level.OVERWORLD) {
         return var1;
      } else if (var0 == Level.END) {
         return new File(var1, "DIM1");
      } else {
         return var0 == Level.NETHER ? new File(var1, "DIM-1") : new File(var1, "dimensions/" + var0.location().getNamespace() + "/" + var0.location().getPath());
      }
   }

   public boolean hasSkyLight() {
      return this.hasSkylight;
   }

   public boolean hasCeiling() {
      return this.hasCeiling;
   }

   public boolean ultraWarm() {
      return this.ultraWarm;
   }

   public boolean natural() {
      return this.natural;
   }

   public double coordinateScale() {
      return this.coordinateScale;
   }

   public boolean piglinSafe() {
      return this.piglinSafe;
   }

   public boolean bedWorks() {
      return this.bedWorks;
   }

   public boolean respawnAnchorWorks() {
      return this.respawnAnchorWorks;
   }

   public boolean hasRaids() {
      return this.hasRaids;
   }

   public int logicalHeight() {
      return this.logicalHeight;
   }

   public boolean createDragonFight() {
      return this.createDragonFight;
   }

   public BiomeZoomer getBiomeZoomer() {
      return this.biomeZoomer;
   }

   public boolean hasFixedTime() {
      return this.fixedTime.isPresent();
   }

   public float timeOfDay(long var1) {
      double var3 = Mth.frac((double)this.fixedTime.orElse(var1) / 24000.0D - 0.25D);
      double var5 = 0.5D - Math.cos(var3 * 3.141592653589793D) / 2.0D;
      return (float)(var3 * 2.0D + var5) / 3.0F;
   }

   public int moonPhase(long var1) {
      return (int)(var1 / 24000L % 8L + 8L) % 8;
   }

   public float brightness(int var1) {
      return this.brightnessRamp[var1];
   }

   public Tag<Block> infiniburn() {
      Tag var1 = BlockTags.getAllTags().getTag(this.infiniburn);
      return (Tag)(var1 != null ? var1 : BlockTags.INFINIBURN_OVERWORLD);
   }

   public ResourceLocation effectsLocation() {
      return this.effectsLocation;
   }

   public boolean equalTo(DimensionType var1) {
      if (this == var1) {
         return true;
      } else {
         return this.hasSkylight == var1.hasSkylight && this.hasCeiling == var1.hasCeiling && this.ultraWarm == var1.ultraWarm && this.natural == var1.natural && this.coordinateScale == var1.coordinateScale && this.createDragonFight == var1.createDragonFight && this.piglinSafe == var1.piglinSafe && this.bedWorks == var1.bedWorks && this.respawnAnchorWorks == var1.respawnAnchorWorks && this.hasRaids == var1.hasRaids && this.logicalHeight == var1.logicalHeight && Float.compare(var1.ambientLight, this.ambientLight) == 0 && this.fixedTime.equals(var1.fixedTime) && this.biomeZoomer.equals(var1.biomeZoomer) && this.infiniburn.equals(var1.infiniburn) && this.effectsLocation.equals(var1.effectsLocation);
      }
   }

   static {
      OVERWORLD_LOCATION = ResourceKey.create(Registry.DIMENSION_TYPE_REGISTRY, new ResourceLocation("overworld"));
      NETHER_LOCATION = ResourceKey.create(Registry.DIMENSION_TYPE_REGISTRY, new ResourceLocation("the_nether"));
      END_LOCATION = ResourceKey.create(Registry.DIMENSION_TYPE_REGISTRY, new ResourceLocation("the_end"));
      DEFAULT_OVERWORLD = new DimensionType(OptionalLong.empty(), true, false, false, true, 1.0D, false, false, true, false, true, 256, FuzzyOffsetConstantColumnBiomeZoomer.INSTANCE, BlockTags.INFINIBURN_OVERWORLD.getName(), OVERWORLD_EFFECTS, 0.0F);
      DEFAULT_NETHER = new DimensionType(OptionalLong.of(18000L), false, true, true, false, 8.0D, false, true, false, true, false, 128, FuzzyOffsetBiomeZoomer.INSTANCE, BlockTags.INFINIBURN_NETHER.getName(), NETHER_EFFECTS, 0.1F);
      DEFAULT_END = new DimensionType(OptionalLong.of(6000L), false, false, false, false, 1.0D, true, false, false, false, true, 256, FuzzyOffsetBiomeZoomer.INSTANCE, BlockTags.INFINIBURN_END.getName(), END_EFFECTS, 0.0F);
      OVERWORLD_CAVES_LOCATION = ResourceKey.create(Registry.DIMENSION_TYPE_REGISTRY, new ResourceLocation("overworld_caves"));
      DEFAULT_OVERWORLD_CAVES = new DimensionType(OptionalLong.empty(), true, true, false, true, 1.0D, false, false, true, false, true, 256, FuzzyOffsetConstantColumnBiomeZoomer.INSTANCE, BlockTags.INFINIBURN_OVERWORLD.getName(), OVERWORLD_EFFECTS, 0.0F);
      CODEC = RegistryFileCodec.create(Registry.DIMENSION_TYPE_REGISTRY, DIRECT_CODEC);
   }
}
