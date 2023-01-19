package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class CoralBlock extends Block {
   private final Block deadBlock;

   public CoralBlock(Block var1, BlockBehaviour.Properties var2) {
      super(var2);
      this.deadBlock = var1;
   }

   @Override
   public void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (!this.scanForWater(var2, var3)) {
         var2.setBlock(var3, this.deadBlock.defaultBlockState(), 2);
      }
   }

   @Override
   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (!this.scanForWater(var4, var5)) {
         var4.scheduleTick(var5, this, 60 + var4.getRandom().nextInt(40));
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   protected boolean scanForWater(BlockGetter var1, BlockPos var2) {
      for(Direction var6 : Direction.values()) {
         FluidState var7 = var1.getFluidState(var2.relative(var6));
         if (var7.is(FluidTags.WATER)) {
            return true;
         }
      }

      return false;
   }

   @Nullable
   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      if (!this.scanForWater(var1.getLevel(), var1.getClickedPos())) {
         var1.getLevel().scheduleTick(var1.getClickedPos(), this, 60 + var1.getLevel().getRandom().nextInt(40));
      }

      return this.defaultBlockState();
   }
}
