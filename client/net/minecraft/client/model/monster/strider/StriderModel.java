package net.minecraft.client.model.monster.strider;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.StriderRenderState;
import net.minecraft.util.Mth;

public abstract class StriderModel extends EntityModel<StriderRenderState> {
   protected static final float SPEED = 1.5F;
   protected final ModelPart rightLeg;
   protected final ModelPart leftLeg;
   protected final ModelPart body;

   public StriderModel(final ModelPart root) {
      super(root);
      this.rightLeg = root.getChild("right_leg");
      this.leftLeg = root.getChild("left_leg");
      this.body = root.getChild("body");
   }

   public void setupAnim(final StriderRenderState state) {
      super.setupAnim(state);
      float animationPos = state.walkAnimationPos;
      float animationSpeed = Math.min(state.walkAnimationSpeed, 0.25F);
      if (!state.isRidden) {
         this.body.xRot = state.xRot * 0.017453292F;
         this.body.yRot = state.yRot * 0.017453292F;
      } else {
         this.body.xRot = 0.0F;
         this.body.yRot = 0.0F;
      }

      this.body.zRot = 0.1F * Mth.sin((double)(animationPos * 1.5F)) * 4.0F * animationSpeed;
      this.leftLeg.xRot = Mth.sin((double)(animationPos * 1.5F * 0.5F)) * 2.0F * animationSpeed;
      this.rightLeg.xRot = Mth.sin((double)(animationPos * 1.5F * 0.5F + 3.1415927F)) * 2.0F * animationSpeed;
      this.leftLeg.zRot = 0.17453292F * Mth.cos((double)(animationPos * 1.5F * 0.5F)) * animationSpeed;
      this.rightLeg.zRot = 0.17453292F * Mth.cos((double)(animationPos * 1.5F * 0.5F + 3.1415927F)) * animationSpeed;
      this.customAnimations(animationPos, animationSpeed, state.ageInTicks);
   }

   protected abstract void customAnimations(final float animationPos, final float animationSpeed, final float ageInTicks);
}
