package net.minecraft.client.renderer.blockentity.state;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.MovingBlockRenderState;

public class PistonHeadRenderState extends BlockEntityRenderState {
   @Nullable
   public MovingBlockRenderState block;
   @Nullable
   public MovingBlockRenderState base;
   public float xOffset;
   public float yOffset;
   public float zOffset;

   public PistonHeadRenderState() {
      super();
   }
}
