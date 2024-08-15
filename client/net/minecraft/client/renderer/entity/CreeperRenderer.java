package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.CreeperPowerLayer;
import net.minecraft.client.renderer.entity.state.CreeperRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Creeper;

public class CreeperRenderer extends MobRenderer<Creeper, CreeperRenderState, CreeperModel> {
   private static final ResourceLocation CREEPER_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/creeper/creeper.png");

   public CreeperRenderer(EntityRendererProvider.Context var1) {
      super(var1, new CreeperModel(var1.bakeLayer(ModelLayers.CREEPER)), 0.5F);
      this.addLayer(new CreeperPowerLayer(this, var1.getModelSet()));
   }

   protected void scale(CreeperRenderState var1, PoseStack var2) {
      float var3 = var1.swelling;
      float var4 = 1.0F + Mth.sin(var3 * 100.0F) * var3 * 0.01F;
      var3 = Mth.clamp(var3, 0.0F, 1.0F);
      var3 *= var3;
      var3 *= var3;
      float var5 = (1.0F + var3 * 0.4F) * var4;
      float var6 = (1.0F + var3 * 0.1F) / var4;
      var2.scale(var5, var6, var5);
   }

   protected float getWhiteOverlayProgress(CreeperRenderState var1) {
      float var2 = var1.swelling;
      return (int)(var2 * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(var2, 0.5F, 1.0F);
   }

   public ResourceLocation getTextureLocation(CreeperRenderState var1) {
      return CREEPER_LOCATION;
   }

   public CreeperRenderState createRenderState() {
      return new CreeperRenderState();
   }

   public void extractRenderState(Creeper var1, CreeperRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.swelling = var1.getSwelling(var3);
      var2.isPowered = var1.isPowered();
   }
}
