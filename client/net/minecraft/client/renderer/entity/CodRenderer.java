package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.CodModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Cod;

public class CodRenderer extends MobRenderer<Cod, LivingEntityRenderState, CodModel> {
   private static final ResourceLocation COD_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/fish/cod.png");

   public CodRenderer(EntityRendererProvider.Context var1) {
      super(var1, new CodModel(var1.bakeLayer(ModelLayers.COD)), 0.3F);
   }

   public ResourceLocation getTextureLocation(LivingEntityRenderState var1) {
      return COD_LOCATION;
   }

   public LivingEntityRenderState createRenderState() {
      return new LivingEntityRenderState();
   }

   protected void setupRotations(LivingEntityRenderState var1, PoseStack var2, float var3, float var4) {
      super.setupRotations(var1, var2, var3, var4);
      float var5 = 4.3F * Mth.sin(0.6F * var1.ageInTicks);
      var2.mulPose(Axis.YP.rotationDegrees(var5));
      if (!var1.isInWater) {
         var2.translate(0.1F, 0.1F, -0.1F);
         var2.mulPose(Axis.ZP.rotationDegrees(90.0F));
      }

   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
