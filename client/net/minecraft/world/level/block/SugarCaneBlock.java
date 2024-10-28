package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.Iterator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SugarCaneBlock extends Block {
   public static final MapCodec<SugarCaneBlock> CODEC = simpleCodec(SugarCaneBlock::new);
   public static final IntegerProperty AGE;
   protected static final float AABB_OFFSET = 6.0F;
   protected static final VoxelShape SHAPE;

   public MapCodec<SugarCaneBlock> codec() {
      return CODEC;
   }

   protected SugarCaneBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, 0));
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   protected void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (!var1.canSurvive(var2, var3)) {
         var2.destroyBlock(var3, true);
      }

   }

   protected void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (var2.isEmptyBlock(var3.above())) {
         int var5;
         for(var5 = 1; var2.getBlockState(var3.below(var5)).is(this); ++var5) {
         }

         if (var5 < 3) {
            int var6 = (Integer)var1.getValue(AGE);
            if (var6 == 15) {
               var2.setBlockAndUpdate(var3.above(), this.defaultBlockState());
               var2.setBlock(var3, (BlockState)var1.setValue(AGE, 0), 4);
            } else {
               var2.setBlock(var3, (BlockState)var1.setValue(AGE, var6 + 1), 4);
            }
         }
      }

   }

   protected BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (!var1.canSurvive(var4, var5)) {
         var4.scheduleTick(var5, (Block)this, 1);
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      BlockState var4 = var2.getBlockState(var3.below());
      if (var4.is(this)) {
         return true;
      } else {
         if (var4.is(BlockTags.DIRT) || var4.is(BlockTags.SAND)) {
            BlockPos var5 = var3.below();
            Iterator var6 = Direction.Plane.HORIZONTAL.iterator();

            while(var6.hasNext()) {
               Direction var7 = (Direction)var6.next();
               BlockState var8 = var2.getBlockState(var5.relative(var7));
               FluidState var9 = var2.getFluidState(var5.relative(var7));
               if (var9.is(FluidTags.WATER) || var8.is(Blocks.FROSTED_ICE)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(AGE);
   }

   static {
      AGE = BlockStateProperties.AGE_15;
      SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);
   }
}
