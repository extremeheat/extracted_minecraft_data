package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class PlayerHeadBlock extends SkullBlock {
   public static final MapCodec<PlayerHeadBlock> CODEC = simpleCodec(PlayerHeadBlock::new);

   @Override
   public MapCodec<PlayerHeadBlock> codec() {
      return CODEC;
   }

   protected PlayerHeadBlock(BlockBehaviour.Properties var1) {
      super(SkullBlock.Types.PLAYER, var1);
   }
}
