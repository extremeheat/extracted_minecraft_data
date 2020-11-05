package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.IronGolemCrackinessLayer;
import net.minecraft.client.renderer.entity.layers.IronGolemFlowerLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.IronGolem;

public class IronGolemRenderer extends MobRenderer<IronGolem, IronGolemModel<IronGolem>> {
   private static final ResourceLocation GOLEM_LOCATION = new ResourceLocation("textures/entity/iron_golem/iron_golem.png");

   public IronGolemRenderer(EntityRendererProvider.Context var1) {
      super(var1, new IronGolemModel(var1.getLayer(ModelLayers.IRON_GOLEM)), 0.7F);
      this.addLayer(new IronGolemCrackinessLayer(this));
      this.addLayer(new IronGolemFlowerLayer(this));
   }

   public ResourceLocation getTextureLocation(IronGolem var1) {
      return GOLEM_LOCATION;
   }

   protected void setupRotations(IronGolem var1, PoseStack var2, float var3, float var4, float var5) {
      super.setupRotations(var1, var2, var3, var4, var5);
      if ((double)var1.animationSpeed >= 0.01D) {
         float var6 = 13.0F;
         float var7 = var1.animationPosition - var1.animationSpeed * (1.0F - var5) + 6.0F;
         float var8 = (Math.abs(var7 % 13.0F - 6.5F) - 3.25F) / 3.25F;
         var2.mulPose(Vector3f.ZP.rotationDegrees(6.5F * var8));
      }
   }
}
