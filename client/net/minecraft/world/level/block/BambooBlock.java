package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BambooLeaves;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BambooBlock extends Block implements BonemealableBlock {
   protected static final float SMALL_LEAVES_AABB_OFFSET = 3.0F;
   protected static final float LARGE_LEAVES_AABB_OFFSET = 5.0F;
   protected static final float COLLISION_AABB_OFFSET = 1.5F;
   protected static final VoxelShape SMALL_SHAPE = Block.box(5.0, 0.0, 5.0, 11.0, 16.0, 11.0);
   protected static final VoxelShape LARGE_SHAPE = Block.box(3.0, 0.0, 3.0, 13.0, 16.0, 13.0);
   protected static final VoxelShape COLLISION_SHAPE = Block.box(6.5, 0.0, 6.5, 9.5, 16.0, 9.5);
   public static final IntegerProperty AGE = BlockStateProperties.AGE_1;
   public static final EnumProperty<BambooLeaves> LEAVES = BlockStateProperties.BAMBOO_LEAVES;
   public static final IntegerProperty STAGE = BlockStateProperties.STAGE;
   public static final int MAX_HEIGHT = 16;
   public static final int STAGE_GROWING = 0;
   public static final int STAGE_DONE_GROWING = 1;
   public static final int AGE_THIN_BAMBOO = 0;
   public static final int AGE_THICK_BAMBOO = 1;

   public BambooBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(
         this.stateDefinition.any().setValue(AGE, Integer.valueOf(0)).setValue(LEAVES, BambooLeaves.NONE).setValue(STAGE, Integer.valueOf(0))
      );
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(AGE, LEAVES, STAGE);
   }

   @Override
   public boolean propagatesSkylightDown(BlockState var1, BlockGetter var2, BlockPos var3) {
      return true;
   }

   @Override
   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      VoxelShape var5 = var1.getValue(LEAVES) == BambooLeaves.LARGE ? LARGE_SHAPE : SMALL_SHAPE;
      Vec3 var6 = var1.getOffset(var2, var3);
      return var5.move(var6.x, var6.y, var6.z);
   }

   @Override
   public boolean isPathfindable(BlockState var1, BlockGetter var2, BlockPos var3, PathComputationType var4) {
      return false;
   }

   @Override
   public VoxelShape getCollisionShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      Vec3 var5 = var1.getOffset(var2, var3);
      return COLLISION_SHAPE.move(var5.x, var5.y, var5.z);
   }

   @Override
   public boolean isCollisionShapeFullBlock(BlockState var1, BlockGetter var2, BlockPos var3) {
      return false;
   }

   @Nullable
   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      FluidState var2 = var1.getLevel().getFluidState(var1.getClickedPos());
      if (!var2.isEmpty()) {
         return null;
      } else {
         BlockState var3 = var1.getLevel().getBlockState(var1.getClickedPos().below());
         if (var3.is(BlockTags.BAMBOO_PLANTABLE_ON)) {
            if (var3.is(Blocks.BAMBOO_SAPLING)) {
               return this.defaultBlockState().setValue(AGE, Integer.valueOf(0));
            } else if (var3.is(Blocks.BAMBOO)) {
               int var5 = var3.getValue(AGE) > 0 ? 1 : 0;
               return this.defaultBlockState().setValue(AGE, Integer.valueOf(var5));
            } else {
               BlockState var4 = var1.getLevel().getBlockState(var1.getClickedPos().above());
               return var4.is(Blocks.BAMBOO) ? this.defaultBlockState().setValue(AGE, var4.getValue(AGE)) : Blocks.BAMBOO_SAPLING.defaultBlockState();
            }
         } else {
            return null;
         }
      }
   }

   @Override
   public void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (!var1.canSurvive(var2, var3)) {
         var2.destroyBlock(var3, true);
      }
   }

   @Override
   public boolean isRandomlyTicking(BlockState var1) {
      return var1.getValue(STAGE) == 0;
   }

   @Override
   public void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (var1.getValue(STAGE) == 0) {
         if (var4.nextInt(3) == 0 && var2.isEmptyBlock(var3.above()) && var2.getRawBrightness(var3.above(), 0) >= 9) {
            int var5 = this.getHeightBelowUpToMax(var2, var3) + 1;
            if (var5 < 16) {
               this.growBamboo(var1, var2, var3, var4, var5);
            }
         }
      }
   }

   @Override
   public boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      return var2.getBlockState(var3.below()).is(BlockTags.BAMBOO_PLANTABLE_ON);
   }

   @Override
   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (!var1.canSurvive(var4, var5)) {
         var4.scheduleTick(var5, this, 1);
      }

      if (var2 == Direction.UP && var3.is(Blocks.BAMBOO) && var3.getValue(AGE) > var1.getValue(AGE)) {
         var4.setBlock(var5, var1.cycle(AGE), 2);
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public boolean isValidBonemealTarget(BlockGetter var1, BlockPos var2, BlockState var3, boolean var4) {
      int var5 = this.getHeightAboveUpToMax(var1, var2);
      int var6 = this.getHeightBelowUpToMax(var1, var2);
      return var5 + var6 + 1 < 16 && var1.getBlockState(var2.above(var5)).getValue(STAGE) != 1;
   }

   @Override
   public boolean isBonemealSuccess(Level var1, RandomSource var2, BlockPos var3, BlockState var4) {
      return true;
   }

   @Override
   public void performBonemeal(ServerLevel var1, RandomSource var2, BlockPos var3, BlockState var4) {
      int var5 = this.getHeightAboveUpToMax(var1, var3);
      int var6 = this.getHeightBelowUpToMax(var1, var3);
      int var7 = var5 + var6 + 1;
      int var8 = 1 + var2.nextInt(2);

      for(int var9 = 0; var9 < var8; ++var9) {
         BlockPos var10 = var3.above(var5);
         BlockState var11 = var1.getBlockState(var10);
         if (var7 >= 16 || var11.getValue(STAGE) == 1 || !var1.isEmptyBlock(var10.above())) {
            return;
         }

         this.growBamboo(var11, var1, var10, var2, var7);
         ++var5;
         ++var7;
      }
   }

   @Override
   public float getDestroyProgress(BlockState var1, Player var2, BlockGetter var3, BlockPos var4) {
      return var2.getMainHandItem().getItem() instanceof SwordItem ? 1.0F : super.getDestroyProgress(var1, var2, var3, var4);
   }

   protected void growBamboo(BlockState var1, Level var2, BlockPos var3, RandomSource var4, int var5) {
      BlockState var6 = var2.getBlockState(var3.below());
      BlockPos var7 = var3.below(2);
      BlockState var8 = var2.getBlockState(var7);
      BambooLeaves var9 = BambooLeaves.NONE;
      if (var5 >= 1) {
         if (!var6.is(Blocks.BAMBOO) || var6.getValue(LEAVES) == BambooLeaves.NONE) {
            var9 = BambooLeaves.SMALL;
         } else if (var6.is(Blocks.BAMBOO) && var6.getValue(LEAVES) != BambooLeaves.NONE) {
            var9 = BambooLeaves.LARGE;
            if (var8.is(Blocks.BAMBOO)) {
               var2.setBlock(var3.below(), var6.setValue(LEAVES, BambooLeaves.SMALL), 3);
               var2.setBlock(var7, var8.setValue(LEAVES, BambooLeaves.NONE), 3);
            }
         }
      }

      int var10 = var1.getValue(AGE) != 1 && !var8.is(Blocks.BAMBOO) ? 0 : 1;
      int var11 = (var5 < 11 || !(var4.nextFloat() < 0.25F)) && var5 != 15 ? 0 : 1;
      var2.setBlock(
         var3.above(), this.defaultBlockState().setValue(AGE, Integer.valueOf(var10)).setValue(LEAVES, var9).setValue(STAGE, Integer.valueOf(var11)), 3
      );
   }

   protected int getHeightAboveUpToMax(BlockGetter var1, BlockPos var2) {
      int var3 = 0;

      while(var3 < 16 && var1.getBlockState(var2.above(var3 + 1)).is(Blocks.BAMBOO)) {
         ++var3;
      }

      return var3;
   }

   protected int getHeightBelowUpToMax(BlockGetter var1, BlockPos var2) {
      int var3 = 0;

      while(var3 < 16 && var1.getBlockState(var2.below(var3 + 1)).is(Blocks.BAMBOO)) {
         ++var3;
      }

      return var3;
   }
}
