package net.minecraft.client.renderer.entity.state;

import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.BlockState;

public class EndermanRenderState extends HumanoidRenderState {
   public boolean isCreepy;
   @Nullable
   public BlockState carriedBlock;

   public EndermanRenderState() {
      super();
   }
}
