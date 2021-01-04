package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CakeBlock extends Block {
   public static final IntegerProperty BITES;
   protected static final VoxelShape[] SHAPE_BY_BITE;

   protected CakeBlock(Block.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(BITES, 0));
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE_BY_BITE[(Integer)var1.getValue(BITES)];
   }

   public boolean use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      if (!var2.isClientSide) {
         return this.eat(var2, var3, var1, var4);
      } else {
         ItemStack var7 = var4.getItemInHand(var5);
         return this.eat(var2, var3, var1, var4) || var7.isEmpty();
      }
   }

   private boolean eat(LevelAccessor var1, BlockPos var2, BlockState var3, Player var4) {
      if (!var4.canEat(false)) {
         return false;
      } else {
         var4.awardStat(Stats.EAT_CAKE_SLICE);
         var4.getFoodData().eat(2, 0.1F);
         int var5 = (Integer)var3.getValue(BITES);
         if (var5 < 6) {
            var1.setBlock(var2, (BlockState)var3.setValue(BITES, var5 + 1), 3);
         } else {
            var1.removeBlock(var2, false);
         }

         return true;
      }
   }

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      return var2 == Direction.DOWN && !var1.canSurvive(var4, var5) ? Blocks.AIR.defaultBlockState() : super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   public boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      return var2.getBlockState(var3.below()).getMaterial().isSolid();
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(BITES);
   }

   public int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3) {
      return (7 - (Integer)var1.getValue(BITES)) * 2;
   }

   public boolean hasAnalogOutputSignal(BlockState var1) {
      return true;
   }

   public boolean isPathfindable(BlockState var1, BlockGetter var2, BlockPos var3, PathComputationType var4) {
      return false;
   }

   static {
      BITES = BlockStateProperties.BITES;
      SHAPE_BY_BITE = new VoxelShape[]{Block.box(1.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.box(3.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.box(5.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.box(7.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.box(9.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.box(11.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.box(13.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D)};
   }
}
