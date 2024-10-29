package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
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
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
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

public class BambooStalkBlock extends Block implements BonemealableBlock {
   public static final MapCodec<BambooStalkBlock> CODEC = simpleCodec(BambooStalkBlock::new);
   protected static final float SMALL_LEAVES_AABB_OFFSET = 3.0F;
   protected static final float LARGE_LEAVES_AABB_OFFSET = 5.0F;
   protected static final float COLLISION_AABB_OFFSET = 1.5F;
   protected static final VoxelShape SMALL_SHAPE = Block.box(5.0, 0.0, 5.0, 11.0, 16.0, 11.0);
   protected static final VoxelShape LARGE_SHAPE = Block.box(3.0, 0.0, 3.0, 13.0, 16.0, 13.0);
   protected static final VoxelShape COLLISION_SHAPE = Block.box(6.5, 0.0, 6.5, 9.5, 16.0, 9.5);
   public static final IntegerProperty AGE;
   public static final EnumProperty<BambooLeaves> LEAVES;
   public static final IntegerProperty STAGE;
   public static final int MAX_HEIGHT = 16;
   public static final int STAGE_GROWING = 0;
   public static final int STAGE_DONE_GROWING = 1;
   public static final int AGE_THIN_BAMBOO = 0;
   public static final int AGE_THICK_BAMBOO = 1;

   public MapCodec<BambooStalkBlock> codec() {
      return CODEC;
   }

   public BambooStalkBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, 0)).setValue(LEAVES, BambooLeaves.NONE)).setValue(STAGE, 0));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(AGE, LEAVES, STAGE);
   }

   protected boolean propagatesSkylightDown(BlockState var1) {
      return true;
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      VoxelShape var5 = var1.getValue(LEAVES) == BambooLeaves.LARGE ? LARGE_SHAPE : SMALL_SHAPE;
      Vec3 var6 = var1.getOffset(var3);
      return var5.move(var6.x, var6.y, var6.z);
   }

   protected boolean isPathfindable(BlockState var1, PathComputationType var2) {
      return false;
   }

   protected VoxelShape getCollisionShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      Vec3 var5 = var1.getOffset(var3);
      return COLLISION_SHAPE.move(var5.x, var5.y, var5.z);
   }

   protected boolean isCollisionShapeFullBlock(BlockState var1, BlockGetter var2, BlockPos var3) {
      return false;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      FluidState var2 = var1.getLevel().getFluidState(var1.getClickedPos());
      if (!var2.isEmpty()) {
         return null;
      } else {
         BlockState var3 = var1.getLevel().getBlockState(var1.getClickedPos().below());
         if (var3.is(BlockTags.BAMBOO_PLANTABLE_ON)) {
            if (var3.is(Blocks.BAMBOO_SAPLING)) {
               return (BlockState)this.defaultBlockState().setValue(AGE, 0);
            } else if (var3.is(Blocks.BAMBOO)) {
               int var5 = (Integer)var3.getValue(AGE) > 0 ? 1 : 0;
               return (BlockState)this.defaultBlockState().setValue(AGE, var5);
            } else {
               BlockState var4 = var1.getLevel().getBlockState(var1.getClickedPos().above());
               return var4.is(Blocks.BAMBOO) ? (BlockState)this.defaultBlockState().setValue(AGE, (Integer)var4.getValue(AGE)) : Blocks.BAMBOO_SAPLING.defaultBlockState();
            }
         } else {
            return null;
         }
      }
   }

   protected void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (!var1.canSurvive(var2, var3)) {
         var2.destroyBlock(var3, true);
      }

   }

   protected boolean isRandomlyTicking(BlockState var1) {
      return (Integer)var1.getValue(STAGE) == 0;
   }

   protected void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if ((Integer)var1.getValue(STAGE) == 0) {
         if (var4.nextInt(3) == 0 && var2.isEmptyBlock(var3.above()) && var2.getRawBrightness(var3.above(), 0) >= 9) {
            int var5 = this.getHeightBelowUpToMax(var2, var3) + 1;
            if (var5 < 16) {
               this.growBamboo(var1, var2, var3, var4, var5);
            }
         }

      }
   }

   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      return var2.getBlockState(var3.below()).is(BlockTags.BAMBOO_PLANTABLE_ON);
   }

   protected BlockState updateShape(BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8) {
      if (!var1.canSurvive(var2, var4)) {
         var3.scheduleTick(var4, (Block)this, 1);
      }

      return var5 == Direction.UP && var7.is(Blocks.BAMBOO) && (Integer)var7.getValue(AGE) > (Integer)var1.getValue(AGE) ? (BlockState)var1.cycle(AGE) : super.updateShape(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public boolean isValidBonemealTarget(LevelReader var1, BlockPos var2, BlockState var3) {
      int var4 = this.getHeightAboveUpToMax(var1, var2);
      int var5 = this.getHeightBelowUpToMax(var1, var2);
      return var4 + var5 + 1 < 16 && (Integer)var1.getBlockState(var2.above(var4)).getValue(STAGE) != 1;
   }

   public boolean isBonemealSuccess(Level var1, RandomSource var2, BlockPos var3, BlockState var4) {
      return true;
   }

   public void performBonemeal(ServerLevel var1, RandomSource var2, BlockPos var3, BlockState var4) {
      int var5 = this.getHeightAboveUpToMax(var1, var3);
      int var6 = this.getHeightBelowUpToMax(var1, var3);
      int var7 = var5 + var6 + 1;
      int var8 = 1 + var2.nextInt(2);

      for(int var9 = 0; var9 < var8; ++var9) {
         BlockPos var10 = var3.above(var5);
         BlockState var11 = var1.getBlockState(var10);
         if (var7 >= 16 || (Integer)var11.getValue(STAGE) == 1 || !var1.isEmptyBlock(var10.above())) {
            return;
         }

         this.growBamboo(var11, var1, var10, var2, var7);
         ++var5;
         ++var7;
      }

   }

   protected float getDestroyProgress(BlockState var1, Player var2, BlockGetter var3, BlockPos var4) {
      return var2.getMainHandItem().getItem() instanceof SwordItem ? 1.0F : super.getDestroyProgress(var1, var2, var3, var4);
   }

   protected void growBamboo(BlockState var1, Level var2, BlockPos var3, RandomSource var4, int var5) {
      BlockState var6 = var2.getBlockState(var3.below());
      BlockPos var7 = var3.below(2);
      BlockState var8 = var2.getBlockState(var7);
      BambooLeaves var9 = BambooLeaves.NONE;
      if (var5 >= 1) {
         if (var6.is(Blocks.BAMBOO) && var6.getValue(LEAVES) != BambooLeaves.NONE) {
            if (var6.is(Blocks.BAMBOO) && var6.getValue(LEAVES) != BambooLeaves.NONE) {
               var9 = BambooLeaves.LARGE;
               if (var8.is(Blocks.BAMBOO)) {
                  var2.setBlock(var3.below(), (BlockState)var6.setValue(LEAVES, BambooLeaves.SMALL), 3);
                  var2.setBlock(var7, (BlockState)var8.setValue(LEAVES, BambooLeaves.NONE), 3);
               }
            }
         } else {
            var9 = BambooLeaves.SMALL;
         }
      }

      int var10 = (Integer)var1.getValue(AGE) != 1 && !var8.is(Blocks.BAMBOO) ? 0 : 1;
      int var11 = (var5 < 11 || !(var4.nextFloat() < 0.25F)) && var5 != 15 ? 0 : 1;
      var2.setBlock(var3.above(), (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(AGE, var10)).setValue(LEAVES, var9)).setValue(STAGE, var11), 3);
   }

   protected int getHeightAboveUpToMax(BlockGetter var1, BlockPos var2) {
      int var3;
      for(var3 = 0; var3 < 16 && var1.getBlockState(var2.above(var3 + 1)).is(Blocks.BAMBOO); ++var3) {
      }

      return var3;
   }

   protected int getHeightBelowUpToMax(BlockGetter var1, BlockPos var2) {
      int var3;
      for(var3 = 0; var3 < 16 && var1.getBlockState(var2.below(var3 + 1)).is(Blocks.BAMBOO); ++var3) {
      }

      return var3;
   }

   static {
      AGE = BlockStateProperties.AGE_1;
      LEAVES = BlockStateProperties.BAMBOO_LEAVES;
      STAGE = BlockStateProperties.STAGE;
   }
}
