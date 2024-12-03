package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

public abstract class MultifaceSpreadeableBlock extends MultifaceBlock {
   public MultifaceSpreadeableBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   public abstract MapCodec<? extends MultifaceSpreadeableBlock> codec();

   public abstract MultifaceSpreader getSpreader();
}
