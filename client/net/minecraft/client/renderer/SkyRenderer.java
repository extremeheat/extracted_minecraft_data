package net.minecraft.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;

public class SkyRenderer {
   private static final ResourceLocation SUN_LOCATION = ResourceLocation.withDefaultNamespace("textures/environment/sun.png");
   private static final ResourceLocation MOON_LOCATION = ResourceLocation.withDefaultNamespace("textures/environment/moon_phases.png");
   private static final ResourceLocation END_SKY_LOCATION = ResourceLocation.withDefaultNamespace("textures/environment/end_sky.png");
   private static final float SKY_DISC_RADIUS = 512.0F;
   private final VertexBuffer starBuffer = this.createStarBuffer();
   private final VertexBuffer topSkyBuffer = this.createTopSkyBuffer();
   private final VertexBuffer bottomSkyBuffer = this.createBottomSkyBuffer();

   public SkyRenderer() {
      super();
   }

   private VertexBuffer createStarBuffer() {
      VertexBuffer var1 = new VertexBuffer(VertexBuffer.Usage.STATIC);
      var1.bind();
      var1.upload(this.drawStars(Tesselator.getInstance()));
      VertexBuffer.unbind();
      return var1;
   }

   private VertexBuffer createTopSkyBuffer() {
      VertexBuffer var1 = new VertexBuffer(VertexBuffer.Usage.STATIC);
      var1.bind();
      var1.upload(this.buildSkyDisc(Tesselator.getInstance(), 16.0F));
      VertexBuffer.unbind();
      return var1;
   }

   private VertexBuffer createBottomSkyBuffer() {
      VertexBuffer var1 = new VertexBuffer(VertexBuffer.Usage.STATIC);
      var1.bind();
      var1.upload(this.buildSkyDisc(Tesselator.getInstance(), -16.0F));
      VertexBuffer.unbind();
      return var1;
   }

   private MeshData drawStars(Tesselator var1) {
      RandomSource var2 = RandomSource.create(10842L);
      short var3 = 1500;
      float var4 = 100.0F;
      BufferBuilder var5 = var1.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);

      for (int var6 = 0; var6 < 1500; var6++) {
         float var7 = var2.nextFloat() * 2.0F - 1.0F;
         float var8 = var2.nextFloat() * 2.0F - 1.0F;
         float var9 = var2.nextFloat() * 2.0F - 1.0F;
         float var10 = 0.15F + var2.nextFloat() * 0.1F;
         float var11 = Mth.lengthSquared(var7, var8, var9);
         if (!(var11 <= 0.010000001F) && !(var11 >= 1.0F)) {
            Vector3f var12 = new Vector3f(var7, var8, var9).normalize(100.0F);
            float var13 = (float)(var2.nextDouble() * 3.1415927410125732 * 2.0);
            Matrix3f var14 = new Matrix3f().rotateTowards(new Vector3f(var12).negate(), new Vector3f(0.0F, 1.0F, 0.0F)).rotateZ(-var13);
            var5.addVertex(new Vector3f(var10, -var10, 0.0F).mul(var14).add(var12));
            var5.addVertex(new Vector3f(var10, var10, 0.0F).mul(var14).add(var12));
            var5.addVertex(new Vector3f(-var10, var10, 0.0F).mul(var14).add(var12));
            var5.addVertex(new Vector3f(-var10, -var10, 0.0F).mul(var14).add(var12));
         }
      }

      return var5.buildOrThrow();
   }

   private MeshData buildSkyDisc(Tesselator var1, float var2) {
      float var3 = Math.signum(var2) * 512.0F;
      BufferBuilder var4 = var1.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION);
      var4.addVertex(0.0F, var2, 0.0F);

      for (short var5 = -180; var5 <= 180; var5 += 45) {
         var4.addVertex(var3 * Mth.cos((float)var5 * 0.017453292F), var2, 512.0F * Mth.sin((float)var5 * 0.017453292F));
      }

      return var4.buildOrThrow();
   }

   public void renderSkyDisc(float var1, float var2, float var3) {
      RenderSystem.depthMask(false);
      RenderSystem.setShaderColor(var1, var2, var3, 1.0F);
      this.topSkyBuffer.bind();
      this.topSkyBuffer.drawWithShader(RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), GameRenderer.getPositionShader());
      VertexBuffer.unbind();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.depthMask(true);
   }

   public void renderDarkDisc(PoseStack var1) {
      RenderSystem.depthMask(false);
      RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);
      var1.pushPose();
      var1.translate(0.0F, 12.0F, 0.0F);
      this.bottomSkyBuffer.bind();
      this.bottomSkyBuffer.drawWithShader(RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), GameRenderer.getPositionShader());
      VertexBuffer.unbind();
      var1.popPose();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.depthMask(true);
   }

   public void renderSunMoonAndStars(PoseStack var1, Tesselator var2, float var3, int var4, float var5, float var6, FogParameters var7) {
      var1.pushPose();
      var1.mulPose(Axis.YP.rotationDegrees(-90.0F));
      var1.mulPose(Axis.XP.rotationDegrees(var3 * 360.0F));
      this.renderSun(var5, var2, var1);
      this.renderMoon(var4, var5, var2, var1);
      if (var6 > 0.0F) {
         this.renderStars(var7, var6, var1);
      }

      var1.popPose();
   }

   private void renderSun(float var1, Tesselator var2, PoseStack var3) {
      float var4 = 30.0F;
      float var5 = 100.0F;
      BufferBuilder var6 = var2.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
      Matrix4f var7 = var3.last().pose();
      RenderSystem.depthMask(false);
      RenderSystem.overlayBlendFunc();
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, var1);
      RenderSystem.setShaderTexture(0, SUN_LOCATION);
      RenderSystem.enableBlend();
      var6.addVertex(var7, -30.0F, 100.0F, -30.0F).setUv(0.0F, 0.0F);
      var6.addVertex(var7, 30.0F, 100.0F, -30.0F).setUv(1.0F, 0.0F);
      var6.addVertex(var7, 30.0F, 100.0F, 30.0F).setUv(1.0F, 1.0F);
      var6.addVertex(var7, -30.0F, 100.0F, 30.0F).setUv(0.0F, 1.0F);
      BufferUploader.drawWithShader(var6.buildOrThrow());
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.disableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.depthMask(true);
   }

   private void renderMoon(int var1, float var2, Tesselator var3, PoseStack var4) {
      float var5 = 20.0F;
      int var6 = var1 % 4;
      int var7 = var1 / 4 % 2;
      float var8 = (float)(var6 + 0) / 4.0F;
      float var9 = (float)(var7 + 0) / 2.0F;
      float var10 = (float)(var6 + 1) / 4.0F;
      float var11 = (float)(var7 + 1) / 2.0F;
      float var12 = 100.0F;
      BufferBuilder var13 = var3.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
      RenderSystem.depthMask(false);
      RenderSystem.overlayBlendFunc();
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, var2);
      RenderSystem.setShaderTexture(0, MOON_LOCATION);
      RenderSystem.enableBlend();
      Matrix4f var14 = var4.last().pose();
      var13.addVertex(var14, -20.0F, -100.0F, 20.0F).setUv(var10, var11);
      var13.addVertex(var14, 20.0F, -100.0F, 20.0F).setUv(var8, var11);
      var13.addVertex(var14, 20.0F, -100.0F, -20.0F).setUv(var8, var9);
      var13.addVertex(var14, -20.0F, -100.0F, -20.0F).setUv(var10, var9);
      BufferUploader.drawWithShader(var13.buildOrThrow());
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.disableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.depthMask(true);
   }

   private void renderStars(FogParameters var1, float var2, PoseStack var3) {
      Matrix4fStack var4 = RenderSystem.getModelViewStack();
      var4.pushMatrix();
      var4.mul(var3.last().pose());
      RenderSystem.depthMask(false);
      RenderSystem.overlayBlendFunc();
      RenderSystem.setShaderColor(var2, var2, var2, var2);
      RenderSystem.enableBlend();
      RenderSystem.setShaderFog(FogParameters.NO_FOG);
      this.starBuffer.bind();
      this.starBuffer.drawWithShader(var4, RenderSystem.getProjectionMatrix(), GameRenderer.getPositionShader());
      VertexBuffer.unbind();
      RenderSystem.setShaderFog(var1);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.disableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.depthMask(true);
      var4.popMatrix();
   }

   public void renderSunriseAndSunset(PoseStack var1, Tesselator var2, float var3, int var4) {
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      RenderSystem.depthMask(false);
      RenderSystem.enableBlend();
      var1.pushPose();
      var1.mulPose(Axis.XP.rotationDegrees(90.0F));
      float var5 = Mth.sin(var3) < 0.0F ? 180.0F : 0.0F;
      var1.mulPose(Axis.ZP.rotationDegrees(var5));
      var1.mulPose(Axis.ZP.rotationDegrees(90.0F));
      Matrix4f var6 = var1.last().pose();
      BufferBuilder var7 = var2.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
      float var8 = ARGB.from8BitChannel(ARGB.alpha(var4));
      var7.addVertex(var6, 0.0F, 100.0F, 0.0F).setColor(var4);
      int var9 = ARGB.transparent(var4);
      byte var10 = 16;

      for (int var11 = 0; var11 <= 16; var11++) {
         float var12 = (float)var11 * 6.2831855F / 16.0F;
         float var13 = Mth.sin(var12);
         float var14 = Mth.cos(var12);
         var7.addVertex(var6, var13 * 120.0F, var14 * 120.0F, -var14 * 40.0F * var8).setColor(var9);
      }

      BufferUploader.drawWithShader(var7.buildOrThrow());
      var1.popPose();
      RenderSystem.disableBlend();
      RenderSystem.depthMask(true);
   }

   public void renderEndSky(PoseStack var1) {
      RenderSystem.enableBlend();
      RenderSystem.depthMask(false);
      RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
      RenderSystem.setShaderTexture(0, END_SKY_LOCATION);
      Tesselator var2 = Tesselator.getInstance();

      for (int var3 = 0; var3 < 6; var3++) {
         var1.pushPose();
         if (var3 == 1) {
            var1.mulPose(Axis.XP.rotationDegrees(90.0F));
         }

         if (var3 == 2) {
            var1.mulPose(Axis.XP.rotationDegrees(-90.0F));
         }

         if (var3 == 3) {
            var1.mulPose(Axis.XP.rotationDegrees(180.0F));
         }

         if (var3 == 4) {
            var1.mulPose(Axis.ZP.rotationDegrees(90.0F));
         }

         if (var3 == 5) {
            var1.mulPose(Axis.ZP.rotationDegrees(-90.0F));
         }

         Matrix4f var4 = var1.last().pose();
         BufferBuilder var5 = var2.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
         var5.addVertex(var4, -100.0F, -100.0F, -100.0F).setUv(0.0F, 0.0F).setColor(-14145496);
         var5.addVertex(var4, -100.0F, -100.0F, 100.0F).setUv(0.0F, 16.0F).setColor(-14145496);
         var5.addVertex(var4, 100.0F, -100.0F, 100.0F).setUv(16.0F, 16.0F).setColor(-14145496);
         var5.addVertex(var4, 100.0F, -100.0F, -100.0F).setUv(16.0F, 0.0F).setColor(-14145496);
         BufferUploader.drawWithShader(var5.buildOrThrow());
         var1.popPose();
      }

      RenderSystem.depthMask(true);
      RenderSystem.disableBlend();
   }
}
