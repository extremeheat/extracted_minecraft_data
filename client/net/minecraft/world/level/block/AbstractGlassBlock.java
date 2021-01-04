package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractGlassBlock extends HalfTransparentBlock {
   protected AbstractGlassBlock(Block.Properties var1) {
      super(var1);
   }

   public float getShadeBrightness(BlockState var1, BlockGetter var2, BlockPos var3) {
      return 1.0F;
   }

   public boolean propagatesSkylightDown(BlockState var1, BlockGetter var2, BlockPos var3) {
      return true;
   }

   public boolean isViewBlocking(BlockState var1, BlockGetter var2, BlockPos var3) {
      return false;
   }

   public boolean isRedstoneConductor(BlockState var1, BlockGetter var2, BlockPos var3) {
      return false;
   }

   public boolean isValidSpawn(BlockState var1, BlockGetter var2, BlockPos var3, EntityType<?> var4) {
      return false;
   }
}
