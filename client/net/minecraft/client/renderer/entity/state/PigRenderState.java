package net.minecraft.client.renderer.entity.state;

import javax.annotation.Nullable;
import net.minecraft.world.entity.animal.PigVariant;

public class PigRenderState extends LivingEntityRenderState implements SaddleableRenderState {
   public boolean isSaddled;
   @Nullable
   public PigVariant variant;

   public PigRenderState() {
      super();
   }

   public boolean isSaddled() {
      return this.isSaddled;
   }
}
