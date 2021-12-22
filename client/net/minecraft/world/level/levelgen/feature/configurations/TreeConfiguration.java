package net.minecraft.world.level.levelgen.feature.configurations;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.featuresize.FeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;

public class TreeConfiguration implements FeatureConfiguration {
   public static final Codec<TreeConfiguration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(BlockStateProvider.CODEC.fieldOf("trunk_provider").forGetter((var0x) -> {
         return var0x.trunkProvider;
      }), TrunkPlacer.CODEC.fieldOf("trunk_placer").forGetter((var0x) -> {
         return var0x.trunkPlacer;
      }), BlockStateProvider.CODEC.fieldOf("foliage_provider").forGetter((var0x) -> {
         return var0x.foliageProvider;
      }), FoliagePlacer.CODEC.fieldOf("foliage_placer").forGetter((var0x) -> {
         return var0x.foliagePlacer;
      }), BlockStateProvider.CODEC.fieldOf("dirt_provider").forGetter((var0x) -> {
         return var0x.dirtProvider;
      }), FeatureSize.CODEC.fieldOf("minimum_size").forGetter((var0x) -> {
         return var0x.minimumSize;
      }), TreeDecorator.CODEC.listOf().fieldOf("decorators").forGetter((var0x) -> {
         return var0x.decorators;
      }), Codec.BOOL.fieldOf("ignore_vines").orElse(false).forGetter((var0x) -> {
         return var0x.ignoreVines;
      }), Codec.BOOL.fieldOf("force_dirt").orElse(false).forGetter((var0x) -> {
         return var0x.forceDirt;
      })).apply(var0, TreeConfiguration::new);
   });
   public final BlockStateProvider trunkProvider;
   public final BlockStateProvider dirtProvider;
   public final TrunkPlacer trunkPlacer;
   public final BlockStateProvider foliageProvider;
   public final FoliagePlacer foliagePlacer;
   public final FeatureSize minimumSize;
   public final List<TreeDecorator> decorators;
   public final boolean ignoreVines;
   public final boolean forceDirt;

   protected TreeConfiguration(BlockStateProvider var1, TrunkPlacer var2, BlockStateProvider var3, FoliagePlacer var4, BlockStateProvider var5, FeatureSize var6, List<TreeDecorator> var7, boolean var8, boolean var9) {
      super();
      this.trunkProvider = var1;
      this.trunkPlacer = var2;
      this.foliageProvider = var3;
      this.foliagePlacer = var4;
      this.dirtProvider = var5;
      this.minimumSize = var6;
      this.decorators = var7;
      this.ignoreVines = var8;
      this.forceDirt = var9;
   }

   public static class TreeConfigurationBuilder {
      public final BlockStateProvider trunkProvider;
      private final TrunkPlacer trunkPlacer;
      public final BlockStateProvider foliageProvider;
      private final FoliagePlacer foliagePlacer;
      private BlockStateProvider dirtProvider;
      private final FeatureSize minimumSize;
      private List<TreeDecorator> decorators = ImmutableList.of();
      private boolean ignoreVines;
      private boolean forceDirt;

      public TreeConfigurationBuilder(BlockStateProvider var1, TrunkPlacer var2, BlockStateProvider var3, FoliagePlacer var4, FeatureSize var5) {
         super();
         this.trunkProvider = var1;
         this.trunkPlacer = var2;
         this.foliageProvider = var3;
         this.dirtProvider = BlockStateProvider.simple(Blocks.DIRT);
         this.foliagePlacer = var4;
         this.minimumSize = var5;
      }

      public TreeConfiguration.TreeConfigurationBuilder dirt(BlockStateProvider var1) {
         this.dirtProvider = var1;
         return this;
      }

      public TreeConfiguration.TreeConfigurationBuilder decorators(List<TreeDecorator> var1) {
         this.decorators = var1;
         return this;
      }

      public TreeConfiguration.TreeConfigurationBuilder ignoreVines() {
         this.ignoreVines = true;
         return this;
      }

      public TreeConfiguration.TreeConfigurationBuilder forceDirt() {
         this.forceDirt = true;
         return this;
      }

      public TreeConfiguration build() {
         return new TreeConfiguration(this.trunkProvider, this.trunkPlacer, this.foliageProvider, this.foliagePlacer, this.dirtProvider, this.minimumSize, this.decorators, this.ignoreVines, this.forceDirt);
      }
   }
}
