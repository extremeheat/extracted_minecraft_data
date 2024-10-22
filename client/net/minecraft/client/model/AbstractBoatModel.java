package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import net.minecraft.util.Mth;

public abstract class AbstractBoatModel extends EntityModel<BoatRenderState> {
   private final ModelPart leftPaddle;
   private final ModelPart rightPaddle;

   public AbstractBoatModel(ModelPart var1) {
      super(var1);
      this.leftPaddle = var1.getChild("left_paddle");
      this.rightPaddle = var1.getChild("right_paddle");
   }

   public void setupAnim(BoatRenderState var1) {
      super.setupAnim(var1);
      animatePaddle(var1.rowingTimeLeft, 0, this.leftPaddle);
      animatePaddle(var1.rowingTimeRight, 1, this.rightPaddle);
   }

   private static void animatePaddle(float var0, int var1, ModelPart var2) {
      var2.xRot = Mth.clampedLerp(-1.0471976F, -0.2617994F, (Mth.sin(-var0) + 1.0F) / 2.0F);
      var2.yRot = Mth.clampedLerp(-0.7853982F, 0.7853982F, (Mth.sin(-var0 + 1.0F) + 1.0F) / 2.0F);
      if (var1 == 1) {
         var2.yRot = 3.1415927F - var2.yRot;
      }
   }
}
