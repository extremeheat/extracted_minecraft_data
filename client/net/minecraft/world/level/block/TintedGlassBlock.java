package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class TintedGlassBlock extends TransparentBlock {
   public static final MapCodec<TintedGlassBlock> CODEC = simpleCodec(TintedGlassBlock::new);

   public MapCodec<TintedGlassBlock> codec() {
      return CODEC;
   }

   public TintedGlassBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   protected boolean propagatesSkylightDown(BlockState var1) {
      return false;
   }

   protected int getLightBlock(BlockState var1) {
      return 15;
   }
}
