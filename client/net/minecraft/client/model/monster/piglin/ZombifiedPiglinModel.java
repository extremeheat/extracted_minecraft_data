package net.minecraft.client.model.monster.piglin;

import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.ZombifiedPiglinRenderState;

public class ZombifiedPiglinModel extends AbstractPiglinModel<ZombifiedPiglinRenderState> {
   public ZombifiedPiglinModel(final ModelPart root) {
      super(root);
   }

   public void setupAnim(final ZombifiedPiglinRenderState state) {
      super.setupAnim(state);
      AnimationUtils.animateZombieArms(this.leftArm, this.rightArm, state.isAggressive, state);
   }

   public void setAllVisible(final boolean visible) {
      super.setAllVisible(visible);
      this.leftSleeve.visible = visible;
      this.rightSleeve.visible = visible;
      this.leftPants.visible = visible;
      this.rightPants.visible = visible;
      this.jacket.visible = visible;
   }
}
