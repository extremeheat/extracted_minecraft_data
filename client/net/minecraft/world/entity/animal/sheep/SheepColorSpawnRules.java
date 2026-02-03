package net.minecraft.world.entity.animal.sheep;

import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.biome.Biome;

public class SheepColorSpawnRules {
   private static final SheepColorSpawnConfiguration TEMPERATE_SPAWN_CONFIGURATION;
   private static final SheepColorSpawnConfiguration WARM_SPAWN_CONFIGURATION;
   private static final SheepColorSpawnConfiguration COLD_SPAWN_CONFIGURATION;

   public SheepColorSpawnRules() {
      super();
   }

   private static SheepColorProvider commonColors(final DyeColor defaultColor) {
      return weighted(builder().add(single(defaultColor), 499).add(single(DyeColor.PINK), 1).build());
   }

   public static DyeColor getSheepColor(final Holder<Biome> biome, final RandomSource random) {
      SheepColorSpawnConfiguration sheepColorConfiguration = getSheepColorConfiguration(biome);
      return sheepColorConfiguration.colors().get(random);
   }

   private static SheepColorSpawnConfiguration getSheepColorConfiguration(final Holder<Biome> biome) {
      if (biome.is(BiomeTags.SPAWNS_WARM_VARIANT_FARM_ANIMALS)) {
         return WARM_SPAWN_CONFIGURATION;
      } else {
         return biome.is(BiomeTags.SPAWNS_COLD_VARIANT_FARM_ANIMALS) ? COLD_SPAWN_CONFIGURATION : TEMPERATE_SPAWN_CONFIGURATION;
      }
   }

   private static SheepColorProvider weighted(final WeightedList<SheepColorProvider> elements) {
      if (elements.isEmpty()) {
         throw new IllegalArgumentException("List must be non-empty");
      } else {
         return (random) -> ((SheepColorProvider)elements.getRandomOrThrow(random)).get(random);
      }
   }

   private static SheepColorProvider single(final DyeColor color) {
      return (random) -> color;
   }

   private static WeightedList.Builder<SheepColorProvider> builder() {
      return WeightedList.<SheepColorProvider>builder();
   }

   static {
      TEMPERATE_SPAWN_CONFIGURATION = new SheepColorSpawnConfiguration(weighted(builder().add(single(DyeColor.BLACK), 5).add(single(DyeColor.GRAY), 5).add(single(DyeColor.LIGHT_GRAY), 5).add(single(DyeColor.BROWN), 3).add(commonColors(DyeColor.WHITE), 82).build()));
      WARM_SPAWN_CONFIGURATION = new SheepColorSpawnConfiguration(weighted(builder().add(single(DyeColor.GRAY), 5).add(single(DyeColor.LIGHT_GRAY), 5).add(single(DyeColor.WHITE), 5).add(single(DyeColor.BLACK), 3).add(commonColors(DyeColor.BROWN), 82).build()));
      COLD_SPAWN_CONFIGURATION = new SheepColorSpawnConfiguration(weighted(builder().add(single(DyeColor.LIGHT_GRAY), 5).add(single(DyeColor.GRAY), 5).add(single(DyeColor.WHITE), 5).add(single(DyeColor.BROWN), 3).add(commonColors(DyeColor.BLACK), 82).build()));
   }

   private static record SheepColorSpawnConfiguration(SheepColorProvider colors) {
      private SheepColorSpawnConfiguration {
         super();
      }
   }

   @FunctionalInterface
   private interface SheepColorProvider {
      DyeColor get(RandomSource random);
   }
}
