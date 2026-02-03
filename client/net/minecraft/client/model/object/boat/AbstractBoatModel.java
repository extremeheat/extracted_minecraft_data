package net.minecraft.client.model.object.boat;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import net.minecraft.util.Mth;

public abstract class AbstractBoatModel extends EntityModel<BoatRenderState> {
   private final ModelPart leftPaddle;
   private final ModelPart rightPaddle;

   public AbstractBoatModel(final ModelPart root) {
      super(root);
      this.leftPaddle = root.getChild("left_paddle");
      this.rightPaddle = root.getChild("right_paddle");
   }

   public void setupAnim(final BoatRenderState state) {
      super.setupAnim(state);
      animatePaddle(state.rowingTimeLeft, 0, this.leftPaddle);
      animatePaddle(state.rowingTimeRight, 1, this.rightPaddle);
   }

   private static void animatePaddle(final float time, final int side, final ModelPart paddle) {
      paddle.xRot = Mth.clampedLerp((Mth.sin((double)(-time)) + 1.0F) / 2.0F, -1.0471976F, -0.2617994F);
      paddle.yRot = Mth.clampedLerp((Mth.sin((double)(-time + 1.0F)) + 1.0F) / 2.0F, -0.7853982F, 0.7853982F);
      if (side == 1) {
         paddle.yRot = 3.1415927F - paddle.yRot;
      }

   }
}
