package net.minecraft.client.renderer.entity.state;

import net.minecraft.world.entity.animal.axolotl.Axolotl;

public class AxolotlRenderState extends LivingEntityRenderState {
   public Axolotl.Variant variant = Axolotl.Variant.LUCY;
   public float playingDeadFactor;
   public float movingFactor;
   public float inWaterFactor = 1.0F;
   public float onGroundFactor;

   public AxolotlRenderState() {
      super();
   }
}
