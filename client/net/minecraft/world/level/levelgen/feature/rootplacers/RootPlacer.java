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
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public abstract class RootPlacer {
   public static final Codec<RootPlacer> CODEC;
   protected final IntProvider trunkOffsetY;
   protected final BlockStateProvider rootProvider;
   protected final Optional<AboveRootPlacement> aboveRootPlacement;

   protected static <P extends RootPlacer> Products.P3<RecordCodecBuilder.Mu<P>, IntProvider, BlockStateProvider, Optional<AboveRootPlacement>> rootPlacerParts(RecordCodecBuilder.Instance<P> var0) {
      return var0.group(IntProvider.CODEC.fieldOf("trunk_offset_y").forGetter((var0x) -> var0x.trunkOffsetY), BlockStateProvider.CODEC.fieldOf("root_provider").forGetter((var0x) -> var0x.rootProvider), AboveRootPlacement.CODEC.optionalFieldOf("above_root_placement").forGetter((var0x) -> var0x.aboveRootPlacement));
   }

   public RootPlacer(IntProvider var1, BlockStateProvider var2, Optional<AboveRootPlacement> var3) {
      super();
      this.trunkOffsetY = var1;
      this.rootProvider = var2;
      this.aboveRootPlacement = var3;
   }

   protected abstract RootPlacerType<?> type();

   public abstract boolean placeRoots(LevelSimulatedReader var1, BiConsumer<BlockPos, BlockState> var2, RandomSource var3, BlockPos var4, BlockPos var5, TreeConfiguration var6);

   protected boolean canPlaceRoot(LevelSimulatedReader var1, BlockPos var2) {
      return TreeFeature.validTreePos(var1, var2);
   }

   protected void placeRoot(LevelSimulatedReader var1, BiConsumer<BlockPos, BlockState> var2, RandomSource var3, BlockPos var4, TreeConfiguration var5) {
      if (this.canPlaceRoot(var1, var4)) {
         var2.accept(var4, this.getPotentiallyWaterloggedState(var1, var4, this.rootProvider.getState(var3, var4)));
         if (this.aboveRootPlacement.isPresent()) {
            AboveRootPlacement var6 = (AboveRootPlacement)this.aboveRootPlacement.get();
            BlockPos var7 = var4.above();
            if (var3.nextFloat() < var6.aboveRootPlacementChance() && var1.isStateAtPosition(var7, BlockBehaviour.BlockStateBase::isAir)) {
               var2.accept(var7, this.getPotentiallyWaterloggedState(var1, var7, var6.aboveRootProvider().getState(var3, var7)));
            }
         }

      }
   }

   protected BlockState getPotentiallyWaterloggedState(LevelSimulatedReader var1, BlockPos var2, BlockState var3) {
      if (var3.hasProperty(BlockStateProperties.WATERLOGGED)) {
         boolean var4 = var1.isFluidAtPosition(var2, (var0) -> var0.is(FluidTags.WATER));
         return (BlockState)var3.setValue(BlockStateProperties.WATERLOGGED, var4);
      } else {
         return var3;
      }
   }

   public BlockPos getTrunkOrigin(BlockPos var1, RandomSource var2) {
      return var1.above(this.trunkOffsetY.sample(var2));
   }

   static {
      CODEC = BuiltInRegistries.ROOT_PLACER_TYPE.byNameCodec().dispatch(RootPlacer::type, RootPlacerType::codec);
   }
}
