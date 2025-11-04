package net.minecraft.world.level.dimension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.nio.file.Path;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.timeline.Timeline;

public record DimensionType(boolean hasFixedTime, boolean hasSkyLight, boolean hasCeiling, double coordinateScale, int minY, int height, int logicalHeight, TagKey<Block> infiniburn, float ambientLight, MonsterSettings monsterSettings, Skybox skybox, CardinalLightType cardinalLightType, EnvironmentAttributeMap attributes, HolderSet<Timeline> timelines) {
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
   public static final float[] MOON_BRIGHTNESS_PER_PHASE;
   public static final Codec<Holder<DimensionType>> CODEC;

   public DimensionType(boolean var1, boolean var2, boolean var3, double var4, int var6, int var7, int var8, TagKey<Block> var9, float var10, MonsterSettings var11, Skybox var12, CardinalLightType var13, EnvironmentAttributeMap var14, HolderSet<Timeline> var15) {
      super();
      if (var7 < 16) {
         throw new IllegalStateException("height has to be at least 16");
      } else if (var6 + var7 > MAX_Y + 1) {
         throw new IllegalStateException("min_y + height cannot be higher than: " + (MAX_Y + 1));
      } else if (var8 > var7) {
         throw new IllegalStateException("logical_height cannot be higher than height");
      } else if (var7 % 16 != 0) {
         throw new IllegalStateException("height has to be multiple of 16");
      } else if (var6 % 16 != 0) {
         throw new IllegalStateException("min_y has to be a multiple of 16");
      } else {
         this.hasFixedTime = var1;
         this.hasSkyLight = var2;
         this.hasCeiling = var3;
         this.coordinateScale = var4;
         this.minY = var6;
         this.height = var7;
         this.logicalHeight = var8;
         this.infiniburn = var9;
         this.ambientLight = var10;
         this.monsterSettings = var11;
         this.skybox = var12;
         this.cardinalLightType = var13;
         this.attributes = var14;
         this.timelines = var15;
      }
   }

   private static Codec<DimensionType> createDirectCodec(Codec<EnvironmentAttributeMap> var0) {
      return ExtraCodecs.<DimensionType>catchDecoderException(RecordCodecBuilder.create((var1) -> var1.group(Codec.BOOL.optionalFieldOf("has_fixed_time", false).forGetter(DimensionType::hasFixedTime), Codec.BOOL.fieldOf("has_skylight").forGetter(DimensionType::hasSkyLight), Codec.BOOL.fieldOf("has_ceiling").forGetter(DimensionType::hasCeiling), Codec.doubleRange(9.999999747378752E-6, 3.0E7).fieldOf("coordinate_scale").forGetter(DimensionType::coordinateScale), Codec.intRange(MIN_Y, MAX_Y).fieldOf("min_y").forGetter(DimensionType::minY), Codec.intRange(16, Y_SIZE).fieldOf("height").forGetter(DimensionType::height), Codec.intRange(0, Y_SIZE).fieldOf("logical_height").forGetter(DimensionType::logicalHeight), TagKey.hashedCodec(Registries.BLOCK).fieldOf("infiniburn").forGetter(DimensionType::infiniburn), Codec.FLOAT.fieldOf("ambient_light").forGetter(DimensionType::ambientLight), DimensionType.MonsterSettings.CODEC.forGetter(DimensionType::monsterSettings), DimensionType.Skybox.CODEC.optionalFieldOf("skybox", DimensionType.Skybox.OVERWORLD).forGetter(DimensionType::skybox), DimensionType.CardinalLightType.CODEC.optionalFieldOf("cardinal_light", DimensionType.CardinalLightType.DEFAULT).forGetter(DimensionType::cardinalLightType), var0.optionalFieldOf("attributes", EnvironmentAttributeMap.EMPTY).forGetter(DimensionType::attributes), RegistryCodecs.homogeneousList(Registries.TIMELINE).optionalFieldOf("timelines", HolderSet.empty()).forGetter(DimensionType::timelines)).apply(var1, DimensionType::new)));
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
         return var0 == Level.NETHER ? var1.resolve("DIM-1") : var1.resolve("dimensions").resolve(var0.identifier().getNamespace()).resolve(var0.identifier().getPath());
      }
   }

   public IntProvider monsterSpawnLightTest() {
      return this.monsterSettings.monsterSpawnLightTest();
   }

   public int monsterSpawnBlockLightLimit() {
      return this.monsterSettings.monsterSpawnBlockLightLimit();
   }

   public boolean hasEndFlashes() {
      return this.skybox == DimensionType.Skybox.END;
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

   public static enum Skybox implements StringRepresentable {
      NONE("none"),
      OVERWORLD("overworld"),
      END("end");

      public static final Codec<Skybox> CODEC = StringRepresentable.<Skybox>fromEnum(Skybox::values);
      private final String name;

      private Skybox(final String var3) {
         this.name = var3;
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static Skybox[] $values() {
         return new Skybox[]{NONE, OVERWORLD, END};
      }
   }

   public static enum CardinalLightType implements StringRepresentable {
      DEFAULT("default"),
      NETHER("nether");

      public static final Codec<CardinalLightType> CODEC = StringRepresentable.<CardinalLightType>fromEnum(CardinalLightType::values);
      private final String name;

      private CardinalLightType(final String var3) {
         this.name = var3;
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static CardinalLightType[] $values() {
         return new CardinalLightType[]{DEFAULT, NETHER};
      }
   }
}
