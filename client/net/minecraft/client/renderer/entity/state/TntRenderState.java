package net.minecraft.client.renderer.entity.state;

import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class TntRenderState extends EntityRenderState {
   public float fuseRemainingInTicks;
   public @Nullable BlockState blockState;

   public TntRenderState() {
      super();
   }
}
