package net.minecraft.world.level.levelgen.feature.rootplacers;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public abstract class RootPlacer {
   public static final Codec<RootPlacer> CODEC;
   protected final IntProvider trunkOffsetY;
   protected final BlockStateProvider rootProvider;
   protected final Optional<AboveRootPlacement> aboveRootPlacement;

   protected static <P extends RootPlacer> Products.P3<RecordCodecBuilder.Mu<P>, IntProvider, BlockStateProvider, Optional<AboveRootPlacement>> rootPlacerParts(final RecordCodecBuilder.Instance<P> instance) {
      return instance.group(IntProviders.CODEC.fieldOf("trunk_offset_y").forGetter((c) -> c.trunkOffsetY), BlockStateProvider.CODEC.fieldOf("root_provider").forGetter((c) -> c.rootProvider), AboveRootPlacement.CODEC.optionalFieldOf("above_root_placement").forGetter((c) -> c.aboveRootPlacement));
   }

   public RootPlacer(final IntProvider trunkOffsetY, final BlockStateProvider rootProvider, final Optional<AboveRootPlacement> aboveRootPlacement) {
      super();
      this.trunkOffsetY = trunkOffsetY;
      this.rootProvider = rootProvider;
      this.aboveRootPlacement = aboveRootPlacement;
   }

   protected abstract RootPlacerType<?> type();

   public abstract boolean placeRoots(final WorldGenLevel level, final BiConsumer<BlockPos, BlockState> rootSetter, final RandomSource random, final BlockPos origin, final BlockPos trunkOrigin, final TreeFeature tree);

   protected boolean canPlaceRoot(final LevelSimulatedReader level, final BlockPos pos) {
      return TreeFeature.validTreePos(level, pos);
   }

   protected void placeRoot(final WorldGenLevel level, final BiConsumer<BlockPos, BlockState> rootSetter, final RandomSource random, final BlockPos pos, final TreeFeature tree) {
      if (this.canPlaceRoot(level, pos)) {
         rootSetter.accept(pos, this.getPotentiallyWaterloggedState(level, pos, this.rootProvider.getState(level, random, pos)));
         if (this.aboveRootPlacement.isPresent()) {
            AboveRootPlacement abovePlacement = (AboveRootPlacement)this.aboveRootPlacement.get();
            BlockPos above = pos.above();
            if (random.nextFloat() < abovePlacement.aboveRootPlacementChance() && level.isStateAtPosition(above, BlockBehaviour.BlockStateBase::isAir)) {
               rootSetter.accept(above, this.getPotentiallyWaterloggedState(level, above, abovePlacement.aboveRootProvider().getState(level, random, above)));
            }
         }

      }
   }

   protected BlockState getPotentiallyWaterloggedState(final LevelSimulatedReader level, final BlockPos pos, final BlockState state) {
      if (state.hasProperty(BlockStateProperties.WATERLOGGED)) {
         boolean waterlogged = level.isFluidAtPosition(pos, (s) -> s.is(FluidTags.WATER));
         return (BlockState)state.setValue(BlockStateProperties.WATERLOGGED, waterlogged);
      } else {
         return state;
      }
   }

   public BlockPos getTrunkOrigin(final BlockPos origin, final RandomSource random) {
      return origin.above(this.trunkOffsetY.sample(random));
   }

   static {
      CODEC = BuiltInRegistries.ROOT_PLACER_TYPE.byNameCodec().dispatch(RootPlacer::type, RootPlacerType::codec);
   }
}
