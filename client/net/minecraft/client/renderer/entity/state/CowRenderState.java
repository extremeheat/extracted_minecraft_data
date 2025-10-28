package net.minecraft.client.renderer.entity.state;

import net.minecraft.world.entity.animal.CowVariant;
import org.jspecify.annotations.Nullable;

public class CowRenderState extends LivingEntityRenderState {
   public @Nullable CowVariant variant;

   public CowRenderState() {
      super();
   }
}
