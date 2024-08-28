package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.SalmonModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.SalmonRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Salmon;

public class SalmonRenderer extends MobRenderer<Salmon, SalmonRenderState, SalmonModel> {
   private static final ResourceLocation SALMON_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/fish/salmon.png");
   private final SalmonModel smallSalmonModel;
   private final SalmonModel mediumSalmonModel;
   private final SalmonModel largeSalmonModel;

   public SalmonRenderer(EntityRendererProvider.Context var1) {
      super(var1, new SalmonModel(var1.bakeLayer(ModelLayers.SALMON)), 0.4F);
      this.smallSalmonModel = new SalmonModel(var1.bakeLayer(ModelLayers.SALMON_SMALL));
      this.mediumSalmonModel = new SalmonModel(var1.bakeLayer(ModelLayers.SALMON));
      this.largeSalmonModel = new SalmonModel(var1.bakeLayer(ModelLayers.SALMON_LARGE));
   }

   public void extractRenderState(Salmon var1, SalmonRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.variant = var1.getVariant();
   }

   public ResourceLocation getTextureLocation(SalmonRenderState var1) {
      return SALMON_LOCATION;
   }

   public SalmonRenderState createRenderState() {
      return new SalmonRenderState();
   }

   protected void setupRotations(SalmonRenderState var1, PoseStack var2, float var3, float var4) {
      super.setupRotations(var1, var2, var3, var4);
      float var5 = 1.0F;
      float var6 = 1.0F;
      if (!var1.isInWater) {
         var5 = 1.3F;
         var6 = 1.7F;
      }

      float var7 = var5 * 4.3F * Mth.sin(var6 * 0.6F * var1.ageInTicks);
      var2.mulPose(Axis.YP.rotationDegrees(var7));
      if (!var1.isInWater) {
         var2.translate(0.2F, 0.1F, 0.0F);
         var2.mulPose(Axis.ZP.rotationDegrees(90.0F));
      }
   }

   public void render(SalmonRenderState var1, PoseStack var2, MultiBufferSource var3, int var4) {
      if (var1.variant == Salmon.Variant.SMALL) {
         this.model = this.smallSalmonModel;
      } else if (var1.variant == Salmon.Variant.LARGE) {
         this.model = this.largeSalmonModel;
      } else {
         this.model = this.mediumSalmonModel;
      }

      super.render(var1, var2, var3, var4);
   }
}
