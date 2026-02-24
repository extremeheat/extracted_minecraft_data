package net.minecraft.client.renderer.entity.state;

import net.minecraft.client.renderer.block.BlockModelRenderState;

public class BlockDisplayEntityRenderState extends DisplayEntityRenderState {
   public final BlockModelRenderState blockModel = new BlockModelRenderState();

   public BlockDisplayEntityRenderState() {
      super();
   }

   public boolean hasSubState() {
      return !this.blockModel.isEmpty();
   }
}
