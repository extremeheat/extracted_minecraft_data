package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class HalfTransparentBlock extends Block {
   public static final MapCodec<HalfTransparentBlock> CODEC = simpleCodec(HalfTransparentBlock::new);

   @Override
   protected MapCodec<? extends HalfTransparentBlock> codec() {
      return CODEC;
   }

   protected HalfTransparentBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   @Override
   protected boolean skipRendering(BlockState var1, BlockState var2, Direction var3) {
      return var2.is(this) ? true : super.skipRendering(var1, var2, var3);
   }
}
