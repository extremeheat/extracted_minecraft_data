package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class LavaCauldronBlock extends AbstractCauldronBlock {
   public LavaCauldronBlock(BlockBehaviour.Properties var1) {
      super(var1, CauldronInteraction.LAVA);
   }

   protected double getContentHeight(BlockState var1) {
      return 0.9375D;
   }

   public boolean isFull(BlockState var1) {
      return true;
   }

   public void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4) {
      if (this.isEntityInsideContent(var1, var3, var4)) {
         var4.lavaHurt();
      }

   }

   public int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3) {
      return 3;
   }
}
