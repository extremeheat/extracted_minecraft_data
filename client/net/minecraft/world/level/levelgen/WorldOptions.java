package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.OptionalLong;
import net.minecraft.util.RandomSource;
import org.apache.commons.lang3.StringUtils;

public class WorldOptions {
   public static final MapCodec<WorldOptions> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.LONG.fieldOf("seed").stable().forGetter(WorldOptions::seed), Codec.BOOL.fieldOf("generate_features").orElse(true).stable().forGetter(WorldOptions::generateStructures), Codec.BOOL.fieldOf("bonus_chest").orElse(false).stable().forGetter(WorldOptions::generateBonusChest), Codec.STRING.lenientOptionalFieldOf("legacy_custom_options").stable().forGetter((s) -> s.legacyCustomOptions)).apply(i, i.stable(WorldOptions::new)));
   public static final WorldOptions DEMO_OPTIONS = new WorldOptions((long)"North Carolina".hashCode(), true, true);
   private final long seed;
   private final boolean generateStructures;
   private final boolean generateBonusChest;
   private final Optional<String> legacyCustomOptions;

   public WorldOptions(final long seed, final boolean generateStructures, final boolean generateBonusChest) {
      this(seed, generateStructures, generateBonusChest, Optional.empty());
   }

   public static WorldOptions defaultWithRandomSeed() {
      return new WorldOptions(randomSeed(), true, false);
   }

   public static WorldOptions testWorldWithRandomSeed() {
      return new WorldOptions(randomSeed(), false, false);
   }

   private WorldOptions(final long seed, final boolean generateStructures, final boolean generateBonusChest, final Optional<String> legacyCustomOptions) {
      super();
      this.seed = seed;
      this.generateStructures = generateStructures;
      this.generateBonusChest = generateBonusChest;
      this.legacyCustomOptions = legacyCustomOptions;
   }

   public long seed() {
      return this.seed;
   }

   public boolean generateStructures() {
      return this.generateStructures;
   }

   public boolean generateBonusChest() {
      return this.generateBonusChest;
   }

   public boolean isOldCustomizedWorld() {
      return this.legacyCustomOptions.isPresent();
   }

   public WorldOptions withBonusChest(final boolean generateBonusChest) {
      return new WorldOptions(this.seed, this.generateStructures, generateBonusChest, this.legacyCustomOptions);
   }

   public WorldOptions withStructures(final boolean generateStructures) {
      return new WorldOptions(this.seed, generateStructures, this.generateBonusChest, this.legacyCustomOptions);
   }

   public WorldOptions withSeed(final OptionalLong seed) {
      return new WorldOptions(seed.orElse(randomSeed()), this.generateStructures, this.generateBonusChest, this.legacyCustomOptions);
   }

   public static OptionalLong parseSeed(String seedString) {
      seedString = seedString.trim();
      if (StringUtils.isEmpty(seedString)) {
         return OptionalLong.empty();
      } else {
         try {
            return OptionalLong.of(Long.parseLong(seedString));
         } catch (NumberFormatException var2) {
            return OptionalLong.of((long)seedString.hashCode());
         }
      }
   }

   public static long randomSeed() {
      return RandomSource.create().nextLong();
   }
}
