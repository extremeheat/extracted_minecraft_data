package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.CodModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Cod;

public class CodRenderer extends MobRenderer<Cod, CodModel<Cod>> {
   private static final ResourceLocation COD_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/fish/cod.png");

   public CodRenderer(EntityRendererProvider.Context var1) {
      super(var1, new CodModel(var1.bakeLayer(ModelLayers.COD)), 0.3F);
   }

   public ResourceLocation getTextureLocation(Cod var1) {
      return COD_LOCATION;
   }

   protected void setupRotations(Cod var1, PoseStack var2, float var3, float var4, float var5, float var6) {
      super.setupRotations(var1, var2, var3, var4, var5, var6);
      float var7 = 4.3F * Mth.sin(0.6F * var3);
      var2.mulPose(Axis.YP.rotationDegrees(var7));
      if (!var1.isInWater()) {
         var2.translate(0.1F, 0.1F, -0.1F);
         var2.mulPose(Axis.ZP.rotationDegrees(90.0F));
      }

   }
}
