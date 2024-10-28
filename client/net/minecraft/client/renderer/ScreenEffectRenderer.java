package net.minecraft.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;

public class ScreenEffectRenderer {
   private static final ResourceLocation UNDERWATER_LOCATION = ResourceLocation.withDefaultNamespace("textures/misc/underwater.png");

   public ScreenEffectRenderer() {
      super();
   }

   public static void renderScreenEffect(Minecraft var0, PoseStack var1) {
      LocalPlayer var2 = var0.player;
      if (!var2.noPhysics) {
         BlockState var3 = getViewBlockingState(var2);
         if (var3 != null) {
            renderTex(var0.getBlockRenderer().getBlockModelShaper().getParticleIcon(var3), var1);
         }
      }

      if (!var0.player.isSpectator()) {
         if (var0.player.isEyeInFluid(FluidTags.WATER)) {
            renderWater(var0, var1);
         }

         if (var0.player.isOnFire()) {
            renderFire(var0, var1);
         }
      }

   }

   @Nullable
   private static BlockState getViewBlockingState(Player var0) {
      BlockPos.MutableBlockPos var1 = new BlockPos.MutableBlockPos();

      for(int var2 = 0; var2 < 8; ++var2) {
         double var3 = var0.getX() + (double)(((float)((var2 >> 0) % 2) - 0.5F) * var0.getBbWidth() * 0.8F);
         double var5 = var0.getEyeY() + (double)(((float)((var2 >> 1) % 2) - 0.5F) * 0.1F * var0.getScale());
         double var7 = var0.getZ() + (double)(((float)((var2 >> 2) % 2) - 0.5F) * var0.getBbWidth() * 0.8F);
         var1.set(var3, var5, var7);
         BlockState var9 = var0.level().getBlockState(var1);
         if (var9.getRenderShape() != RenderShape.INVISIBLE && var9.isViewBlocking(var0.level(), var1)) {
            return var9;
         }
      }

      return null;
   }

   private static void renderTex(TextureAtlasSprite var0, PoseStack var1) {
      RenderSystem.setShaderTexture(0, var0.atlasLocation());
      RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
      float var2 = 0.1F;
      float var3 = -1.0F;
      float var4 = 1.0F;
      float var5 = -1.0F;
      float var6 = 1.0F;
      float var7 = -0.5F;
      float var8 = var0.getU0();
      float var9 = var0.getU1();
      float var10 = var0.getV0();
      float var11 = var0.getV1();
      Matrix4f var12 = var1.last().pose();
      BufferBuilder var13 = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
      var13.addVertex(var12, -1.0F, -1.0F, -0.5F).setUv(var9, var11).setColor(0.1F, 0.1F, 0.1F, 1.0F);
      var13.addVertex(var12, 1.0F, -1.0F, -0.5F).setUv(var8, var11).setColor(0.1F, 0.1F, 0.1F, 1.0F);
      var13.addVertex(var12, 1.0F, 1.0F, -0.5F).setUv(var8, var10).setColor(0.1F, 0.1F, 0.1F, 1.0F);
      var13.addVertex(var12, -1.0F, 1.0F, -0.5F).setUv(var9, var10).setColor(0.1F, 0.1F, 0.1F, 1.0F);
      BufferUploader.drawWithShader(var13.buildOrThrow());
   }

   private static void renderWater(Minecraft var0, PoseStack var1) {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderTexture(0, UNDERWATER_LOCATION);
      BlockPos var2 = BlockPos.containing(var0.player.getX(), var0.player.getEyeY(), var0.player.getZ());
      float var3 = LightTexture.getBrightness(var0.player.level().dimensionType(), var0.player.level().getMaxLocalRawBrightness(var2));
      RenderSystem.enableBlend();
      RenderSystem.setShaderColor(var3, var3, var3, 0.1F);
      float var4 = 4.0F;
      float var5 = -1.0F;
      float var6 = 1.0F;
      float var7 = -1.0F;
      float var8 = 1.0F;
      float var9 = -0.5F;
      float var10 = -var0.player.getYRot() / 64.0F;
      float var11 = var0.player.getXRot() / 64.0F;
      Matrix4f var12 = var1.last().pose();
      BufferBuilder var13 = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
      var13.addVertex(var12, -1.0F, -1.0F, -0.5F).setUv(4.0F + var10, 4.0F + var11);
      var13.addVertex(var12, 1.0F, -1.0F, -0.5F).setUv(0.0F + var10, 4.0F + var11);
      var13.addVertex(var12, 1.0F, 1.0F, -0.5F).setUv(0.0F + var10, 0.0F + var11);
      var13.addVertex(var12, -1.0F, 1.0F, -0.5F).setUv(4.0F + var10, 0.0F + var11);
      BufferUploader.drawWithShader(var13.buildOrThrow());
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.disableBlend();
   }

   private static void renderFire(Minecraft var0, PoseStack var1) {
      RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
      RenderSystem.depthFunc(519);
      RenderSystem.depthMask(false);
      RenderSystem.enableBlend();
      TextureAtlasSprite var2 = ModelBakery.FIRE_1.sprite();
      RenderSystem.setShaderTexture(0, var2.atlasLocation());
      float var3 = var2.getU0();
      float var4 = var2.getU1();
      float var5 = (var3 + var4) / 2.0F;
      float var6 = var2.getV0();
      float var7 = var2.getV1();
      float var8 = (var6 + var7) / 2.0F;
      float var9 = var2.uvShrinkRatio();
      float var10 = Mth.lerp(var9, var3, var5);
      float var11 = Mth.lerp(var9, var4, var5);
      float var12 = Mth.lerp(var9, var6, var8);
      float var13 = Mth.lerp(var9, var7, var8);
      float var14 = 1.0F;

      for(int var15 = 0; var15 < 2; ++var15) {
         var1.pushPose();
         float var16 = -0.5F;
         float var17 = 0.5F;
         float var18 = -0.5F;
         float var19 = 0.5F;
         float var20 = -0.5F;
         var1.translate((float)(-(var15 * 2 - 1)) * 0.24F, -0.3F, 0.0F);
         var1.mulPose(Axis.YP.rotationDegrees((float)(var15 * 2 - 1) * 10.0F));
         Matrix4f var21 = var1.last().pose();
         BufferBuilder var22 = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
         var22.addVertex(var21, -0.5F, -0.5F, -0.5F).setUv(var11, var13).setColor(1.0F, 1.0F, 1.0F, 0.9F);
         var22.addVertex(var21, 0.5F, -0.5F, -0.5F).setUv(var10, var13).setColor(1.0F, 1.0F, 1.0F, 0.9F);
         var22.addVertex(var21, 0.5F, 0.5F, -0.5F).setUv(var10, var12).setColor(1.0F, 1.0F, 1.0F, 0.9F);
         var22.addVertex(var21, -0.5F, 0.5F, -0.5F).setUv(var11, var12).setColor(1.0F, 1.0F, 1.0F, 0.9F);
         BufferUploader.drawWithShader(var22.buildOrThrow());
         var1.popPose();
      }

      RenderSystem.disableBlend();
      RenderSystem.depthMask(true);
      RenderSystem.depthFunc(515);
   }
}
