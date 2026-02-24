package net.minecraft.client.renderer.state;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.core.BlockPos;

public class BlockBreakingRenderState extends MovingBlockRenderState {
   public final int progress;

   public BlockBreakingRenderState(final ClientLevel level, final BlockPos pos, final int progress) {
      super();
      this.cardinalLighting = level.cardinalLighting();
      this.lightEngine = level.getLightEngine();
      this.blockPos = pos;
      this.blockState = level.getBlockState(pos);
      this.progress = progress;
      this.biome = level.getBiome(pos);
   }
}
