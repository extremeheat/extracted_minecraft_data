package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class WaterCauldronBlock extends AbstractCauldronBlock {
   public static final IntegerProperty LEVEL;

   public WaterCauldronBlock(BlockBehaviour.Properties var1) {
      super(var1, CauldronInteraction.WATER);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(LEVEL, 1));
   }

   protected double getContentHeight(BlockState var1) {
      return (double)(6 + (Integer)var1.getValue(LEVEL) * 3) / 16.0D;
   }

   public void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4) {
      if (!var2.isClientSide && var4.isOnFire() && this.isEntityInsideContent(var1, var3, var4)) {
         var4.clearFire();
         lowerWaterLevel(var1, var2, var3);
      }

   }

   public static void lowerWaterLevel(BlockState var0, Level var1, BlockPos var2) {
      int var3 = (Integer)var0.getValue(LEVEL) - 1;
      var1.setBlockAndUpdate(var2, var3 == 0 ? Blocks.CAULDRON.defaultBlockState() : (BlockState)var0.setValue(LEVEL, var3));
   }

   public void handleRain(BlockState var1, Level var2, BlockPos var3) {
      if (CauldronBlock.shouldHandleRain(var2, var3) && (Integer)var1.getValue(LEVEL) != 3) {
         var2.setBlockAndUpdate(var3, (BlockState)var1.cycle(LEVEL));
      }
   }

   public int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3) {
      return (Integer)var1.getValue(LEVEL);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(LEVEL);
   }

   static {
      LEVEL = BlockStateProperties.LEVEL_CAULDRON;
   }
}
