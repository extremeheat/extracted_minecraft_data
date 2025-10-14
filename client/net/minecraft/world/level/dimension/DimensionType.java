package net.minecraft.world.level.dimension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.nio.file.Path;
import java.util.OptionalLong;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.MoonPhase;
import net.minecraft.world.level.block.Block;

public record DimensionType(OptionalLong fixedTime, boolean hasSkyLight, boolean hasCeiling, boolean natural, double coordinateScale, int minY, int height, int logicalHeight, TagKey<Block> infiniburn, ResourceLocation effectsLocation, float ambientLight, MonsterSettings monsterSettings, EnvironmentAttributeMap attributes) {
   public static final int BITS_FOR_Y;
   public static final int MIN_HEIGHT = 16;
   public static final int Y_SIZE;
   public static final int MAX_Y;
   public static final int MIN_Y;
   public static final int WAY_ABOVE_MAX_Y;
   public static final int WAY_BELOW_MIN_Y;
   public static final Codec<DimensionType> DIRECT_CODEC;
   public static final Codec<DimensionType> NETWORK_CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, Holder<DimensionType>> STREAM_CODEC;
   private static final MoonPhase[] MOON_PHASES;
   public static final float[] MOON_BRIGHTNESS_PER_PHASE;
   public static final Codec<Holder<DimensionType>> CODEC;

   public DimensionType(OptionalLong var1, boolean var2, boolean var3, boolean var4, double var5, int var7, int var8, int var9, TagKey<Block> var10, ResourceLocation var11, float var12, MonsterSettings var13, EnvironmentAttributeMap var14) {
      super();
      if (var8 < 16) {
         throw new IllegalStateException("height has to be at least 16");
      } else if (var7 + var8 > MAX_Y + 1) {
         throw new IllegalStateException("min_y + height cannot be higher than: " + (MAX_Y + 1));
      } else if (var9 > var8) {
         throw new IllegalStateException("logical_height cannot be higher than height");
      } else if (var8 % 16 != 0) {
         throw new IllegalStateException("height has to be multiple of 16");
      } else if (var7 % 16 != 0) {
         throw new IllegalStateException("min_y has to be a multiple of 16");
      } else {
         this.fixedTime = var1;
         this.hasSkyLight = var2;
         this.hasCeiling = var3;
         this.natural = var4;
         this.coordinateScale = var5;
         this.minY = var7;
         this.height = var8;
         this.logicalHeight = var9;
         this.infiniburn = var10;
         this.effectsLocation = var11;
         this.ambientLight = var12;
         this.monsterSettings = var13;
         this.attributes = var14;
      }
   }

   private static Codec<DimensionType> createDirectCodec(Codec<EnvironmentAttributeMap> var0) {
      return ExtraCodecs.<DimensionType>catchDecoderException(RecordCodecBuilder.create((var1) -> var1.group(ExtraCodecs.asOptionalLong(Codec.LONG.lenientOptionalFieldOf("fixed_time")).forGetter(DimensionType::fixedTime), Codec.BOOL.fieldOf("has_skylight").forGetter(DimensionType::hasSkyLight), Codec.BOOL.fieldOf("has_ceiling").forGetter(DimensionType::hasCeiling), Codec.BOOL.fieldOf("natural").forGetter(DimensionType::natural), Codec.doubleRange(9.999999747378752E-6, 3.0E7).fieldOf("coordinate_scale").forGetter(DimensionType::coordinateScale), Codec.intRange(MIN_Y, MAX_Y).fieldOf("min_y").forGetter(DimensionType::minY), Codec.intRange(16, Y_SIZE).fieldOf("height").forGetter(DimensionType::height), Codec.intRange(0, Y_SIZE).fieldOf("logical_height").forGetter(DimensionType::logicalHeight), TagKey.hashedCodec(Registries.BLOCK).fieldOf("infiniburn").forGetter(DimensionType::infiniburn), ResourceLocation.CODEC.fieldOf("effects").orElse(BuiltinDimensionTypes.OVERWORLD_EFFECTS).forGetter(DimensionType::effectsLocation), Codec.FLOAT.fieldOf("ambient_light").forGetter(DimensionType::ambientLight), DimensionType.MonsterSettings.CODEC.forGetter(DimensionType::monsterSettings), var0.optionalFieldOf("attributes", EnvironmentAttributeMap.EMPTY).forGetter(DimensionType::attributes)).apply(var1, DimensionType::new)));
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
         return var0 == Level.NETHER ? var1.resolve("DIM-1") : var1.resolve("dimensions").resolve(var0.location().getNamespace()).resolve(var0.location().getPath());
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

   public MoonPhase moonPhase(long var1) {
      int var3 = (int)(var1 / 24000L % (long)MOON_PHASES.length + (long)MOON_PHASES.length) % MOON_PHASES.length;
      return MOON_PHASES[var3];
   }

   public IntProvider monsterSpawnLightTest() {
      return this.monsterSettings.monsterSpawnLightTest();
   }

   public int monsterSpawnBlockLightLimit() {
      return this.monsterSettings.monsterSpawnBlockLightLimit();
   }

   static {
      BITS_FOR_Y = BlockPos.PACKED_Y_LENGTH;
      Y_SIZE = (1 << BITS_FOR_Y) - 32;
      MAX_Y = (Y_SIZE >> 1) - 1;
      MIN_Y = MAX_Y - Y_SIZE + 1;
      WAY_ABOVE_MAX_Y = MAX_Y << 4;
      WAY_BELOW_MIN_Y = MIN_Y << 4;
      DIRECT_CODEC = createDirectCodec(EnvironmentAttributeMap.CODEC);
      NETWORK_CODEC = createDirectCodec(EnvironmentAttributeMap.NETWORK_CODEC);
      STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.DIMENSION_TYPE);
      MOON_PHASES = MoonPhase.values();
      MOON_BRIGHTNESS_PER_PHASE = new float[]{1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};
      CODEC = RegistryFileCodec.<Holder<DimensionType>>create(Registries.DIMENSION_TYPE, DIRECT_CODEC);
   }

   public static record MonsterSettings(IntProvider monsterSpawnLightTest, int monsterSpawnBlockLightLimit) {
      public static final MapCodec<MonsterSettings> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(IntProvider.codec(0, 15).fieldOf("monster_spawn_light_level").forGetter(MonsterSettings::monsterSpawnLightTest), Codec.intRange(0, 15).fieldOf("monster_spawn_block_light_limit").forGetter(MonsterSettings::monsterSpawnBlockLightLimit)).apply(var0, MonsterSettings::new));

      public MonsterSettings(IntProvider var1, int var2) {
         super();
         this.monsterSpawnLightTest = var1;
         this.monsterSpawnBlockLightLimit = var2;
      }
   }
}
