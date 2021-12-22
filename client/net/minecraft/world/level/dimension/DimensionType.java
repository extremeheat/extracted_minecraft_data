package net.minecraft.world.level.dimension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.nio.file.Path;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.TheEndBiomeSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

public class DimensionType {
   public static final int BITS_FOR_Y;
   public static final int MIN_HEIGHT = 16;
   public static final int Y_SIZE;
   public static final int MAX_Y;
   public static final int MIN_Y;
   public static final int WAY_ABOVE_MAX_Y;
   public static final int WAY_BELOW_MIN_Y;
   public static final ResourceLocation OVERWORLD_EFFECTS;
   public static final ResourceLocation NETHER_EFFECTS;
   public static final ResourceLocation END_EFFECTS;
   public static final Codec<DimensionType> DIRECT_CODEC;
   private static final int MOON_PHASES = 8;
   public static final float[] MOON_BRIGHTNESS_PER_PHASE;
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
   private final int minY;
   private final int height;
   private final int logicalHeight;
   private final ResourceLocation infiniburn;
   private final ResourceLocation effectsLocation;
   private final float ambientLight;
   private final transient float[] brightnessRamp;

   private static DataResult<DimensionType> guardY(DimensionType var0) {
      if (var0.height() < 16) {
         return DataResult.error("height has to be at least 16");
      } else if (var0.minY() + var0.height() > MAX_Y + 1) {
         return DataResult.error("min_y + height cannot be higher than: " + (MAX_Y + 1));
      } else if (var0.logicalHeight() > var0.height()) {
         return DataResult.error("logical_height cannot be higher than height");
      } else if (var0.height() % 16 != 0) {
         return DataResult.error("height has to be multiple of 16");
      } else {
         return var0.minY() % 16 != 0 ? DataResult.error("min_y has to be a multiple of 16") : DataResult.success(var0);
      }
   }

   private DimensionType(OptionalLong var1, boolean var2, boolean var3, boolean var4, boolean var5, double var6, boolean var8, boolean var9, boolean var10, boolean var11, int var12, int var13, int var14, ResourceLocation var15, ResourceLocation var16, float var17) {
      this(var1, var2, var3, var4, var5, var6, false, var8, var9, var10, var11, var12, var13, var14, var15, var16, var17);
   }

   public static DimensionType create(OptionalLong var0, boolean var1, boolean var2, boolean var3, boolean var4, double var5, boolean var7, boolean var8, boolean var9, boolean var10, boolean var11, int var12, int var13, int var14, ResourceLocation var15, ResourceLocation var16, float var17) {
      DimensionType var18 = new DimensionType(var0, var1, var2, var3, var4, var5, var7, var8, var9, var10, var11, var12, var13, var14, var15, var16, var17);
      guardY(var18).error().ifPresent((var0x) -> {
         throw new IllegalStateException(var0x.message());
      });
      return var18;
   }

   /** @deprecated */
   @Deprecated
   private DimensionType(OptionalLong var1, boolean var2, boolean var3, boolean var4, boolean var5, double var6, boolean var8, boolean var9, boolean var10, boolean var11, boolean var12, int var13, int var14, int var15, ResourceLocation var16, ResourceLocation var17, float var18) {
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
      this.minY = var13;
      this.height = var14;
      this.logicalHeight = var15;
      this.infiniburn = var16;
      this.effectsLocation = var17;
      this.ambientLight = var18;
      this.brightnessRamp = fillBrightnessRamp(var18);
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

   /** @deprecated */
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

   public static RegistryAccess registerBuiltin(RegistryAccess var0) {
      WritableRegistry var1 = var0.ownedRegistryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
      var1.register(OVERWORLD_LOCATION, DEFAULT_OVERWORLD, Lifecycle.stable());
      var1.register(OVERWORLD_CAVES_LOCATION, DEFAULT_OVERWORLD_CAVES, Lifecycle.stable());
      var1.register(NETHER_LOCATION, DEFAULT_NETHER, Lifecycle.stable());
      var1.register(END_LOCATION, DEFAULT_END, Lifecycle.stable());
      return var0;
   }

   public static MappedRegistry<LevelStem> defaultDimensions(RegistryAccess var0, long var1) {
      return defaultDimensions(var0, var1, true);
   }

   public static MappedRegistry<LevelStem> defaultDimensions(RegistryAccess var0, long var1, boolean var3) {
      MappedRegistry var4 = new MappedRegistry(Registry.LEVEL_STEM_REGISTRY, Lifecycle.experimental());
      Registry var5 = var0.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
      Registry var6 = var0.registryOrThrow(Registry.BIOME_REGISTRY);
      Registry var7 = var0.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY);
      Registry var8 = var0.registryOrThrow(Registry.NOISE_REGISTRY);
      var4.register(LevelStem.NETHER, new LevelStem(() -> {
         return (DimensionType)var5.getOrThrow(NETHER_LOCATION);
      }, new NoiseBasedChunkGenerator(var8, MultiNoiseBiomeSource.Preset.NETHER.biomeSource(var6, var3), var1, () -> {
         return (NoiseGeneratorSettings)var7.getOrThrow(NoiseGeneratorSettings.NETHER);
      })), Lifecycle.stable());
      var4.register(LevelStem.END, new LevelStem(() -> {
         return (DimensionType)var5.getOrThrow(END_LOCATION);
      }, new NoiseBasedChunkGenerator(var8, new TheEndBiomeSource(var6, var1), var1, () -> {
         return (NoiseGeneratorSettings)var7.getOrThrow(NoiseGeneratorSettings.END);
      })), Lifecycle.stable());
      return var4;
   }

   public static double getTeleportationScale(DimensionType var0, DimensionType var1) {
      double var2 = var0.coordinateScale();
      double var4 = var1.coordinateScale();
      return var2 / var4;
   }

   /** @deprecated */
   @Deprecated
   public String getFileSuffix() {
      return this.equalTo(DEFAULT_END) ? "_end" : "";
   }

   public static Path getStorageFolder(ResourceKey<Level> var0, Path var1) {
      if (var0 == Level.OVERWORLD) {
         return var1;
      } else if (var0 == Level.END) {
         return var1.resolve("DIM1");
      } else {
         return var0 == Level.NETHER ? var1.resolve("DIM-1") : var1.resolve("dimensions").resolve(var0.location().getNamespace()).resolve(var0.location().getPath());
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

   public int minY() {
      return this.minY;
   }

   public int height() {
      return this.height;
   }

   public int logicalHeight() {
      return this.logicalHeight;
   }

   public boolean createDragonFight() {
      return this.createDragonFight;
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
         return this.hasSkylight == var1.hasSkylight && this.hasCeiling == var1.hasCeiling && this.ultraWarm == var1.ultraWarm && this.natural == var1.natural && this.coordinateScale == var1.coordinateScale && this.createDragonFight == var1.createDragonFight && this.piglinSafe == var1.piglinSafe && this.bedWorks == var1.bedWorks && this.respawnAnchorWorks == var1.respawnAnchorWorks && this.hasRaids == var1.hasRaids && this.minY == var1.minY && this.height == var1.height && this.logicalHeight == var1.logicalHeight && Float.compare(var1.ambientLight, this.ambientLight) == 0 && this.fixedTime.equals(var1.fixedTime) && this.infiniburn.equals(var1.infiniburn) && this.effectsLocation.equals(var1.effectsLocation);
      }
   }

   static {
      BITS_FOR_Y = BlockPos.PACKED_Y_LENGTH;
      Y_SIZE = (1 << BITS_FOR_Y) - 32;
      MAX_Y = (Y_SIZE >> 1) - 1;
      MIN_Y = MAX_Y - Y_SIZE + 1;
      WAY_ABOVE_MAX_Y = MAX_Y << 4;
      WAY_BELOW_MIN_Y = MIN_Y << 4;
      OVERWORLD_EFFECTS = new ResourceLocation("overworld");
      NETHER_EFFECTS = new ResourceLocation("the_nether");
      END_EFFECTS = new ResourceLocation("the_end");
      DIRECT_CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(Codec.LONG.optionalFieldOf("fixed_time").xmap((var0x) -> {
            return (OptionalLong)var0x.map(OptionalLong::of).orElseGet(OptionalLong::empty);
         }, (var0x) -> {
            return var0x.isPresent() ? Optional.of(var0x.getAsLong()) : Optional.empty();
         }).forGetter((var0x) -> {
            return var0x.fixedTime;
         }), Codec.BOOL.fieldOf("has_skylight").forGetter(DimensionType::hasSkyLight), Codec.BOOL.fieldOf("has_ceiling").forGetter(DimensionType::hasCeiling), Codec.BOOL.fieldOf("ultrawarm").forGetter(DimensionType::ultraWarm), Codec.BOOL.fieldOf("natural").forGetter(DimensionType::natural), Codec.doubleRange(9.999999747378752E-6D, 3.0E7D).fieldOf("coordinate_scale").forGetter(DimensionType::coordinateScale), Codec.BOOL.fieldOf("piglin_safe").forGetter(DimensionType::piglinSafe), Codec.BOOL.fieldOf("bed_works").forGetter(DimensionType::bedWorks), Codec.BOOL.fieldOf("respawn_anchor_works").forGetter(DimensionType::respawnAnchorWorks), Codec.BOOL.fieldOf("has_raids").forGetter(DimensionType::hasRaids), Codec.intRange(MIN_Y, MAX_Y).fieldOf("min_y").forGetter(DimensionType::minY), Codec.intRange(16, Y_SIZE).fieldOf("height").forGetter(DimensionType::height), Codec.intRange(0, Y_SIZE).fieldOf("logical_height").forGetter(DimensionType::logicalHeight), ResourceLocation.CODEC.fieldOf("infiniburn").forGetter((var0x) -> {
            return var0x.infiniburn;
         }), ResourceLocation.CODEC.fieldOf("effects").orElse(OVERWORLD_EFFECTS).forGetter((var0x) -> {
            return var0x.effectsLocation;
         }), Codec.FLOAT.fieldOf("ambient_light").forGetter((var0x) -> {
            return var0x.ambientLight;
         })).apply(var0, DimensionType::new);
      }).comapFlatMap(DimensionType::guardY, Function.identity());
      MOON_BRIGHTNESS_PER_PHASE = new float[]{1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};
      OVERWORLD_LOCATION = ResourceKey.create(Registry.DIMENSION_TYPE_REGISTRY, new ResourceLocation("overworld"));
      NETHER_LOCATION = ResourceKey.create(Registry.DIMENSION_TYPE_REGISTRY, new ResourceLocation("the_nether"));
      END_LOCATION = ResourceKey.create(Registry.DIMENSION_TYPE_REGISTRY, new ResourceLocation("the_end"));
      DEFAULT_OVERWORLD = create(OptionalLong.empty(), true, false, false, true, 1.0D, false, false, true, false, true, -64, 384, 384, BlockTags.INFINIBURN_OVERWORLD.getName(), OVERWORLD_EFFECTS, 0.0F);
      DEFAULT_NETHER = create(OptionalLong.of(18000L), false, true, true, false, 8.0D, false, true, false, true, false, 0, 256, 128, BlockTags.INFINIBURN_NETHER.getName(), NETHER_EFFECTS, 0.1F);
      DEFAULT_END = create(OptionalLong.of(6000L), false, false, false, false, 1.0D, true, false, false, false, true, 0, 256, 256, BlockTags.INFINIBURN_END.getName(), END_EFFECTS, 0.0F);
      OVERWORLD_CAVES_LOCATION = ResourceKey.create(Registry.DIMENSION_TYPE_REGISTRY, new ResourceLocation("overworld_caves"));
      DEFAULT_OVERWORLD_CAVES = create(OptionalLong.empty(), true, true, false, true, 1.0D, false, false, true, false, true, -64, 384, 384, BlockTags.INFINIBURN_OVERWORLD.getName(), OVERWORLD_EFFECTS, 0.0F);
      CODEC = RegistryFileCodec.create(Registry.DIMENSION_TYPE_REGISTRY, DIRECT_CODEC);
   }
}
