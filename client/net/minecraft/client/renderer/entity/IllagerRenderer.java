package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.entity.state.IllagerRenderState;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.item.CrossbowItem;

public abstract class IllagerRenderer<T extends AbstractIllager, S extends IllagerRenderState> extends MobRenderer<T, S, IllagerModel<S>> {
   protected IllagerRenderer(EntityRendererProvider.Context var1, IllagerModel<S> var2, float var3) {
      super(var1, var2, var3);
      this.addLayer(new CustomHeadLayer(this, var1.getModelSet()));
   }

   public void extractRenderState(T var1, S var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      ArmedEntityRenderState.extractArmedEntityRenderState(var1, var2, this.itemModelResolver);
      var2.isRiding = var1.isPassenger();
      var2.mainArm = var1.getMainArm();
      var2.armPose = var1.getArmPose();
      var2.maxCrossbowChargeDuration = var2.armPose == AbstractIllager.IllagerArmPose.CROSSBOW_CHARGE ? CrossbowItem.getChargeDuration(var1.getUseItem(), var1) : 0;
      var2.ticksUsingItem = var1.getTicksUsingItem();
      var2.attackAnim = var1.getAttackAnim(var3);
      var2.isAggressive = var1.isAggressive();
   }
}
