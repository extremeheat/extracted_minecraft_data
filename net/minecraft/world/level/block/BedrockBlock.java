package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public class BedrockBlock extends Block {
   public BedrockBlock(Block.Properties var1) {
      super(var1);
   }

   public boolean isValidSpawn(BlockState var1, BlockGetter var2, BlockPos var3, EntityType var4) {
      return false;
   }
}
