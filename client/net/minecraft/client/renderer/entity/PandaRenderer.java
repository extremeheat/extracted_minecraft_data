package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.client.model.PandaModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.PandaHoldsItemLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Panda;

public class PandaRenderer extends MobRenderer<Panda, PandaModel<Panda>> {
   private static final Map<Panda.Gene, ResourceLocation> TEXTURES = (Map)Util.make(Maps.newEnumMap(Panda.Gene.class), (var0) -> {
      var0.put(Panda.Gene.NORMAL, ResourceLocation.withDefaultNamespace("textures/entity/panda/panda.png"));
      var0.put(Panda.Gene.LAZY, ResourceLocation.withDefaultNamespace("textures/entity/panda/lazy_panda.png"));
      var0.put(Panda.Gene.WORRIED, ResourceLocation.withDefaultNamespace("textures/entity/panda/worried_panda.png"));
      var0.put(Panda.Gene.PLAYFUL, ResourceLocation.withDefaultNamespace("textures/entity/panda/playful_panda.png"));
      var0.put(Panda.Gene.BROWN, ResourceLocation.withDefaultNamespace("textures/entity/panda/brown_panda.png"));
      var0.put(Panda.Gene.WEAK, ResourceLocation.withDefaultNamespace("textures/entity/panda/weak_panda.png"));
      var0.put(Panda.Gene.AGGRESSIVE, ResourceLocation.withDefaultNamespace("textures/entity/panda/aggressive_panda.png"));
   });

   public PandaRenderer(EntityRendererProvider.Context var1) {
      super(var1, new PandaModel(var1.bakeLayer(ModelLayers.PANDA)), 0.9F);
      this.addLayer(new PandaHoldsItemLayer(this, var1.getItemInHandRenderer()));
   }

   public ResourceLocation getTextureLocation(Panda var1) {
      return (ResourceLocation)TEXTURES.getOrDefault(var1.getVariant(), (ResourceLocation)TEXTURES.get(Panda.Gene.NORMAL));
   }

   protected void setupRotations(Panda var1, PoseStack var2, float var3, float var4, float var5, float var6) {
      super.setupRotations(var1, var2, var3, var4, var5, var6);
      float var9;
      if (var1.rollCounter > 0) {
         int var7 = var1.rollCounter;
         int var8 = var7 + 1;
         var9 = 7.0F;
         float var10 = var1.isBaby() ? 0.3F : 0.8F;
         float var11;
         float var12;
         float var13;
         if (var7 < 8) {
            var12 = (float)(90 * var7) / 7.0F;
            var13 = (float)(90 * var8) / 7.0F;
            var11 = this.getAngle(var12, var13, var8, var5, 8.0F);
            var2.translate(0.0F, (var10 + 0.2F) * (var11 / 90.0F), 0.0F);
            var2.mulPose(Axis.XP.rotationDegrees(-var11));
         } else {
            float var14;
            if (var7 < 16) {
               var12 = ((float)var7 - 8.0F) / 7.0F;
               var13 = 90.0F + 90.0F * var12;
               var14 = 90.0F + 90.0F * ((float)var8 - 8.0F) / 7.0F;
               var11 = this.getAngle(var13, var14, var8, var5, 16.0F);
               var2.translate(0.0F, var10 + 0.2F + (var10 - 0.2F) * (var11 - 90.0F) / 90.0F, 0.0F);
               var2.mulPose(Axis.XP.rotationDegrees(-var11));
            } else if ((float)var7 < 24.0F) {
               var12 = ((float)var7 - 16.0F) / 7.0F;
               var13 = 180.0F + 90.0F * var12;
               var14 = 180.0F + 90.0F * ((float)var8 - 16.0F) / 7.0F;
               var11 = this.getAngle(var13, var14, var8, var5, 24.0F);
               var2.translate(0.0F, var10 + var10 * (270.0F - var11) / 90.0F, 0.0F);
               var2.mulPose(Axis.XP.rotationDegrees(-var11));
            } else if (var7 < 32) {
               var12 = ((float)var7 - 24.0F) / 7.0F;
               var13 = 270.0F + 90.0F * var12;
               var14 = 270.0F + 90.0F * ((float)var8 - 24.0F) / 7.0F;
               var11 = this.getAngle(var13, var14, var8, var5, 32.0F);
               var2.translate(0.0F, var10 * ((360.0F - var11) / 90.0F), 0.0F);
               var2.mulPose(Axis.XP.rotationDegrees(-var11));
            }
         }
      }

      float var15 = var1.getSitAmount(var5);
      float var16;
      if (var15 > 0.0F) {
         var2.translate(0.0F, 0.8F * var15, 0.0F);
         var2.mulPose(Axis.XP.rotationDegrees(Mth.lerp(var15, var1.getXRot(), var1.getXRot() + 90.0F)));
         var2.translate(0.0F, -1.0F * var15, 0.0F);
         if (var1.isScared()) {
            var16 = (float)(Math.cos((double)var1.tickCount * 1.25) * 3.141592653589793 * 0.05000000074505806);
            var2.mulPose(Axis.YP.rotationDegrees(var16));
            if (var1.isBaby()) {
               var2.translate(0.0F, 0.8F, 0.55F);
            }
         }
      }

      var16 = var1.getLieOnBackAmount(var5);
      if (var16 > 0.0F) {
         var9 = var1.isBaby() ? 0.5F : 1.3F;
         var2.translate(0.0F, var9 * var16, 0.0F);
         var2.mulPose(Axis.XP.rotationDegrees(Mth.lerp(var16, var1.getXRot(), var1.getXRot() + 180.0F)));
      }

   }

   private float getAngle(float var1, float var2, int var3, float var4, float var5) {
      return (float)var3 < var5 ? Mth.lerp(var4, var1, var2) : var1;
   }
}
