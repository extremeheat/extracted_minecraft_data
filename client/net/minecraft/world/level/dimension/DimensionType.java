package net.minecraft.world.level.dimension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.nio.file.Path;
import java.util.Optional;
import java.util.OptionalLong;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public record DimensionType(
   OptionalLong k,
   boolean l,
   boolean m,
   boolean n,
   boolean o,
   double p,
   boolean q,
   boolean r,
   int s,
   int t,
   int u,
   TagKey<Block> v,
   ResourceLocation w,
   float x,
   DimensionType.MonsterSettings y
) {
   private final OptionalLong fixedTime;
   private final boolean hasSkyLight;
   private final boolean hasCeiling;
   private final boolean ultraWarm;
   private final boolean natural;
   private final double coordinateScale;
   private final boolean bedWorks;
   private final boolean respawnAnchorWorks;
   private final int minY;
   private final int height;
   private final int logicalHeight;
   private final TagKey<Block> infiniburn;
   private final ResourceLocation effectsLocation;
   private final float ambientLight;
   private final DimensionType.MonsterSettings monsterSettings;
   public static final int BITS_FOR_Y = BlockPos.PACKED_Y_LENGTH;
   public static final int MIN_HEIGHT = 16;
   public static final int Y_SIZE = (1 << BITS_FOR_Y) - 32;
   public static final int MAX_Y = (Y_SIZE >> 1) - 1;
   public static final int MIN_Y = MAX_Y - Y_SIZE + 1;
   public static final int WAY_ABOVE_MAX_Y = MAX_Y << 4;
   public static final int WAY_BELOW_MIN_Y = MIN_Y << 4;
   public static final Codec<DimensionType> DIRECT_CODEC = ExtraCodecs.catchDecoderException(
      RecordCodecBuilder.create(
         var0 -> var0.group(
                  ExtraCodecs.asOptionalLong(Codec.LONG.optionalFieldOf("fixed_time")).forGetter(DimensionType::fixedTime),
                  Codec.BOOL.fieldOf("has_skylight").forGetter(DimensionType::hasSkyLight),
                  Codec.BOOL.fieldOf("has_ceiling").forGetter(DimensionType::hasCeiling),
                  Codec.BOOL.fieldOf("ultrawarm").forGetter(DimensionType::ultraWarm),
                  Codec.BOOL.fieldOf("natural").forGetter(DimensionType::natural),
                  Codec.doubleRange(9.999999747378752E-6, 3.0E7).fieldOf("coordinate_scale").forGetter(DimensionType::coordinateScale),
                  Codec.BOOL.fieldOf("bed_works").forGetter(DimensionType::bedWorks),
                  Codec.BOOL.fieldOf("respawn_anchor_works").forGetter(DimensionType::respawnAnchorWorks),
                  Codec.intRange(MIN_Y, MAX_Y).fieldOf("min_y").forGetter(DimensionType::minY),
                  Codec.intRange(16, Y_SIZE).fieldOf("height").forGetter(DimensionType::height),
                  Codec.intRange(0, Y_SIZE).fieldOf("logical_height").forGetter(DimensionType::logicalHeight),
                  TagKey.hashedCodec(Registries.BLOCK).fieldOf("infiniburn").forGetter(DimensionType::infiniburn),
                  ResourceLocation.CODEC.fieldOf("effects").orElse(BuiltinDimensionTypes.OVERWORLD_EFFECTS).forGetter(DimensionType::effectsLocation),
                  Codec.FLOAT.fieldOf("ambient_light").forGetter(DimensionType::ambientLight),
                  DimensionType.MonsterSettings.CODEC.forGetter(DimensionType::monsterSettings)
               )
               .apply(var0, DimensionType::new)
      )
   );
   private static final int MOON_PHASES = 8;
   public static final float[] MOON_BRIGHTNESS_PER_PHASE = new float[]{1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};
   public static final Codec<Holder<DimensionType>> CODEC = RegistryFileCodec.create(Registries.DIMENSION_TYPE, DIRECT_CODEC);

   public DimensionType(
      OptionalLong var1,
      boolean var2,
      boolean var3,
      boolean var4,
      boolean var5,
      double var6,
      boolean var8,
      boolean var9,
      int var10,
      int var11,
      int var12,
      TagKey<Block> var13,
      ResourceLocation var14,
      float var15,
      DimensionType.MonsterSettings var16
   ) {
      super();
      if (var11 < 16) {
         throw new IllegalStateException("height has to be at least 16");
      } else if (var10 + var11 > MAX_Y + 1) {
         throw new IllegalStateException("min_y + height cannot be higher than: " + (MAX_Y + 1));
      } else if (var12 > var11) {
         throw new IllegalStateException("logical_height cannot be higher than height");
      } else if (var11 % 16 != 0) {
         throw new IllegalStateException("height has to be multiple of 16");
      } else if (var10 % 16 != 0) {
         throw new IllegalStateException("min_y has to be a multiple of 16");
      } else {
         this.fixedTime = var1;
         this.hasSkyLight = var2;
         this.hasCeiling = var3;
         this.ultraWarm = var4;
         this.natural = var5;
         this.coordinateScale = var6;
         this.bedWorks = var8;
         this.respawnAnchorWorks = var9;
         this.minY = var10;
         this.height = var11;
         this.logicalHeight = var12;
         this.infiniburn = var13;
         this.effectsLocation = var14;
         this.ambientLight = var15;
         this.monsterSettings = var16;
      }
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

   public static double getTeleportationScale(DimensionType var0, DimensionType var1) {
      double var2 = var0.coordinateScale();
      double var4 = var1.coordinateScale();
      return var2 / var4;
   }

   public static Path getStorageFolder(ResourceKey<Level> var0, Path var1) {
      if (var0 == Level.OVERWORLD) {
         return var1;
      } else if (var0 == Level.END) {
         return var1.resolve("DIM1");
      } else {
         return var0 == Level.NETHER
            ? var1.resolve("DIM-1")
            : var1.resolve("dimensions").resolve(var0.location().getNamespace()).resolve(var0.location().getPath());
      }
   }

   public boolean hasFixedTime() {
      return this.fixedTime.isPresent();
   }

   public float timeOfDay(long var1) {
      double var3 = Mth.frac((double)this.fixedTime.orElse(var1) / 24000.0 - 0.25);
      double var5 = 0.5 - Math.cos(var3 * 3.141592653589793) / 2.0;
      return (float)(var3 * 2.0 + var5) / 3.0F;
   }

   public int moonPhase(long var1) {
      return (int)(var1 / 24000L % 8L + 8L) % 8;
   }

   public boolean piglinSafe() {
      return this.monsterSettings.piglinSafe();
   }

   public boolean hasRaids() {
      return this.monsterSettings.hasRaids();
   }

   public IntProvider monsterSpawnLightTest() {
      return this.monsterSettings.monsterSpawnLightTest();
   }

   public int monsterSpawnBlockLightLimit() {
      return this.monsterSettings.monsterSpawnBlockLightLimit();
   }

   public static record MonsterSettings(boolean b, boolean c, IntProvider d, int e) {
      private final boolean piglinSafe;
      private final boolean hasRaids;
      private final IntProvider monsterSpawnLightTest;
      private final int monsterSpawnBlockLightLimit;
      public static final MapCodec<DimensionType.MonsterSettings> CODEC = RecordCodecBuilder.mapCodec(
         var0 -> var0.group(
                  Codec.BOOL.fieldOf("piglin_safe").forGetter(DimensionType.MonsterSettings::piglinSafe),
                  Codec.BOOL.fieldOf("has_raids").forGetter(DimensionType.MonsterSettings::hasRaids),
                  IntProvider.codec(0, 15).fieldOf("monster_spawn_light_level").forGetter(DimensionType.MonsterSettings::monsterSpawnLightTest),
                  Codec.intRange(0, 15).fieldOf("monster_spawn_block_light_limit").forGetter(DimensionType.MonsterSettings::monsterSpawnBlockLightLimit)
               )
               .apply(var0, DimensionType.MonsterSettings::new)
      );

      public MonsterSettings(boolean var1, boolean var2, IntProvider var3, int var4) {
         super();
         this.piglinSafe = var1;
         this.hasRaids = var2;
         this.monsterSpawnLightTest = var3;
         this.monsterSpawnBlockLightLimit = var4;
      }
   }
}
