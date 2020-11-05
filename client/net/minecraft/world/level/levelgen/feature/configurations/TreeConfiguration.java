package net.minecraft.world.level.levelgen.feature.configurations;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.featuresize.FeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;

public class TreeConfiguration implements FeatureConfiguration {
   public static final Codec<TreeConfiguration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(BlockStateProvider.CODEC.fieldOf("trunk_provider").forGetter((var0x) -> {
         return var0x.trunkProvider;
      }), BlockStateProvider.CODEC.fieldOf("leaves_provider").forGetter((var0x) -> {
         return var0x.leavesProvider;
      }), FoliagePlacer.CODEC.fieldOf("foliage_placer").forGetter((var0x) -> {
         return var0x.foliagePlacer;
      }), TrunkPlacer.CODEC.fieldOf("trunk_placer").forGetter((var0x) -> {
         return var0x.trunkPlacer;
      }), FeatureSize.CODEC.fieldOf("minimum_size").forGetter((var0x) -> {
         return var0x.minimumSize;
      }), TreeDecorator.CODEC.listOf().fieldOf("decorators").forGetter((var0x) -> {
         return var0x.decorators;
      }), Codec.INT.fieldOf("max_water_depth").orElse(0).forGetter((var0x) -> {
         return var0x.maxWaterDepth;
      }), Codec.BOOL.fieldOf("ignore_vines").orElse(false).forGetter((var0x) -> {
         return var0x.ignoreVines;
      }), Heightmap.Types.CODEC.fieldOf("heightmap").forGetter((var0x) -> {
         return var0x.heightmap;
      })).apply(var0, TreeConfiguration::new);
   });
   public final BlockStateProvider trunkProvider;
   public final BlockStateProvider leavesProvider;
   public final List<TreeDecorator> decorators;
   public transient boolean fromSapling;
   public final FoliagePlacer foliagePlacer;
   public final TrunkPlacer trunkPlacer;
   public final FeatureSize minimumSize;
   public final int maxWaterDepth;
   public final boolean ignoreVines;
   public final Heightmap.Types heightmap;

   protected TreeConfiguration(BlockStateProvider var1, BlockStateProvider var2, FoliagePlacer var3, TrunkPlacer var4, FeatureSize var5, List<TreeDecorator> var6, int var7, boolean var8, Heightmap.Types var9) {
      super();
      this.trunkProvider = var1;
      this.leavesProvider = var2;
      this.decorators = var6;
      this.foliagePlacer = var3;
      this.minimumSize = var5;
      this.trunkPlacer = var4;
      this.maxWaterDepth = var7;
      this.ignoreVines = var8;
      this.heightmap = var9;
   }

   public void setFromSapling() {
      this.fromSapling = true;
   }

   public TreeConfiguration withDecorators(List<TreeDecorator> var1) {
      return new TreeConfiguration(this.trunkProvider, this.leavesProvider, this.foliagePlacer, this.trunkPlacer, this.minimumSize, var1, this.maxWaterDepth, this.ignoreVines, this.heightmap);
   }

   public static class TreeConfigurationBuilder {
      public final BlockStateProvider trunkProvider;
      public final BlockStateProvider leavesProvider;
      private final FoliagePlacer foliagePlacer;
      private final TrunkPlacer trunkPlacer;
      private final FeatureSize minimumSize;
      private List<TreeDecorator> decorators = ImmutableList.of();
      private int maxWaterDepth;
      private boolean ignoreVines;
      private Heightmap.Types heightmap;

      public TreeConfigurationBuilder(BlockStateProvider var1, BlockStateProvider var2, FoliagePlacer var3, TrunkPlacer var4, FeatureSize var5) {
         super();
         this.heightmap = Heightmap.Types.OCEAN_FLOOR;
         this.trunkProvider = var1;
         this.leavesProvider = var2;
         this.foliagePlacer = var3;
         this.trunkPlacer = var4;
         this.minimumSize = var5;
      }

      public TreeConfiguration.TreeConfigurationBuilder decorators(List<TreeDecorator> var1) {
         this.decorators = var1;
         return this;
      }

      public TreeConfiguration.TreeConfigurationBuilder maxWaterDepth(int var1) {
         this.maxWaterDepth = var1;
         return this;
      }

      public TreeConfiguration.TreeConfigurationBuilder ignoreVines() {
         this.ignoreVines = true;
         return this;
      }

      public TreeConfiguration.TreeConfigurationBuilder heightmap(Heightmap.Types var1) {
         this.heightmap = var1;
         return this;
      }

      public TreeConfiguration build() {
         return new TreeConfiguration(this.trunkProvider, this.leavesProvider, this.foliagePlacer, this.trunkPlacer, this.minimumSize, this.decorators, this.maxWaterDepth, this.ignoreVines, this.heightmap);
      }
   }
}
