package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.SalmonModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Salmon;

public class SalmonRenderer extends MobRenderer<Salmon, SalmonModel<Salmon>> {
   private static final ResourceLocation SALMON_LOCATION = new ResourceLocation("textures/entity/fish/salmon.png");

   public SalmonRenderer(EntityRendererProvider.Context var1) {
      super(var1, new SalmonModel(var1.bakeLayer(ModelLayers.SALMON)), 0.4F);
   }

   public ResourceLocation getTextureLocation(Salmon var1) {
      return SALMON_LOCATION;
   }

   protected void setupRotations(Salmon var1, PoseStack var2, float var3, float var4, float var5) {
      super.setupRotations(var1, var2, var3, var4, var5);
      float var6 = 1.0F;
      float var7 = 1.0F;
      if (!var1.isInWater()) {
         var6 = 1.3F;
         var7 = 1.7F;
      }

      float var8 = var6 * 4.3F * Mth.sin(var7 * 0.6F * var3);
      var2.mulPose(Vector3f.YP.rotationDegrees(var8));
      var2.translate(0.0, 0.0, -0.4000000059604645);
      if (!var1.isInWater()) {
         var2.translate(0.20000000298023224, 0.10000000149011612, 0.0);
         var2.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
      }

   }
}
