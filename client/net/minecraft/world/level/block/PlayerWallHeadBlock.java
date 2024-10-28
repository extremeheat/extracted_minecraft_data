package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class PlayerWallHeadBlock extends WallSkullBlock {
   public static final MapCodec<PlayerWallHeadBlock> CODEC = simpleCodec(PlayerWallHeadBlock::new);

   public MapCodec<PlayerWallHeadBlock> codec() {
      return CODEC;
   }

   protected PlayerWallHeadBlock(BlockBehaviour.Properties var1) {
      super(SkullBlock.Types.PLAYER, var1);
   }
}
