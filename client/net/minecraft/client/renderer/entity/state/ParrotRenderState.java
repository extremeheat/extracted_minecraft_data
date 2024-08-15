package net.minecraft.client.renderer.entity.state;

import net.minecraft.client.model.ParrotModel;
import net.minecraft.world.entity.animal.Parrot;

public class ParrotRenderState extends LivingEntityRenderState {
   public Parrot.Variant variant = Parrot.Variant.RED_BLUE;
   public float flapAngle;
   public ParrotModel.Pose pose = ParrotModel.Pose.FLYING;

   public ParrotRenderState() {
      super();
   }
}
