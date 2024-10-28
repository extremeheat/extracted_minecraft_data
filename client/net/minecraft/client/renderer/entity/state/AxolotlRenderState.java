package net.minecraft.client.renderer.entity.state;

import net.minecraft.world.entity.animal.axolotl.Axolotl;

public class AxolotlRenderState extends LivingEntityRenderState {
   public Axolotl.Variant variant;
   public float playingDeadFactor;
   public float movingFactor;
   public float inWaterFactor;
   public float onGroundFactor;

   public AxolotlRenderState() {
      super();
      this.variant = Axolotl.Variant.LUCY;
      this.inWaterFactor = 1.0F;
   }
}
