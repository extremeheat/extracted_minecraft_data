package net.minecraft.client.renderer.entity.state;

import javax.annotation.Nullable;
import net.minecraft.world.entity.animal.ChickenVariant;

public class ChickenRenderState extends LivingEntityRenderState {
   public float flap;
   public float flapSpeed;
   @Nullable
   public ChickenVariant variant;

   public ChickenRenderState() {
      super();
   }
}
