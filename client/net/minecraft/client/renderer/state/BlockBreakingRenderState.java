package net.minecraft.client.renderer.state;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.core.BlockPos;

public class BlockBreakingRenderState extends MovingBlockRenderState {
   public int progress;

   public BlockBreakingRenderState(ClientLevel var1, BlockPos var2, int var3) {
      super();
      this.level = var1;
      this.blockPos = var2;
      this.blockState = var1.getBlockState(var2);
      this.progress = var3;
      this.biome = var1.getBiome(var2);
   }
}
