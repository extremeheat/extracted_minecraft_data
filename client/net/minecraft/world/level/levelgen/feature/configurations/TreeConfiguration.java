package net.minecraft.world.level.levelgen.feature.configurations;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
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
   public static final Codec<TreeConfiguration> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               BlockStateProvider.CODEC.fieldOf("trunk_provider").forGetter(var0x -> var0x.trunkProvider),
               TrunkPlacer.CODEC.fieldOf("trunk_placer").forGetter(var0x -> var0x.trunkPlacer),
               BlockStateProvider.CODEC.fieldOf("foliage_provider").forGetter(var0x -> var0x.foliageProvider),
               FoliagePlacer.CODEC.fieldOf("foliage_placer").forGetter(var0x -> var0x.foliagePlacer),
               RootPlacer.CODEC.optionalFieldOf("root_placer").forGetter(var0x -> var0x.rootPlacer),
               BlockStateProvider.CODEC.fieldOf("dirt_provider").forGetter(var0x -> var0x.dirtProvider),
               FeatureSize.CODEC.fieldOf("minimum_size").forGetter(var0x -> var0x.minimumSize),
               TreeDecorator.CODEC.listOf().fieldOf("decorators").forGetter(var0x -> var0x.decorators),
               Codec.BOOL.fieldOf("ignore_vines").orElse(false).forGetter(var0x -> var0x.ignoreVines),
               Codec.BOOL.fieldOf("force_dirt").orElse(false).forGetter(var0x -> var0x.forceDirt)
            )
            .apply(var0, TreeConfiguration::new)
   );
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

   protected TreeConfiguration(
      BlockStateProvider var1,
      TrunkPlacer var2,
      BlockStateProvider var3,
      FoliagePlacer var4,
      Optional<RootPlacer> var5,
      BlockStateProvider var6,
      FeatureSize var7,
      List<TreeDecorator> var8,
      boolean var9,
      boolean var10
   ) {
      super();
      this.trunkProvider = var1;
      this.trunkPlacer = var2;
      this.foliageProvider = var3;
      this.foliagePlacer = var4;
      this.rootPlacer = var5;
      this.dirtProvider = var6;
      this.minimumSize = var7;
      this.decorators = var8;
      this.ignoreVines = var9;
      this.forceDirt = var10;
   }

   public static class TreeConfigurationBuilder {
      public final BlockStateProvider trunkProvider;
      private final TrunkPlacer trunkPlacer;
      public final BlockStateProvider foliageProvider;
      private final FoliagePlacer foliagePlacer;
      private final Optional<RootPlacer> rootPlacer;
      private BlockStateProvider dirtProvider;
      private final FeatureSize minimumSize;
      private List<TreeDecorator> decorators = ImmutableList.of();
      private boolean ignoreVines;
      private boolean forceDirt;

      public TreeConfigurationBuilder(
         BlockStateProvider var1, TrunkPlacer var2, BlockStateProvider var3, FoliagePlacer var4, Optional<RootPlacer> var5, FeatureSize var6
      ) {
         super();
         this.trunkProvider = var1;
         this.trunkPlacer = var2;
         this.foliageProvider = var3;
         this.dirtProvider = BlockStateProvider.simple(Blocks.DIRT);
         this.foliagePlacer = var4;
         this.rootPlacer = var5;
         this.minimumSize = var6;
      }

      public TreeConfigurationBuilder(BlockStateProvider var1, TrunkPlacer var2, BlockStateProvider var3, FoliagePlacer var4, FeatureSize var5) {
         this(var1, var2, var3, var4, Optional.empty(), var5);
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
         return new TreeConfiguration(
            this.trunkProvider,
            this.trunkPlacer,
            this.foliageProvider,
            this.foliagePlacer,
            this.rootPlacer,
            this.dirtProvider,
            this.minimumSize,
            this.decorators,
            this.ignoreVines,
            this.forceDirt
         );
      }
   }
}
