package net.minecraft.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
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

public class ScreenEffectRenderer {
   private static final ResourceLocation UNDERWATER_LOCATION = new ResourceLocation("textures/misc/underwater.png");

   public static void renderScreenEffect(Minecraft var0, PoseStack var1) {
      RenderSystem.disableAlphaTest();
      LocalPlayer var2 = var0.player;
      if (!var2.noPhysics) {
         BlockState var3 = getViewBlockingState(var2);
         if (var3 != null) {
            renderTex(var0, var0.getBlockRenderer().getBlockModelShaper().getParticleIcon(var3), var1);
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

      RenderSystem.enableAlphaTest();
   }

   @Nullable
   private static BlockState getViewBlockingState(Player var0) {
      BlockPos.MutableBlockPos var1 = new BlockPos.MutableBlockPos();

      for(int var2 = 0; var2 < 8; ++var2) {
         double var3 = var0.getX() + (double)(((float)((var2 >> 0) % 2) - 0.5F) * var0.getBbWidth() * 0.8F);
         double var5 = var0.getEyeY() + (double)(((float)((var2 >> 1) % 2) - 0.5F) * 0.1F);
         double var7 = var0.getZ() + (double)(((float)((var2 >> 2) % 2) - 0.5F) * var0.getBbWidth() * 0.8F);
         var1.set(var3, var5, var7);
         BlockState var9 = var0.level.getBlockState(var1);
         if (var9.getRenderShape() != RenderShape.INVISIBLE && var9.isViewBlocking(var0.level, var1)) {
            return var9;
         }
      }

      return null;
   }

   private static void renderTex(Minecraft var0, TextureAtlasSprite var1, PoseStack var2) {
      var0.getTextureManager().bind(var1.atlas().location());
      BufferBuilder var3 = Tesselator.getInstance().getBuilder();
      float var4 = 0.1F;
      float var5 = -1.0F;
      float var6 = 1.0F;
      float var7 = -1.0F;
      float var8 = 1.0F;
      float var9 = -0.5F;
      float var10 = var1.getU0();
      float var11 = var1.getU1();
      float var12 = var1.getV0();
      float var13 = var1.getV1();
      Matrix4f var14 = var2.last().pose();
      var3.begin(7, DefaultVertexFormat.POSITION_COLOR_TEX);
      var3.vertex(var14, -1.0F, -1.0F, -0.5F).color(0.1F, 0.1F, 0.1F, 1.0F).uv(var11, var13).endVertex();
      var3.vertex(var14, 1.0F, -1.0F, -0.5F).color(0.1F, 0.1F, 0.1F, 1.0F).uv(var10, var13).endVertex();
      var3.vertex(var14, 1.0F, 1.0F, -0.5F).color(0.1F, 0.1F, 0.1F, 1.0F).uv(var10, var12).endVertex();
      var3.vertex(var14, -1.0F, 1.0F, -0.5F).color(0.1F, 0.1F, 0.1F, 1.0F).uv(var11, var12).endVertex();
      var3.end();
      BufferUploader.end(var3);
   }

   private static void renderWater(Minecraft var0, PoseStack var1) {
      RenderSystem.enableTexture();
      var0.getTextureManager().bind(UNDERWATER_LOCATION);
      BufferBuilder var2 = Tesselator.getInstance().getBuilder();
      float var3 = var0.player.getBrightness();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      float var4 = 4.0F;
      float var5 = -1.0F;
      float var6 = 1.0F;
      float var7 = -1.0F;
      float var8 = 1.0F;
      float var9 = -0.5F;
      float var10 = -var0.player.yRot / 64.0F;
      float var11 = var0.player.xRot / 64.0F;
      Matrix4f var12 = var1.last().pose();
      var2.begin(7, DefaultVertexFormat.POSITION_COLOR_TEX);
      var2.vertex(var12, -1.0F, -1.0F, -0.5F).color(var3, var3, var3, 0.1F).uv(4.0F + var10, 4.0F + var11).endVertex();
      var2.vertex(var12, 1.0F, -1.0F, -0.5F).color(var3, var3, var3, 0.1F).uv(0.0F + var10, 4.0F + var11).endVertex();
      var2.vertex(var12, 1.0F, 1.0F, -0.5F).color(var3, var3, var3, 0.1F).uv(0.0F + var10, 0.0F + var11).endVertex();
      var2.vertex(var12, -1.0F, 1.0F, -0.5F).color(var3, var3, var3, 0.1F).uv(4.0F + var10, 0.0F + var11).endVertex();
      var2.end();
      BufferUploader.end(var2);
      RenderSystem.disableBlend();
   }

   private static void renderFire(Minecraft var0, PoseStack var1) {
      BufferBuilder var2 = Tesselator.getInstance().getBuilder();
      RenderSystem.depthFunc(519);
      RenderSystem.depthMask(false);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.enableTexture();
      TextureAtlasSprite var3 = ModelBakery.FIRE_1.sprite();
      var0.getTextureManager().bind(var3.atlas().location());
      float var4 = var3.getU0();
      float var5 = var3.getU1();
      float var6 = (var4 + var5) / 2.0F;
      float var7 = var3.getV0();
      float var8 = var3.getV1();
      float var9 = (var7 + var8) / 2.0F;
      float var10 = var3.uvShrinkRatio();
      float var11 = Mth.lerp(var10, var4, var6);
      float var12 = Mth.lerp(var10, var5, var6);
      float var13 = Mth.lerp(var10, var7, var9);
      float var14 = Mth.lerp(var10, var8, var9);
      float var15 = 1.0F;

      for(int var16 = 0; var16 < 2; ++var16) {
         var1.pushPose();
         float var17 = -0.5F;
         float var18 = 0.5F;
         float var19 = -0.5F;
         float var20 = 0.5F;
         float var21 = -0.5F;
         var1.translate((double)((float)(-(var16 * 2 - 1)) * 0.24F), -0.30000001192092896D, 0.0D);
         var1.mulPose(Vector3f.YP.rotationDegrees((float)(var16 * 2 - 1) * 10.0F));
         Matrix4f var22 = var1.last().pose();
         var2.begin(7, DefaultVertexFormat.POSITION_COLOR_TEX);
         var2.vertex(var22, -0.5F, -0.5F, -0.5F).color(1.0F, 1.0F, 1.0F, 0.9F).uv(var12, var14).endVertex();
         var2.vertex(var22, 0.5F, -0.5F, -0.5F).color(1.0F, 1.0F, 1.0F, 0.9F).uv(var11, var14).endVertex();
         var2.vertex(var22, 0.5F, 0.5F, -0.5F).color(1.0F, 1.0F, 1.0F, 0.9F).uv(var11, var13).endVertex();
         var2.vertex(var22, -0.5F, 0.5F, -0.5F).color(1.0F, 1.0F, 1.0F, 0.9F).uv(var12, var13).endVertex();
         var2.end();
         BufferUploader.end(var2);
         var1.popPose();
      }

      RenderSystem.disableBlend();
      RenderSystem.depthMask(true);
      RenderSystem.depthFunc(515);
   }
}
