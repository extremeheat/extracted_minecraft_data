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

   private static SheepColorProvider commonColors(DyeColor var0) {
      return weighted(builder().add(single(var0), 499).add(single(DyeColor.PINK), 1).build());
   }

   public static DyeColor getSheepColor(Holder<Biome> var0, RandomSource var1) {
      SheepColorSpawnConfiguration var2 = getSheepColorConfiguration(var0);
      return var2.colors().get(var1);
   }

   private static SheepColorSpawnConfiguration getSheepColorConfiguration(Holder<Biome> var0) {
      if (var0.is(BiomeTags.SPAWNS_WARM_VARIANT_FARM_ANIMALS)) {
         return WARM_SPAWN_CONFIGURATION;
      } else {
         return var0.is(BiomeTags.SPAWNS_COLD_VARIANT_FARM_ANIMALS) ? COLD_SPAWN_CONFIGURATION : TEMPERATE_SPAWN_CONFIGURATION;
      }
   }

   private static SheepColorProvider weighted(WeightedList<SheepColorProvider> var0) {
      if (var0.isEmpty()) {
         throw new IllegalArgumentException("List must be non-empty");
      } else {
         return (var1) -> ((SheepColorProvider)var0.getRandomOrThrow(var1)).get(var1);
      }
   }

   private static SheepColorProvider single(DyeColor var0) {
      return (var1) -> var0;
   }

   private static WeightedList.Builder<SheepColorProvider> builder() {
      return WeightedList.<SheepColorProvider>builder();
   }

   static {
      TEMPERATE_SPAWN_CONFIGURATION = new SheepColorSpawnConfiguration(weighted(builder().add(single(DyeColor.BLACK), 5).add(single(DyeColor.GRAY), 5).add(single(DyeColor.LIGHT_GRAY), 5).add(single(DyeColor.BROWN), 3).add(commonColors(DyeColor.WHITE), 82).build()));
      WARM_SPAWN_CONFIGURATION = new SheepColorSpawnConfiguration(weighted(builder().add(single(DyeColor.GRAY), 5).add(single(DyeColor.LIGHT_GRAY), 5).add(single(DyeColor.WHITE), 5).add(single(DyeColor.BLACK), 3).add(commonColors(DyeColor.BROWN), 82).build()));
      COLD_SPAWN_CONFIGURATION = new SheepColorSpawnConfiguration(weighted(builder().add(single(DyeColor.LIGHT_GRAY), 5).add(single(DyeColor.GRAY), 5).add(single(DyeColor.WHITE), 5).add(single(DyeColor.BROWN), 3).add(commonColors(DyeColor.BLACK), 82).build()));
   }

   static record SheepColorSpawnConfiguration(SheepColorProvider colors) {
      SheepColorSpawnConfiguration(SheepColorProvider var1) {
         super();
         this.colors = var1;
      }
   }

   @FunctionalInterface
   interface SheepColorProvider {
      DyeColor get(RandomSource var1);
   }
}
