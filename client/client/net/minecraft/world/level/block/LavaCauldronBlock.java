package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class LavaCauldronBlock extends AbstractCauldronBlock {
   public static final MapCodec<LavaCauldronBlock> CODEC = simpleCodec(LavaCauldronBlock::new);

   @Override
   public MapCodec<LavaCauldronBlock> codec() {
      return CODEC;
   }

   public LavaCauldronBlock(BlockBehaviour.Properties var1) {
      super(var1, CauldronInteraction.LAVA);
   }

   @Override
   protected double getContentHeight(BlockState var1) {
      return 0.9375;
   }

   @Override
   public boolean isFull(BlockState var1) {
      return true;
   }

   @Override
   protected void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4) {
      if (this.isEntityInsideContent(var1, var3, var4)) {
         var4.lavaHurt();
      }
   }

   @Override
   protected int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3) {
      return 3;
   }
}