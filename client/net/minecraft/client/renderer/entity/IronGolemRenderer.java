package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.IronGolemCrackinessLayer;
import net.minecraft.client.renderer.entity.layers.IronGolemFlowerLayer;
import net.minecraft.client.renderer.entity.state.IronGolemRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.IronGolem;

public class IronGolemRenderer extends MobRenderer<IronGolem, IronGolemRenderState, IronGolemModel> {
   private static final ResourceLocation GOLEM_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/iron_golem/iron_golem.png");

   public IronGolemRenderer(EntityRendererProvider.Context var1) {
      super(var1, new IronGolemModel(var1.bakeLayer(ModelLayers.IRON_GOLEM)), 0.7F);
      this.addLayer(new IronGolemCrackinessLayer(this));
      this.addLayer(new IronGolemFlowerLayer(this, var1.getBlockRenderDispatcher()));
   }

   public ResourceLocation getTextureLocation(IronGolemRenderState var1) {
      return GOLEM_LOCATION;
   }

   public IronGolemRenderState createRenderState() {
      return new IronGolemRenderState();
   }

   public void extractRenderState(IronGolem var1, IronGolemRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.attackTicksRemaining = (float)var1.getAttackAnimationTick() > 0.0F ? (float)var1.getAttackAnimationTick() - var3 : 0.0F;
      var2.offerFlowerTick = var1.getOfferFlowerTick();
      var2.crackiness = var1.getCrackiness();
   }

   protected void setupRotations(IronGolemRenderState var1, PoseStack var2, float var3, float var4) {
      super.setupRotations(var1, var2, var3, var4);
      if (!((double)var1.walkAnimationSpeed < 0.01)) {
         float var5 = 13.0F;
         float var6 = var1.walkAnimationPos + 6.0F;
         float var7 = (Math.abs(var6 % 13.0F - 6.5F) - 3.25F) / 3.25F;
         var2.mulPose(Axis.ZP.rotationDegrees(6.5F * var7));
      }
   }
}
