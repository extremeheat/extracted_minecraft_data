package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.model.PandaModel;
import net.minecraft.client.renderer.entity.layers.PandaHoldsItemLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Panda;

public class PandaRenderer extends MobRenderer<Panda, PandaModel<Panda>> {
   private static final Map<Panda.Gene, ResourceLocation> TEXTURES = (Map)Util.make(Maps.newEnumMap(Panda.Gene.class), (var0) -> {
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

   @Nullable
   protected ResourceLocation getTextureLocation(Panda var1) {
      return (ResourceLocation)TEXTURES.getOrDefault(var1.getVariant(), TEXTURES.get(Panda.Gene.NORMAL));
   }

   protected void setupRotations(Panda var1, float var2, float var3, float var4) {
      super.setupRotations(var1, var2, var3, var4);
      float var7;
      if (var1.rollCounter > 0) {
         int var5 = var1.rollCounter;
         int var6 = var5 + 1;
         var7 = 7.0F;
         float var8 = var1.isBaby() ? 0.3F : 0.8F;
         float var9;
         float var10;
         float var11;
         if (var5 < 8) {
            var10 = (float)(90 * var5) / 7.0F;
            var11 = (float)(90 * var6) / 7.0F;
            var9 = this.getAngle(var10, var11, var6, var4, 8.0F);
            GlStateManager.translatef(0.0F, (var8 + 0.2F) * (var9 / 90.0F), 0.0F);
            GlStateManager.rotatef(-var9, 1.0F, 0.0F, 0.0F);
         } else {
            float var12;
            if (var5 < 16) {
               var10 = ((float)var5 - 8.0F) / 7.0F;
               var11 = 90.0F + 90.0F * var10;
               var12 = 90.0F + 90.0F * ((float)var6 - 8.0F) / 7.0F;
               var9 = this.getAngle(var11, var12, var6, var4, 16.0F);
               GlStateManager.translatef(0.0F, var8 + 0.2F + (var8 - 0.2F) * (var9 - 90.0F) / 90.0F, 0.0F);
               GlStateManager.rotatef(-var9, 1.0F, 0.0F, 0.0F);
            } else if ((float)var5 < 24.0F) {
               var10 = ((float)var5 - 16.0F) / 7.0F;
               var11 = 180.0F + 90.0F * var10;
               var12 = 180.0F + 90.0F * ((float)var6 - 16.0F) / 7.0F;
               var9 = this.getAngle(var11, var12, var6, var4, 24.0F);
               GlStateManager.translatef(0.0F, var8 + var8 * (270.0F - var9) / 90.0F, 0.0F);
               GlStateManager.rotatef(-var9, 1.0F, 0.0F, 0.0F);
            } else if (var5 < 32) {
               var10 = ((float)var5 - 24.0F) / 7.0F;
               var11 = 270.0F + 90.0F * var10;
               var12 = 270.0F + 90.0F * ((float)var6 - 24.0F) / 7.0F;
               var9 = this.getAngle(var11, var12, var6, var4, 32.0F);
               GlStateManager.translatef(0.0F, var8 * ((360.0F - var9) / 90.0F), 0.0F);
               GlStateManager.rotatef(-var9, 1.0F, 0.0F, 0.0F);
            }
         }
      } else {
         GlStateManager.rotatef(0.0F, 1.0F, 0.0F, 0.0F);
      }

      float var13 = var1.getSitAmount(var4);
      float var14;
      if (var13 > 0.0F) {
         GlStateManager.translatef(0.0F, 0.8F * var13, 0.0F);
         GlStateManager.rotatef(Mth.lerp(var13, var1.xRot, var1.xRot + 90.0F), 1.0F, 0.0F, 0.0F);
         GlStateManager.translatef(0.0F, -1.0F * var13, 0.0F);
         if (var1.isScared()) {
            var14 = (float)(Math.cos((double)var1.tickCount * 1.25D) * 3.141592653589793D * 0.05000000074505806D);
            GlStateManager.rotatef(var14, 0.0F, 1.0F, 0.0F);
            if (var1.isBaby()) {
               GlStateManager.translatef(0.0F, 0.8F, 0.55F);
            }
         }
      }

      var14 = var1.getLieOnBackAmount(var4);
      if (var14 > 0.0F) {
         var7 = var1.isBaby() ? 0.5F : 1.3F;
         GlStateManager.translatef(0.0F, var7 * var14, 0.0F);
         GlStateManager.rotatef(Mth.lerp(var14, var1.xRot, var1.xRot + 180.0F), 1.0F, 0.0F, 0.0F);
      }

   }

   private float getAngle(float var1, float var2, int var3, float var4, float var5) {
      return (float)var3 < var5 ? Mth.lerp(var4, var1, var2) : var1;
   }
}
