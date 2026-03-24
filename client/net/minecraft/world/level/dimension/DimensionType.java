package net.minecraft.world.level.dimension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.nio.file.Path;
import java.util.Optional;
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
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.timeline.Timeline;

public record DimensionType(boolean hasFixedTime, boolean hasSkyLight, boolean hasCeiling, boolean hasEnderDragonFight, double coordinateScale, int minY, int height, int logicalHeight, TagKey<Block> infiniburn, float ambientLight, MonsterSettings monsterSettings, Skybox skybox, CardinalLighting.Type cardinalLightType, EnvironmentAttributeMap attributes, HolderSet<Timeline> timelines, Optional<Holder<WorldClock>> defaultClock) {
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

   public DimensionType {
      super();
      if (height < 16) {
         throw new IllegalStateException("height has to be at least 16");
      } else if (minY + height > MAX_Y + 1) {
         throw new IllegalStateException("min_y + height cannot be higher than: " + (MAX_Y + 1));
      } else if (logicalHeight > height) {
         throw new IllegalStateException("logical_height cannot be higher than height");
      } else if (height % 16 != 0) {
         throw new IllegalStateException("height has to be multiple of 16");
      } else if (minY % 16 != 0) {
         throw new IllegalStateException("min_y has to be a multiple of 16");
      }
   }

   private static Codec<DimensionType> createDirectCodec(final Codec<EnvironmentAttributeMap> attributeMapCodec) {
      return ExtraCodecs.<DimensionType>catchDecoderException(RecordCodecBuilder.create((i) -> i.group(Codec.BOOL.optionalFieldOf("has_fixed_time", false).forGetter(DimensionType::hasFixedTime), Codec.BOOL.fieldOf("has_skylight").forGetter(DimensionType::hasSkyLight), Codec.BOOL.fieldOf("has_ceiling").forGetter(DimensionType::hasCeiling), Codec.BOOL.fieldOf("has_ender_dragon_fight").forGetter(DimensionType::hasEnderDragonFight), Codec.doubleRange(9.999999747378752E-6, 3.0E7).fieldOf("coordinate_scale").forGetter(DimensionType::coordinateScale), Codec.intRange(MIN_Y, MAX_Y).fieldOf("min_y").forGetter(DimensionType::minY), Codec.intRange(16, Y_SIZE).fieldOf("height").forGetter(DimensionType::height), Codec.intRange(0, Y_SIZE).fieldOf("logical_height").forGetter(DimensionType::logicalHeight), TagKey.hashedCodec(Registries.BLOCK).fieldOf("infiniburn").forGetter(DimensionType::infiniburn), Codec.FLOAT.fieldOf("ambient_light").forGetter(DimensionType::ambientLight), DimensionType.MonsterSettings.CODEC.forGetter(DimensionType::monsterSettings), DimensionType.Skybox.CODEC.optionalFieldOf("skybox", DimensionType.Skybox.OVERWORLD).forGetter(DimensionType::skybox), CardinalLighting.Type.CODEC.optionalFieldOf("cardinal_light", CardinalLighting.Type.DEFAULT).forGetter(DimensionType::cardinalLightType), attributeMapCodec.optionalFieldOf("attributes", EnvironmentAttributeMap.EMPTY).forGetter(DimensionType::attributes), RegistryCodecs.homogeneousList(Registries.TIMELINE).optionalFieldOf("timelines", HolderSet.empty()).forGetter(DimensionType::timelines), WorldClock.CODEC.optionalFieldOf("default_clock").forGetter(DimensionType::defaultClock)).apply(i, DimensionType::new)));
   }

   public static double getTeleportationScale(final DimensionType lastDimensionType, final DimensionType newDimensionType) {
      double oldScale = lastDimensionType.coordinateScale();
      double newScale = newDimensionType.coordinateScale();
      return oldScale / newScale;
   }

   public static Path getStorageFolder(final ResourceKey<Level> name, final Path baseFolder) {
      return name.identifier().resolveAgainst(baseFolder.resolve("dimensions"));
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
      public static final MapCodec<MonsterSettings> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(IntProviders.codec(0, 15).fieldOf("monster_spawn_light_level").forGetter(MonsterSettings::monsterSpawnLightTest), Codec.intRange(0, 15).fieldOf("monster_spawn_block_light_limit").forGetter(MonsterSettings::monsterSpawnBlockLightLimit)).apply(i, MonsterSettings::new));

      public MonsterSettings {
         super();
      }
   }

   public static enum Skybox implements StringRepresentable {
      NONE("none"),
      OVERWORLD("overworld"),
      END("end");

      public static final Codec<Skybox> CODEC = StringRepresentable.<Skybox>fromEnum(Skybox::values);
      private final String name;

      private Skybox(final String name) {
         this.name = name;
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static Skybox[] $values() {
         return new Skybox[]{NONE, OVERWORLD, END};
      }
   }
}
