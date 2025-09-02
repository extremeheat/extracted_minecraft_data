package net.minecraft.client.renderer.blockentity.state;

import javax.annotation.Nullable;
import net.minecraft.core.Direction;

public class BellRenderState extends BlockEntityRenderState {
   @Nullable
   public Direction shakeDirection;
   public float ticks;

   public BellRenderState() {
      super();
   }
}
