package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

public class CoralFanBlock extends BaseCoralFanBlock {
   private final Block deadBlock;

   protected CoralFanBlock(Block var1, BlockBehaviour.Properties var2) {
      super(var2);
      this.deadBlock = var1;
   }

   public void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      this.tryScheduleDieTick(var1, var2, var3);
   }

   public void tick(BlockState var1, ServerLevel var2, BlockPos var3, Random var4) {
      if (!scanForWater(var1, var2, var3)) {
         var2.setBlock(var3, (BlockState)this.deadBlock.defaultBlockState().setValue(WATERLOGGED, false), 2);
      }

   }

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (var2 == Direction.DOWN && !var1.canSurvive(var4, var5)) {
         return Blocks.AIR.defaultBlockState();
      } else {
         this.tryScheduleDieTick(var1, var4, var5);
         if ((Boolean)var1.getValue(WATERLOGGED)) {
            var4.getLiquidTicks().scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(var4));
         }

         return super.updateShape(var1, var2, var3, var4, var5, var6);
      }
   }
}
