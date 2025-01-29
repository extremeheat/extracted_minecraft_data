package net.minecraft.client.renderer.entity.state;

import javax.annotation.Nullable;
import net.minecraft.world.entity.animal.CowVariant;

public class CowRenderState extends LivingEntityRenderState {
   @Nullable
   public CowVariant variant;

   public CowRenderState() {
      super();
   }
}
