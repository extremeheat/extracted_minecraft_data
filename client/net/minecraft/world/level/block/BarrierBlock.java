package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public class BarrierBlock extends Block {
   protected BarrierBlock(Block.Properties var1) {
      super(var1);
   }

   public boolean propagatesSkylightDown(BlockState var1, BlockGetter var2, BlockPos var3) {
      return true;
   }

   public RenderShape getRenderShape(BlockState var1) {
      return RenderShape.INVISIBLE;
   }

   public boolean canOcclude(BlockState var1) {
      return false;
   }

   public float getShadeBrightness(BlockState var1, BlockGetter var2, BlockPos var3) {
      return 1.0F;
   }

   public boolean isValidSpawn(BlockState var1, BlockGetter var2, BlockPos var3, EntityType<?> var4) {
      return false;
   }
}
