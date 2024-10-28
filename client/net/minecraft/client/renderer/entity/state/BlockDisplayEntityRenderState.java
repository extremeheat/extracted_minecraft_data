package net.minecraft.client.renderer.entity.state;

import javax.annotation.Nullable;
import net.minecraft.world.entity.Display;

public class BlockDisplayEntityRenderState extends DisplayEntityRenderState {
   @Nullable
   public Display.BlockDisplay.BlockRenderState blockRenderState;

   public BlockDisplayEntityRenderState() {
      super();
   }

   public boolean hasSubState() {
      return this.blockRenderState != null;
   }
}
