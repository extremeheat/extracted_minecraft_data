package net.minecraft.client.renderer.entity.state;

import net.minecraft.world.entity.Display;
import org.jspecify.annotations.Nullable;

public class BlockDisplayEntityRenderState extends DisplayEntityRenderState {
   public Display.BlockDisplay.@Nullable BlockRenderState blockRenderState;

   public BlockDisplayEntityRenderState() {
      super();
   }

   public boolean hasSubState() {
      return this.blockRenderState != null;
   }
}
