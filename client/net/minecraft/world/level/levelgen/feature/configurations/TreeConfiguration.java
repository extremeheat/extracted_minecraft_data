package net.minecraft.world.level.levelgen.feature.configurations;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.featuresize.FeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;

public class TreeConfiguration implements FeatureConfiguration {
   public static final Codec<TreeConfiguration> CODEC = RecordCodecBuilder.create((i) -> i.group(BlockStateProvider.CODEC.fieldOf("trunk_provider").forGetter((c) -> c.trunkProvider), TrunkPlacer.CODEC.fieldOf("trunk_placer").forGetter((c) -> c.trunkPlacer), BlockStateProvider.CODEC.fieldOf("foliage_provider").forGetter((c) -> c.foliageProvider), FoliagePlacer.CODEC.fieldOf("foliage_placer").forGetter((c) -> c.foliagePlacer), RootPlacer.CODEC.optionalFieldOf("root_placer").forGetter((c) -> c.rootPlacer), BlockStateProvider.CODEC.fieldOf("dirt_provider").forGetter((c) -> c.dirtProvider), FeatureSize.CODEC.fieldOf("minimum_size").forGetter((c) -> c.minimumSize), TreeDecorator.CODEC.listOf().fieldOf("decorators").forGetter((c) -> c.decorators), Codec.BOOL.fieldOf("ignore_vines").orElse(false).forGetter((c) -> c.ignoreVines), Codec.BOOL.fieldOf("force_dirt").orElse(false).forGetter((c) -> c.forceDirt)).apply(i, TreeConfiguration::new));
   public final BlockStateProvider trunkProvider;
   public final BlockStateProvider dirtProvider;
   public final TrunkPlacer trunkPlacer;
   public final BlockStateProvider foliageProvider;
   public final FoliagePlacer foliagePlacer;
   public final Optional<RootPlacer> rootPlacer;
   public final FeatureSize minimumSize;
   public final List<TreeDecorator> decorators;
   public final boolean ignoreVines;
   public final boolean forceDirt;

   protected TreeConfiguration(final BlockStateProvider trunkProvider, final TrunkPlacer trunkPlacer, final BlockStateProvider foliageProvider, final FoliagePlacer foliagePlacer, final Optional<RootPlacer> rootPlacer, final BlockStateProvider dirtProvider, final FeatureSize minimumSize, final List<TreeDecorator> decorators, final boolean ignoreVines, final boolean forceDirt) {
      super();
      this.trunkProvider = trunkProvider;
      this.trunkPlacer = trunkPlacer;
      this.foliageProvider = foliageProvider;
      this.foliagePlacer = foliagePlacer;
      this.rootPlacer = rootPlacer;
      this.dirtProvider = dirtProvider;
      this.minimumSize = minimumSize;
      this.decorators = decorators;
      this.ignoreVines = ignoreVines;
      this.forceDirt = forceDirt;
   }

   public static class TreeConfigurationBuilder {
      public final BlockStateProvider trunkProvider;
      private final TrunkPlacer trunkPlacer;
      public final BlockStateProvider foliageProvider;
      private final FoliagePlacer foliagePlacer;
      private final Optional<RootPlacer> rootPlacer;
      private BlockStateProvider dirtProvider;
      private final FeatureSize minimumSize;
      private List<TreeDecorator> decorators;
      private boolean ignoreVines;
      private boolean forceDirt;

      public TreeConfigurationBuilder(final BlockStateProvider trunkProvider, final TrunkPlacer trunkPlacer, final BlockStateProvider foliageProvider, final FoliagePlacer foliagePlacer, final Optional<RootPlacer> rootPlacer, final FeatureSize minimumSize) {
         super();
         this.decorators = ImmutableList.of();
         this.trunkProvider = trunkProvider;
         this.trunkPlacer = trunkPlacer;
         this.foliageProvider = foliageProvider;
         this.dirtProvider = BlockStateProvider.simple(Blocks.DIRT);
         this.foliagePlacer = foliagePlacer;
         this.rootPlacer = rootPlacer;
         this.minimumSize = minimumSize;
      }

      public TreeConfigurationBuilder(final BlockStateProvider trunkProvider, final TrunkPlacer trunkPlacer, final BlockStateProvider foliageProvider, final FoliagePlacer foliagePlacer, final FeatureSize minimumSize) {
         this(trunkProvider, trunkPlacer, foliageProvider, foliagePlacer, Optional.empty(), minimumSize);
      }

      public TreeConfigurationBuilder dirt(final BlockStateProvider dirtProvider) {
         this.dirtProvider = dirtProvider;
         return this;
      }

      public TreeConfigurationBuilder decorators(final List<TreeDecorator> decorators) {
         this.decorators = decorators;
         return this;
      }

      public TreeConfigurationBuilder ignoreVines() {
         this.ignoreVines = true;
         return this;
      }

      public TreeConfigurationBuilder forceDirt() {
         this.forceDirt = true;
         return this;
      }

      public TreeConfiguration build() {
         return new TreeConfiguration(this.trunkProvider, this.trunkPlacer, this.foliageProvider, this.foliagePlacer, this.rootPlacer, this.dirtProvider, this.minimumSize, this.decorators, this.ignoreVines, this.forceDirt);
      }
   }
}
