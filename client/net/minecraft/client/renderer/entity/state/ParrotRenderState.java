package net.minecraft.client.renderer.entity.state;

import net.minecraft.client.model.animal.parrot.ParrotModel;
import net.minecraft.world.entity.animal.parrot.Parrot;

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
