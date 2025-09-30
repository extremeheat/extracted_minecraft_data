package net.minecraft.client.renderer.entity.state;

import net.minecraft.client.renderer.block.MovingBlockRenderState;

public class FallingBlockRenderState extends EntityRenderState {
   public MovingBlockRenderState movingBlockRenderState = new MovingBlockRenderState();

   public FallingBlockRenderState() {
      super();
   }
}
