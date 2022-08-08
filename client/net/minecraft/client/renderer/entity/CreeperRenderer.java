package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.CreeperPowerLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;

public class CreeperRenderer extends MobRenderer<Creeper, CreeperModel<Creeper>> {
   private static final ResourceLocation CREEPER_LOCATION = new ResourceLocation("textures/entity/creeper/creeper.png");

   public CreeperRenderer(EntityRendererProvider.Context var1) {
      super(var1, new CreeperModel(var1.bakeLayer(ModelLayers.CREEPER)), 0.5F);
      this.addLayer(new CreeperPowerLayer(this, var1.getModelSet()));
   }

   protected void scale(Creeper var1, PoseStack var2, float var3) {
      float var4 = var1.getSwelling(var3);
      float var5 = 1.0F + Mth.sin(var4 * 100.0F) * var4 * 0.01F;
      var4 = Mth.clamp(var4, 0.0F, 1.0F);
      var4 *= var4;
      var4 *= var4;
      float var6 = (1.0F + var4 * 0.4F) * var5;
      float var7 = (1.0F + var4 * 0.1F) / var5;
      var2.scale(var6, var7, var6);
   }

   protected float getWhiteOverlayProgress(Creeper var1, float var2) {
      float var3 = var1.getSwelling(var2);
      return (int)(var3 * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(var3, 0.5F, 1.0F);
   }

   public ResourceLocation getTextureLocation(Creeper var1) {
      return CREEPER_LOCATION;
   }

   // $FF: synthetic method
   protected float getWhiteOverlayProgress(LivingEntity var1, float var2) {
      return this.getWhiteOverlayProgress((Creeper)var1, var2);
   }
}
