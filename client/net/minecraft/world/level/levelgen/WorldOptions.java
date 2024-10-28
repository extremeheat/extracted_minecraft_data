package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.OptionalLong;
import net.minecraft.util.RandomSource;
import org.apache.commons.lang3.StringUtils;

public class WorldOptions {
   public static final MapCodec<WorldOptions> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(Codec.LONG.fieldOf("seed").stable().forGetter(WorldOptions::seed), Codec.BOOL.fieldOf("generate_features").orElse(true).stable().forGetter(WorldOptions::generateStructures), Codec.BOOL.fieldOf("bonus_chest").orElse(false).stable().forGetter(WorldOptions::generateBonusChest), Codec.STRING.lenientOptionalFieldOf("legacy_custom_options").stable().forGetter((var0x) -> {
         return var0x.legacyCustomOptions;
      })).apply(var0, var0.stable(WorldOptions::new));
   });
   public static final WorldOptions DEMO_OPTIONS = new WorldOptions((long)"North Carolina".hashCode(), true, true);
   private final long seed;
   private final boolean generateStructures;
   private final boolean generateBonusChest;
   private final Optional<String> legacyCustomOptions;

   public WorldOptions(long var1, boolean var3, boolean var4) {
      this(var1, var3, var4, Optional.empty());
   }

   public static WorldOptions defaultWithRandomSeed() {
      return new WorldOptions(randomSeed(), true, false);
   }

   private WorldOptions(long var1, boolean var3, boolean var4, Optional<String> var5) {
      super();
      this.seed = var1;
      this.generateStructures = var3;
      this.generateBonusChest = var4;
      this.legacyCustomOptions = var5;
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

   public WorldOptions withBonusChest(boolean var1) {
      return new WorldOptions(this.seed, this.generateStructures, var1, this.legacyCustomOptions);
   }

   public WorldOptions withStructures(boolean var1) {
      return new WorldOptions(this.seed, var1, this.generateBonusChest, this.legacyCustomOptions);
   }

   public WorldOptions withSeed(OptionalLong var1) {
      return new WorldOptions(var1.orElse(randomSeed()), this.generateStructures, this.generateBonusChest, this.legacyCustomOptions);
   }

   public static OptionalLong parseSeed(String var0) {
      var0 = var0.trim();
      if (StringUtils.isEmpty(var0)) {
         return OptionalLong.empty();
      } else {
         try {
            return OptionalLong.of(Long.parseLong(var0));
         } catch (NumberFormatException var2) {
            return OptionalLong.of((long)var0.hashCode());
         }
      }
   }

   public static long randomSeed() {
      return RandomSource.create().nextLong();
   }
}
