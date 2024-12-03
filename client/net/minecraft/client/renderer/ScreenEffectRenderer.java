package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ARGB;
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

   public static void renderScreenEffect(Minecraft var0, PoseStack var1, MultiBufferSource var2) {
      LocalPlayer var3 = var0.player;
      if (!var3.noPhysics) {
         BlockState var4 = getViewBlockingState(var3);
         if (var4 != null) {
            renderTex(var0.getBlockRenderer().getBlockModelShaper().getParticleIcon(var4), var1, var2);
         }
      }

      if (!var0.player.isSpectator()) {
         if (var0.player.isEyeInFluid(FluidTags.WATER)) {
            renderWater(var0, var1, var2);
         }

         if (var0.player.isOnFire()) {
            renderFire(var1, var2);
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

   private static void renderTex(TextureAtlasSprite var0, PoseStack var1, MultiBufferSource var2) {
      float var3 = 0.1F;
      int var4 = ARGB.colorFromFloat(1.0F, 0.1F, 0.1F, 0.1F);
      float var5 = -1.0F;
      float var6 = 1.0F;
      float var7 = -1.0F;
      float var8 = 1.0F;
      float var9 = -0.5F;
      float var10 = var0.getU0();
      float var11 = var0.getU1();
      float var12 = var0.getV0();
      float var13 = var0.getV1();
      Matrix4f var14 = var1.last().pose();
      VertexConsumer var15 = var2.getBuffer(RenderType.blockScreenEffect(var0.atlasLocation()));
      var15.addVertex(var14, -1.0F, -1.0F, -0.5F).setUv(var11, var13).setColor(var4);
      var15.addVertex(var14, 1.0F, -1.0F, -0.5F).setUv(var10, var13).setColor(var4);
      var15.addVertex(var14, 1.0F, 1.0F, -0.5F).setUv(var10, var12).setColor(var4);
      var15.addVertex(var14, -1.0F, 1.0F, -0.5F).setUv(var11, var12).setColor(var4);
   }

   private static void renderWater(Minecraft var0, PoseStack var1, MultiBufferSource var2) {
      BlockPos var3 = BlockPos.containing(var0.player.getX(), var0.player.getEyeY(), var0.player.getZ());
      float var4 = LightTexture.getBrightness(var0.player.level().dimensionType(), var0.player.level().getMaxLocalRawBrightness(var3));
      int var5 = ARGB.colorFromFloat(0.1F, var4, var4, var4);
      float var6 = 4.0F;
      float var7 = -1.0F;
      float var8 = 1.0F;
      float var9 = -1.0F;
      float var10 = 1.0F;
      float var11 = -0.5F;
      float var12 = -var0.player.getYRot() / 64.0F;
      float var13 = var0.player.getXRot() / 64.0F;
      Matrix4f var14 = var1.last().pose();
      VertexConsumer var15 = var2.getBuffer(RenderType.blockScreenEffect(UNDERWATER_LOCATION));
      var15.addVertex(var14, -1.0F, -1.0F, -0.5F).setUv(4.0F + var12, 4.0F + var13).setColor(var5);
      var15.addVertex(var14, 1.0F, -1.0F, -0.5F).setUv(0.0F + var12, 4.0F + var13).setColor(var5);
      var15.addVertex(var14, 1.0F, 1.0F, -0.5F).setUv(0.0F + var12, 0.0F + var13).setColor(var5);
      var15.addVertex(var14, -1.0F, 1.0F, -0.5F).setUv(4.0F + var12, 0.0F + var13).setColor(var5);
   }

   private static void renderFire(PoseStack var0, MultiBufferSource var1) {
      TextureAtlasSprite var2 = ModelBakery.FIRE_1.sprite();
      VertexConsumer var3 = var1.getBuffer(RenderType.fireScreenEffect(var2.atlasLocation()));
      float var4 = var2.getU0();
      float var5 = var2.getU1();
      float var6 = (var4 + var5) / 2.0F;
      float var7 = var2.getV0();
      float var8 = var2.getV1();
      float var9 = (var7 + var8) / 2.0F;
      float var10 = var2.uvShrinkRatio();
      float var11 = Mth.lerp(var10, var4, var6);
      float var12 = Mth.lerp(var10, var5, var6);
      float var13 = Mth.lerp(var10, var7, var9);
      float var14 = Mth.lerp(var10, var8, var9);
      float var15 = 1.0F;

      for(int var16 = 0; var16 < 2; ++var16) {
         var0.pushPose();
         float var17 = -0.5F;
         float var18 = 0.5F;
         float var19 = -0.5F;
         float var20 = 0.5F;
         float var21 = -0.5F;
         var0.translate((float)(-(var16 * 2 - 1)) * 0.24F, -0.3F, 0.0F);
         var0.mulPose(Axis.YP.rotationDegrees((float)(var16 * 2 - 1) * 10.0F));
         Matrix4f var22 = var0.last().pose();
         var3.addVertex(var22, -0.5F, -0.5F, -0.5F).setUv(var12, var14).setColor(1.0F, 1.0F, 1.0F, 0.9F);
         var3.addVertex(var22, 0.5F, -0.5F, -0.5F).setUv(var11, var14).setColor(1.0F, 1.0F, 1.0F, 0.9F);
         var3.addVertex(var22, 0.5F, 0.5F, -0.5F).setUv(var11, var13).setColor(1.0F, 1.0F, 1.0F, 0.9F);
         var3.addVertex(var22, -0.5F, 0.5F, -0.5F).setUv(var12, var13).setColor(1.0F, 1.0F, 1.0F, 0.9F);
         var0.popPose();
      }

   }
}
