package net.minecraft.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexConsumer;
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

public class SkyRenderer implements AutoCloseable {
   private static final ResourceLocation SUN_LOCATION = ResourceLocation.withDefaultNamespace("textures/environment/sun.png");
   private static final ResourceLocation MOON_LOCATION = ResourceLocation.withDefaultNamespace("textures/environment/moon_phases.png");
   public static final ResourceLocation END_SKY_LOCATION = ResourceLocation.withDefaultNamespace("textures/environment/end_sky.png");
   private static final float SKY_DISC_RADIUS = 512.0F;
   private final VertexBuffer starBuffer;
   private final VertexBuffer topSkyBuffer;
   private final VertexBuffer bottomSkyBuffer;
   private final VertexBuffer endSkyBuffer;

   public SkyRenderer() {
      super();
      this.starBuffer = VertexBuffer.uploadStatic(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION, this::buildStars);
      this.topSkyBuffer = VertexBuffer.uploadStatic(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION, (var1) -> {
         this.buildSkyDisc(var1, 16.0F);
      });
      this.bottomSkyBuffer = VertexBuffer.uploadStatic(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION, (var1) -> {
         this.buildSkyDisc(var1, -16.0F);
      });
      this.endSkyBuffer = VertexBuffer.uploadStatic(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR, this::buildEndSky);
   }

   private void buildStars(VertexConsumer var1) {
      RandomSource var2 = RandomSource.create(10842L);
      boolean var3 = true;
      float var4 = 100.0F;

      for(int var5 = 0; var5 < 1500; ++var5) {
         float var6 = var2.nextFloat() * 2.0F - 1.0F;
         float var7 = var2.nextFloat() * 2.0F - 1.0F;
         float var8 = var2.nextFloat() * 2.0F - 1.0F;
         float var9 = 0.15F + var2.nextFloat() * 0.1F;
         float var10 = Mth.lengthSquared(var6, var7, var8);
         if (!(var10 <= 0.010000001F) && !(var10 >= 1.0F)) {
            Vector3f var11 = (new Vector3f(var6, var7, var8)).normalize(100.0F);
            float var12 = (float)(var2.nextDouble() * 3.1415927410125732 * 2.0);
            Matrix3f var13 = (new Matrix3f()).rotateTowards((new Vector3f(var11)).negate(), new Vector3f(0.0F, 1.0F, 0.0F)).rotateZ(-var12);
            var1.addVertex((new Vector3f(var9, -var9, 0.0F)).mul(var13).add(var11));
            var1.addVertex((new Vector3f(var9, var9, 0.0F)).mul(var13).add(var11));
            var1.addVertex((new Vector3f(-var9, var9, 0.0F)).mul(var13).add(var11));
            var1.addVertex((new Vector3f(-var9, -var9, 0.0F)).mul(var13).add(var11));
         }
      }

   }

   private void buildSkyDisc(VertexConsumer var1, float var2) {
      float var3 = Math.signum(var2) * 512.0F;
      var1.addVertex(0.0F, var2, 0.0F);

      for(int var4 = -180; var4 <= 180; var4 += 45) {
         var1.addVertex(var3 * Mth.cos((float)var4 * 0.017453292F), var2, 512.0F * Mth.sin((float)var4 * 0.017453292F));
      }

   }

   public void renderSkyDisc(float var1, float var2, float var3) {
      RenderSystem.setShaderColor(var1, var2, var3, 1.0F);
      this.topSkyBuffer.drawWithRenderType(RenderType.sky());
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public void renderDarkDisc(PoseStack var1) {
      RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);
      var1.pushPose();
      var1.translate(0.0F, 12.0F, 0.0F);
      this.bottomSkyBuffer.drawWithRenderType(RenderType.sky());
      var1.popPose();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public void renderSunMoonAndStars(PoseStack var1, MultiBufferSource.BufferSource var2, float var3, int var4, float var5, float var6, FogParameters var7) {
      var1.pushPose();
      var1.mulPose(Axis.YP.rotationDegrees(-90.0F));
      var1.mulPose(Axis.XP.rotationDegrees(var3 * 360.0F));
      this.renderSun(var5, var2, var1);
      this.renderMoon(var4, var5, var2, var1);
      var2.endBatch();
      if (var6 > 0.0F) {
         this.renderStars(var7, var6, var1);
      }

      var1.popPose();
   }

   private void renderSun(float var1, MultiBufferSource var2, PoseStack var3) {
      float var4 = 30.0F;
      float var5 = 100.0F;
      VertexConsumer var6 = var2.getBuffer(RenderType.celestial(SUN_LOCATION));
      int var7 = ARGB.white(var1);
      Matrix4f var8 = var3.last().pose();
      var6.addVertex(var8, -30.0F, 100.0F, -30.0F).setUv(0.0F, 0.0F).setColor(var7);
      var6.addVertex(var8, 30.0F, 100.0F, -30.0F).setUv(1.0F, 0.0F).setColor(var7);
      var6.addVertex(var8, 30.0F, 100.0F, 30.0F).setUv(1.0F, 1.0F).setColor(var7);
      var6.addVertex(var8, -30.0F, 100.0F, 30.0F).setUv(0.0F, 1.0F).setColor(var7);
   }

   private void renderMoon(int var1, float var2, MultiBufferSource var3, PoseStack var4) {
      float var5 = 20.0F;
      int var6 = var1 % 4;
      int var7 = var1 / 4 % 2;
      float var8 = (float)(var6 + 0) / 4.0F;
      float var9 = (float)(var7 + 0) / 2.0F;
      float var10 = (float)(var6 + 1) / 4.0F;
      float var11 = (float)(var7 + 1) / 2.0F;
      float var12 = 100.0F;
      VertexConsumer var13 = var3.getBuffer(RenderType.celestial(MOON_LOCATION));
      int var14 = ARGB.white(var2);
      Matrix4f var15 = var4.last().pose();
      var13.addVertex(var15, -20.0F, -100.0F, 20.0F).setUv(var10, var11).setColor(var14);
      var13.addVertex(var15, 20.0F, -100.0F, 20.0F).setUv(var8, var11).setColor(var14);
      var13.addVertex(var15, 20.0F, -100.0F, -20.0F).setUv(var8, var9).setColor(var14);
      var13.addVertex(var15, -20.0F, -100.0F, -20.0F).setUv(var10, var9).setColor(var14);
   }

   private void renderStars(FogParameters var1, float var2, PoseStack var3) {
      Matrix4fStack var4 = RenderSystem.getModelViewStack();
      var4.pushMatrix();
      var4.mul(var3.last().pose());
      RenderSystem.setShaderColor(var2, var2, var2, var2);
      RenderSystem.setShaderFog(FogParameters.NO_FOG);
      this.starBuffer.drawWithRenderType(RenderType.stars());
      RenderSystem.setShaderFog(var1);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      var4.popMatrix();
   }

   public void renderSunriseAndSunset(PoseStack var1, MultiBufferSource.BufferSource var2, float var3, int var4) {
      var1.pushPose();
      var1.mulPose(Axis.XP.rotationDegrees(90.0F));
      float var5 = Mth.sin(var3) < 0.0F ? 180.0F : 0.0F;
      var1.mulPose(Axis.ZP.rotationDegrees(var5));
      var1.mulPose(Axis.ZP.rotationDegrees(90.0F));
      Matrix4f var6 = var1.last().pose();
      VertexConsumer var7 = var2.getBuffer(RenderType.sunriseSunset());
      float var8 = ARGB.alphaFloat(var4);
      var7.addVertex(var6, 0.0F, 100.0F, 0.0F).setColor(var4);
      int var9 = ARGB.transparent(var4);
      boolean var10 = true;

      for(int var11 = 0; var11 <= 16; ++var11) {
         float var12 = (float)var11 * 6.2831855F / 16.0F;
         float var13 = Mth.sin(var12);
         float var14 = Mth.cos(var12);
         var7.addVertex(var6, var13 * 120.0F, var14 * 120.0F, -var14 * 40.0F * var8).setColor(var9);
      }

      var1.popPose();
   }

   private void buildEndSky(VertexConsumer var1) {
      for(int var2 = 0; var2 < 6; ++var2) {
         Matrix4f var3 = new Matrix4f();
         switch (var2) {
            case 1 -> var3.rotationX(1.5707964F);
            case 2 -> var3.rotationX(-1.5707964F);
            case 3 -> var3.rotationX(3.1415927F);
            case 4 -> var3.rotationZ(1.5707964F);
            case 5 -> var3.rotationZ(-1.5707964F);
         }

         var1.addVertex(var3, -100.0F, -100.0F, -100.0F).setUv(0.0F, 0.0F).setColor(-14145496);
         var1.addVertex(var3, -100.0F, -100.0F, 100.0F).setUv(0.0F, 16.0F).setColor(-14145496);
         var1.addVertex(var3, 100.0F, -100.0F, 100.0F).setUv(16.0F, 16.0F).setColor(-14145496);
         var1.addVertex(var3, 100.0F, -100.0F, -100.0F).setUv(16.0F, 0.0F).setColor(-14145496);
      }

   }

   public void renderEndSky() {
      this.endSkyBuffer.drawWithRenderType(RenderType.endSky());
   }

   public void close() {
      this.starBuffer.close();
      this.topSkyBuffer.close();
      this.bottomSkyBuffer.close();
      this.endSkyBuffer.close();
   }
}
