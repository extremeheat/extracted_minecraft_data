package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.IronGolemCrackinessLayer;
import net.minecraft.client.renderer.entity.layers.IronGolemFlowerLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.IronGolem;

public class IronGolemRenderer extends MobRenderer<IronGolem, IronGolemModel<IronGolem>> {
   private static final ResourceLocation GOLEM_LOCATION = new ResourceLocation("textures/entity/iron_golem/iron_golem.png");

   public IronGolemRenderer(EntityRendererProvider.Context var1) {
      super(var1, new IronGolemModel<>(var1.bakeLayer(ModelLayers.IRON_GOLEM)), 0.7F);
      this.addLayer(new IronGolemCrackinessLayer(this));
      this.addLayer(new IronGolemFlowerLayer(this, var1.getBlockRenderDispatcher()));
   }

   public ResourceLocation getTextureLocation(IronGolem var1) {
      return GOLEM_LOCATION;
   }

   protected void setupRotations(IronGolem var1, PoseStack var2, float var3, float var4, float var5, float var6) {
      super.setupRotations(var1, var2, var3, var4, var5, var6);
      if (!((double)var1.walkAnimation.speed() < 0.01)) {
         float var7 = 13.0F;
         float var8 = var1.walkAnimation.position(var5) + 6.0F;
         float var9 = (Math.abs(var8 % 13.0F - 6.5F) - 3.25F) / 3.25F;
         var2.mulPose(Axis.ZP.rotationDegrees(6.5F * var9));
      }
   }
}
