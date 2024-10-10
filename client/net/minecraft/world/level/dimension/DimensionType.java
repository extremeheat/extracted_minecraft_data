package net.minecraft.world.level.dimension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.nio.file.Path;
import java.util.Optional;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public record DimensionType(
   OptionalLong fixedTime,
   boolean hasSkyLight,
   boolean hasCeiling,
   boolean ultraWarm,
   boolean natural,
   double coordinateScale,
   boolean bedWorks,
   boolean respawnAnchorWorks,
   int minY,
   int height,
   int logicalHeight,
   TagKey<Block> infiniburn,
   ResourceLocation effectsLocation,
   float ambientLight,
   DimensionType.MonsterSettings monsterSettings
) {
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
                  ExtraCodecs.asOptionalLong(Codec.LONG.lenientOptionalFieldOf("fixed_time")).forGetter(DimensionType::fixedTime),
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
   public static final StreamCodec<RegistryFriendlyByteBuf, Holder<DimensionType>> STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.DIMENSION_TYPE);
   private static final int MOON_PHASES = 8;
   public static final float[] MOON_BRIGHTNESS_PER_PHASE = new float[]{1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};
   public static final Codec<Holder<DimensionType>> CODEC = RegistryFileCodec.create(Registries.DIMENSION_TYPE, DIRECT_CODEC);

   public DimensionType(
      OptionalLong fixedTime,
      boolean hasSkyLight,
      boolean hasCeiling,
      boolean ultraWarm,
      boolean natural,
      double coordinateScale,
      boolean bedWorks,
      boolean respawnAnchorWorks,
      int minY,
      int height,
      int logicalHeight,
      TagKey<Block> infiniburn,
      ResourceLocation effectsLocation,
      float ambientLight,
      DimensionType.MonsterSettings monsterSettings
   ) {
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
      } else {
         this.fixedTime = fixedTime;
         this.hasSkyLight = hasSkyLight;
         this.hasCeiling = hasCeiling;
         this.ultraWarm = ultraWarm;
         this.natural = natural;
         this.coordinateScale = coordinateScale;
         this.bedWorks = bedWorks;
         this.respawnAnchorWorks = respawnAnchorWorks;
         this.minY = minY;
         this.height = height;
         this.logicalHeight = logicalHeight;
         this.infiniburn = infiniburn;
         this.effectsLocation = effectsLocation;
         this.ambientLight = ambientLight;
         this.monsterSettings = monsterSettings;
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

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "varName" is null
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
