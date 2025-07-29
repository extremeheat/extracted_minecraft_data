package net.minecraft.client.renderer;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;
import org.joml.Quaternionfc;

public class ScreenEffectRenderer {
   private static final ResourceLocation UNDERWATER_LOCATION = ResourceLocation.withDefaultNamespace("textures/misc/underwater.png");
   private final Minecraft minecraft;
   private final MaterialSet materials;
   private final MultiBufferSource bufferSource;
   public static final int ITEM_ACTIVATION_ANIMATION_LENGTH = 40;
   @Nullable
   private ItemStack itemActivationItem;
   private int itemActivationTicks;
   private float itemActivationOffX;
   private float itemActivationOffY;

   public ScreenEffectRenderer(Minecraft var1, MaterialSet var2, MultiBufferSource var3) {
      super();
      this.minecraft = var1;
      this.materials = var2;
      this.bufferSource = var3;
   }

   public void tick() {
      if (this.itemActivationTicks > 0) {
         --this.itemActivationTicks;
         if (this.itemActivationTicks == 0) {
            this.itemActivationItem = null;
         }
      }

   }

   public void renderScreenEffect(boolean var1, float var2) {
      PoseStack var3 = new PoseStack();
      LocalPlayer var4 = this.minecraft.player;
      if (this.minecraft.options.getCameraType().isFirstPerson() && !var1) {
         if (!var4.noPhysics) {
            BlockState var5 = getViewBlockingState(var4);
            if (var5 != null) {
               renderTex(this.minecraft.getBlockRenderer().getBlockModelShaper().getParticleIcon(var5), var3, this.bufferSource);
            }
         }

         if (!this.minecraft.player.isSpectator()) {
            if (this.minecraft.player.isEyeInFluid(FluidTags.WATER)) {
               renderWater(this.minecraft, var3, this.bufferSource);
            }

            if (this.minecraft.player.isOnFire()) {
               TextureAtlasSprite var6 = this.materials.get(ModelBakery.FIRE_1);
               renderFire(var3, this.bufferSource, var6);
            }
         }
      }

      if (!this.minecraft.options.hideGui) {
         this.renderItemActivationAnimation(var3, var2);
      }

   }

   private void renderItemActivationAnimation(PoseStack var1, float var2) {
      if (this.itemActivationItem != null && this.itemActivationTicks > 0) {
         int var3 = 40 - this.itemActivationTicks;
         float var4 = ((float)var3 + var2) / 40.0F;
         float var5 = var4 * var4;
         float var6 = var4 * var5;
         float var7 = 10.25F * var6 * var5 - 24.95F * var5 * var5 + 25.5F * var6 - 13.8F * var5 + 4.0F * var4;
         float var8 = var7 * 3.1415927F;
         float var9 = (float)this.minecraft.getWindow().getWidth() / (float)this.minecraft.getWindow().getHeight();
         float var10 = this.itemActivationOffX * 0.3F * var9;
         float var11 = this.itemActivationOffY * 0.3F;
         var1.pushPose();
         var1.translate(var10 * Mth.abs(Mth.sin(var8 * 2.0F)), var11 * Mth.abs(Mth.sin(var8 * 2.0F)), -10.0F + 9.0F * Mth.sin(var8));
         float var12 = 0.8F;
         var1.scale(0.8F, 0.8F, 0.8F);
         var1.mulPose((Quaternionfc)Axis.YP.rotationDegrees(900.0F * Mth.abs(Mth.sin(var8))));
         var1.mulPose((Quaternionfc)Axis.XP.rotationDegrees(6.0F * Mth.cos(var4 * 8.0F)));
         var1.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(6.0F * Mth.cos(var4 * 8.0F)));
         this.minecraft.gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_3D);
         this.minecraft.getItemRenderer().renderStatic(this.itemActivationItem, ItemDisplayContext.FIXED, 15728880, OverlayTexture.NO_OVERLAY, var1, this.bufferSource, this.minecraft.level, 0);
         var1.popPose();
      }
   }

   public void resetItemActivation() {
      this.itemActivationItem = null;
   }

   public void displayItemActivation(ItemStack var1, RandomSource var2) {
      this.itemActivationItem = var1;
      this.itemActivationTicks = 40;
      this.itemActivationOffX = var2.nextFloat() * 2.0F - 1.0F;
      this.itemActivationOffY = var2.nextFloat() * 2.0F - 1.0F;
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

   private static void renderFire(PoseStack var0, MultiBufferSource var1, TextureAtlasSprite var2) {
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
         var0.mulPose((Quaternionfc)Axis.YP.rotationDegrees((float)(var16 * 2 - 1) * 10.0F));
         Matrix4f var22 = var0.last().pose();
         var3.addVertex(var22, -0.5F, -0.5F, -0.5F).setUv(var12, var14).setColor(1.0F, 1.0F, 1.0F, 0.9F);
         var3.addVertex(var22, 0.5F, -0.5F, -0.5F).setUv(var11, var14).setColor(1.0F, 1.0F, 1.0F, 0.9F);
         var3.addVertex(var22, 0.5F, 0.5F, -0.5F).setUv(var11, var13).setColor(1.0F, 1.0F, 1.0F, 0.9F);
         var3.addVertex(var22, -0.5F, 0.5F, -0.5F).setUv(var12, var13).setColor(1.0F, 1.0F, 1.0F, 0.9F);
         var0.popPose();
      }

   }
}
