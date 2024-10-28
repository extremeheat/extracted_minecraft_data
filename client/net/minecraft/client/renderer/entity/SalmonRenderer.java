package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.SalmonModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Salmon;

public class SalmonRenderer extends MobRenderer<Salmon, SalmonModel<Salmon>> {
   private static final ResourceLocation SALMON_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/fish/salmon.png");

   public SalmonRenderer(EntityRendererProvider.Context var1) {
      super(var1, new SalmonModel(var1.bakeLayer(ModelLayers.SALMON)), 0.4F);
   }

   public ResourceLocation getTextureLocation(Salmon var1) {
      return SALMON_LOCATION;
   }

   protected void setupRotations(Salmon var1, PoseStack var2, float var3, float var4, float var5, float var6) {
      super.setupRotations(var1, var2, var3, var4, var5, var6);
      float var7 = 1.0F;
      float var8 = 1.0F;
      if (!var1.isInWater()) {
         var7 = 1.3F;
         var8 = 1.7F;
      }

      float var9 = var7 * 4.3F * Mth.sin(var8 * 0.6F * var3);
      var2.mulPose(Axis.YP.rotationDegrees(var9));
      var2.translate(0.0F, 0.0F, -0.4F);
      if (!var1.isInWater()) {
         var2.translate(0.2F, 0.1F, 0.0F);
         var2.mulPose(Axis.ZP.rotationDegrees(90.0F));
      }

   }
}
