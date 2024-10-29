package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.WolfModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.WolfArmorLayer;
import net.minecraft.client.renderer.entity.layers.WolfCollarLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.WolfRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.animal.Wolf;

public class WolfRenderer extends AgeableMobRenderer<Wolf, WolfRenderState, WolfModel> {
   public WolfRenderer(EntityRendererProvider.Context var1) {
      super(var1, new WolfModel(var1.bakeLayer(ModelLayers.WOLF)), new WolfModel(var1.bakeLayer(ModelLayers.WOLF_BABY)), 0.5F);
      this.addLayer(new WolfArmorLayer(this, var1.getModelSet(), var1.getEquipmentRenderer()));
      this.addLayer(new WolfCollarLayer(this));
   }

   protected int getModelTint(WolfRenderState var1) {
      float var2 = var1.wetShade;
      return var2 == 1.0F ? -1 : ARGB.colorFromFloat(1.0F, var2, var2, var2);
   }

   public ResourceLocation getTextureLocation(WolfRenderState var1) {
      return var1.texture;
   }

   public WolfRenderState createRenderState() {
      return new WolfRenderState();
   }

   public void extractRenderState(Wolf var1, WolfRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.isAngry = var1.isAngry();
      var2.isSitting = var1.isInSittingPose();
      var2.tailAngle = var1.getTailAngle();
      var2.headRollAngle = var1.getHeadRollAngle(var3);
      var2.shakeAnim = var1.getShakeAnim(var3);
      var2.texture = var1.getTexture();
      var2.wetShade = var1.getWetShade(var3);
      var2.collarColor = var1.isTame() ? var1.getCollarColor() : null;
      var2.bodyArmorItem = var1.getBodyArmorItem().copy();
   }

   // $FF: synthetic method
   protected int getModelTint(final LivingEntityRenderState var1) {
      return this.getModelTint((WolfRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
