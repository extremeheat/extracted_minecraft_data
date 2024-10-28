package net.minecraft.client.renderer.entity.state;

import net.minecraft.client.model.ParrotModel;
import net.minecraft.world.entity.animal.Parrot;

public class ParrotRenderState extends LivingEntityRenderState {
   public Parrot.Variant variant;
   public float flapAngle;
   public ParrotModel.Pose pose;

   public ParrotRenderState() {
      super();
      this.variant = Parrot.Variant.RED_BLUE;
      this.pose = ParrotModel.Pose.FLYING;
   }
}
