package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SnowLayerBlock extends Block {
   public static final MapCodec<SnowLayerBlock> CODEC = simpleCodec(SnowLayerBlock::new);
   public static final int MAX_HEIGHT = 8;
   public static final IntegerProperty LAYERS;
   protected static final VoxelShape[] SHAPE_BY_LAYER;
   public static final int HEIGHT_IMPASSABLE = 5;

   public MapCodec<SnowLayerBlock> codec() {
      return CODEC;
   }

   protected SnowLayerBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(LAYERS, 1));
   }

   protected boolean isPathfindable(BlockState var1, PathComputationType var2) {
      switch (var2) {
         case LAND -> {
            return (Integer)var1.getValue(LAYERS) < 5;
         }
         case WATER -> {
            return false;
         }
         case AIR -> {
            return false;
         }
         default -> {
            return false;
         }
      }
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE_BY_LAYER[(Integer)var1.getValue(LAYERS)];
   }

   protected VoxelShape getCollisionShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE_BY_LAYER[(Integer)var1.getValue(LAYERS) - 1];
   }

   protected VoxelShape getBlockSupportShape(BlockState var1, BlockGetter var2, BlockPos var3) {
      return SHAPE_BY_LAYER[(Integer)var1.getValue(LAYERS)];
   }

   protected VoxelShape getVisualShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE_BY_LAYER[(Integer)var1.getValue(LAYERS)];
   }

   protected boolean useShapeForLightOcclusion(BlockState var1) {
      return true;
   }

   protected float getShadeBrightness(BlockState var1, BlockGetter var2, BlockPos var3) {
      return (Integer)var1.getValue(LAYERS) == 8 ? 0.2F : 1.0F;
   }

   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      BlockState var4 = var2.getBlockState(var3.below());
      if (var4.is(BlockTags.SNOW_LAYER_CANNOT_SURVIVE_ON)) {
         return false;
      } else if (var4.is(BlockTags.SNOW_LAYER_CAN_SURVIVE_ON)) {
         return true;
      } else {
         return Block.isFaceFull(var4.getCollisionShape(var2, var3.below()), Direction.UP) || var4.is(this) && (Integer)var4.getValue(LAYERS) == 8;
      }
   }

   protected BlockState updateShape(BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8) {
      return !var1.canSurvive(var2, var4) ? Blocks.AIR.defaultBlockState() : super.updateShape(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   protected void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (var2.getBrightness(LightLayer.BLOCK, var3) > 11) {
         dropResources(var1, var2, var3);
         var2.removeBlock(var3, false);
      }

   }

   protected boolean canBeReplaced(BlockState var1, BlockPlaceContext var2) {
      int var3 = (Integer)var1.getValue(LAYERS);
      if (var2.getItemInHand().is(this.asItem()) && var3 < 8) {
         if (var2.replacingClickedOnBlock()) {
            return var2.getClickedFace() == Direction.UP;
         } else {
            return true;
         }
      } else {
         return var3 == 1;
      }
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      BlockState var2 = var1.getLevel().getBlockState(var1.getClickedPos());
      if (var2.is(this)) {
         int var3 = (Integer)var2.getValue(LAYERS);
         return (BlockState)var2.setValue(LAYERS, Math.min(8, var3 + 1));
      } else {
         return super.getStateForPlacement(var1);
      }
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(LAYERS);
   }

   static {
      LAYERS = BlockStateProperties.LAYERS;
      SHAPE_BY_LAYER = new VoxelShape[]{Shapes.empty(), Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 6.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 10.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 14.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)};
   }
}
