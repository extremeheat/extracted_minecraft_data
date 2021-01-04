package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import java.util.Random;
import net.minecraft.client.model.dragon.DragonModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;

public class EnderDragonDeathLayer extends RenderLayer<EnderDragon, DragonModel> {
   public EnderDragonDeathLayer(RenderLayerParent<EnderDragon, DragonModel> var1) {
      super(var1);
   }

   public void render(EnderDragon var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      if (var1.dragonDeathTime > 0) {
         Tesselator var9 = Tesselator.getInstance();
         BufferBuilder var10 = var9.getBuilder();
         Lighting.turnOff();
         float var11 = ((float)var1.dragonDeathTime + var4) / 200.0F;
         float var12 = 0.0F;
         if (var11 > 0.8F) {
            var12 = (var11 - 0.8F) / 0.2F;
         }

         Random var13 = new Random(432L);
         GlStateManager.disableTexture();
         GlStateManager.shadeModel(7425);
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
         GlStateManager.disableAlphaTest();
         GlStateManager.enableCull();
         GlStateManager.depthMask(false);
         GlStateManager.pushMatrix();
         GlStateManager.translatef(0.0F, -1.0F, -2.0F);

         for(int var14 = 0; (float)var14 < (var11 + var11 * var11) / 2.0F * 60.0F; ++var14) {
            GlStateManager.rotatef(var13.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(var13.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotatef(var13.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotatef(var13.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(var13.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotatef(var13.nextFloat() * 360.0F + var11 * 90.0F, 0.0F, 0.0F, 1.0F);
            float var15 = var13.nextFloat() * 20.0F + 5.0F + var12 * 10.0F;
            float var16 = var13.nextFloat() * 2.0F + 1.0F + var12 * 2.0F;
            var10.begin(6, DefaultVertexFormat.POSITION_COLOR);
            var10.vertex(0.0D, 0.0D, 0.0D).color(255, 255, 255, (int)(255.0F * (1.0F - var12))).endVertex();
            var10.vertex(-0.866D * (double)var16, (double)var15, (double)(-0.5F * var16)).color(255, 0, 255, 0).endVertex();
            var10.vertex(0.866D * (double)var16, (double)var15, (double)(-0.5F * var16)).color(255, 0, 255, 0).endVertex();
            var10.vertex(0.0D, (double)var15, (double)(1.0F * var16)).color(255, 0, 255, 0).endVertex();
            var10.vertex(-0.866D * (double)var16, (double)var15, (double)(-0.5F * var16)).color(255, 0, 255, 0).endVertex();
            var9.end();
         }

         GlStateManager.popMatrix();
         GlStateManager.depthMask(true);
         GlStateManager.disableCull();
         GlStateManager.disableBlend();
         GlStateManager.shadeModel(7424);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.enableTexture();
         GlStateManager.enableAlphaTest();
         Lighting.turnOn();
      }
   }

   public boolean colorsOnDamage() {
      return false;
   }
}
