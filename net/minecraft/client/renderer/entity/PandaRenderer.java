package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.client.model.PandaModel;
import net.minecraft.client.renderer.entity.layers.PandaHoldsItemLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Panda;

public class PandaRenderer extends MobRenderer {
   private static final Map TEXTURES = (Map)Util.make(Maps.newEnumMap(Panda.Gene.class), (var0) -> {
      var0.put(Panda.Gene.NORMAL, new ResourceLocation("textures/entity/panda/panda.png"));
      var0.put(Panda.Gene.LAZY, new ResourceLocation("textures/entity/panda/lazy_panda.png"));
      var0.put(Panda.Gene.WORRIED, new ResourceLocation("textures/entity/panda/worried_panda.png"));
      var0.put(Panda.Gene.PLAYFUL, new ResourceLocation("textures/entity/panda/playful_panda.png"));
      var0.put(Panda.Gene.BROWN, new ResourceLocation("textures/entity/panda/brown_panda.png"));
      var0.put(Panda.Gene.WEAK, new ResourceLocation("textures/entity/panda/weak_panda.png"));
      var0.put(Panda.Gene.AGGRESSIVE, new ResourceLocation("textures/entity/panda/aggressive_panda.png"));
   });

   public PandaRenderer(EntityRenderDispatcher var1) {
      super(var1, new PandaModel(9, 0.0F), 0.9F);
      this.addLayer(new PandaHoldsItemLayer(this));
   }

   public ResourceLocation getTextureLocation(Panda var1) {
      return (ResourceLocation)TEXTURES.getOrDefault(var1.getVariant(), TEXTURES.get(Panda.Gene.NORMAL));
   }

   protected void setupRotations(Panda var1, PoseStack var2, float var3, float var4, float var5) {
      super.setupRotations(var1, var2, var3, var4, var5);
      float var8;
      if (var1.rollCounter > 0) {
         int var6 = var1.rollCounter;
         int var7 = var6 + 1;
         var8 = 7.0F;
         float var9 = var1.isBaby() ? 0.3F : 0.8F;
         float var10;
         float var11;
         float var12;
         if (var6 < 8) {
            var11 = (float)(90 * var6) / 7.0F;
            var12 = (float)(90 * var7) / 7.0F;
            var10 = this.getAngle(var11, var12, var7, var5, 8.0F);
            var2.translate(0.0D, (double)((var9 + 0.2F) * (var10 / 90.0F)), 0.0D);
            var2.mulPose(Vector3f.XP.rotationDegrees(-var10));
         } else {
            float var13;
            if (var6 < 16) {
               var11 = ((float)var6 - 8.0F) / 7.0F;
               var12 = 90.0F + 90.0F * var11;
               var13 = 90.0F + 90.0F * ((float)var7 - 8.0F) / 7.0F;
               var10 = this.getAngle(var12, var13, var7, var5, 16.0F);
               var2.translate(0.0D, (double)(var9 + 0.2F + (var9 - 0.2F) * (var10 - 90.0F) / 90.0F), 0.0D);
               var2.mulPose(Vector3f.XP.rotationDegrees(-var10));
            } else if ((float)var6 < 24.0F) {
               var11 = ((float)var6 - 16.0F) / 7.0F;
               var12 = 180.0F + 90.0F * var11;
               var13 = 180.0F + 90.0F * ((float)var7 - 16.0F) / 7.0F;
               var10 = this.getAngle(var12, var13, var7, var5, 24.0F);
               var2.translate(0.0D, (double)(var9 + var9 * (270.0F - var10) / 90.0F), 0.0D);
               var2.mulPose(Vector3f.XP.rotationDegrees(-var10));
            } else if (var6 < 32) {
               var11 = ((float)var6 - 24.0F) / 7.0F;
               var12 = 270.0F + 90.0F * var11;
               var13 = 270.0F + 90.0F * ((float)var7 - 24.0F) / 7.0F;
               var10 = this.getAngle(var12, var13, var7, var5, 32.0F);
               var2.translate(0.0D, (double)(var9 * ((360.0F - var10) / 90.0F)), 0.0D);
               var2.mulPose(Vector3f.XP.rotationDegrees(-var10));
            }
         }
      }

      float var14 = var1.getSitAmount(var5);
      float var15;
      if (var14 > 0.0F) {
         var2.translate(0.0D, (double)(0.8F * var14), 0.0D);
         var2.mulPose(Vector3f.XP.rotationDegrees(Mth.lerp(var14, var1.xRot, var1.xRot + 90.0F)));
         var2.translate(0.0D, (double)(-1.0F * var14), 0.0D);
         if (var1.isScared()) {
            var15 = (float)(Math.cos((double)var1.tickCount * 1.25D) * 3.141592653589793D * 0.05000000074505806D);
            var2.mulPose(Vector3f.YP.rotationDegrees(var15));
            if (var1.isBaby()) {
               var2.translate(0.0D, 0.800000011920929D, 0.550000011920929D);
            }
         }
      }

      var15 = var1.getLieOnBackAmount(var5);
      if (var15 > 0.0F) {
         var8 = var1.isBaby() ? 0.5F : 1.3F;
         var2.translate(0.0D, (double)(var8 * var15), 0.0D);
         var2.mulPose(Vector3f.XP.rotationDegrees(Mth.lerp(var15, var1.xRot, var1.xRot + 180.0F)));
      }

   }

   private float getAngle(float var1, float var2, int var3, float var4, float var5) {
      return (float)var3 < var5 ? Mth.lerp(var4, var1, var2) : var1;
   }
}
