package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.ZombifiedPiglinRenderState;

public class ZombifiedPiglinModel extends AbstractPiglinModel<ZombifiedPiglinRenderState> {
   public ZombifiedPiglinModel(ModelPart var1) {
      super(var1);
   }

   public void setupAnim(ZombifiedPiglinRenderState var1) {
      super.setupAnim(var1);
      AnimationUtils.animateZombieArms(this.leftArm, this.rightArm, var1.isAggressive, var1.attackTime, var1.ageInTicks);
   }

   @Override
   public void setAllVisible(boolean var1) {
      super.setAllVisible(var1);
      this.leftSleeve.visible = var1;
      this.rightSleeve.visible = var1;
      this.leftPants.visible = var1;
      this.rightPants.visible = var1;
      this.jacket.visible = var1;
   }
}
