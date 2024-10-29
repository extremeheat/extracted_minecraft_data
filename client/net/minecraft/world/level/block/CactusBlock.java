package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.Iterator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CactusBlock extends Block {
   public static final MapCodec<CactusBlock> CODEC = simpleCodec(CactusBlock::new);
   public static final IntegerProperty AGE;
   public static final int MAX_AGE = 15;
   protected static final int AABB_OFFSET = 1;
   protected static final VoxelShape COLLISION_SHAPE;
   protected static final VoxelShape OUTLINE_SHAPE;

   public MapCodec<CactusBlock> codec() {
      return CODEC;
   }

   protected CactusBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, 0));
   }

   protected void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (!var1.canSurvive(var2, var3)) {
         var2.destroyBlock(var3, true);
      }

   }

   protected void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      BlockPos var5 = var3.above();
      if (var2.isEmptyBlock(var5)) {
         int var6;
         for(var6 = 1; var2.getBlockState(var3.below(var6)).is(this); ++var6) {
         }

         if (var6 < 3) {
            int var7 = (Integer)var1.getValue(AGE);
            if (var7 == 15) {
               var2.setBlockAndUpdate(var5, this.defaultBlockState());
               BlockState var8 = (BlockState)var1.setValue(AGE, 0);
               var2.setBlock(var3, var8, 4);
               var2.neighborChanged(var8, var5, this, (Orientation)null, false);
            } else {
               var2.setBlock(var3, (BlockState)var1.setValue(AGE, var7 + 1), 4);
            }

         }
      }
   }

   protected VoxelShape getCollisionShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return COLLISION_SHAPE;
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return OUTLINE_SHAPE;
   }

   protected BlockState updateShape(BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8) {
      if (!var1.canSurvive(var2, var4)) {
         var3.scheduleTick(var4, (Block)this, 1);
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      Iterator var4 = Direction.Plane.HORIZONTAL.iterator();

      Direction var5;
      BlockState var6;
      do {
         if (!var4.hasNext()) {
            BlockState var7 = var2.getBlockState(var3.below());
            return (var7.is(Blocks.CACTUS) || var7.is(BlockTags.SAND)) && !var2.getBlockState(var3.above()).liquid();
         }

         var5 = (Direction)var4.next();
         var6 = var2.getBlockState(var3.relative(var5));
      } while(!var6.isSolid() && !var2.getFluidState(var3.relative(var5)).is(FluidTags.LAVA));

      return false;
   }

   protected void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4) {
      var4.hurt(var2.damageSources().cactus(), 1.0F);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(AGE);
   }

   protected boolean isPathfindable(BlockState var1, PathComputationType var2) {
      return false;
   }

   static {
      AGE = BlockStateProperties.AGE_15;
      COLLISION_SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 15.0, 15.0);
      OUTLINE_SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);
   }
}
