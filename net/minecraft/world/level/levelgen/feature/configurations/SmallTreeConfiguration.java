package net.minecraft.world.level.levelgen.feature.configurations;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class SmallTreeConfiguration extends TreeConfiguration {
   public final FoliagePlacer foliagePlacer;
   public final int heightRandA;
   public final int heightRandB;
   public final int trunkHeight;
   public final int trunkHeightRandom;
   public final int trunkTopOffset;
   public final int trunkTopOffsetRandom;
   public final int foliageHeight;
   public final int foliageHeightRandom;
   public final int maxWaterDepth;
   public final boolean ignoreVines;

   protected SmallTreeConfiguration(BlockStateProvider var1, BlockStateProvider var2, FoliagePlacer var3, List var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12, int var13, int var14, boolean var15) {
      super(var1, var2, var4, var5);
      this.foliagePlacer = var3;
      this.heightRandA = var6;
      this.heightRandB = var7;
      this.trunkHeight = var8;
      this.trunkHeightRandom = var9;
      this.trunkTopOffset = var10;
      this.trunkTopOffsetRandom = var11;
      this.foliageHeight = var12;
      this.foliageHeightRandom = var13;
      this.maxWaterDepth = var14;
      this.ignoreVines = var15;
   }

   public Dynamic serialize(DynamicOps var1) {
      Builder var2 = ImmutableMap.builder();
      var2.put(var1.createString("foliage_placer"), this.foliagePlacer.serialize(var1)).put(var1.createString("height_rand_a"), var1.createInt(this.heightRandA)).put(var1.createString("height_rand_b"), var1.createInt(this.heightRandB)).put(var1.createString("trunk_height"), var1.createInt(this.trunkHeight)).put(var1.createString("trunk_height_random"), var1.createInt(this.trunkHeightRandom)).put(var1.createString("trunk_top_offset"), var1.createInt(this.trunkTopOffset)).put(var1.createString("trunk_top_offset_random"), var1.createInt(this.trunkTopOffsetRandom)).put(var1.createString("foliage_height"), var1.createInt(this.foliageHeight)).put(var1.createString("foliage_height_random"), var1.createInt(this.foliageHeightRandom)).put(var1.createString("max_water_depth"), var1.createInt(this.maxWaterDepth)).put(var1.createString("ignore_vines"), var1.createBoolean(this.ignoreVines));
      Dynamic var3 = new Dynamic(var1, var1.createMap(var2.build()));
      return var3.merge(super.serialize(var1));
   }

   public static SmallTreeConfiguration deserialize(Dynamic var0) {
      TreeConfiguration var1 = TreeConfiguration.deserialize(var0);
      FoliagePlacerType var2 = (FoliagePlacerType)Registry.FOLIAGE_PLACER_TYPES.get(new ResourceLocation((String)var0.get("foliage_placer").get("type").asString().orElseThrow(RuntimeException::new)));
      return new SmallTreeConfiguration(var1.trunkProvider, var1.leavesProvider, var2.deserialize(var0.get("foliage_placer").orElseEmptyMap()), var1.decorators, var1.baseHeight, var0.get("height_rand_a").asInt(0), var0.get("height_rand_b").asInt(0), var0.get("trunk_height").asInt(-1), var0.get("trunk_height_random").asInt(0), var0.get("trunk_top_offset").asInt(0), var0.get("trunk_top_offset_random").asInt(0), var0.get("foliage_height").asInt(-1), var0.get("foliage_height_random").asInt(0), var0.get("max_water_depth").asInt(0), var0.get("ignore_vines").asBoolean(false));
   }

   public static class SmallTreeConfigurationBuilder extends TreeConfiguration.TreeConfigurationBuilder {
      private final FoliagePlacer foliagePlacer;
      private List decorators = ImmutableList.of();
      private int baseHeight;
      private int heightRandA;
      private int heightRandB;
      private int trunkHeight = -1;
      private int trunkHeightRandom;
      private int trunkTopOffset;
      private int trunkTopOffsetRandom;
      private int foliageHeight = -1;
      private int foliageHeightRandom;
      private int maxWaterDepth;
      private boolean ignoreVines;

      public SmallTreeConfigurationBuilder(BlockStateProvider var1, BlockStateProvider var2, FoliagePlacer var3) {
         super(var1, var2);
         this.foliagePlacer = var3;
      }

      public SmallTreeConfiguration.SmallTreeConfigurationBuilder decorators(List var1) {
         this.decorators = var1;
         return this;
      }

      public SmallTreeConfiguration.SmallTreeConfigurationBuilder baseHeight(int var1) {
         this.baseHeight = var1;
         return this;
      }

      public SmallTreeConfiguration.SmallTreeConfigurationBuilder heightRandA(int var1) {
         this.heightRandA = var1;
         return this;
      }

      public SmallTreeConfiguration.SmallTreeConfigurationBuilder heightRandB(int var1) {
         this.heightRandB = var1;
         return this;
      }

      public SmallTreeConfiguration.SmallTreeConfigurationBuilder trunkHeight(int var1) {
         this.trunkHeight = var1;
         return this;
      }

      public SmallTreeConfiguration.SmallTreeConfigurationBuilder trunkHeightRandom(int var1) {
         this.trunkHeightRandom = var1;
         return this;
      }

      public SmallTreeConfiguration.SmallTreeConfigurationBuilder trunkTopOffset(int var1) {
         this.trunkTopOffset = var1;
         return this;
      }

      public SmallTreeConfiguration.SmallTreeConfigurationBuilder trunkTopOffsetRandom(int var1) {
         this.trunkTopOffsetRandom = var1;
         return this;
      }

      public SmallTreeConfiguration.SmallTreeConfigurationBuilder foliageHeight(int var1) {
         this.foliageHeight = var1;
         return this;
      }

      public SmallTreeConfiguration.SmallTreeConfigurationBuilder foliageHeightRandom(int var1) {
         this.foliageHeightRandom = var1;
         return this;
      }

      public SmallTreeConfiguration.SmallTreeConfigurationBuilder maxWaterDepth(int var1) {
         this.maxWaterDepth = var1;
         return this;
      }

      public SmallTreeConfiguration.SmallTreeConfigurationBuilder ignoreVines() {
         this.ignoreVines = true;
         return this;
      }

      public SmallTreeConfiguration build() {
         return new SmallTreeConfiguration(this.trunkProvider, this.leavesProvider, this.foliagePlacer, this.decorators, this.baseHeight, this.heightRandA, this.heightRandB, this.trunkHeight, this.trunkHeightRandom, this.trunkTopOffset, this.trunkTopOffsetRandom, this.foliageHeight, this.foliageHeightRandom, this.maxWaterDepth, this.ignoreVines);
      }

      // $FF: synthetic method
      public TreeConfiguration build() {
         return this.build();
      }

      // $FF: synthetic method
      public TreeConfiguration.TreeConfigurationBuilder baseHeight(int var1) {
         return this.baseHeight(var1);
      }
   }
}
