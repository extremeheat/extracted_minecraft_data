package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class WebBlock extends Block {
   public WebBlock(Block.Properties var1) {
      super(var1);
   }

   public void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4) {
      var4.makeStuckInBlock(var1, new Vec3(0.25D, 0.05000000074505806D, 0.25D));
   }

   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }
}
