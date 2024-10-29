package net.minecraft.client.renderer.entity.state;

import net.minecraft.world.phys.Vec3;

public class FishingHookRenderState extends EntityRenderState {
   public Vec3 lineOriginOffset;

   public FishingHookRenderState() {
      super();
      this.lineOriginOffset = Vec3.ZERO;
   }
}
