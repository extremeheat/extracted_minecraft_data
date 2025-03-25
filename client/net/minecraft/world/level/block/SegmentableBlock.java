package net.minecraft.world.level.block;

import java.util.Map;
import java.util.function.Function;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface SegmentableBlock {
   int MIN_SEGMENT = 1;
   int MAX_SEGMENT = 4;
   IntegerProperty AMOUNT = BlockStateProperties.SEGMENT_AMOUNT;

   default Function<BlockState, VoxelShape> getShapeCalculator(EnumProperty<Direction> var1, IntegerProperty var2) {
      Map var3 = Shapes.rotateHorizontal(Block.box(0.0, 0.0, 0.0, 8.0, this.getShapeHeight(), 8.0));
      return (var3x) -> {
         VoxelShape var4 = Shapes.empty();
         Direction var5 = (Direction)var3x.getValue(var1);
         int var6 = (Integer)var3x.getValue(var2);

         for(int var7 = 0; var7 < var6; ++var7) {
            var4 = Shapes.or(var4, (VoxelShape)var3.get(var5));
            var5 = var5.getCounterClockWise();
         }

         return var4.singleEncompassing();
      };
   }

   default IntegerProperty getSegmentAmountProperty() {
      return AMOUNT;
   }

   default double getShapeHeight() {
      return 1.0;
   }

   default boolean canBeReplaced(BlockState var1, BlockPlaceContext var2, IntegerProperty var3) {
      return !var2.isSecondaryUseActive() && var2.getItemInHand().is(var1.getBlock().asItem()) && (Integer)var1.getValue(var3) < 4;
   }

   default BlockState getStateForPlacement(BlockPlaceContext var1, Block var2, IntegerProperty var3, EnumProperty<Direction> var4) {
      BlockState var5 = var1.getLevel().getBlockState(var1.getClickedPos());
      return var5.is(var2) ? (BlockState)var5.setValue(var3, Math.min(4, (Integer)var5.getValue(var3) + 1)) : (BlockState)var2.defaultBlockState().setValue(var4, var1.getHorizontalDirection().getOpposite());
   }
}
