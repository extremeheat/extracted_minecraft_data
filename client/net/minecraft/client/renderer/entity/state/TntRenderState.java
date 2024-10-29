package net.minecraft.client.renderer.entity.state;

import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.BlockState;

public class TntRenderState extends EntityRenderState {
   public float fuseRemainingInTicks;
   @Nullable
   public BlockState blockState;

   public TntRenderState() {
      super();
   }
}
