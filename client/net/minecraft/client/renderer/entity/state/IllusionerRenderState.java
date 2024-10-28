package net.minecraft.client.renderer.entity.state;

import net.minecraft.world.phys.Vec3;

public class IllusionerRenderState extends IllagerRenderState {
   public Vec3[] illusionOffsets = new Vec3[0];
   public boolean isCastingSpell;

   public IllusionerRenderState() {
      super();
   }
}
