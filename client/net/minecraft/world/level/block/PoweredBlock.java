package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class PoweredBlock extends Block {
   public static final MapCodec<PoweredBlock> CODEC = simpleCodec(PoweredBlock::new);

   public MapCodec<PoweredBlock> codec() {
      return CODEC;
   }

   public PoweredBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   protected boolean isSignalSource(BlockState var1) {
      return true;
   }

   protected int getSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return 15;
   }
}
