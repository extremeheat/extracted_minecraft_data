package net.minecraft.world.level.block;

import net.minecraft.world.level.BlockLayer;

public class GlassBlock extends AbstractGlassBlock {
   public GlassBlock(Block.Properties var1) {
      super(var1);
   }

   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }
}
