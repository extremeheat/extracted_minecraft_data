package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.MemoryTracker;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import java.nio.FloatBuffer;
import java.util.Random;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;

public class TheEndPortalRenderer extends BlockEntityRenderer<TheEndPortalBlockEntity> {
   private static final ResourceLocation END_SKY_LOCATION = new ResourceLocation("textures/environment/end_sky.png");
   private static final ResourceLocation END_PORTAL_LOCATION = new ResourceLocation("textures/entity/end_portal.png");
   private static final Random RANDOM = new Random(31100L);
   private static final FloatBuffer MODELVIEW = MemoryTracker.createFloatBuffer(16);
   private static final FloatBuffer PROJECTION = MemoryTracker.createFloatBuffer(16);
   private final FloatBuffer buffer = MemoryTracker.createFloatBuffer(16);

   public TheEndPortalRenderer() {
      super();
   }

   public void render(TheEndPortalBlockEntity var1, double var2, double var4, double var6, float var8, int var9) {
      GlStateManager.disableLighting();
      RANDOM.setSeed(31100L);
      GlStateManager.getMatrix(2982, MODELVIEW);
      GlStateManager.getMatrix(2983, PROJECTION);
      double var10 = var2 * var2 + var4 * var4 + var6 * var6;
      int var12 = this.getPasses(var10);
      float var13 = this.getOffset();
      boolean var14 = false;
      GameRenderer var15 = Minecraft.getInstance().gameRenderer;

      for(int var16 = 0; var16 < var12; ++var16) {
         GlStateManager.pushMatrix();
         float var17 = 2.0F / (float)(18 - var16);
         if (var16 == 0) {
            this.bindTexture(END_SKY_LOCATION);
            var17 = 0.15F;
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
         }

         if (var16 >= 1) {
            this.bindTexture(END_PORTAL_LOCATION);
            var14 = true;
            var15.resetFogColor(true);
         }

         if (var16 == 1) {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
         }

         GlStateManager.texGenMode(GlStateManager.TexGen.S, 9216);
         GlStateManager.texGenMode(GlStateManager.TexGen.T, 9216);
         GlStateManager.texGenMode(GlStateManager.TexGen.R, 9216);
         GlStateManager.texGenParam(GlStateManager.TexGen.S, 9474, this.getBuffer(1.0F, 0.0F, 0.0F, 0.0F));
         GlStateManager.texGenParam(GlStateManager.TexGen.T, 9474, this.getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
         GlStateManager.texGenParam(GlStateManager.TexGen.R, 9474, this.getBuffer(0.0F, 0.0F, 1.0F, 0.0F));
         GlStateManager.enableTexGen(GlStateManager.TexGen.S);
         GlStateManager.enableTexGen(GlStateManager.TexGen.T);
         GlStateManager.enableTexGen(GlStateManager.TexGen.R);
         GlStateManager.popMatrix();
         GlStateManager.matrixMode(5890);
         GlStateManager.pushMatrix();
         GlStateManager.loadIdentity();
         GlStateManager.translatef(0.5F, 0.5F, 0.0F);
         GlStateManager.scalef(0.5F, 0.5F, 1.0F);
         float var18 = (float)(var16 + 1);
         GlStateManager.translatef(17.0F / var18, (2.0F + var18 / 1.5F) * ((float)(Util.getMillis() % 800000L) / 800000.0F), 0.0F);
         GlStateManager.rotatef((var18 * var18 * 4321.0F + var18 * 9.0F) * 2.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.scalef(4.5F - var18 / 4.0F, 4.5F - var18 / 4.0F, 1.0F);
         GlStateManager.multMatrix(PROJECTION);
         GlStateManager.multMatrix(MODELVIEW);
         Tesselator var19 = Tesselator.getInstance();
         BufferBuilder var20 = var19.getBuilder();
         var20.begin(7, DefaultVertexFormat.POSITION_COLOR);
         float var21 = (RANDOM.nextFloat() * 0.5F + 0.1F) * var17;
         float var22 = (RANDOM.nextFloat() * 0.5F + 0.4F) * var17;
         float var23 = (RANDOM.nextFloat() * 0.5F + 0.5F) * var17;
         if (var1.shouldRenderFace(Direction.SOUTH)) {
            var20.vertex(var2, var4, var6 + 1.0D).color(var21, var22, var23, 1.0F).endVertex();
            var20.vertex(var2 + 1.0D, var4, var6 + 1.0D).color(var21, var22, var23, 1.0F).endVertex();
            var20.vertex(var2 + 1.0D, var4 + 1.0D, var6 + 1.0D).color(var21, var22, var23, 1.0F).endVertex();
            var20.vertex(var2, var4 + 1.0D, var6 + 1.0D).color(var21, var22, var23, 1.0F).endVertex();
         }

         if (var1.shouldRenderFace(Direction.NORTH)) {
            var20.vertex(var2, var4 + 1.0D, var6).color(var21, var22, var23, 1.0F).endVertex();
            var20.vertex(var2 + 1.0D, var4 + 1.0D, var6).color(var21, var22, var23, 1.0F).endVertex();
            var20.vertex(var2 + 1.0D, var4, var6).color(var21, var22, var23, 1.0F).endVertex();
            var20.vertex(var2, var4, var6).color(var21, var22, var23, 1.0F).endVertex();
         }

         if (var1.shouldRenderFace(Direction.EAST)) {
            var20.vertex(var2 + 1.0D, var4 + 1.0D, var6).color(var21, var22, var23, 1.0F).endVertex();
            var20.vertex(var2 + 1.0D, var4 + 1.0D, var6 + 1.0D).color(var21, var22, var23, 1.0F).endVertex();
            var20.vertex(var2 + 1.0D, var4, var6 + 1.0D).color(var21, var22, var23, 1.0F).endVertex();
            var20.vertex(var2 + 1.0D, var4, var6).color(var21, var22, var23, 1.0F).endVertex();
         }

         if (var1.shouldRenderFace(Direction.WEST)) {
            var20.vertex(var2, var4, var6).color(var21, var22, var23, 1.0F).endVertex();
            var20.vertex(var2, var4, var6 + 1.0D).color(var21, var22, var23, 1.0F).endVertex();
            var20.vertex(var2, var4 + 1.0D, var6 + 1.0D).color(var21, var22, var23, 1.0F).endVertex();
            var20.vertex(var2, var4 + 1.0D, var6).color(var21, var22, var23, 1.0F).endVertex();
         }

         if (var1.shouldRenderFace(Direction.DOWN)) {
            var20.vertex(var2, var4, var6).color(var21, var22, var23, 1.0F).endVertex();
            var20.vertex(var2 + 1.0D, var4, var6).color(var21, var22, var23, 1.0F).endVertex();
            var20.vertex(var2 + 1.0D, var4, var6 + 1.0D).color(var21, var22, var23, 1.0F).endVertex();
            var20.vertex(var2, var4, var6 + 1.0D).color(var21, var22, var23, 1.0F).endVertex();
         }

         if (var1.shouldRenderFace(Direction.UP)) {
            var20.vertex(var2, var4 + (double)var13, var6 + 1.0D).color(var21, var22, var23, 1.0F).endVertex();
            var20.vertex(var2 + 1.0D, var4 + (double)var13, var6 + 1.0D).color(var21, var22, var23, 1.0F).endVertex();
            var20.vertex(var2 + 1.0D, var4 + (double)var13, var6).color(var21, var22, var23, 1.0F).endVertex();
            var20.vertex(var2, var4 + (double)var13, var6).color(var21, var22, var23, 1.0F).endVertex();
         }

         var19.end();
         GlStateManager.popMatrix();
         GlStateManager.matrixMode(5888);
         this.bindTexture(END_SKY_LOCATION);
      }

      GlStateManager.disableBlend();
      GlStateManager.disableTexGen(GlStateManager.TexGen.S);
      GlStateManager.disableTexGen(GlStateManager.TexGen.T);
      GlStateManager.disableTexGen(GlStateManager.TexGen.R);
      GlStateManager.enableLighting();
      if (var14) {
         var15.resetFogColor(false);
      }

   }

   protected int getPasses(double var1) {
      byte var3;
      if (var1 > 36864.0D) {
         var3 = 1;
      } else if (var1 > 25600.0D) {
         var3 = 3;
      } else if (var1 > 16384.0D) {
         var3 = 5;
      } else if (var1 > 9216.0D) {
         var3 = 7;
      } else if (var1 > 4096.0D) {
         var3 = 9;
      } else if (var1 > 1024.0D) {
         var3 = 11;
      } else if (var1 > 576.0D) {
         var3 = 13;
      } else if (var1 > 256.0D) {
         var3 = 14;
      } else {
         var3 = 15;
      }

      return var3;
   }

   protected float getOffset() {
      return 0.75F;
   }

   private FloatBuffer getBuffer(float var1, float var2, float var3, float var4) {
      this.buffer.clear();
      this.buffer.put(var1).put(var2).put(var3).put(var4);
      this.buffer.flip();
      return this.buffer;
   }
}
