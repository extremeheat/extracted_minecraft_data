package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public abstract class EntityRenderer {
   protected final EntityRenderDispatcher entityRenderDispatcher;
   protected float shadowRadius;
   protected float shadowStrength = 1.0F;

   protected EntityRenderer(EntityRenderDispatcher var1) {
      this.entityRenderDispatcher = var1;
   }

   public final int getPackedLightCoords(Entity var1, float var2) {
      return LightTexture.pack(this.getBlockLightLevel(var1, var2), var1.level.getBrightness(LightLayer.SKY, new BlockPos(var1.getEyePosition(var2))));
   }

   protected int getBlockLightLevel(Entity var1, float var2) {
      return var1.isOnFire() ? 15 : var1.level.getBrightness(LightLayer.BLOCK, new BlockPos(var1.getEyePosition(var2)));
   }

   public boolean shouldRender(Entity var1, Frustum var2, double var3, double var5, double var7) {
      if (!var1.shouldRender(var3, var5, var7)) {
         return false;
      } else if (var1.noCulling) {
         return true;
      } else {
         AABB var9 = var1.getBoundingBoxForCulling().inflate(0.5D);
         if (var9.hasNaN() || var9.getSize() == 0.0D) {
            var9 = new AABB(var1.getX() - 2.0D, var1.getY() - 2.0D, var1.getZ() - 2.0D, var1.getX() + 2.0D, var1.getY() + 2.0D, var1.getZ() + 2.0D);
         }

         return var2.isVisible(var9);
      }
   }

   public Vec3 getRenderOffset(Entity var1, float var2) {
      return Vec3.ZERO;
   }

   public void render(Entity var1, float var2, float var3, PoseStack var4, MultiBufferSource var5, int var6) {
      if (this.shouldShowName(var1)) {
         this.renderNameTag(var1, var1.getDisplayName().getColoredString(), var4, var5, var6);
      }
   }

   protected boolean shouldShowName(Entity var1) {
      return var1.shouldShowName() && var1.hasCustomName();
   }

   public abstract ResourceLocation getTextureLocation(Entity var1);

   public Font getFont() {
      return this.entityRenderDispatcher.getFont();
   }

   protected void renderNameTag(Entity var1, String var2, PoseStack var3, MultiBufferSource var4, int var5) {
      double var6 = this.entityRenderDispatcher.distanceToSqr(var1);
      if (var6 <= 4096.0D) {
         boolean var8 = !var1.isDiscrete();
         float var9 = var1.getBbHeight() + 0.5F;
         int var10 = "deadmau5".equals(var2) ? -10 : 0;
         var3.pushPose();
         var3.translate(0.0D, (double)var9, 0.0D);
         var3.mulPose(this.entityRenderDispatcher.cameraOrientation());
         var3.scale(-0.025F, -0.025F, 0.025F);
         Matrix4f var11 = var3.last().pose();
         float var12 = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
         int var13 = (int)(var12 * 255.0F) << 24;
         Font var14 = this.getFont();
         float var15 = (float)(-var14.width(var2) / 2);
         var14.drawInBatch(var2, var15, (float)var10, 553648127, false, var11, var4, var8, var13, var5);
         if (var8) {
            var14.drawInBatch(var2, var15, (float)var10, -1, false, var11, var4, false, 0, var5);
         }

         var3.popPose();
      }
   }

   public EntityRenderDispatcher getDispatcher() {
      return this.entityRenderDispatcher;
   }
}
