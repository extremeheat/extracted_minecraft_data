package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ExperienceOrb;

public class ExperienceOrbRenderer extends EntityRenderer<ExperienceOrb> {
   private static final ResourceLocation EXPERIENCE_ORB_LOCATION = new ResourceLocation("textures/entity/experience_orb.png");

   public ExperienceOrbRenderer(EntityRenderDispatcher var1) {
      super(var1);
      this.shadowRadius = 0.15F;
      this.shadowStrength = 0.75F;
   }

   public void render(ExperienceOrb var1, double var2, double var4, double var6, float var8, float var9) {
      if (!this.solidRender && Minecraft.getInstance().getEntityRenderDispatcher().options != null) {
         GlStateManager.pushMatrix();
         GlStateManager.translatef((float)var2, (float)var4, (float)var6);
         this.bindTexture(var1);
         Lighting.turnOn();
         int var10 = var1.getIcon();
         float var11 = (float)(var10 % 4 * 16 + 0) / 64.0F;
         float var12 = (float)(var10 % 4 * 16 + 16) / 64.0F;
         float var13 = (float)(var10 / 4 * 16 + 0) / 64.0F;
         float var14 = (float)(var10 / 4 * 16 + 16) / 64.0F;
         float var15 = 1.0F;
         float var16 = 0.5F;
         float var17 = 0.25F;
         int var18 = var1.getLightColor();
         int var19 = var18 % 65536;
         int var20 = var18 / 65536;
         GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)var19, (float)var20);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         float var21 = 255.0F;
         float var22 = ((float)var1.tickCount + var9) / 2.0F;
         int var23 = (int)((Mth.sin(var22 + 0.0F) + 1.0F) * 0.5F * 255.0F);
         boolean var24 = true;
         int var25 = (int)((Mth.sin(var22 + 4.1887903F) + 1.0F) * 0.1F * 255.0F);
         GlStateManager.translatef(0.0F, 0.1F, 0.0F);
         GlStateManager.rotatef(180.0F - this.entityRenderDispatcher.playerRotY, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotatef((float)(this.entityRenderDispatcher.options.thirdPersonView == 2 ? -1 : 1) * -this.entityRenderDispatcher.playerRotX, 1.0F, 0.0F, 0.0F);
         float var26 = 0.3F;
         GlStateManager.scalef(0.3F, 0.3F, 0.3F);
         Tesselator var27 = Tesselator.getInstance();
         BufferBuilder var28 = var27.getBuilder();
         var28.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
         var28.vertex(-0.5D, -0.25D, 0.0D).uv((double)var11, (double)var14).color(var23, 255, var25, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
         var28.vertex(0.5D, -0.25D, 0.0D).uv((double)var12, (double)var14).color(var23, 255, var25, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
         var28.vertex(0.5D, 0.75D, 0.0D).uv((double)var12, (double)var13).color(var23, 255, var25, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
         var28.vertex(-0.5D, 0.75D, 0.0D).uv((double)var11, (double)var13).color(var23, 255, var25, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
         var27.end();
         GlStateManager.disableBlend();
         GlStateManager.disableRescaleNormal();
         GlStateManager.popMatrix();
         super.render(var1, var2, var4, var6, var8, var9);
      }
   }

   protected ResourceLocation getTextureLocation(ExperienceOrb var1) {
      return EXPERIENCE_ORB_LOCATION;
   }
}
